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

### Layering vs Partitioning

For this project we chose a Layered Monolith rather than partitioning into multiple services. The layered approach (Presentation → Application → Domain → Infrastructure) keeps dependencies clear and enforces separation of concerns, but still allows us to run everything in a single process. This makes the system easier to reason about, test, and evolve during development. A monolith avoids the overhead of service boundaries, network calls, and deployment complexity, which would be disproportionate for a small student project. It also ensures that features like checkout, pricing, and receipt formatting can be demonstrated quickly without needing distributed infrastructure.

Looking ahead, there are natural seams that could be partitioned into separate services. Payments are a clear candidate, since they often integrate with external providers and require stronger isolation. Notifications (e.g., sending receipts or delivery updates) could also be split off, as they are asynchronous and loosely coupled. If we were to evolve into a multi‑service architecture, connectors would be defined explicitly: domain events via an event bus or message queue, and REST APIs for synchronous calls between services. For now, keeping everything layered in one monolith is the simplest way to deliver functionality while still preparing for future evolution.