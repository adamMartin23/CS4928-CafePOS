---

# ADR: EventBus vs Direct Service Calls

## Context
In the Café POS & Delivery system, multiple components (UI, order service, delivery service, payment adapters) need to react to domain events such as `OrderCreated` and `OrderPaid`.  
We faced a design choice: should these components communicate via **direct method calls** (tight coupling) or through a **publish/subscribe mechanism** (loose coupling)?

The demo code (`FinalCafePosDemo.java`) shows this need clearly:
```java
EventBus bus = new EventBus();
bus.on(OrderCreated.class, e -> System.out.println("[UI] order created: " + e.orderId()));
bus.on(OrderPaid.class, e -> System.out.println("[UI] order paid: " + e.orderId()));

bus.emit(new OrderCreated(id));
bus.emit(new OrderPaid(id));
```

## Decision
We chose to implement an **EventBus** as the connector between components.  
Instead of direct calls from `OrderService` to UI or delivery modules, events are emitted and subscribers handle them independently.

## Alternatives considered
- **Direct calls:**  
  - `OrderService` invokes `UI.notifyOrderCreated(id)` or `Delivery.assignCourier(id)` directly.  
  - Simple, but couples services tightly and makes testing harder.
- **Shared service registry:**  
  - Central service locator to route calls.  
  - Adds indirection but still couples modules at compile time.
- **EventBus (chosen):**  
  - Decouples publishers and subscribers.  
  - Allows multiple listeners without changing publisher code.

## Consequences
- **Pros:**  
  - Loose coupling between POS and Delivery modules.  
  - Easy to add new subscribers (e.g., analytics, notifications) without touching existing code.  
  - Improves testability: events can be simulated and handlers verified independently.
- **Cons:**  
  - Harder to trace event flow compared to direct calls.  
  - Debugging requires logging or monitoring tools.  
  - Asynchronous/event-driven complexity may introduce ordering issues if not carefully managed.

## Status
Accepted and implemented.  
Evidence: `FinalCafePosDemo.java` shows `EventBus` emitting `OrderCreated` and `OrderPaid` events, with UI subscribers reacting without direct service calls.

---