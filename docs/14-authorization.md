# 14 - Authorization

Status: Draft for approval

## Model

Use role-based access control for admin portal.

## Launch Role

Initial launch starts with `ADMIN`.

## Future Roles

- SUPER_ADMIN
- CATALOG_ADMIN
- INVENTORY_ADMIN
- ORDER_ADMIN
- MARKETING_ADMIN
- FINANCE_ADMIN
- SUPPORT_ADMIN

## Rules

- Admin APIs require admin token.
- Customer APIs require customer token when personal data or checkout is involved.
- Public catalog APIs allow guest access.
- Sensitive admin actions must be audited.

