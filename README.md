## Refactoring Reflection & Design Notes

### Smells Removed
- **God Class & Long Method**: Refactored `OrderManagerGod.process(...)` into smaller components (`CheckoutService`, `PricingService`, `ReceiptPrinter`).
- **Primitive Obsession**: Replaced string-based `discountCode` and `paymentType` with polymorphic `DiscountPolicy` and `PaymentStrategy`.
- **Global/Static State**: Removed `TAX_PERCENT` and `LAST_DISCOUNT_CODE`; replaced with constructor-injected values.
- **Duplicated Logic**: Centralized tax and discount calculations using reusable services.
- **Shotgun Surgery**: Isolated tax and discount logic to prevent ripple effects from future changes.

### Refactorings Applied
- `Extract Class`: `DiscountPolicy`, `TaxPolicy`, `ReceiptPrinter`, `PricingService`.
- `Replace Conditional with Polymorphism`: Used for payment and discount handling.
- `Constructor Injection`: Injected all dependencies into `CheckoutService`.
- `Delete Global State`: Removed static fields and replaced with injected configuration.

### SOLID Principles Satisfied
- **Single Responsibility**: Each class handles one concern (pricing, printing, payment).
- **Open/Closed**: New discount types can be added via new `DiscountPolicy` implementations.
- **Liskov Substitution**: All strategies and policies are interchangeable.
- **Interface Segregation**: Small, focused interfaces (`DiscountPolicy`, `TaxPolicy`, `PaymentStrategy`).
- **Dependency Inversion**: High-level modules depend on abstractions, not concrete implementations.

### Extensibility
To add a new discount type (e.g., `HolidayDiscount`), implement `DiscountPolicy` and inject it into `PricingService`. No changes to existing classes are required.