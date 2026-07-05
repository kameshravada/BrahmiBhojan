# 11 - ER Diagrams

Status: Draft for approval

## High-Level ERD

```mermaid
erDiagram
    CUSTOMER ||--o{ ADDRESS : has
    CUSTOMER ||--o{ CART : owns
    CART ||--o{ CART_ITEM : contains
    CUSTOMER ||--o{ ORDER : places
    ORDER ||--o{ ORDER_ITEM : contains
    ORDER ||--o{ PAYMENT : has
    ORDER ||--o{ DELIVERY_SHIPMENT : has
    ORDER_ITEM ||--o{ RETURN_REQUEST : may_have
    PRODUCT ||--o{ ORDER_ITEM : purchased_as
    PRODUCT ||--o{ CART_ITEM : added_as
    CATEGORY ||--o{ PRODUCT_CATEGORY : maps
    PRODUCT ||--o{ PRODUCT_CATEGORY : maps
    PRODUCT ||--o{ INVENTORY : has
    PRODUCT ||--o{ REVIEW : receives
    CUSTOMER ||--o{ REVIEW : writes
    ADMIN_USER ||--o{ AUDIT_LOG : creates
```

## Notes

Detailed columns, keys, indexes, and constraints will be finalized during implementation planning after database design approval.

