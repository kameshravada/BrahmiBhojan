# 15 - Cart System

Status: Draft for approval

## Requirements

- Support guest cart.
- Support authenticated user cart.
- Merge guest cart after OTP login.
- Recalculate totals server-side.
- Block unavailable products.
- Show price changes clearly.

## Merge Rules

If guest and user cart contain same product/variant, combine quantities up to allowed limits. If stock is insufficient, cap to available stock and show warning.

## Cart Calculations

Cart subtotal, discount, GST at cart level, delivery charges if any, and payable total must be calculated by backend.

