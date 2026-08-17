# 🚀 JUnit 5 + Mockito Interview Preparation Guide

A practical, interview-focused guide to **JUnit 5, Mockito, annotations, mocking, stubbing, verification, exception testing, parameterized tests, asynchronous testing, and advanced testing features**.

---

# 1. What is JUnit?

### Interview answer

> **JUnit is a Java testing framework used to write and execute automated unit tests. JUnit 5 is the modern version and provides annotations, assertions, parameterized testing, lifecycle management, and an extension model.**

Think:

```text
Production Code
      |
      v
   JUnit 5
      |
      +---- Test setup
      +---- Execute method
      +---- Assert result
      +---- Verify interactions
```

JUnit answers:

**"Did my code produce the expected result?"**

Mockito answers:

**"Did my class interact correctly with its dependencies?"**

---

# 2. JUnit 5 Architecture

JUnit 5 is made of three major parts:

```text
JUnit 5
   |
   +-- JUnit Platform
   |      |
   |      +-- Runs tests
   |
   +-- JUnit Jupiter
   |      |
   |      +-- JUnit 5 programming model
   |      +-- @Test
   |      +-- @BeforeEach
   |      +-- @ParameterizedTest
   |
   +-- JUnit Vintage
          |
          +-- Supports older JUnit 3/4 tests
```

### Interview point

> **JUnit Platform is the foundation used to launch tests. JUnit Jupiter provides the JUnit 5 programming model and APIs. Vintage provides backward compatibility for JUnit 3 and JUnit 4 tests.**

---

# 3. Important JUnit 5 Annotations

## @Test

Marks a method as a test method.

```java
@Test
void shouldReturnUser() {
    User user = service.getUser(1L);

    assertEquals("John", user.getName());
}
```

---

## @BeforeEach

Runs **before every test method**.

```java
@BeforeEach
void setUp() {
    service = new UserService();
}
```

Flow:

```text
Test 1 -> BeforeEach -> Test
Test 2 -> BeforeEach -> Test
Test 3 -> BeforeEach -> Test
```

Use it for common test setup.

---

## @AfterEach

Runs after every test.

```java
@AfterEach
void cleanup() {
    // cleanup
}
```

---

## @BeforeAll

Runs **once before all tests**.

```java
@BeforeAll
static void setupAll() {
    // one-time setup
}
```

By default, it must be static.

---

## @AfterAll

Runs once after all tests.

```java
@AfterAll
static void cleanupAll() {
}
```

---

# 4. Test Lifecycle

```text
@BeforeAll
     |
     v
@BeforeEach
     |
     v
@Test
     |
     v
@AfterEach
     |
     v
@BeforeEach
     |
     v
@Test
     |
     v
@AfterEach
     |
     v
@AfterAll
```

### Interview question

**What is the difference between @BeforeEach and @BeforeAll?**

### Answer

> `@BeforeEach` executes before every test method, while `@BeforeAll` executes only once before all test methods.

---

# 5. @DisplayName

Provides a readable test name.

```java
@Test
@DisplayName("Should return user when valid ID is provided")
void getUserTest() {
}
```

Useful for readable test reports.

---

# 6. @Disabled

Temporarily disables a test.

```java
@Test
@Disabled("Feature is under development")
void testSomething() {
}
```

---

# 7. Assertions

Assertions verify expected behavior.

## assertEquals

```java
assertEquals(10, result);
```

Expected:

```text
10 == result
```

---

## assertNotEquals

```java
assertNotEquals(20, result);
```

---

## assertTrue

```java
assertTrue(user.isActive());
```

---

## assertFalse

```java
assertFalse(user.isDeleted());
```

---

## assertNull

```java
assertNull(result);
```

---

## assertNotNull

```java
assertNotNull(result);
```

---

# 8. assertThrows

Used to test exceptions.

```java
@Test
void shouldThrowException() {

    assertThrows(
        UserNotFoundException.class,
        () -> service.getUser(999L)
    );
}
```

Interview answer:

> **I use `assertThrows()` when the expected behavior of a method is to throw a particular exception.**

You can also inspect the exception:

```java
UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> service.getUser(999L)
        );

assertEquals("User not found", exception.getMessage());
```

---

# 9. assertAll

Runs multiple assertions together.

```java
assertAll(
    () -> assertEquals("John", user.getName()),
    () -> assertEquals("john@gmail.com", user.getEmail()),
    () -> assertTrue(user.isActive())
);
```

Instead of stopping at the first assertion failure, JUnit reports failures from the grouped assertions.

---

# 10. assertTimeout

Checks that an operation completes within a specified duration.

```java
assertTimeout(
    Duration.ofSeconds(2),
    () -> service.process()
);
```

Useful when testing execution-time expectations.

---

# 11. JUnit + Mockito

JUnit and Mockito have different responsibilities.

```text
             JUnit
               |
       Test execution
       Assertions
       Lifecycle
               |
               v
            Mockito
               |
       Mock dependencies
       Stub behavior
       Verify interactions
```

Example:

```java
@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService;
```

---

# 12. What is Mockito?

### Interview answer

> **Mockito is a mocking framework used to create test doubles for dependencies so that I can test a class in isolation without calling real databases, external APIs, or other expensive dependencies.**

Example:

```text
UserService
    |
    +---- UserRepository
    |
    +---- EmailService
```

While testing `UserService`:

```text
UserService
    |
    +---- Mock UserRepository
    |
    +---- Mock EmailService
```

---

# 13. @Mock

Creates a Mockito mock.

```java
@Mock
private UserRepository userRepository;
```

Mockito controls this object.

By default, methods generally return Mockito's default values unless behavior is stubbed.

---

# 14. @InjectMocks

Creates the class under test and injects Mockito mocks/spies into it.

```java
@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService;
```

Conceptually:

```text
Mock Repository
       |
       v
UserService
```

### Interview answer

> **`@Mock` creates a mock dependency, while `@InjectMocks` creates the class being tested and injects the available mocks into it.**

---

# 15. @Spy

A spy wraps a real object.

```java
@Spy
private UserService userService;
```

Important difference:

```text
@Mock
   |
   +-- Fake behavior
   +-- Real method is NOT called by default

@Spy
   |
   +-- Real object
   +-- Real method is called by default
   +-- Specific methods can be stubbed
```

Use a spy carefully because it can make tests more complicated.

---

# 16. @Captor

Creates an `ArgumentCaptor`.

```java
@Captor
ArgumentCaptor<User> userCaptor;
```

Used when you want to inspect an object passed to a mocked method.

Example:

```java
verify(userRepository).save(userCaptor.capture());

User savedUser = userCaptor.getValue();

assertEquals("John", savedUser.getName());
```

---

# 17. MockitoExtension

JUnit 5 does not automatically initialize Mockito annotations.

Use:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
}
```

Example:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;
}
```

### Interview answer

> **`MockitoExtension` integrates Mockito with JUnit 5 and initializes annotations such as `@Mock`, `@Spy`, `@Captor`, and `@InjectMocks`.**

---

# 18. Mock vs Stub vs Verify

This is one of the most important interview topics.

Think:

```text
STUB
 |
 | "When this happens, return this"
 v
Mock dependency

VERIFY
 |
 | "Did this interaction happen?"
 v
Mock dependency
```

---

# 19. Stubbing with when().thenReturn()

Suppose:

```java
@Mock
UserRepository repository;
```

Stub:

```java
when(repository.findById(1L))
    .thenReturn(Optional.of(user));
```

Meaning:

```text
If repository.findById(1L)
        |
        v
Return user
```

---

# 20. thenReturn()

```java
when(repository.findById(1L))
    .thenReturn(Optional.of(user));
```

---

# 21. thenThrow()

Used when the dependency should throw an exception.

```java
when(repository.findById(999L))
    .thenThrow(new RuntimeException("Database error"));
```

---

# 22. thenAnswer()

Used when the return value needs dynamic logic.

```java
when(repository.save(any(User.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));
```

Meaning:

```text
save(user)
   |
   v
Return the same user
```

Useful when behavior depends on the input.

---

# 23. doReturn()

Useful especially with spies.

```java
doReturn(user)
    .when(userService)
    .getUser(1L);
```

This can avoid calling the real method during stubbing.

---

# 24. doThrow()

```java
doThrow(new RuntimeException())
    .when(emailService)
    .sendEmail(anyString());
```

---

# 25. Mockito Argument Matchers

Common matchers:

```java
any()
anyString()
anyLong()
anyInt()
eq()
isNull()
isNotNull()
```

Example:

```java
when(repository.findById(anyLong()))
    .thenReturn(Optional.of(user));
```

---

# 26. Important Matcher Rule

Do not mix raw values and matchers incorrectly.

Bad:

```java
when(service.process(anyString(), "ACTIVE"))
```

Better:

```java
when(service.process(anyString(), eq("ACTIVE")))
```

### Interview answer

> **When using Mockito argument matchers in a method call, I generally use matchers for all arguments rather than mixing matchers with raw values.**

---

# 27. verify()

`verify()` checks whether a mock interaction happened.

```java
verify(repository).save(user);
```

Meaning:

```text
Did repository.save(user) happen?
        |
        +---- YES -> Test passes
        |
        +---- NO  -> Test fails
```

---

# 28. verify() with Times

```java
verify(repository, times(1))
    .save(user);
```

Other options:

```java
verify(repository, times(2)).save(user);

verify(repository, never()).deleteById(anyLong());

verify(repository, atLeastOnce()).save(any());

verify(repository, atMost(2)).save(any());
```

---

# 29. verifyNoInteractions()

Checks that a mock was never interacted with.

```java
verifyNoInteractions(emailService);
```

Useful when a business condition should prevent a dependency call.

---

# 30. verifyNoMoreInteractions()

Checks that no unexpected interactions happened after expected interactions.

```java
verify(repository).findById(1L);

verifyNoMoreInteractions(repository);
```

Use it carefully. Overusing it can make tests unnecessarily brittle.

---

# 31. reset() vs clearInvocations()

### reset()

Removes:

```text
Stubbed behavior
+
Recorded interactions
```

Example:

```java
reset(repository);
```

Usually avoid it because it can make tests harder to understand.

### clearInvocations()

Removes recorded interaction history but keeps stubbing.

```java
clearInvocations(repository);
```

---

# 32. Complete Mockito Unit Test Example

Production class:

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found"));
    }
}
```

Test:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    @Test
    void shouldReturnUser() {

        User user = new User(1L, "John");

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = service.getUser(1L);

        assertEquals("John", result.getName());

        verify(repository).findById(1L);
    }
}
```

Interview explanation:

```text
1. @Mock
   ↓
Create fake repository

2. @InjectMocks
   ↓
Inject repository into UserService

3. when()
   ↓
Define expected dependency behavior

4. Call service
   ↓
Execute real business logic

5. assertEquals()
   ↓
Check returned result

6. verify()
   ↓
Check repository interaction
```

---

# 33. Testing Async Methods

This is an important advanced interview topic.

Suppose:

```java
@Async
public CompletableFuture<String> process() {

    return CompletableFuture.completedFuture("SUCCESS");
}
```

The method executes asynchronously.

You should **not** blindly use:

```java
Thread.sleep(5000);
```

Why?

```text
Thread.sleep()
    |
    +-- Slow tests
    +-- Flaky tests
    +-- Wasted time
```

Prefer waiting on the actual asynchronous result.

---

# 34. Testing CompletableFuture

Example:

```java
@Test
void shouldProcessAsync() throws Exception {

    CompletableFuture<String> future =
            service.process();

    String result = future.get(2, TimeUnit.SECONDS);

    assertEquals("SUCCESS", result);
}
```

Better:

```text
Call async method
       |
       v
CompletableFuture
       |
       v
Wait with timeout
       |
       v
Assert result
```

Using a timeout prevents the test from hanging forever.

---

# 35. Testing @Async in Spring

Suppose:

```java
@Async
public CompletableFuture<String> process() {
    return CompletableFuture.completedFuture("SUCCESS");
}
```

A Spring integration-style test can use:

```java
@SpringBootTest
class AsyncServiceTest {

    @Autowired
    AsyncService service;

    @Test
    void shouldProcessAsync() throws Exception {

        CompletableFuture<String> future =
                service.process();

        assertEquals(
            "SUCCESS",
            future.get(2, TimeUnit.SECONDS)
        );
    }
}
```

Important:

`@Async` works through a Spring proxy.

Therefore:

```java
this.process();
```

does not go through the Spring proxy and will not provide the normal `@Async` behavior.

---

# 36. Async Testing Best Practices

Avoid:

```java
Thread.sleep(5000);
```

Prefer:

```java
future.get(2, TimeUnit.SECONDS);
```

or:

```java
future.orTimeout(2, TimeUnit.SECONDS);
```

The exact approach depends on the async API being tested.

### Interview answer

> **For asynchronous code, I avoid fixed sleeps because they make tests slow and flaky. I wait on the actual completion mechanism, such as `CompletableFuture.get()` with a timeout, and then assert the result.**

---

# 37. Testing Exceptions in Async Code

For a `CompletableFuture`, the exception may be wrapped.

Example:

```java
CompletableFuture<String> future =
        service.process();

ExecutionException exception =
        assertThrows(
            ExecutionException.class,
            () -> future.get(2, TimeUnit.SECONDS)
        );

assertInstanceOf(
    UserNotFoundException.class,
    exception.getCause()
);
```

Think:

```text
Async method
     |
     v
CompletableFuture
     |
     v
future.get()
     |
     v
ExecutionException
     |
     v
getCause()
     |
     v
Actual exception
```

---

# 38. Parameterized Tests

JUnit 5 supports parameterized tests.

Instead of:

```text
test1
test2
test3
test4
```

You can write:

```java
@ParameterizedTest
@ValueSource(strings = {
    "JAVA",
    "SPRING",
    "JPA"
})
void shouldNotBeBlank(String value) {

    assertFalse(value.isBlank());
}
```

The same test runs for every input.

---

# 39. @CsvSource

Useful for multiple input values.

```java
@ParameterizedTest
@CsvSource({
    "2,3,5",
    "10,20,30",
    "5,5,10"
})
void shouldAddNumbers(
        int a,
        int b,
        int expected) {

    assertEquals(expected, a + b);
}
```

---

# 40. @MethodSource

Useful for complex test data.

```java
@ParameterizedTest
@MethodSource("provideUsers")
void shouldValidateUsers(User user) {

    assertNotNull(user);
}

static Stream<User> provideUsers() {
    return Stream.of(
        new User(1L, "John"),
        new User(2L, "Alex")
    );
}
```

Use `@MethodSource` when test data is more complex than simple strings/numbers.

---

# 41. @NullSource, @EmptySource, @NullAndEmptySource

Useful for edge-case testing.

```java
@ParameterizedTest
@NullAndEmptySource
void shouldRejectInvalidName(String name) {

    assertTrue(name == null || name.isEmpty());
}
```

---

# 42. Dynamic Tests

JUnit 5 allows tests to be generated dynamically.

```java
@TestFactory
Stream<DynamicTest> dynamicTests() {

    return Stream.of("A", "B", "C")
        .map(value ->
            DynamicTest.dynamicTest(
                "Testing " + value,
                () -> assertNotNull(value)
            )
        );
}
```

Difference:

```text
@Test
    |
    +-- Test is known when code is compiled

@TestFactory
    |
    +-- Tests are generated dynamically at runtime
```

---

# 43. Nested Tests

JUnit 5 supports `@Nested`.

```java
@Nested
class WhenUserExists {

    @Test
    void shouldReturnUser() {
    }
}

@Nested
class WhenUserDoesNotExist {

    @Test
    void shouldThrowException() {
    }
}
```

This helps organize tests by behavior.

---

# 44. Repeated Tests

JUnit 5 supports:

```java
@RepeatedTest(5)
void shouldRunMultipleTimes() {
}
```

The test executes five times.

Useful for testing behavior where repeated execution matters, although repeated tests should not be used as a substitute for proper concurrency testing.

---

# 45. TestInstance Lifecycle

By default:

```java
@Test
```

JUnit generally creates a new test class instance for each test method.

You can change this:

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
}
```

Then one test instance is used for the entire class.

This can allow non-static `@BeforeAll` and `@AfterAll`.

### Interview point

> **JUnit's default lifecycle is PER_METHOD. PER_CLASS creates one test instance for all test methods.**

---

# 46. @Tag

Categorize tests.

```java
@Tag("integration")
@Test
void integrationTest() {
}
```

Examples:

```text
unit
integration
slow
security
```

Tags can be used by build tools and test configuration to select test groups.

---

# 47. JUnit 5 Extension Model

JUnit 5 provides an extension model.

Example:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
}
```

Extensions can add behavior such as:

```text
Before test
After test
Before each
After each
Parameter resolution
Exception handling
```

### Interview answer

> **JUnit 5 uses an extension model to add reusable test behavior. Mockito integrates with JUnit 5 through `MockitoExtension`.**

---

# 48. Mockito Strict Stubbing

Mockito can detect unnecessary or mismatched stubbing depending on configuration and test setup.

Example:

```java
when(repository.findById(1L))
    .thenReturn(Optional.of(user));
```

But if the test never calls:

```java
repository.findById(1L)
```

the stub may be considered unnecessary under strict-stubbing behavior.

### Why is this useful?

It helps identify:

```text
Unused stubs
Incorrect stubs
Test setup mistakes
```

---

# 49. Mocking Static Methods

Mockito supports static mocking.

Example:

```java
try (MockedStatic<Utility> mocked =
        Mockito.mockStatic(Utility.class)) {

    mocked.when(Utility::getValue)
          .thenReturn("MOCKED");

    assertEquals("MOCKED", Utility.getValue());
}
```

Important:

> Static mocking should be used carefully. Excessive static mocking can indicate tightly coupled code and make tests harder to maintain.

---

# 50. Mocking Final Classes and Methods

Modern Mockito can mock many final classes and methods depending on the Mockito configuration/version.

Interview point:

> **Modern Mockito has support for mocking final types/methods, so the old statement that Mockito can never mock final methods is no longer generally correct.**

---

# 51. Timeout Verification

Mockito can verify an interaction that should happen within a period.

```java
verify(notificationService, timeout(1000))
    .send(anyString());
```

Meaning:

```text
Wait up to 1 second
        |
        v
Did send() happen?
```

Useful for asynchronous interaction testing, but prefer deterministic synchronization mechanisms when possible.

---

# 52. ArgumentCaptor vs ArgumentMatcher

### ArgumentMatcher

Used to say:

> "I don't care about the exact argument."

```java
verify(repository).save(any(User.class));
```

### ArgumentCaptor

Used to say:

> "Capture the exact argument and inspect it."

```java
ArgumentCaptor<User> captor =
        ArgumentCaptor.forClass(User.class);

verify(repository).save(captor.capture());

User saved = captor.getValue();

assertEquals("John", saved.getName());
```

Think:

```text
Matcher
   |
   +-- Check interaction broadly

Captor
   |
   +-- Capture + inspect actual value
```

---

# 53. Unit Test vs Integration Test

## Unit Test

```text
UserService
   |
   +-- Mock Repository
   +-- Mock Email Service
```

Focus:

> One class/business unit.

Usually:

- Fast
- Isolated
- No real database

---

## Integration Test

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
Database
```

Focus:

> Whether multiple components work together.

Usually:

- Slower
- More realistic
- May use a real or containerized database

---

# 54. @SpringBootTest vs Mockito Unit Test

### Mockito unit test

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
}
```

Loads:

```text
Mockito
+
Test class
+
Mocks
```

Does not load the complete Spring application context.

### @SpringBootTest

```java
@SpringBootTest
class UserServiceIntegrationTest {
}
```

Loads the Spring application context.

### Interview answer

> **I use Mockito/JUnit unit tests when I want fast isolated business-logic tests. I use `@SpringBootTest` when I need to verify Spring configuration and integration between components.**

---

# 55. Common Spring Testing Annotations

## @WebMvcTest

Used mainly for controller-layer testing.

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
}
```

Usually combine with mocked service dependencies.

---

## @DataJpaTest

Used for JPA repository testing.

```java
@DataJpaTest
class UserRepositoryTest {
}
```

Focus:

```text
Repository
   |
   v
JPA
   |
   v
Test database
```

---

# 56. Common Interview Question: Mock vs @MockBean

### @Mock

Mockito annotation:

```java
@Mock
UserService service;
```

Used in a Mockito-based unit test.

### @MockBean

Spring Boot test feature traditionally used to place a Mockito mock into the Spring application context.

Important modern note:

> In newer Spring Boot versions, `@MockBean` is being replaced/deprecated in favor of Spring Framework's `@MockitoBean` in supported setups.

For current projects, check the Spring Boot/Spring Framework version before choosing the annotation.

---

# 57. Best Unit Test Structure — AAA

A very common pattern:

```text
Arrange
   ↓
Act
   ↓
Assert
```

Example:

```java
@Test
void shouldReturnUser() {

    // Arrange
    User user = new User(1L, "John");

    when(repository.findById(1L))
        .thenReturn(Optional.of(user));

    // Act
    User result = service.getUser(1L);

    // Assert
    assertEquals("John", result.getName());
}
```

Interview answer:

> **I structure unit tests using AAA: Arrange the test data and mocks, Act by calling the method under test, and Assert the result and required interactions.**

---

# 58. What Should You Verify?

Do not verify everything.

Good:

```java
verify(repository).save(user);
```

Useful when the interaction is part of the behavior.

Avoid excessive:

```java
verify(repository).method1();
verify(repository).method2();
verify(repository).method3();
verify(repository).method4();
```

if those interactions are implementation details rather than business behavior.

### Interview point

> **I verify important observable interactions, not every internal implementation detail. This keeps tests less brittle.**

---

# 59. Common Mistakes in Mockito

### Mistake 1 — Forgetting MockitoExtension

```java
@Mock
UserRepository repository;
```

without:

```java
@ExtendWith(MockitoExtension.class)
```

can leave annotations uninitialized in a plain JUnit 5 test.

---

### Mistake 2 — Testing the mock instead of the real class

Wrong:

```java
@Mock
UserService service;
```

and then trying to test `service`.

Usually the service itself should be the class under test:

```java
@InjectMocks
UserService service;
```

---

### Mistake 3 — Using Thread.sleep()

Bad:

```java
Thread.sleep(5000);
```

Prefer deterministic completion/wait mechanisms.

---

### Mistake 4 — Overusing @Spy

Spies can make tests harder to understand.

Prefer clean dependency injection and mocks when possible.

---

### Mistake 5 — Too many verifications

Tests should focus on behavior rather than implementation details.

---

# 60. Advanced Interview Scenario

### Question

> Your service calls a repository asynchronously and returns a CompletableFuture. How would you test it?

### Answer

> First, I would mock the repository or external dependency. I would call the asynchronous service method and capture the returned `CompletableFuture`. Instead of using `Thread.sleep()`, I would wait for completion with a bounded timeout such as `future.get(timeout, unit)` or use another deterministic completion mechanism. Then I would assert the returned value and verify important interactions with Mockito.

Flow:

```text
Mock dependency
      |
      v
Call async service
      |
      v
CompletableFuture
      |
      v
Wait with timeout
      |
      v
Assert result
      |
      v
Verify dependency interaction
```

---

# 61. Advanced Interview Scenario — Exception

### Question

> How do you test an exception in JUnit 5?

### Answer

```java
UserNotFoundException exception =
    assertThrows(
        UserNotFoundException.class,
        () -> service.getUser(100L)
    );

assertEquals(
    "User not found",
    exception.getMessage()
);
```

Interview explanation:

> **I use `assertThrows()` and then inspect the returned exception if I need to validate the message or cause.**

---

# 62. Advanced Interview Scenario — Mockito

### Question

> What is the difference between when(), verify(), and assertEquals()?

### Answer

```text
when()
  ↓
Defines mock behavior
  "When this happens, return this"

verify()
  ↓
Checks interaction
  "Did this method get called?"

assertEquals()
  ↓
Checks result
  "Is the returned value correct?"
```

Example:

```java
when(repository.findById(1L))
    .thenReturn(Optional.of(user));

User result = service.getUser(1L);

assertEquals("John", result.getName());

verify(repository).findById(1L);
```

---

# 63. Advanced JUnit 5 Features to Remember

For interviews, remember these:

```text
JUnit 5
 |
 +-- @Test
 +-- @BeforeEach / @AfterEach
 +-- @BeforeAll / @AfterAll
 +-- @ParameterizedTest
 |      |
 |      +-- @ValueSource
 |      +-- @CsvSource
 |      +-- @MethodSource
 |      +-- @NullSource
 |      +-- @EmptySource
 |
 +-- @Nested
 +-- @RepeatedTest
 +-- @TestFactory
 +-- @Tag
 +-- @Disabled
 +-- @DisplayName
 +-- @ExtendWith
 +-- Assertions
        |
        +-- assertEquals
        +-- assertTrue
        +-- assertThrows
        +-- assertAll
        +-- assertTimeout
```

---

# 64. Mockito Features to Remember

```text
Mockito
 |
 +-- @Mock
 +-- @InjectMocks
 +-- @Spy
 +-- @Captor
 |
 +-- when()
 |    +-- thenReturn()
 |    +-- thenThrow()
 |    +-- thenAnswer()
 |
 +-- doReturn()
 +-- doThrow()
 |
 +-- verify()
 |    +-- times()
 |    +-- never()
 |    +-- atLeastOnce()
 |    +-- atMost()
 |    +-- timeout()
 |
 +-- ArgumentMatchers
 |    +-- any()
 |    +-- eq()
 |    +-- anyString()
 |
 +-- ArgumentCaptor
 +-- verifyNoInteractions()
 +-- verifyNoMoreInteractions()
```

---

# 65. ⭐ Interview Cheat Sheet

| Question | Short Answer |
|---|---|
| What is JUnit? | Java framework for automated testing |
| What is JUnit 5? | Modern JUnit programming model based on Jupiter |
| `@Test`? | Marks a test method |
| `@BeforeEach`? | Runs before every test |
| `@BeforeAll`? | Runs once before all tests |
| `@AfterEach`? | Runs after every test |
| `@AfterAll`? | Runs once after all tests |
| `assertThrows()`? | Tests expected exceptions |
| `assertAll()`? | Groups multiple assertions |
| `@ParameterizedTest`? | Runs one test with multiple inputs |
| `@Nested`? | Organizes related tests |
| `@Mock`? | Creates Mockito mock |
| `@InjectMocks`? | Creates class under test and injects mocks |
| `@Spy`? | Wraps a real object |
| `@Captor`? | Captures method arguments |
| `when()`? | Stubs mock behavior |
| `verify()`? | Verifies mock interaction |
| `thenReturn()`? | Defines return value |
| `thenThrow()`? | Defines exception |
| `thenAnswer()`? | Defines dynamic behavior |
| `any()`? | Mockito argument matcher |
| `eq()`? | Matches an exact argument |
| Async testing? | Wait for actual completion with a timeout |
| `Thread.sleep()`? | Avoid for deterministic async tests |
| Unit test? | Tests one unit in isolation |
| Integration test? | Tests multiple components together |
| `@SpringBootTest`? | Loads Spring application context |
| `@WebMvcTest`? | Controller-layer Spring test |
| `@DataJpaTest`? | JPA/repository-focused test |

---

# 66. 🎯 Best Interview Answer: "How Do You Write Unit Tests?"

A strong short answer:

> **I normally follow the AAA pattern: Arrange, Act, and Assert. I use JUnit 5 for test execution and assertions, and Mockito to mock external dependencies such as repositories and clients. I use `when().thenReturn()` for stubbing, `verify()` for important interactions, `assertThrows()` for exception scenarios, and parameterized tests for multiple input combinations. For asynchronous methods, I avoid fixed `Thread.sleep()` and wait for the actual completion mechanism with a bounded timeout. For Spring integration scenarios, I use focused annotations such as `@WebMvcTest`, `@DataJpaTest`, or `@SpringBootTest` depending on what I need to validate.**

---

# 67. 🎯 30-Second Interview Summary

Remember:

```text
JUnit 5
   |
   +-- Run tests
   +-- Assertions
   +-- Lifecycle
   +-- Parameterized tests
   +-- Extensions

Mockito
   |
   +-- Mock
   +-- Stub
   +-- Verify

Test Flow
   |
   v
Arrange
   |
   v
Act
   |
   v
Assert
   |
   v
Verify

Async
   |
   v
CompletableFuture
   |
   v
Wait with timeout
   |
   v
Assert
```

### One-line memory trick

> **JUnit checks the result, Mockito controls dependencies, `when()` stubs behavior, `verify()` checks interactions, and `assert*()` checks the outcome.**
