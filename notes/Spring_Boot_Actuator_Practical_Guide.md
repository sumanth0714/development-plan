# Spring Boot Actuator — Practical Guide

## 1. What is Spring Boot Actuator?

**Spring Boot Actuator = production monitoring and management capability for a Spring Boot application.**

Think of your application like a car:

```text
                 YOUR SPRING BOOT APPLICATION
                          |
          +---------------+---------------+
          |               |               |
        Engine          Fuel            Speed
          |               |               |
       JVM/CPU           DB            Requests
          |
          v
     ┌─────────────────────────┐
     │    SPRING ACTUATOR       │
     ├─────────────────────────┤
     │ Health                   │
     │ Metrics                  │
     │ Loggers                  │
     │ Beans                    │
     │ Mappings                 │
     │ Thread Dump              │
     │ Heap Dump                │
     │ Environment              │
     └─────────────────────────┘
```

Actuator helps answer:

- Is my application UP?
- Is the database UP?
- How much memory is being used?
- How many requests are coming?
- Which API mappings exist?
- Which beans are loaded?
- Which threads are running?
- What configuration is being used?
- Can I change logging level without restarting?
- Is my application ready to receive traffic?

---

# 2. Add Actuator Dependency

For Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

After starting the application:

```text
http://localhost:8080/actuator/health
```

Example response:

```json
{
    "status": "UP"
}
```

---

# 3. Endpoint vs Exposed Endpoint

This is an important interview concept.

```text
Actuator endpoint
       |
       v
   Is it enabled?
       |
       v
   Is it exposed?
       |
       v
 Can user access it?
```

For example, Spring Boot provides the `beans` endpoint, but it is not automatically available over HTTP.

By default, only the `health` endpoint is exposed over HTTP.

---

# 4. Expose Actuator Endpoints

In `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

Now these are available:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

You can expose everything:

```properties
management.endpoints.web.exposure.include=*
```

**Do not normally expose everything in production.**

Sensitive endpoints such as `env`, `beans`, `heapdump`, etc. should be secured carefully.

---

# 5. Exclude Endpoints

Example:

```properties
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=env,beans
```

Result:

```text
Everything
   |
   +---- env       ❌
   +---- beans     ❌
   |
   +---- health    ✅
   +---- metrics   ✅
   +---- info      ✅
```

### Important interview point

> `exclude` takes precedence over `include`.

---

# 6. Important Actuator Endpoints

## 6.1 `/actuator/health`

### What does it answer?

> Is my application healthy?

```text
GET /actuator/health
```

Example:

```json
{
    "status": "UP"
}
```

### Practical use

A load balancer or Kubernetes can check:

```text
GET /actuator/health
```

If the application is unhealthy, traffic can be removed from that instance.

### Database example

```text
Spring Boot
     |
     v
   MySQL
```

Health can include:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### Interview answer

> I use the health endpoint for application and dependency health checks, especially for load balancers and Kubernetes probes.

---

# 7. Health Details

By default, you may only see:

```json
{
   "status": "UP"
}
```

You can configure:

```properties
management.endpoint.health.show-details=always
```

Then you may see:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

Be careful with detailed health information in production because it can reveal infrastructure details.

---

# 8. `/actuator/info`

### What does it answer?

> What information do I want to publish about my application?

```text
GET /actuator/info
```

Configuration:

```properties
info.app.name=Order Service
info.app.version=1.0.0
info.app.team=Payment Team
```

Example:

```json
{
  "app": {
    "name": "Order Service",
    "version": "1.0.0",
    "team": "Payment Team"
  }
}
```

### Practical use

Support team can check:

```text
Which service?
Which version?
Which build?
```

### Interview answer

> I use the info endpoint to expose non-sensitive application metadata such as application name, version, build or Git information.

---

# 9. `/actuator/metrics`

One of the most important endpoints.

### What does it answer?

> What is happening inside my application?

```text
GET /actuator/metrics
```

Example:

```json
{
  "names": [
    "jvm.memory.used",
    "jvm.threads.live",
    "http.server.requests",
    "system.cpu.usage"
  ]
}
```

Specific metric:

```text
GET /actuator/metrics/jvm.memory.used
```

or:

```text
GET /actuator/metrics/http.server.requests
```

### Practical use

Investigate:

- CPU
- JVM memory
- Threads
- HTTP requests
- Request performance
- Application metrics

### Interview answer

> I use the metrics endpoint to monitor JVM, HTTP, CPU, memory and application metrics and to investigate performance issues.

---

# 10. `/actuator/loggers`

### What does it answer?

> What is the current logging configuration?

Example:

```text
GET /actuator/loggers
```

Specific logger:

```text
GET /actuator/loggers/com.example.payment
```

### Practical scenario

Production application:

```text
INFO logging
```

You have a problem and need:

```text
DEBUG logging
```

Instead of restarting the application, Actuator can be used to inspect/change logger configuration.

```text
Production application
        |
        v
Problem found
        |
        v
Need DEBUG logs
        |
        v
Actuator /loggers
        |
        v
Change logging level
        |
        v
No application restart
```

### Interview answer

> I use the loggers endpoint to inspect and dynamically change logging levels in a running application, which is useful for production troubleshooting.

---

# 11. `/actuator/beans`

### What does it answer?

> What Spring beans are currently loaded?

```text
GET /actuator/beans
```

It can show:

```text
Controller
Service
Repository
Configuration
DataSource
Security beans
etc.
```

Think:

```text
Spring ApplicationContext

       |
       +---- UserService
       +---- UserController
       +---- UserRepository
       +---- DataSource
       +---- SecurityFilterChain
```

### Practical use

If a bean is not being created or injected as expected, this endpoint can help troubleshoot the Spring application context.

**Do not expose this publicly in production.**

### Interview answer

> I use the beans endpoint mainly for troubleshooting Spring application context and bean creation.

---

# 12. `/actuator/mappings`

### What does it answer?

> What API mappings does my application currently have?

```text
GET /actuator/mappings
```

Suppose:

```java
@GetMapping("/users")
public List<User> getUsers() {
    return users;
}
```

Mappings can show:

```text
GET /users
```

### Practical scenario

Someone says:

> Why is my API returning 404?

Check:

```text
/actuator/mappings
```

and verify whether the mapping actually exists.

### Interview answer

> I use the mappings endpoint to inspect registered controller and request mappings when troubleshooting API routing or 404 problems.

---

# 13. `/actuator/env`

### What does it answer?

> What environment/configuration values is my application using?

```text
GET /actuator/env
```

Useful for investigating:

```text
server.port
spring.datasource.url
spring.profiles.active
custom properties
environment variables
```

### Practical scenario

Database connection fails.

You suspect:

```text
spring.datasource.url
```

is incorrect.

The environment endpoint can help investigate configuration.

### Important

This endpoint can contain sensitive information.

Spring Boot sanitizes sensitive values by default.

**Do not casually expose `/actuator/env` publicly.**

### Interview answer

> I use env mainly for configuration troubleshooting, but I secure it carefully because it can contain sensitive configuration information.

---

# 14. `/actuator/configprops`

Similar to `env`, but there is an important difference.

```text
/env
   |
   +---- Environment/property sources

/configprops
   |
   +---- @ConfigurationProperties beans
```

Example:

```java
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private String url;
    private int timeout;
}
```

Then:

```text
GET /actuator/configprops
```

can help troubleshoot configuration binding.

### Interview answer

> `configprops` is useful for checking values bound to `@ConfigurationProperties` classes.

---

# 15. `/actuator/threaddump`

### What does it answer?

> What are my application's threads doing right now?

```text
GET /actuator/threaddump
```

Think:

```text
Application
   |
   +---- Thread-1 RUNNABLE
   +---- Thread-2 WAITING
   +---- Thread-3 BLOCKED
   +---- Thread-4 TIMED_WAITING
```

### Practical use

Useful for:

- Deadlocks
- Thread contention
- Blocked threads
- Thread pool problems
- Application hangs

### Interview answer

> I use threaddump when diagnosing concurrency problems such as blocked threads, thread contention or potential deadlocks.

---

# 16. `/actuator/heapdump`

### What does it answer?

> What objects are currently in the JVM heap?

```text
GET /actuator/heapdump
```

Useful when investigating:

```text
Memory leak
OutOfMemoryError
Unexpected memory growth
```

Think:

```text
Java Application
       |
       v
     Heap
       |
       +---- User objects
       +---- String objects
       +---- Collections
       +---- Cached objects
```

Heap dumps can be analyzed with tools such as:

```text
Eclipse MAT
VisualVM
JProfiler
```

### Interview answer

> I use heapdump when investigating memory leaks or OutOfMemoryError issues.

**Do not expose this endpoint publicly.**

---

# 17. `/actuator/caches`

Useful when the application uses Spring caching.

Example:

```java
@Cacheable("products")
public Product getProduct(Long id) {
    ...
}
```

Think:

```text
Request
   |
   v
 Cache
   |
   +---- HIT
   |
   +---- MISS
```

Useful for troubleshooting cache configuration and behavior.

---

# 18. `/actuator/scheduledtasks`

Suppose:

```java
@Scheduled(fixedRate = 5000)
public void processOrders() {
}
```

Use:

```text
GET /actuator/scheduledtasks
```

to inspect scheduled tasks.

### Interview answer

> I use scheduledtasks to inspect configured scheduled jobs when troubleshooting scheduler-related issues.

---

# 19. `/actuator/quartz`

If the application uses Quartz Scheduler:

```text
GET /actuator/quartz
```

Useful for information about:

```text
Jobs
Triggers
Schedules
```

Think:

```text
Quartz
  |
  +---- Jobs
  +---- Triggers
  +---- Schedules
```

---

# 20. `/actuator/auditevents`

Used for application audit events.

Examples:

```text
User logged in
User logged out
Authentication failed
```

Useful for security/audit-related troubleshooting.

---

# 21. `/actuator/sessions`

If the application uses Spring Session, this endpoint can provide information about sessions.

Think:

```text
User
  |
  v
Session
  |
  +---- sessionId
  +---- attributes
  +---- expiration
```

---

# 22. `/actuator/startup`

### What does it answer?

> Why is my application taking a long time to start?

Useful for startup performance investigation.

It shows startup steps collected by Spring's `ApplicationStartup`.

---

# 23. `/actuator/shutdown`

This endpoint can shut down the application.

```text
POST /actuator/shutdown
```

Important:

- Disabled by default
- Should be enabled only when really needed
- Must be strongly secured

Think:

```text
POST /actuator/shutdown
        |
        v
Spring Boot
        |
        v
Graceful shutdown
```

---

# 24. `/actuator/prometheus`

Very important in real production environments.

Architecture:

```text
Spring Boot
     |
     v
Actuator
     |
     v
Prometheus
     |
     v
Grafana
```

Prometheus collects metrics from:

```text
/actuator/prometheus
```

Add:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Expose it:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

---

# 25. Actuator + Micrometer + Prometheus + Grafana

This is an important interview concept.

```text
Your Application
       |
       v
   Micrometer
       |
       v
   Actuator
       |
       +------------+
       |            |
       v            v
 Prometheus       Metrics
       |
       v
    Grafana
```

Remember:

- **Micrometer** → metrics instrumentation/facade
- **Actuator** → exposes monitoring/management endpoints
- **Prometheus** → collects/stores metrics
- **Grafana** → visualizes metrics

---

# 26. Change Actuator Base Path

Default:

```text
/actuator/health
```

Change:

```properties
management.endpoints.web.base-path=/manage
```

Now:

```text
/manage/health
/manage/info
/manage/metrics
```

instead of:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

---

# 27. Run Actuator on a Different Port

Application:

```text
8080
```

Actuator:

```text
8081
```

Configuration:

```properties
server.port=8080
management.server.port=8081
```

Now:

```text
Business APIs:
http://localhost:8080/users

Actuator:
http://localhost:8081/actuator/health
```

This can be useful for separating management traffic from normal application traffic.

---

# 28. Actuator + Kubernetes

One of the most important real-world use cases.

Kubernetes needs to know:

```text
Is application alive?
```

and:

```text
Can application receive traffic?
```

Actuator supports health probes such as:

```text
/actuator/health/liveness
/actuator/health/readiness
```

---

# 29. Liveness vs Readiness

### Liveness

```text
Is my application alive?
```

If it fails:

```text
Restart container
```

### Readiness

```text
Is my application ready to receive requests?
```

If it fails:

```text
Remove application from traffic
```

Visualize:

```text
             Application
                  |
          +-------+-------+
          |               |
          v               v
      Liveness         Readiness
          |               |
          v               v
      "Alive?"        "Ready?"
          |               |
       NO ↓             NO ↓
       Restart       Stop traffic
```

---

# 30. Liveness vs Readiness Interview Answer

> **Liveness** tells Kubernetes whether the application is alive and should continue running.

> **Readiness** tells Kubernetes whether the application is ready to receive traffic.

Simple memory trick:

```text
Liveness  = Should I RESTART it?
Readiness = Should I SEND TRAFFIC to it?
```

---

# 31. Disable an Endpoint

You can control endpoint access.

Example:

```properties
management.endpoint.shutdown.access=none
```

You can also exclude it from HTTP exposure:

```properties
management.endpoints.web.exposure.exclude=shutdown
```

Think:

```text
Access
   |
   v
Can endpoint be available?

Exposure
   |
   v
Can it be reached through HTTP/JMX?
```

---

# 32. Spring Security + Actuator

Do not assume that exposing an endpoint means everyone should access it.

Example:

```text
Public
  |
  +---- /actuator/health

Admin/Internal only
  |
  +---- /actuator/env
  +---- /actuator/beans
  +---- /actuator/loggers
  +---- /actuator/heapdump
```

Sensitive endpoints should be protected using Spring Security, network restrictions, or a separate management port.

---

# 33. Recommended Configuration for a Normal Microservice

Start with:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus

management.endpoint.health.show-details=when_authorized

info.app.name=Order Service
info.app.version=1.0.0
```

Useful endpoints:

```text
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
GET /actuator/prometheus
```

Avoid:

```properties
management.endpoints.web.exposure.include=*
```

unless you understand and secure all exposed endpoints.

---

# 34. Complete Endpoint Cheat Sheet

| Endpoint | Simple meaning | Practical use |
|---|---|---|
| `/health` | Is app healthy? | Load balancer/Kubernetes |
| `/info` | App information | Version/build information |
| `/metrics` | Application metrics | Performance monitoring |
| `/prometheus` | Metrics for Prometheus | Monitoring/Grafana |
| `/loggers` | Logger configuration | Change DEBUG/INFO dynamically |
| `/beans` | Spring beans | Bean troubleshooting |
| `/mappings` | API mappings | Find/debug endpoints |
| `/env` | Environment/config | Configuration troubleshooting |
| `/configprops` | Bound properties | `@ConfigurationProperties` troubleshooting |
| `/threaddump` | Thread information | Deadlock/thread issues |
| `/heapdump` | JVM heap | Memory leak investigation |
| `/scheduledtasks` | Scheduled jobs | Scheduler troubleshooting |
| `/quartz` | Quartz jobs | Quartz troubleshooting |
| `/caches` | Cache information | Cache troubleshooting |
| `/auditevents` | Audit events | Security/audit |
| `/sessions` | Sessions | Spring Session |
| `/startup` | Startup steps | Slow startup troubleshooting |
| `/shutdown` | Shutdown application | Controlled shutdown |

---

# 35. The Six Categories to Remember

Instead of memorizing every endpoint, remember these categories:

```text
             SPRING ACTUATOR
                    |
     +--------------+--------------+
     |              |              |
     v              v              v
   HEALTH         METRICS        CONFIG
     |              |              |
  health        metrics         env
  readiness     prometheus      configprops
  liveness

     +--------------+--------------+
     |              |
     v              v
  DEBUGGING       JVM
     |              |
  mappings       threaddump
  loggers        heapdump
  beans
  scheduledtasks
```

---

# 36. Production Architecture

A common production setup:

```text
                    INTERNET
                       |
                       v
                 Load Balancer
                       |
                       v
              +----------------+
              | Order Service  |
              |     :8080      |
              +----------------+
                       |
                Internal network
                       |
                       v
              +----------------+
              |   Actuator     |
              |     :8081      |
              +----------------+
                       |
          +------------+-------------+
          |            |             |
          v            v             v
       Health       Metrics       Prometheus
                                      |
                                      v
                                   Grafana
```

---

# 37. Practical Hands-On Exercise

Create a small Spring Boot application.

## Step 1 — Add dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Step 2 — Start application

Open:

```text
http://localhost:8080/actuator/health
```

## Step 3 — Expose endpoints

```properties
management.endpoints.web.exposure.include=health,info,metrics,loggers,mappings
```

## Step 4 — Test

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/loggers
/actuator/mappings
```

## Step 5 — Create a Controller

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String getUsers() {
        return "Users";
    }
}
```

Then check:

```text
/actuator/mappings
```

Find:

```text
GET /users
```

## Step 6 — Check JVM metric

```text
/actuator/metrics/jvm.memory.used
```

## Step 7 — Check HTTP metrics

```text
/actuator/metrics/http.server.requests
```

## Step 8 — Check loggers

```text
/actuator/loggers
```

## Step 9 — Add a database

Then check:

```text
/actuator/health
```

You can now see the real value of Actuator:

```text
Application
     |
     +---- MySQL DOWN
     |
     v
/actuator/health
     |
     v
status = DOWN
```

---

# 38. Interview Quick Answers

### What is Spring Boot Actuator?

> Spring Boot Actuator provides production-ready monitoring and management endpoints for a Spring Boot application.

### How do you add Actuator?

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### How do you expose endpoints?

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

### How do you exclude endpoints?

```properties
management.endpoints.web.exposure.exclude=env,beans
```

### Which takes priority?

> Exclude takes priority over include.

### Health endpoint?

```text
/actuator/health
```

### Metrics endpoint?

```text
/actuator/metrics
```

### Thread problems?

```text
/actuator/threaddump
```

### Memory leak?

```text
/actuator/heapdump
```

### API mapping?

```text
/actuator/mappings
```

### Dynamic logging?

```text
/actuator/loggers
```

### Configuration troubleshooting?

```text
/actuator/env
/actuator/configprops
```

### Prometheus integration?

```text
/actuator/prometheus
```

### Change base path?

```properties
management.endpoints.web.base-path=/manage
```

### Separate Actuator port?

```properties
management.server.port=8081
```

### Liveness?

> Is the application alive? If not, Kubernetes may restart it.

### Readiness?

> Is the application ready to receive traffic?

### Should we expose all endpoints?

> No. Expose only the endpoints required and secure sensitive endpoints.

---

# 39. Final Mental Model

Remember this:

```text
                     SPRING BOOT
                         |
                         v
                     ACTUATOR
                         |
        +----------------+----------------+
        |                |                |
        v                v                v
     HEALTH           METRICS          CONFIG
        |                |                |
        |                |                |
        v                v                v
    Kubernetes       Prometheus         env
    Load Balancer       |              configprops
                        v
                     Grafana

        +----------------+----------------+
        |                |
        v                v
    DEBUGGING          JVM
        |                |
        v                v
   mappings          threaddump
   loggers           heapdump
   beans
```

### One-line memory trick

```text
Health  → Is it alive?
Metrics → How is it performing?
Loggers → What logs do I need?
Mappings → What APIs exist?
Beans → What Spring objects exist?
Env → What configuration is being used?
ThreadDump → What are threads doing?
HeapDump → What is consuming memory?
Prometheus → How do I send metrics to monitoring?
```

That is the practical way to remember Spring Actuator for both **real projects and interviews**.
