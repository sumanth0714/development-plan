# 🚀 SOLID Principles & Design Principles — Interview Preparation Guide

A practical, interview-focused guide to **SOLID principles and important software design principles**, explained using:

- Simple definitions
- Bad code ❌
- Good code ✅
- Production examples
- Interview answers
- Visual memory tricks

The goal is not just to remember the definitions, but to understand **why the bad design causes problems and how the good design improves production code**.

---

# 1. What Are Design Principles?

Design principles are guidelines that help us write software that is:

```text
Easy to understand
       ↓
Easy to change
       ↓
Easy to test
       ↓
Easy to maintain
       ↓
Less coupled
       ↓
More reusable
```

They are **guidelines, not strict rules**.

A good design should make future changes cheaper and safer.

---

# 2. SOLID Principles

SOLID stands for:

```text
S → Single Responsibility Principle
O → Open/Closed Principle
L → Liskov Substitution Principle
I → Interface Segregation Principle
D → Dependency Inversion Principle
```

Easy memory:

```text
S → One responsibility
O → Extend, don't modify
L → Child should behave like parent
I → Small interfaces
D → Depend on abstractions
```

---

# 3. S — Single Responsibility Principle

## Definition

> **A class should have one reason to change.**

It does NOT necessarily mean:

> "A class can have only one method."

It means the class should have **one clear responsibility**.

---

## ❌ Bad Production Code

Suppose we have an order service:

```java
public class OrderService {

    public void createOrder(Order order) {
        // Save order
    }

    public void sendEmail(Order order) {
        // Send email
    }

    public void generateInvoice(Order order) {
        // Generate PDF invoice
    }

    public void saveAuditLog(Order order) {
        // Save audit log
    }
}
```

This class has multiple responsibilities:

```text
OrderService
 |
 +-- Order processing
 +-- Email
 +-- Invoice generation
 +-- Audit logging
```

Now imagine the invoice format changes.

We have to modify `OrderService`.

If email logic changes, again we modify `OrderService`.

This creates a large, difficult-to-maintain class.

---

## ✅ Good Production Code

Separate responsibilities:

```java
public class OrderService {

    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public void createOrder(Order order) {

        orderRepository.save(order);

        invoiceService.generate(order);

        notificationService.sendOrderConfirmation(order);
    }
}
```

Separate services:

```java
public class InvoiceService {

    public void generate(Order order) {
        // Generate invoice
    }
}
```

```java
public class NotificationService {

    public void sendOrderConfirmation(Order order) {
        // Send notification
    }
}
```

```text
OrderService
     |
     +---- OrderRepository
     |
     +---- InvoiceService
     |
     +---- NotificationService
```

Each class has a focused responsibility.

---

## Production Example

In an e-commerce application:

```text
OrderService
   |
   +-- Create order

PaymentService
   |
   +-- Process payment

InventoryService
   |
   +-- Reserve stock

NotificationService
   |
   +-- Send email/SMS

InvoiceService
   |
   +-- Generate invoice
```

---

## Interview Answer

> **SRP means a class should have one reason to change. I avoid putting order processing, notification, invoice generation, and auditing into one service. I separate those responsibilities so changes in one area don't affect unrelated functionality.**

---

# 4. O — Open/Closed Principle

## Definition

> **Software entities should be open for extension but closed for modification.**

Meaning:

```text
Existing code
     |
     | Don't keep modifying it
     v
Add new behavior through extension
```

---

## ❌ Bad Code

Payment processing:

```java
public class PaymentService {

    public void pay(String type, double amount) {

        if (type.equals("CARD")) {
            // Card payment
        }
        else if (type.equals("UPI")) {
            // UPI payment
        }
        else if (type.equals("PAYPAL")) {
            // PayPal payment
        }
    }
}
```

Now business adds:

```text
APPLE_PAY
GOOGLE_PAY
BANK_TRANSFER
CRYPTO
```

We keep modifying the same class.

This becomes:

```text
if
else if
else if
else if
else if
```

---

## ✅ Good Code

Create an abstraction:

```java
public interface PaymentProcessor {

    void pay(double amount);
}
```

Implement different processors:

```java
public class CardPaymentProcessor
        implements PaymentProcessor {

    public void pay(double amount) {
        // Card payment
    }
}
```

```java
public class UpiPaymentProcessor
        implements PaymentProcessor {

    public void pay(double amount) {
        // UPI payment
    }
}
```

```java
public class PaypalPaymentProcessor
        implements PaymentProcessor {

    public void pay(double amount) {
        // PayPal payment
    }
}
```

Service:

```java
public class PaymentService {

    private final PaymentProcessor processor;

    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }

    public void pay(double amount) {
        processor.pay(amount);
    }
}
```

Adding a new payment method:

```java
public class ApplePayProcessor
        implements PaymentProcessor {

    public void pay(double amount) {
        // Apple Pay
    }
}
```

Existing payment service doesn't need modification.

---

## Production Example

A real payment system may support:

```text
PaymentProcessor
       |
       +-- CreditCardProcessor
       +-- UpiProcessor
       +-- PayPalProcessor
       +-- BankTransferProcessor
```

Adding a new payment method means adding a new implementation.

---

## Interview Answer

> **OCP means existing code should be stable while new behavior can be added through extension. For example, instead of adding more if-else conditions for every payment type, I define a PaymentProcessor interface and add new implementations.**

---

# 5. L — Liskov Substitution Principle

## Definition

> **Objects of a child class should be replaceable for objects of the parent type without breaking the expected behavior of the program.**

Simple version:

```text
Parent contract
      |
      v
Child must honor the contract
```

---

## ❌ Classic Bad Example

```java
class Bird {

    public void fly() {
        System.out.println("Flying");
    }
}
```

Now:

```java
class Penguin extends Bird {

    @Override
    public void fly() {
        throw new UnsupportedOperationException();
    }
}
```

Problem:

```java
Bird bird = new Penguin();

bird.fly();
```

The parent promises:

```text
Bird → fly()
```

But the child cannot fulfill that contract.

---

## ✅ Good Design

Separate capabilities:

```java
interface Bird {
}
```

```java
interface FlyingBird extends Bird {

    void fly();
}
```

```java
class Eagle implements FlyingBird {

    public void fly() {
        System.out.println("Flying");
    }
}
```

```java
class Penguin implements Bird {
}
```

Now the model doesn't force penguins to implement flying.

---

## Production Example

A better enterprise example is a notification abstraction.

Bad:

```java
interface Notification {

    void send();

    void schedule();

    void cancel();
}
```

If one implementation cannot support scheduling, it may throw:

```java
throw new UnsupportedOperationException();
```

This can indicate a broken abstraction.

Instead, model capabilities properly.

---

## Interview Answer

> **LSP means a subtype must honor the behavioral contract of its parent. If a subclass needs to throw UnsupportedOperationException for a normal parent operation, I would reconsider the abstraction.**

---

# 6. I — Interface Segregation Principle

## Definition

> **Clients should not be forced to depend on methods they do not use.**

Simple:

```text
❌ One huge interface

       ↓

✅ Several focused interfaces
```

---

## ❌ Bad Code

```java
interface Employee {

    void work();

    void eat();

    void manageTeam();

    void generateReport();

    void approveLeave();
}
```

A developer may not need:

```text
manageTeam()
approveLeave()
```

but is forced to depend on them.

---

## ✅ Good Code

Split interfaces:

```java
interface Worker {
    void work();
}
```

```java
interface Manager {
    void manageTeam();
    void approveLeave();
}
```

```java
interface ReportGenerator {
    void generateReport();
}
```

Now classes implement only what they need.

---

## Production Example

Instead of:

```java
interface UserService {

    createUser();
    deleteUser();
    generateReport();
    exportExcel();
    sendEmail();
    resetPassword();
    processPayment();
}
```

split responsibilities:

```text
UserManagement
Notification
Reporting
Payment
Authentication
```

---

## Interview Answer

> **ISP means interfaces should be small and client-specific. A class should not be forced to implement methods it doesn't need.**

---

# 7. D — Dependency Inversion Principle

## Definition

> **High-level modules should not depend directly on low-level modules. Both should depend on abstractions.**

And:

> **Abstractions should not depend on details; details should depend on abstractions.**

---

## ❌ Bad Code

```java
public class OrderService {

    private final MySqlOrderRepository repository;

    public OrderService() {
        repository = new MySqlOrderRepository();
    }
}
```

Problem:

```text
OrderService
     |
     v
MySqlOrderRepository
```

The service is tightly coupled to MySQL implementation.

Changing to MongoDB or another persistence mechanism requires modifying the service.

---

## ✅ Good Code

Create abstraction:

```java
public interface OrderRepository {

    void save(Order order);
}
```

Implementation:

```java
public class MySqlOrderRepository
        implements OrderRepository {

    public void save(Order order) {
        // MySQL implementation
    }
}
```

Service:

```java
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

Now:

```text
             OrderService
                  |
                  v
          OrderRepository
             /        \
            /          \
       MySQL           MongoDB
```

---

## Spring Boot Production Example

This is one reason constructor injection is so useful:

```java
@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

Spring provides the implementation.

The service depends on the abstraction.

---

## Interview Answer

> **DIP means high-level business logic should depend on abstractions rather than concrete infrastructure classes. In Spring Boot, I commonly achieve this through interfaces and dependency injection.**

---

# 8. SOLID Complete Memory Diagram

```text
                    SOLID
                      |
      +---------------+---------------+
      |               |               |
      S               O               L
      |               |               |
 One responsibility   Extend          Child
                     don't modify     must behave
                                      like parent

      I               D
      |               |
 Small interfaces     Depend on
                      abstractions
```

---

# 9. DRY — Don't Repeat Yourself

## Definition

> **Every piece of knowledge should have a single authoritative representation in the system.**

---

## ❌ Bad Code

```java
public void createUser(User user) {

    if (user.getEmail() == null ||
        !user.getEmail().contains("@")) {
        throw new IllegalArgumentException();
    }

    // create
}
```

Another class:

```java
public void updateUser(User user) {

    if (user.getEmail() == null ||
        !user.getEmail().contains("@")) {
        throw new IllegalArgumentException();
    }

    // update
}
```

Validation is duplicated.

---

## ✅ Good Code

```java
public class EmailValidator {

    public static void validate(String email) {

        if (email == null ||
            !email.contains("@")) {

            throw new IllegalArgumentException();
        }
    }
}
```

Use:

```java
EmailValidator.validate(user.getEmail());
```

---

## Production Example

Common reusable logic:

```text
Authentication
     |
     +-- JWT validation

Multiple APIs
     |
     +-- Same validation logic
```

Instead of copying JWT validation into every controller, centralize it using Spring Security filters/components.

---

## Interview Point

> **DRY reduces duplication, but I avoid extracting every repeated line blindly. I look for repeated knowledge or business rules.**

---

# 10. KISS — Keep It Simple

## Definition

> **Prefer the simplest design that correctly solves the problem.**

---

## ❌ Bad Code

Suppose:

```java
if (user != null) {
    if (user.isActive()) {
        if (user.getRole() != null) {
            if (user.getRole().equals("ADMIN")) {
                return true;
            }
        }
    }
}
return false;
```

---

## ✅ Good Code

```java
return user != null
        && user.isActive()
        && "ADMIN".equals(user.getRole());
```

Or use a dedicated method:

```java
public boolean isActiveAdmin(User user) {
    return user != null
            && user.isActive()
            && "ADMIN".equals(user.getRole());
}
```

---

## Production Example

Do not introduce:

```text
5 design patterns
+
3 abstraction layers
+
10 interfaces
```

for a simple CRUD operation.

Use the simplest architecture that meets current requirements while leaving reasonable room for change.

---

# 11. YAGNI — You Aren't Gonna Need It

## Definition

> **Don't implement functionality until it is actually required.**

---

## ❌ Bad

Requirement:

```text
Create customer
```

Developer builds:

```text
Customer
 |
 +-- Payment engine
 +-- Recommendation engine
 +-- Reporting engine
 +-- AI engine
 +-- Multi-region abstraction
```

even though none is currently required.

---

## ✅ Good

Start with:

```text
Customer API
    |
    v
Customer Service
    |
    v
Customer Repository
```

Add new capabilities when requirements justify them.

---

## Production Example

Don't build a complete event-driven architecture for a small internal CRUD application unless there is a real requirement for it.

---

# 12. Separation of Concerns

## Definition

> **Different concerns should be handled by different components.**

Typical Spring Boot structure:

```text
Controller
   |
   | HTTP concerns
   v
Service
   |
   | Business logic
   v
Repository
   |
   | Data access
   v
Database
```

---

## ❌ Bad

```java
@RestController
class OrderController {

    @PostMapping
    public void create(Order order) {

        // Validate
        // Business logic
        // SQL
        // Email
        // Logging
        // Payment
    }
}
```

---

## ✅ Good

```text
Controller
   ↓
Service
   ↓
Repository
```

and external responsibilities are separated:

```text
Service
  |
  +-- PaymentClient
  +-- NotificationService
  +-- InventoryService
```

---

# 13. Composition Over Inheritance

## Principle

> **Prefer composing objects with smaller behaviors rather than creating deep inheritance hierarchies when composition better models the problem.**

---

## ❌ Bad

```text
Vehicle
   |
   +-- Car
       |
       +-- ElectricCar
           |
           +-- FlyingElectricCar
               |
               +-- AutonomousFlyingElectricCar
```

This can become difficult to maintain.

---

## ✅ Good

Compose capabilities:

```text
Car
 |
 +-- Engine
 +-- Navigation
 +-- Payment
 +-- AutonomousDriving
```

Example:

```java
class Car {

    private final Engine engine;
    private final Navigation navigation;

    Car(Engine engine,
        Navigation navigation) {

        this.engine = engine;
        this.navigation = navigation;
    }
}
```

---

## Production Example

Spring itself heavily uses composition and dependency injection:

```java
@Service
class OrderService {

    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    // dependencies are composed into the service
}
```

---

# 14. Program to an Interface

## Principle

> **Depend on abstractions instead of concrete implementations.**

Bad:

```java
ArrayList<User> users = new ArrayList<>();
```

More flexible:

```java
List<User> users = new ArrayList<>();
```

Why?

```text
List
 |
 +-- ArrayList
 +-- LinkedList
```

The caller depends on the abstraction.

---

# 15. Encapsulation

## Definition

> **Hide internal state and implementation details and expose controlled operations.**

---

## ❌ Bad

```java
class BankAccount {

    public double balance;
}
```

Anyone can do:

```java
account.balance = -100000;
```

---

## ✅ Good

```java
class BankAccount {

    private double balance;

    public void withdraw(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException();
        }

        if (amount > balance) {
            throw new IllegalArgumentException();
        }

        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}
```

The object controls its own state.

---

# 16. Law of Demeter

## Definition

> **An object should have limited knowledge of the internal structure of other objects.**

Simple idea:

```text
Don't talk to strangers.
```

---

## ❌ Bad

```java
order.getCustomer()
     .getAddress()
     .getCity()
     .getCountry()
     .getName();
```

This creates knowledge of internal object structure.

---

## ✅ Better

Expose behavior:

```java
order.getCustomerCountry();
```

or:

```java
order.getShippingCountry();
```

The caller doesn't need to know how the information is internally structured.

---

# 17. Tell, Don't Ask

## Principle

> **Tell an object what to do instead of asking for its data and performing the object's logic elsewhere.**

---

## ❌ Bad

```java
if (account.getBalance() >= amount) {
    account.setBalance(
        account.getBalance() - amount
    );
}
```

The caller manipulates internal state.

---

## ✅ Good

```java
account.withdraw(amount);
```

The account owns the withdrawal business rule.

---

# 18. Fail Fast

## Principle

> **Detect invalid input or invalid state as early as possible.**

---

## ❌ Bad

```java
public void createUser(User user) {

    // 50 lines of processing

    if (user.getEmail() == null) {
        throw new IllegalArgumentException();
    }
}
```

---

## ✅ Good

```java
public void createUser(User user) {

    if (user == null) {
        throw new IllegalArgumentException("User required");
    }

    if (user.getEmail() == null) {
        throw new IllegalArgumentException("Email required");
    }

    // continue processing
}
```

In Spring Boot, validation annotations such as:

```java
@NotBlank
@Email
@Size
```

can help reject invalid request data early.

---

# 19. Immutability

## Principle

> **Prefer objects whose state cannot be changed after creation when practical.**

Example:

```java
public final class Money {

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
```

Benefits:

```text
Immutable object
      |
      +-- Easier reasoning
      +-- Thread-friendly
      +-- Safer sharing
      +-- Fewer accidental changes
```

---

# 20. Dependency Injection

Dependency Injection is both a framework technique and an important design practice.

## ❌ Bad

```java
class OrderService {

    private PaymentService paymentService =
            new PaymentService();
}
```

The class creates its dependency.

---

## ✅ Good

```java
class OrderService {

    private final PaymentService paymentService;

    OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Spring:

```java
@Service
class OrderService {

    private final PaymentService paymentService;

    OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Benefits:

```text
Loose coupling
      +
Easy mocking
      +
Easy testing
      +
Flexible implementation
```

---

# 21. Coupling vs Cohesion

This is an important interview topic.

## Coupling

How strongly components depend on each other.

Prefer:

```text
LOW COUPLING
```

---

## Cohesion

How closely related the responsibilities inside a component are.

Prefer:

```text
HIGH COHESION
```

Ideal:

```text
High Cohesion
      +
Low Coupling
      =
Maintainable Design
```

---

# 22. Bad vs Good Architecture

## ❌ Bad

```text
Controller
   |
   +-- SQL
   +-- Business logic
   +-- Payment
   +-- Email
   +-- Logging
   +-- Validation
```

Problems:

```text
High coupling
Low cohesion
Hard to test
Hard to change
Large classes
```

---

## ✅ Good

```text
Controller
    |
    v
Service
    |
    +---- Repository
    |
    +---- PaymentClient
    |
    +---- NotificationService
    |
    +---- InventoryService
```

Benefits:

```text
High cohesion
Low coupling
Easy testing
Easy maintenance
```

---

# 23. Production Example — E-Commerce Order

Imagine an order flow:

```text
Customer
   |
   v
OrderController
   |
   v
OrderService
   |
   +---- InventoryService
   |
   +---- PaymentService
   |
   +---- OrderRepository
   |
   +---- NotificationService
```

Apply principles:

### SRP

Each service owns one major responsibility.

### OCP

Payment methods can be added using `PaymentProcessor`.

### LSP

Payment implementations must honor the payment contract.

### ISP

Use focused interfaces such as:

```text
PaymentProcessor
NotificationSender
InventoryManager
```

instead of one huge interface.

### DIP

`OrderService` depends on abstractions.

### DRY

Common validation and business rules are centralized.

### KISS

Don't introduce unnecessary architecture.

### YAGNI

Don't implement unused features.

### Composition

Build `OrderService` using injected collaborators.

---

# 24. SOLID vs Other Design Principles

| Principle | Main Goal |
|---|---|
| SRP | One reason to change |
| OCP | Extend without repeatedly modifying stable code |
| LSP | Subtypes honor parent contracts |
| ISP | Small focused interfaces |
| DIP | Depend on abstractions |
| DRY | Avoid duplicated knowledge |
| KISS | Keep design simple |
| YAGNI | Don't build unnecessary features |
| Separation of Concerns | Separate different responsibilities |
| Composition over Inheritance | Prefer flexible object composition |
| Encapsulation | Protect internal state |
| Law of Demeter | Reduce knowledge of object internals |
| Tell Don't Ask | Put behavior with the object that owns the data |
| Fail Fast | Detect invalid states early |
| Immutability | Prevent accidental state changes |
| Dependency Injection | Supply dependencies from outside |
| Low Coupling | Reduce dependency between components |
| High Cohesion | Keep related responsibilities together |

---

# 25. Most Important Principles for a Java/Spring Interview

If the interviewer asks:

> "Which design principles do you commonly use in production?"

A strong answer is:

> **I commonly apply SOLID principles, especially SRP, OCP, DIP, and ISP. In Spring Boot, I use dependency injection and programming to interfaces to reduce coupling. I also follow DRY and KISS, prefer composition over deep inheritance, keep responsibilities separated between controller/service/repository layers, and use encapsulation to protect domain state. I try to avoid YAGNI by not introducing complexity before there is a real requirement.**

---

# 26. Interview Scenario — "Your Service Is 1,500 Lines. What Would You Do?"

### Answer

I would not immediately split it randomly.

First identify responsibilities:

```text
OrderService
   |
   +-- Order creation
   +-- Payment
   +-- Notification
   +-- Invoice
   +-- Reporting
   +-- Validation
```

Then separate responsibilities based on **reasons to change**.

For example:

```text
OrderService
PaymentService
NotificationService
InvoiceService
OrderValidator
```

This applies:

```text
SRP
+
Separation of Concerns
+
High Cohesion
+
Low Coupling
```

---

# 27. Interview Scenario — "There Are Many If-Else Conditions"

Example:

```java
if (type.equals("CARD")) {
}
else if (type.equals("UPI")) {
}
else if (type.equals("PAYPAL")) {
}
else if (type.equals("BANK")) {
}
```

### Answer

If these branches represent independently changing business strategies, I would consider the **Strategy Pattern** with an interface:

```java
interface PaymentProcessor {
    void pay(BigDecimal amount);
}
```

Then:

```text
PaymentProcessor
       |
       +-- CardProcessor
       +-- UpiProcessor
       +-- PaypalProcessor
       +-- BankProcessor
```

This supports OCP and reduces conditional complexity.

Important:

> Not every if-else needs a design pattern. I introduce the abstraction when the behavior is genuinely variable or expected to grow.

---

# 28. Interview Scenario — "How Do You Avoid Circular Dependencies?"

Bad:

```text
OrderService
     ↕
PaymentService
```

Better:

```text
OrderService
     |
     v
PaymentService

or

OrderService
     |
     v
Event
     |
     v
PaymentListener
```

Or extract shared responsibility:

```text
OrderService ----\
                  > SharedService
PaymentService --/
```

Principles involved:

```text
SRP
DIP
Low Coupling
Separation of Concerns
```

---

# 29. Interview Scenario — "Why Constructor Injection?"

Good answer:

> **Constructor injection makes dependencies explicit, supports immutability through final fields, makes the class easier to unit test, and helps reveal excessive dependencies. If a class requires ten dependencies, that can be a design smell indicating too many responsibilities.**

Example:

```java
@Service
public class OrderService {

    private final OrderRepository repository;
    private final PaymentService paymentService;

    public OrderService(
            OrderRepository repository,
            PaymentService paymentService) {

        this.repository = repository;
        this.paymentService = paymentService;
    }
}
```

---

# 30. Design Principles — One-Line Memory Map

```text
SOLID
 |
 +-- S → One responsibility
 +-- O → Extend, don't repeatedly modify stable code
 +-- L → Child must honor parent contract
 +-- I → Small interfaces
 +-- D → Depend on abstractions

DRY
 |
 +-- Don't duplicate business knowledge

KISS
 |
 +-- Keep it simple

YAGNI
 |
 +-- Don't build unnecessary features

Separation of Concerns
 |
 +-- Separate responsibilities

Composition
 |
 +-- Build behavior from components

Encapsulation
 |
 +-- Protect internal state

Law of Demeter
 |
 +-- Don't know too much about object internals

Tell Don't Ask
 |
 +-- Give behavior to the object owning the data

Fail Fast
 |
 +-- Reject invalid state early

DI
 |
 +-- Dependencies come from outside

High Cohesion
 |
 +-- Related responsibilities stay together

Low Coupling
 |
 +-- Minimize dependency between components
```

---

# 31. ⭐ Final Interview Cheat Sheet

### SOLID

**S — SRP**

> One class should have one reason to change.

**O — OCP**

> Add new behavior through extension rather than repeatedly modifying stable code.

**L — LSP**

> A subtype must honor the contract expected from its parent type.

**I — ISP**

> Don't force clients to depend on methods they don't use.

**D — DIP**

> High-level business logic should depend on abstractions, not concrete infrastructure.

---

### Other Important Principles

**DRY**

> Don't duplicate business knowledge.

**KISS**

> Prefer simple solutions.

**YAGNI**

> Don't implement functionality until it is actually needed.

**Separation of Concerns**

> Keep different responsibilities in different components.

**Composition over Inheritance**

> Prefer composing behavior when that provides better flexibility than deep inheritance.

**Encapsulation**

> Keep state protected and expose controlled behavior.

**Low Coupling + High Cohesion**

> Keep components independent and keep related responsibilities together.

---

# 32. 🎯 60-Second Interview Answer

> **In production, I use SOLID as a guideline rather than applying every principle mechanically. For example, I use SRP to keep services focused, OCP when I have behavior that is expected to vary such as payment strategies, DIP with interfaces and dependency injection, and ISP to keep interfaces focused. I also use DRY to avoid duplicated business rules, KISS to avoid unnecessary complexity, and YAGNI to avoid building features that aren't required. In Spring Boot, this usually results in a layered design where controllers handle HTTP concerns, services handle business logic, repositories handle persistence, and external integrations are represented through abstractions. The main goal is high cohesion, low coupling, testability, and making future changes safer.**

---

# 33. 🎯 The Most Important Rule

Don't say in an interview:

> "I always use SOLID."

Say:

> **"I use SOLID principles where they improve maintainability and flexibility. I avoid over-engineering and choose the simplest design that meets the current requirements."**

That answer demonstrates practical engineering judgment rather than just memorizing design principles.
