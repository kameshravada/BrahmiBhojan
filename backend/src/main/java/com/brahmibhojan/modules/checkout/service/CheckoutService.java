package com.brahmibhojan.modules.checkout.service;

import com.brahmibhojan.modules.cart.model.Cart;
import com.brahmibhojan.modules.cart.model.CartItem;
import com.brahmibhojan.modules.cart.model.CartStatus;
import com.brahmibhojan.modules.cart.repository.CartItemRepository;
import com.brahmibhojan.modules.cart.repository.CartRepository;
import com.brahmibhojan.modules.catalog.model.ProductVariant;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import com.brahmibhojan.modules.checkout.dto.CheckoutItemResponse;
import com.brahmibhojan.modules.checkout.dto.CheckoutValidateResponse;
import com.brahmibhojan.modules.checkout.dto.CreateOrderRequest;
import com.brahmibhojan.modules.checkout.dto.CreateOrderResponse;
import com.brahmibhojan.modules.customer.model.CustomerAddress;
import com.brahmibhojan.modules.customer.repository.CustomerAddressRepository;
import com.brahmibhojan.modules.inventory.dto.InventoryReserveItem;
import com.brahmibhojan.modules.inventory.service.InventoryService;
import com.brahmibhojan.modules.notifications.service.NotificationService;
import com.brahmibhojan.modules.orders.model.Order;
import com.brahmibhojan.modules.orders.model.OrderItem;
import com.brahmibhojan.modules.orders.model.OrderStatus;
import com.brahmibhojan.modules.orders.model.PaymentStatus;
import com.brahmibhojan.modules.orders.repository.OrderItemRepository;
import com.brahmibhojan.modules.orders.repository.OrderRepository;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.model.UserStatus;
import com.brahmibhojan.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private static final DateTimeFormatter ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);
    private static final BigDecimal TAX_RATE = new BigDecimal("0.00");
    private static final BigDecimal DELIVERY_FEE = BigDecimal.ZERO;

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional(readOnly = true)
    public CheckoutValidateResponse validateCheckout(String mobile, UUID addressId) {
        CheckoutContext context = buildCheckoutContext(mobile, addressId);
        return toValidateResponse(context);
    }

    @Transactional
    public CreateOrderResponse createOrder(String mobile, CreateOrderRequest request) {
        CheckoutContext context = buildCheckoutContext(mobile, request.addressId());

        String normalizedIdempotencyKey = normalizeIdempotencyKey(request.idempotencyKey());
        if (normalizedIdempotencyKey != null) {
            Order existing = orderRepository
                    .findByUserIdAndIdempotencyKey(context.user().getId(), normalizedIdempotencyKey)
                    .orElse(null);
            if (existing != null) {
                return toCreateOrderResponse(existing);
            }
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(context.user());
        order.setIdempotencyKey(normalizedIdempotencyKey);
        order.setAddressId(context.address().getId());
        order.setRecipientName(context.address().getRecipientName());
        order.setPhoneNumber(context.address().getPhoneNumber());
        order.setLine1(context.address().getLine1());
        order.setLine2(context.address().getLine2());
        order.setLandmark(context.address().getLandmark());
        order.setCity(context.address().getCity());
        order.setState(context.address().getState());
        order.setCountry(context.address().getCountry());
        order.setPostalCode(context.address().getPostalCode());
        order.setSubtotalAmount(context.subtotal());
        order.setTaxAmount(context.taxAmount());
        order.setDeliveryFee(context.deliveryFee());
        order.setPayableAmount(context.payableAmount());
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(request.paymentMethod().trim());
        order.setNotes(request.notes() == null ? null : request.notes().trim());

        Order savedOrder = orderRepository.save(order);
        reserveInventory(savedOrder.getId(), context.items());
        saveOrderItems(savedOrder, context.items());

        context.cart().setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(context.cart());

        try {
            notificationService.sendOrderConfirmation(savedOrder);
        } catch (Exception ex) {
            log.warn("Order confirmation notification failed orderId={}", savedOrder.getId(), ex);
        }

        return toCreateOrderResponse(savedOrder);
    }

    private void reserveInventory(UUID orderId, List<CheckoutCartItem> items) {
        List<InventoryReserveItem> reserveItems = items.stream()
                .map(item -> new InventoryReserveItem(item.variant().getId(), item.cartItem().getQuantity()))
                .toList();
        inventoryService.reserveForOrder(orderId, reserveItems);
    }

    private void saveOrderItems(Order order, List<CheckoutCartItem> items) {
        for (CheckoutCartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(item.cartItem().getProduct().getId());
            orderItem.setProductName(item.cartItem().getProduct().getName());
            orderItem.setProductSlug(item.cartItem().getProduct().getSlug());
            orderItem.setVariantId(item.variant().getId());
            orderItem.setVariantLabel(item.variant().getLabel());
            orderItem.setUnitPrice(item.variant().getPrice());
            orderItem.setQuantity(item.cartItem().getQuantity());
            orderItem.setLineTotal(item.lineTotal());
            orderItemRepository.save(orderItem);
        }
    }

    private CheckoutContext buildCheckoutContext(String mobile, UUID addressId) {
        User user = findActiveUser(mobile);
        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active cart not found"));

        List<CartItem> cartItems = cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(cart.getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        CustomerAddress address = customerAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery address not found"));

        List<CheckoutCartItem> checkoutItems = cartItems.stream()
                .map(this::toCheckoutItem)
                .toList();

        BigDecimal subtotal = checkoutItems.stream()
                .map(CheckoutCartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = subtotal.multiply(TAX_RATE);
        BigDecimal payableAmount = subtotal.add(taxAmount).add(DELIVERY_FEE);

        return new CheckoutContext(user, cart, address, checkoutItems, subtotal, taxAmount, DELIVERY_FEE, payableAmount);
    }

    private CheckoutCartItem toCheckoutItem(CartItem cartItem) {
        ProductVariant variant = productVariantRepository.findByIdAndAvailableTrue(cartItem.getProductVariant().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Variant unavailable for product " + cartItem.getProduct().getName()
                ));

        BigDecimal unitPrice = variant.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        cartItem.setUnitPrice(unitPrice);
        cartItem.setLineTotal(lineTotal);

        return new CheckoutCartItem(cartItem, variant, lineTotal);
    }

    private User findActiveUser(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is not active");
        }

        return user;
    }

    private CheckoutValidateResponse toValidateResponse(CheckoutContext context) {
        List<CheckoutItemResponse> items = context.items().stream()
                .map(item -> new CheckoutItemResponse(
                        item.cartItem().getProduct().getId(),
                        item.cartItem().getProduct().getName(),
                        item.variant().getId(),
                        item.variant().getLabel(),
                        item.variant().getPrice(),
                        item.cartItem().getQuantity(),
                        item.lineTotal()
                ))
                .toList();

        return new CheckoutValidateResponse(
                context.cart().getId(),
                context.address().getId(),
                items.size(),
                context.subtotal(),
                context.taxAmount(),
                context.deliveryFee(),
                context.payableAmount(),
                items
        );
    }

    private CreateOrderResponse toCreateOrderResponse(Order order) {
        return new CreateOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getPayableAmount()
        );
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return null;
        }
        return idempotencyKey.trim();
    }

    private String generateOrderNumber() {
        String datePart = ORDER_DATE_FORMATTER.format(Instant.now());
        int randomPart = 100000 + secureRandom.nextInt(900000);
        return "BB-" + datePart + "-" + randomPart;
    }

    private record CheckoutContext(
            User user,
            Cart cart,
            CustomerAddress address,
            List<CheckoutCartItem> items,
            BigDecimal subtotal,
            BigDecimal taxAmount,
            BigDecimal deliveryFee,
            BigDecimal payableAmount
    ) {
    }

    private record CheckoutCartItem(
            CartItem cartItem,
            ProductVariant variant,
            BigDecimal lineTotal
    ) {
    }
}



