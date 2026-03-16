# What is Spring?

At its core, Spring is just a **dependency injection container**. No web server, no database, no HTTP — just a plain Java process that creates and connects your objects. Everything else is a module you opt into.

What Spring Core gives you:

- **IoC Container** (ApplicationContext) — creates objects, manages their lifecycle, wires them together
- **Dependency Injection** — constructor, setter, and field injection
- **Component scanning** — auto-discovers your `@Component` classes

Popular Spring modules:

- **Spring MVC** — Web & REST APIs
- **Spring Data** — Database access
- **Spring Security** — Authentication & authorization
- **Spring AOP** — Aspect-oriented programming
- **Spring Batch** — Batch processing
- **Spring Cloud** — Distributed systems & microservices
- **Spring WebFlux** — Reactive web framework
- **Spring Kafka** — Kafka integration

# Inversion of Control (IoC)

> *"Inversion of Control is a principle where the control of object creation and lifecycle is transferred from the application code to a framework or container."*

- The **Spring container** (ApplicationContext) is responsible for creating and managing objects — you don't call `new` yourself
- You declare your classes as beans (via annotations like `@Component`, `@Service`, `@Repository`) and Spring takes ownership of their lifecycle
- This inverts the traditional flow: instead of your code controlling what gets created, the **framework** is in control — your code just declares what it needs

```kotlin
@Component
class PriceCalculator {
    // Spring creates and manages this object for you
}
```

```java
@Component
class PriceCalculator {
    // Spring creates and manages this object for you
}
```

# Dependency Injection

> *"Dependency Injection is a technique where an object receives its dependencies from an external source rather than creating them itself."*

- Dependency Injection is *how* Inversion of Control is implemented in practice — Spring looks at what a bean needs and automatically provides ("injects") those dependencies
- **Constructor injection** is the recommended approach: declare dependencies as constructor parameters and Spring fills them in
- This makes your classes easy to test — in a unit test, you just pass in a mock; no Spring container needed

```kotlin
@Component
class OrderService(
    private val calculator: PriceCalculator  // Spring injects this
)
```

```java
@Component
class OrderService {
    private final PriceCalculator calculator;

    OrderService(PriceCalculator calculator) {
        this.calculator = calculator; // Spring injects this
    }
}
```

# Why Interfaces Matter

Dependency Injection without interfaces is just object construction. Dependency Injection *with* interfaces is what makes your code swappable, testable, and extensible.

- **Loose coupling** — your class depends on *what* something does (the interface), not *how* it does it (the implementation)
- **Testability** — in tests, pass a mock that implements the interface. No real database, no real HTTP calls.
- **Multiple implementations** — `StripeGateway` and `VippsGateway` can both implement `PaymentGateway`. Spring picks the right one via `@Primary`, `@Qualifier`, or profiles.

```kotlin
interface PaymentGateway {
    fun charge(amount: Money): Result
}

@Component
class StripeGateway : PaymentGateway {
    override fun charge(amount: Money): Result { ... }
}
```

```java
interface PaymentGateway {
    Result charge(Money amount);
}

@Component
class StripeGateway implements PaymentGateway {
    public Result charge(Money amount) { ... }
}
```

```kotlin
@Component
class OrderService(
    private val gateway: PaymentGateway  // interface — not a concrete class
)
```

```java
@Component
class OrderService {
    private final PaymentGateway gateway; // interface — not a concrete class

    OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

> Without interfaces, changing an implementation means editing every class that uses it. With interfaces, you swap the bean and nothing else changes.

# Spring Beans

A **bean** is an object whose lifecycle is managed by the Spring container. You *declare* what should be a bean — Spring takes care of creating the instance, injecting its dependencies, and cleaning it up.

You declare beans in two ways:

- **Annotation-based** — mark your own class with `@Component` (or `@Service`, `@Repository`, etc.) and Spring instantiates it
- **Manual definition** — use `@Bean` in a `@Configuration` class to tell Spring how to create an object you don't own (e.g. a third-party library class)

```kotlin
@Configuration
class AppConfig {
    @Bean
    fun jsonMapper(): JsonMapper =
        JsonMapper.builder().build()
}
```

```java
@Configuration
class AppConfig {
    @Bean
    JsonMapper jsonMapper() {
        return JsonMapper.builder().build();
    }
}
```

Bean scopes:

- **singleton** (default) — one shared instance for the entire application. Use for stateless services, repositories, and most components.
- **prototype** — a new instance every time the bean is requested. Use when the bean holds per-use state (e.g. a builder or request-specific context).
- **request** — one instance per HTTP request. Use for request-scoped data like the current user's context.
- **session** — one instance per HTTP session. Use for session-specific state like a shopping cart.

> Most beans should be singletons. Only use other scopes when you have a specific reason — unnecessary scoping adds complexity.

# Annotations

> Spring uses annotations to identify what role a class plays. All [stereotype]{tooltip: The term comes from UML/design patterns where a stereotype classifies something by its role. In Spring, @Component is the base stereotype ("this is a bean") and @Service, @Repository, @Controller are refined stereotypes that say what kind of bean it is. Functionally they're the same — the label is metadata for humans and tools.} annotations make a class a bean, but they signal different intent. **@Component** is the base — **@Service**, **@Repository**, and **@Controller** are specializations that tell both Spring and other developers what layer the class belongs to. **@Configuration** classes are different — they don't represent a component themselves, but define how other beans are created.

**Beans & Services**

| Annotation | Purpose | Extra behavior |
|---|---|---|
| `@Component` | General-purpose bean | None — base stereotype |
| `@Service` | Business logic / service classes | None — purely semantic |
| `@Repository` | Data access / DAO classes | Vendor-specific SQL exceptions are re-thrown as Spring's `DataAccessException`, so your code doesn't depend on a specific database driver |

**Web**

| Annotation | Purpose | Extra behavior |
|---|---|---|
| `@Controller` | HTTP request handlers | Enables request mapping (`@GetMapping`, etc.) |
| `@RestController` | REST API endpoints | Combines `@Controller` + `@ResponseBody` — returns JSON/XML directly |

**Configuration**

| Annotation | Purpose | Extra behavior |
|---|---|---|
| `@Configuration` | Bean factory class | Houses `@Bean` methods |
| `@Bean` | Declares a single bean | Used inside `@Configuration` — you control instantiation (useful for third-party classes) |
| `@ConfigurationProperties` | Binds external config to a Java object | Type-safe access to groups of properties from `application.yml` — supports validation and nested objects |


# Autowiring

- **Autowiring** is Spring's mechanism for automatically resolving and injecting bean dependencies — you declare what you need, Spring finds a matching bean
- **How Spring finds the right bean:** it matches by type — if your constructor needs a `PaymentGateway`, Spring finds the bean that implements it. If multiple candidates exist, use `@Qualifier("name")` to disambiguate
- **Constructor injection** happens automatically when a bean has a single constructor — no `@Autowired` annotation needed (since Spring 4.3)

# Autowiring — @Autowired

Use `@Autowired` to explicitly tell Spring to inject a dependency. Can be used on fields, setters, or constructors.

```kotlin
// Field injection
@Service
class OrderService {
    @Autowired
    lateinit var repo: OrderRepository
}

// Setter injection
@Service
class OrderService {
    private lateinit var gateway: PaymentGateway

    @Autowired
    fun setPaymentGateway(gateway: PaymentGateway) {
        this.gateway = gateway
    }
}
```

```java
// Field injection
@Service
class OrderService {
    @Autowired
    private OrderRepository repo;
}

// Setter injection
@Service
class OrderService {
    private PaymentGateway gateway;

    @Autowired
    void setPaymentGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

# Autowiring — Constructor Injection

With a single constructor, Spring autowires parameters automatically — no annotation needed. This is the **recommended** approach because dependencies are explicit, immutable, and easy to test.

```kotlin
@Service
class OrderService(
    private val repo: OrderRepository,
    private val gateway: PaymentGateway
)
```

```java
@Service
class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway gateway;

    OrderService(OrderRepository repo, PaymentGateway gateway) {
        this.repo = repo;
        this.gateway = gateway;
    }
}
```

# Spring Modules

- Spring is not one big library — it's a collection of **modules** you pick from based on what your app needs
- Common modules: **Spring MVC** (web/REST), **Spring Data** (database access), **Spring Security** (auth), **Spring AOP** (cross-cutting concerns)
- All modules share the same Inversion of Control and Dependency Injection foundation, so they integrate seamlessly — learn the core, and every module feels familiar

- **Spring MVC** — Web & REST APIs
- **Spring Data** — Database access
- **Spring Security** — Authentication & authorization
- **Spring AOP** — Aspect-oriented programming
- **Spring Batch** — Batch processing
- **Spring Cloud** — Distributed systems & microservices
- **Spring WebFlux** — Reactive web framework
- **Spring Kafka** — Kafka integration

# What Does Spring Boot Do For You?

Everything you've learned so far — Inversion of Control, Dependency Injection, beans, annotations, autowiring — that's all Spring Framework. Spring Boot is an [opinionated]{tooltip: An opinionated framework makes decisions for you — it picks sensible defaults so you don't have to configure everything yourself. You can still override any choice, but you only need to when the default doesn't fit.} layer on top that handles the boring parts.

- **Auto-configuration** — Spring Boot detects which libraries are on your classpath and automatically configures beans for them. You don't write `@Configuration` classes for common setups — Boot does it for you.
- **Starter dependencies** — Instead of manually adding each library and matching compatible versions, you add a single starter (e.g. `spring-boot-starter-webmvc`) and Boot pulls in everything you need. Spring Boot 4 ships even smaller, more focused modules than before.
- **Production defaults** — Health checks, metrics, and environment info via Spring Actuator. Sensible configuration out of the box — you only override what you need.

> Spring Boot is not a different framework. It's the same beans, the same Dependency Injection, the same annotations — with less boilerplate to get started.

# Configuration Files

- **application.yml** / **application.properties** — your app's central config
- Profile-specific files: `application-dev.yml`, `application-prod.yml`
- Activate with `spring.profiles.active=dev` or env var `SPRING_PROFILES_ACTIVE`
- Inject values with `@Value("${property.name}")` or type-safe `@ConfigurationProperties`

```yaml
# application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: ${DB_USER:admin}       # env var with default
    password: ${DB_PASSWORD}

app:
  feature-flags:
    new-checkout: true               # custom properties — your own namespace
```

> **Tip:** Use `@ConfigurationProperties` for groups of related settings — you get type safety and validation for free.

**Property loading order** — each level overrides the one above it:

1. `application.yml` / `application.properties` — your defaults
2. `application-default.yml` — loaded when no profile is explicitly activated
3. Profile-specific files — `application-dev.yml` overrides values from `application.yml` (replaces default)
3. Environment variables — `SPRING_DATASOURCE_URL` overrides `spring.datasource.url`
4. Command-line arguments — `--server.port=9090` overrides everything

This means you set sensible defaults in your config file, override per environment with profiles, and can still override anything at deploy time with env vars or CLI args.

## Quiz: Configuration Ordering 1

You have the following setup:

```yaml
# application.yml
server:
  port: 8080

# application-dev.yml
server:
  port: 9090
```

You start the app with:

```bash
java -jar app.jar --spring.profiles.active=dev
```

**What port does the server start on?**

{quiz}
- 8080
- 9090 {correct}
- Neither — it fails to start
{/quiz}

`application-dev.yml` overrides `application.yml` because profile-specific files take precedence over the default config.

## Quiz: Configuration Ordering 2

You have the following setup:

```yaml
# application.yml
server:
  port: 8080

# application-prod.yml
server:
  port: 3000
```

You start the app with:

```bash
SPRING_PROFILES_ACTIVE=prod SERVER_PORT=9999 java -jar app.jar --server.port=4000
```

**What port does the server start on?**

{quiz}
- 8080
- 3000
- 9999
- 4000 {correct}
{/quiz}

Command-line arguments override everything. The full chain: `application.yml` (8080) → `application-prod.yml` (3000) → env var (9999) → CLI arg (4000).

# Testing with @SpringBootTest

`@SpringBootTest` loads the **full application context** — every bean, every config, every auto-configuration. Use it for integration tests where you need the real wiring between components.

- **@MockitoBean** — replaces a specific bean with a Mockito mock inside the Spring context (replaces the deprecated `@MockBean` from earlier Spring Boot versions)

```kotlin
@SpringBootTest
class OrderServiceTest {

    @Autowired
    lateinit var orderService: OrderService

    @MockitoBean
    lateinit var paymentGateway: PaymentGateway  // replaced with mock

    @Test
    fun shouldCreateOrder() {
        whenever(paymentGateway.charge(any())).thenReturn(Result.ok())

        val order = orderService.create(OrderRequest("item-1", 2))

        assertThat(order.status).isEqualTo(Status.CREATED)
    }
}
```

```java
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private PaymentGateway paymentGateway;  // replaced with mock

    @Test
    void shouldCreateOrder() {
        when(paymentGateway.charge(any())).thenReturn(Result.ok());

        Order order = orderService.create(new OrderRequest("item-1", 2));

        assertThat(order.getStatus()).isEqualTo(Status.CREATED);
    }
}
```

> Full context tests can take 5–30 seconds to start. Use **slice tests** to load only the layer you're testing.

## @WebMvcTest — Testing the Web Layer

Loads only the web layer: `@Controller`, `@RestController`, `@ControllerAdvice`, filters. Does **not** load services, repositories, or other components. Auto-configures `MockMvc` — an HTTP client simulator that sends requests without starting a real server.

> In Spring Boot 4, `@SpringBootTest` no longer auto-configures MockMvc. If you need MockMvc in a full context test, add `@AutoConfigureMockMvc` explicitly. With `@WebMvcTest`, MockMvc is still auto-configured.

```kotlin
@WebMvcTest(OrderController::class)  // only loads this one controller
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc          // no real HTTP server

    @MockitoBean
    lateinit var orderService: OrderService // fake — the real bean isn't loaded

    @Test
    fun shouldReturnOrder() {
        whenever(orderService.findById(1L)).thenReturn(Order(1L, "item"))

        mockMvc.perform(get("/orders/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }
}
```

```java
@WebMvcTest(OrderController.class)  // only loads this one controller
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;          // no real HTTP server

    @MockitoBean
    private OrderService orderService; // fake — the real bean isn't loaded

    @Test
    void shouldReturnOrder() throws Exception {
        when(orderService.findById(1L)).thenReturn(new Order(1L, "item"));

        mockMvc.perform(get("/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

## @DataJpaTest — Testing the Data Layer

Loads only the JPA layer: `@Repository`, `@Entity`, Spring Data repositories. Auto-configures an **embedded in-memory database** (H2) replacing your real datasource. Each test is `@Transactional` and **rolls back** automatically — tests don't pollute each other.

```kotlin
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    lateinit var repo: OrderRepository    // real repo, backed by in-memory H2

    @Autowired
    lateinit var em: TestEntityManager    // helper for setting up test data

    @Test
    fun shouldFindByStatus() {
        em.persistAndFlush(Order("item", Status.CREATED))

        val found = repo.findByStatus(Status.CREATED)

        assertThat(found).hasSize(1)
    }
    // transaction rolls back here — DB is clean for next test
}
```

```java
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repo;    // real repo, backed by in-memory H2

    @Autowired
    private TestEntityManager em;    // helper for setting up test data

    @Test
    void shouldFindByStatus() {
        em.persistAndFlush(new Order("item", Status.CREATED));

        List<Order> found = repo.findByStatus(Status.CREATED);

        assertThat(found).hasSize(1);
    }
    // transaction rolls back here — DB is clean for next test
}
```

> **Rule of thumb:** Use the *narrowest* slice annotation that covers what you're testing — `@WebMvcTest` for controllers, `@DataJpaTest` for repositories, `@SpringBootTest` only when you need the full picture.
