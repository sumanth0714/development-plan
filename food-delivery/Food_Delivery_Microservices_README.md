# Food Delivery Microservices

A production-style **Spring Boot Microservices** project demonstrating how a food-delivery system can be designed using independent services, service discovery, API Gateway, OpenFeign, JPA, DTOs, Resilience4j Circuit Breaker, Actuator, and centralized request routing.

The project is intentionally structured as an interview-friendly learning project so that each microservice concept can be understood independently and then as part of the complete architecture.

---

## 1. Project Goal

The main goal is to understand how a monolithic application can be divided into independently deployable microservices and how those services communicate reliably.

The project demonstrates:

- Microservice architecture
- Service-to-service communication
- Eureka Service Discovery
- Spring Cloud Gateway
- OpenFeign
- DTO-based communication
- Spring Data JPA / Hibernate
- Database-per-service approach
- Resilience4j Circuit Breaker
- Fallback handling
- Circuit Breaker state transitions
- Spring Boot Actuator
- Health monitoring
- Error handling
- Testing and failure scenarios

---

# 2. High-Level Architecture

```text
                         CLIENT
                           |
                           v
                  +-------------------+
                  |   API GATEWAY     |
                  +-------------------+
                           |
                           v
                  +-------------------+
                  |  EUREKA SERVER    |
                  | Service Discovery  |
                  +-------------------+
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
      +------------+ +------------+ +------------+
      |   Order    | | Restaurant | |   Other    |
      |  Service   | |  Service   | |  Services  |
      +------------+ +------------+ +------------+
             |
             | OpenFeign
             v
      +----------------+
      |   Restaurant   |
      |    Service     |
      +----------------+
             |
             v
        Restaurant DB
```

The important communication flow demonstrated in this project is:

```text
Client
  |
  v
API Gateway
  |
  v
Order Service
  |
  | OpenFeign
  v
Restaurant Service
```

The Circuit Breaker is placed around the call from:

```text
Order Service
      |
      | Feign
      v
Restaurant Service
```

---

# 3. Microservices

## 3.1 API Gateway

The API Gateway acts as the single entry point for clients.

Responsibilities:

- Accept client requests
- Route requests to the correct microservice
- Hide internal service locations
- Integrate with service discovery
- Provide a centralized entry point

Example:

```text
POST /orders
      |
      v
API Gateway
      |
      v
Order Service
```

---

## 3.2 Eureka Server

Eureka provides **service discovery**.

Instead of hardcoding service URLs, services register themselves with Eureka.

```text
Order Service --------\
Restaurant Service ----> Eureka Server
Other Services -------/
```

When Order Service needs Restaurant Service, it does not need to know the exact host and port.

```text
Order Service
     |
     | "Where is Restaurant Service?"
     v
 Eureka
     |
     | Restaurant Service instance
     v
 Restaurant Service
```

### Why Service Discovery?

Without service discovery:

```text
http://localhost:8082/restaurants
```

With service discovery:

```text
RESTAURANT-SERVICE
```

This becomes especially useful when multiple instances of a service are running.

---

# 4. Order Service

The Order Service manages order-related operations.

Typical responsibilities:

- Create an order
- Validate order information
- Communicate with Restaurant Service
- Store order information
- Return order response to the client

Typical flow:

```text
Controller
    |
    v
Service
    |
    +----> Repository
    |
    +----> Restaurant Feign Client
                  |
                  v
          Restaurant Service
```

---

# 5. Restaurant Service

The Restaurant Service manages restaurant-related information.

Typical responsibilities:

- Restaurant information
- Restaurant availability
- Menu information
- Restaurant lookup
- Restaurant-related business operations

The Order Service communicates with it through OpenFeign.

---

# 6. OpenFeign

OpenFeign provides a declarative way to communicate with another microservice.

Instead of manually creating HTTP requests, we define an interface.

Conceptually:

```java
@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

    @GetMapping("/restaurants/{id}")
    RestaurantResponse getRestaurant(Long id);
}
```

The important idea is:

```text
Order Service
     |
     | Feign Client
     v
Restaurant Service
```

Feign handles the HTTP communication.

---

# 7. Why Feign?

Without Feign, developers may need to write code for:

```text
HTTP request
URL construction
Headers
Serialization
Deserialization
Response handling
Exception handling
```

With Feign:

```text
Java Interface
      |
      v
Feign
      |
      v
HTTP Request
```

This makes service-to-service communication cleaner and easier to maintain.

---

# 8. DTOs

DTO means **Data Transfer Object**.

DTOs are used to transfer only the data required between layers or services.

Example:

```text
Entity
  |
  | Mapping
  v
DTO
  |
  v
API Response
```

### Why not expose entities directly?

Because entities are database models while DTOs are API contracts.

Using DTOs helps with:

- Encapsulation
- API stability
- Security
- Separation of concerns
- Avoiding accidental exposure of database fields

---

# 9. JPA and Hibernate

The services use Spring Data JPA for database interaction.

Typical flow:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
JPA / Hibernate
    |
    v
Database
```

Example repository:

```java
public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {
}
```

Spring Data JPA provides common CRUD operations without manually writing SQL for every operation.

Hibernate acts as the JPA implementation and handles ORM.

---

# 10. Database Per Service

Each microservice should own its data.

Conceptually:

```text
Order Service       -> Order DB

Restaurant Service  -> Restaurant DB
```

The Order Service should not directly query the Restaurant database.

Instead:

```text
Order Service
     |
     | API / Feign
     v
Restaurant Service
     |
     v
Restaurant DB
```

This keeps services loosely coupled.

---

# 11. Circuit Breaker

One of the most important concepts demonstrated in this project is the **Circuit Breaker**.

Consider:

```text
Order Service
     |
     | Feign
     v
Restaurant Service
```

Suppose Restaurant Service is down.

Without a Circuit Breaker:

```text
Request
   |
   v
Order Service
   |
   v
Restaurant Service
   |
   X DOWN
   |
   v
Timeout / Exception
```

Repeated requests continue hitting the unavailable service.

This can cause:

- Slow responses
- Thread exhaustion
- Increased resource usage
- Cascading failures
- Poor user experience

---

# 12. Resilience4j Circuit Breaker

Resilience4j provides the Circuit Breaker implementation.

Conceptually:

```text
Order Service
      |
      v
Circuit Breaker
      |
      v
Feign Client
      |
      v
Restaurant Service
```

The Circuit Breaker monitors failures.

If failures cross the configured threshold, the circuit opens.

---

# 13. Circuit Breaker States

Resilience4j Circuit Breaker has three important states.

```text
              failures
 CLOSED --------------------> OPEN
   ^                            |
   |                            |
   | successful test             | wait duration
   |                            |
   |                            v
   +------------------------- HALF_OPEN
```

## CLOSED

Normal operation.

```text
Request
  |
  v
Circuit Breaker
  |
  v
Restaurant Service
```

Requests are allowed through.

Failures are monitored.

---

## OPEN

Too many failures occurred.

The Circuit Breaker opens.

```text
Request
   |
   v
Circuit Breaker
   |
   X
   |
   v
Fallback
```

The request is rejected immediately instead of calling the failing service.

This prevents unnecessary calls to an unavailable dependency.

---

## HALF_OPEN

After the configured wait duration, the Circuit Breaker allows a limited number of test calls.

```text
HALF_OPEN
    |
    +----> Test request
             |
       +-----+-----+
       |           |
    Success      Failure
       |           |
       v           v
    CLOSED        OPEN
```

If the dependency has recovered, the Circuit Breaker closes.

If failures continue, it returns to OPEN.

---

# 14. Fallback

A fallback provides an alternative response when the dependent service is unavailable.

Example:

```text
Order Service
      |
      v
Circuit Breaker
      |
      v
Restaurant Service
      |
      X
   unavailable
      |
      v
Fallback
```

Example conceptual response:

```json
{
  "message": "Restaurant service is currently unavailable"
}
```

The exact fallback response should be designed according to the business requirement.

A fallback should not hide serious failures silently. It should provide a meaningful degraded response and appropriate logging/monitoring.

---

# 15. Without Circuit Breaker vs With Circuit Breaker

## Without Circuit Breaker

```text
Request 1 ---> Restaurant Service ---> FAIL
Request 2 ---> Restaurant Service ---> FAIL
Request 3 ---> Restaurant Service ---> FAIL
Request 4 ---> Restaurant Service ---> FAIL
Request 5 ---> Restaurant Service ---> FAIL
```

The application keeps attempting the failing dependency.

---

## With Circuit Breaker

```text
Request 1 ---> Restaurant Service ---> FAIL
Request 2 ---> Restaurant Service ---> FAIL
Request 3 ---> Restaurant Service ---> FAIL
                         |
                         v
                    Threshold
                         |
                         v
                    OPEN CIRCUIT
                         |
                         v
                  Fallback response
```

Later requests fail fast without unnecessarily calling Restaurant Service.

---

# 16. Practical Failure Test

A useful way to understand the Circuit Breaker is to deliberately stop Restaurant Service.

### Step 1

Start:

```text
Eureka Server
API Gateway
Order Service
Restaurant Service
```

### Step 2

Call an Order API that requires Restaurant Service.

```text
Client
  |
  v
Gateway
  |
  v
Order Service
  |
  v
Circuit Breaker
  |
  v
Restaurant Service
```

The request succeeds when Restaurant Service is healthy.

### Step 3

Stop Restaurant Service.

Now:

```text
Order Service
     |
     v
Circuit Breaker
     |
     X
Restaurant Service DOWN
```

The Circuit Breaker records failures.

### Step 4

After the configured failure threshold is reached:

```text
CLOSED
   |
   v
OPEN
```

Further requests are handled by the fallback.

### Step 5

Wait for the configured wait duration.

The Circuit Breaker moves to:

```text
HALF_OPEN
```

A limited test request is allowed.

### Step 6

Start Restaurant Service again.

If the test call succeeds:

```text
HALF_OPEN
      |
      v
   CLOSED
```

The system returns to normal operation.

---

# 17. Spring Boot Actuator

Actuator provides production-oriented monitoring and management endpoints.

Typical endpoints include:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

The health endpoint can be used to verify whether the application is running.

Example:

```text
GET /actuator/health
```

Possible response:

```json
{
  "status": "UP"
}
```

Actuator is useful for:

- Health checks
- Monitoring
- Metrics
- Production diagnostics
- Container orchestration integration

---

# 18. Typical Project Structure

A typical service follows a layered structure:

```text
order-service
|
+-- controller
|     +-- OrderController.java
|
+-- service
|     +-- OrderService.java
|     +-- OrderServiceImpl.java
|
+-- repository
|     +-- OrderRepository.java
|
+-- entity
|     +-- Order.java
|
+-- dto
|     +-- OrderRequest.java
|     +-- OrderResponse.java
|
+-- client
|     +-- RestaurantClient.java
|
+-- exception
|     +-- GlobalExceptionHandler.java
|
+-- config
|
+-- OrderServiceApplication.java
```

The same principle can be applied to Restaurant Service and other services.

---

# 19. Request Flow

A complete request can look like:

```text
Client
  |
  | HTTP Request
  v
API Gateway
  |
  | Route
  v
Order Service
  |
  | Controller
  v
Order Service Layer
  |
  +--------------------+
  |                    |
  v                    v
Repository         Circuit Breaker
  |                    |
  v                    v
Order DB          Feign Client
                       |
                       v
               Restaurant Service
                       |
                       v
               Restaurant DB
```

---

# 20. Error Handling

Microservices should return meaningful errors rather than exposing internal exceptions.

A centralized exception handler can be used.

Conceptually:

```text
Controller
    |
    v
Business Exception
    |
    v
Global Exception Handler
    |
    v
Standard Error Response
```

Example:

```json
{
  "status": 404,
  "message": "Restaurant not found"
}
```

---

# 21. Key Microservice Principles Demonstrated

## Loose Coupling

Services communicate through APIs rather than directly accessing another service's database.

```text
Order Service
     |
     | API
     v
Restaurant Service
```

---

## Independent Deployment

A service should be deployable without requiring all other services to be redeployed.

---

## Fault Isolation

A failure in one service should not bring down the complete system.

Circuit Breaker helps achieve this.

```text
Restaurant DOWN
      |
      v
Order Service
      |
      v
Fallback
```

---

## Scalability

Individual services can be scaled independently.

```text
Order Service
   |
   +-- Instance 1
   +-- Instance 2
   +-- Instance 3
```

while Restaurant Service may have a different number of instances.

---

# 22. Important Concepts to Remember

```text
API Gateway
     |
     v
Routing

Eureka
     |
     v
Service Discovery

Feign
     |
     v
Service Communication

JPA/Hibernate
     |
     v
Database Access

Resilience4j
     |
     v
Fault Tolerance

Actuator
     |
     v
Monitoring
```

---

# 23. Interview Explanation

A concise interview explanation can be:

> "I built a Spring Boot based food-delivery microservices application where each business capability is separated into an independent service. API Gateway acts as the entry point and Eureka handles service discovery. For service-to-service communication, I used OpenFeign. Each service follows Controller-Service-Repository layering and owns its database. To handle failures between services, I integrated Resilience4j Circuit Breaker around the Order Service to Restaurant Service communication, with fallback handling when Restaurant Service becomes unavailable. I also used Spring Boot Actuator for health and monitoring endpoints."

---

# 24. Circuit Breaker Interview Explanation

If the interviewer asks:

### Why do we need Circuit Breaker?

Answer:

> "Circuit Breaker prevents repeated calls to an unhealthy downstream service. When failures cross a configured threshold, the circuit opens and requests fail fast or go to a fallback instead of continuously calling the unavailable service. This helps prevent cascading failures and improves system resilience."

### What are the states?

```text
CLOSED
   |
   | failures exceed threshold
   v
OPEN
   |
   | wait duration
   v
HALF_OPEN
   |
   | success
   v
CLOSED
```

### What is fallback?

> "Fallback is an alternative response or operation executed when the downstream call fails or the Circuit Breaker is open."

---

# 25. Important Differences

## Gateway vs Eureka

| Component | Responsibility |
|---|---|
| API Gateway | Routes client requests |
| Eureka | Discovers service instances |

```text
Gateway = Where should the client request go?

Eureka  = Where is the service currently running?
```

---

## Feign vs Circuit Breaker

| Component | Responsibility |
|---|---|
| Feign | Calls another service |
| Circuit Breaker | Protects the application from repeated dependency failures |

```text
Feign
  |
  v
"How do I call the service?"

Circuit Breaker
  |
  v
"Should I allow this call?"
```

---

## JPA vs Hibernate

| Technology | Meaning |
|---|---|
| JPA | Specification/API |
| Hibernate | Popular JPA implementation |

```text
Application
    |
    v
JPA
    |
    v
Hibernate
    |
    v
Database
```

---

# 26. Production-Oriented Improvements

The project can be extended with:

- Centralized configuration
- Distributed tracing
- Correlation IDs
- Centralized logging
- Kafka/RabbitMQ
- SAGA pattern
- Authentication and authorization
- Rate limiting
- Retry with backoff
- Bulkhead
- Database migrations using Flyway/Liquibase
- Docker
- Kubernetes
- CI/CD
- Prometheus/Grafana monitoring
- Integration testing
- Contract testing

These are natural next steps for moving the learning project closer to a production-grade architecture.

---

# 27. Learning Order

For someone learning this project, the recommended order is:

```text
1. Spring Boot
      |
      v
2. REST API
      |
      v
3. JPA / Hibernate
      |
      v
4. Microservices
      |
      v
5. Eureka
      |
      v
6. API Gateway
      |
      v
7. OpenFeign
      |
      v
8. Resilience4j
      |
      v
9. Circuit Breaker
      |
      v
10. Actuator
      |
      v
11. Monitoring / Observability
```

---

# 28. Final Architecture

```text
                         +-------------+
                         |   Client    |
                         +------+------+
                                |
                                v
                       +----------------+
                       |  API Gateway   |
                       +-------+--------+
                               |
                               v
                       +----------------+
                       | Eureka Server  |
                       +-------+--------+
                               |
                +--------------+--------------+
                |                             |
                v                             v
        +---------------+             +---------------+
        | Order Service |             |   Restaurant  |
        |               |             |    Service    |
        +-------+-------+             +-------+-------+
                |                             |
                | Feign                       |
                | + Circuit Breaker           |
                +---------------------------->|
                                              |
                                              v
                                      +---------------+
                                      | Restaurant DB |
                                      +---------------+

        +---------------+
        |   Order DB    |
        +---------------+

                    Actuator
                       |
                       v
              Health / Metrics
```

---

# 29. Final Takeaway

This project demonstrates the most important building blocks of a Spring Boot microservices system:

```text
Microservices
     +
Service Discovery
     +
API Gateway
     +
OpenFeign
     +
JPA / Hibernate
     +
Database Per Service
     +
Circuit Breaker
     +
Fallback
     +
Actuator
     =
Resilient Microservice Architecture
```

The most important architectural lesson is:

> **A microservice system is not just multiple Spring Boot applications. It also needs service discovery, controlled communication, failure handling, observability, and clear ownership of data.**

The Order Service → Restaurant Service flow is the central example used to understand how normal service communication behaves and how Resilience4j Circuit Breaker changes that behavior when a dependency fails.
