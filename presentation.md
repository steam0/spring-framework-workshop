# What is Spring?

At its core, Spring is just a **dependency injection container**. No web server, no database, no HTTP — just a plain Java process that creates and connects your objects. Everything else is a module you opt into.

What Spring Core gives you:

- **IoC Container** ([ApplicationContext]{tooltip: The central interface in Spring's IoC container. It's the object that holds all your beans, resolves dependencies, and manages their lifecycle. Think of it as a smart HashMap<Class, Object> that knows how to wire everything together.}) — creates objects, manages their lifecycle, wires them together
- **Dependency Injection** — constructor, setter, and field injection
- **[Component scanning]{tooltip: At startup, Spring scans your package tree for classes annotated with @Component (and its specializations like @Service, @Repository). Each match is automatically registered as a bean — no manual listing required.}** — auto-discovers your `@Component` classes

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

# Real Life Example

Let's look at a real application at Norsk Tipping — the same service, rewritten from version 1.6.5 to 1.7.0. This is a plain Java Servlet application, not Spring — but the principles of interfaces and dependency injection are exactly the same.

## Before — v1.6.5 (no interfaces, no DI)

The old version has a single factory class that creates everything directly. Dependencies are hardcoded — the class knows exactly which implementations exist and constructs them itself.

- Concrete classes instantiated with `new`
- No interfaces — every dependency is a specific implementation
- Changing one component means editing the factory
- Testing requires the real dependencies — no easy way to mock

[VasPrizeNotificationBatchFactory.java (v1.6.5)](https://bitbucket.norsk-tipping.no/projects/BIB/repos/vasprizenotificationbatch/browse/src/main/java/no/norsktipping/batchjobs/vasprizenotification/factory/VasPrizeNotificationBatchFactory.java?at=refs%2Ftags%2F1.6.5#53)

## After — v1.7.0 (interfaces + DI)

The new version uses interfaces and constructor injection — done manually, without any framework. The controller declares *what* it needs through interfaces, and receives implementations via the constructor.

- Dependencies are interfaces, passed in through the constructor
- No `new` inside the controller — it doesn't know which implementation it gets
- Swapping an implementation means changing what's passed in, not the controller
- Each dependency can be mocked independently in tests

[MainController.java (v1.7.0)](https://bitbucket.norsk-tipping.no/projects/BIB/repos/vasprizenotificationbatch/browse/src/main/java/no/norsktipping/batchjobs/vasprizenotification/application/MainController.java?at=refs%2Ftags%2F1.7.0)

> Same application, same business logic — but the v1.7.0 version is testable, extensible, and follows the principles we just learned. This is what interfaces + DI look like in practice — even without Spring. Spring just automates the wiring for you.

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

- **Auto-configuration** — Spring Boot detects which libraries are on your [classpath]{tooltip: The classpath is the list of JARs and directories the JVM searches when loading classes. In a Maven/Gradle project, it's your compiled code plus all declared dependencies. When Spring Boot sees e.g. Jackson on the classpath, it auto-configures a JSON mapper for you.} and automatically configures beans for them. You don't write `@Configuration` classes for common setups — Boot does it for you.
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

Loads only the JPA layer: `@Repository`, `@Entity`, Spring Data repositories. Auto-configures an **embedded in-memory database** (H2) replacing your real datasource. Each test is [`@Transactional`]{tooltip: Wraps the test method in a database transaction. When the test finishes, the transaction is rolled back instead of committed — so any rows you inserted or updated are undone. This keeps each test isolated without manual cleanup.} and **rolls back** automatically — tests don't pollute each other.

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

---

# Why nt-boot?

> TODO: Add content about why Norsk Tipping created nt-boot. This section will be filled in later.

# nt-boot Modules

nt-boot is a set of Spring Boot extensions that standardize how we build applications at Norsk Tipping. You add `nt-boot-starter-parent` as your Maven parent and pick the modules you need — each one auto-configures a specific concern with NT defaults already in place.

```xml
<parent>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-starter-parent</artifactId>
    <version>4.x.x</version>
</parent>
```

- **nt-boot-core** — Structured JSON logging, Prometheus metrics, application metadata
- **nt-boot-web** — Tracing headers, request/response logging, CORS configuration
- **nt-boot-web-multi-auth** — Authentication with NT Token and/or OAuth2 (Keycloak)
- **nt-boot-feign** — Declarative HTTP clients with automatic header forwarding
- **nt-boot-kafka** — Standardized Kafka consumer/producer configuration
- **nt-boot-unleash** — Feature flag management via Unleash
- **nt-boot-leader-election-openshift** — Leader election for multi-pod deployments
- **nt-boot-redis** — Redis caching with StringRedisTemplate

All configuration lives under the `nt.*` namespace in `application.yml`.

# nt-boot-core

This module contains what every NT application must have. It exists because our Container Pipeline has strict requirements for all applications — nt-boot-core auto-configures them so you don't have to.

**What the pipeline requires → what nt-boot-core does:**

| Pipeline requirement | nt-boot-core solution |
|---|---|
| Structured JSON logging | Logstash encoder on by default — every log line is JSON to stdout |
| Application metrics (OpenMetrics) | Prometheus endpoint on `/actuator/prometheus` |
| Health probes for Kubernetes | Liveness, readiness, and startup probes pre-configured |
| Traceability through environments | `application` name and `deployment_number` added to every log line and metric |

**Structured JSON logging** is on by default. Every log statement includes:
- `application` — your artifactId (from build-info)
- `deployment_number` — from the `DEPLOYMENT_NUMBER` env var (set automatically by the pipeline)

The deployment number gives full traceability from commit to production — every log line can be traced back to the exact build.

For local development, switch to human-readable output:

```bash
LOG_APPENDER=CONSOLE
```

**Health probes** are pre-configured and exposed on the actuator:
- `/actuator/health/liveness` — is the app alive?
- `/actuator/health/readiness` — is the app ready for traffic?
- Kubernetes uses these to route traffic and restart unhealthy pods

**Prometheus metrics** are exposed on `/actuator/prometheus` out of the box. All metrics are tagged with `application` and `applicationVersion` for dashboard filtering.

# nt-boot-web

Extends `spring-boot-web` with tracing and request logging.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-web</artifactId>
</dependency>
```

Every incoming request automatically gets these **tracing headers** added to the MDC:

| Header | Description | Default |
|---|---|---|
| `X-Session-Id` | Unique session identifier | Random UUID |
| `X-Vendor-Id` | Identifies the calling vendor | Unknown |
| `X-Client-Id` | Identifies the calling client | Unknown |
| `X-Client-Version` | Client version | Unknown |

You can **require** these headers on all requests — any request missing them gets rejected:

```yaml
nt:
  web:
    require-client-tracing-headers: true
    ignored-paths:
      - "/actuator/**"
```

> CORS is fully disabled by default — any origin can reach any endpoint. Configure it explicitly when exposing public APIs.

# nt-boot-web-multi-auth

The recommended authentication module. Supports **NT Token** and **OAuth2** (Keycloak) simultaneously — one module for both internal and customer-facing APIs.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-web-multi-auth</artifactId>
</dependency>
```

```yaml
nt:
  auth:
    multi:
      nt-token-enabled: true
      issuers:
        - "https://auth.dev.norsk-tipping.no/auth/realms/ntint-ntboot"
      public-paths:
        - "/public/**"
        - "/actuator/**"
```

All paths are **secured by default** — only paths in `public-paths` are open.

Use `@PreAuthorize` for fine-grained authorization:

```kotlin
@GetMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
fun getCustomer(@AuthenticationPrincipal user: User): ResponseEntity<String> {
    return ResponseEntity.ok("Customer: ${user.personId}")
}
```

```java
@GetMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public ResponseEntity<String> getCustomer(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok("Customer: " + user.getPersonId());
}
```

**NT Token authorities:** `ROLE_CUSTOMER`, `HAS_CUSTOMER_ID`, `ID_LEVEL_1` through `ID_LEVEL_4`

**OAuth2 authorities:** Scopes as `SCOPE_profile`, `SCOPE_email`, etc.

# nt-boot-feign

Declarative HTTP clients — define an interface, configure a URL, and nt-boot handles the rest. Tracing headers (`X-Session-Id`, `X-Vendor-Id`, `X-Client-Id`, `X-Client-Version`) are forwarded automatically.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-feign</artifactId>
</dependency>
```

**1. Enable scanning** in your application class:

```kotlin
@FeignServiceScan
@SpringBootApplication
class Application
```

```java
@FeignServiceScan
@SpringBootApplication
public class Application { }
```

**2. Define your client** as an interface:

```kotlin
@FeignService(key = "HelloWorld")
interface HelloWorldClient {
    @RequestLine("GET /api/hello-world")
    fun getHelloWorld(): HelloWorldResponse
}
```

```java
@FeignService(key = "HelloWorld")
public interface HelloWorldClient {
    @RequestLine("GET /api/hello-world")
    HelloWorldResponse getHelloWorld();
}
```

**3. Configure** the URL in `application.yml`:

```yaml
nt:
  feign:
    clients:
      HelloWorld:
        url: "http://my-service:8080"
        readTimeout: 10000    # default
        connectTimeout: 3000  # default
```

**4. Inject and use** — it's a regular Spring bean:

```kotlin
@Service
class MyService(private val client: HelloWorldClient) {
    fun doStuff() = client.getHelloWorld()
}
```

```java
@Service
public class MyService {
    private final HelloWorldClient client;

    public MyService(HelloWorldClient client) {
        this.client = client;
    }

    public void doStuff() {
        client.getHelloWorld();
    }
}
```

# nt-boot-kafka

Standardized Kafka configuration with NT defaults. Removes the boilerplate of setting up consumers and producers.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-kafka</artifactId>
</dependency>
```

```yaml
nt:
  kafka:
    my-consumer:
      properties:
        topic: "my-topic"
        schema-registry-url: "http://schema-registry:8081"
        bootstrap-servers: "kafka:9092"
```

The configuration becomes a Spring bean. Use it to build consumers:

```kotlin
@Bean
fun myConsumer(
    ntKafkaClientProperties: NtKafkaClientProperties,
    ntApplicationConfig: NtApplicationConfig
): NTKafkaConsumer<String, MyEvent> {
    return NTKafkaConsumerBuilder.builder(
        ntKafkaClientProperties.topic,
        ntApplicationConfig.clientId
    )
        .withGroupId(ntApplicationConfig.clientId)
        .withSchemaRegistryUrl(ntKafkaClientProperties.schemaRegistryUrl)
        .withBootstrapServers(ntKafkaClientProperties.bootstrapServers)
        .withAutoOffsetReset("latest")
        .build()
}
```

```java
@Bean
NTKafkaConsumer<String, MyEvent> myConsumer(
        NtKafkaClientProperties ntKafkaClientProperties,
        NtApplicationConfig ntApplicationConfig) {
    return NTKafkaConsumerBuilder.builder(
        ntKafkaClientProperties.getTopic(),
        ntApplicationConfig.getClientId()
    )
        .withGroupId(ntApplicationConfig.getClientId())
        .withSchemaRegistryUrl(ntKafkaClientProperties.getSchemaRegistryUrl())
        .withBootstrapServers(ntKafkaClientProperties.getBootstrapServers())
        .withAutoOffsetReset("latest")
        .build();
}
```

**Auto-start consumers** by implementing `RunnableKafkaConsumer` — Spring launches each one in its own thread:

```kotlin
@Component
class MyEventConsumer(
    private val consumer: NTKafkaConsumer<String, MyEvent>
) : RunnableKafkaConsumer {
    override fun run() {
        consumer.ntSyncPoll { record -> processRecord(record) }
    }
    override fun stop() { consumer.shutdown() }
}
```

```java
@Component
public class MyEventConsumer implements RunnableKafkaConsumer {
    private final NTKafkaConsumer<String, MyEvent> consumer;

    public MyEventConsumer(NTKafkaConsumer<String, MyEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        consumer.ntSyncPoll(record -> processRecord(record));
    }

    @Override
    public void stop() { consumer.shutdown(); }
}
```

For **multiple Kafka clients**, use `@Qualifier` to distinguish them:

```yaml
nt:
  kafka:
    orders:
      properties:
        topic: "orders-topic"
        bootstrap-servers: "kafka:9092"
        schema-registry-url: "http://registry:8081"
    notifications:
      properties:
        topic: "notifications-topic"
        bootstrap-servers: "kafka:9092"
        schema-registry-url: "http://registry:8081"
```

```kotlin
@Bean
fun ordersConsumer(
    @Qualifier("orders") props: NtKafkaClientProperties
): NTKafkaConsumer<String, Order> { ... }
```

```java
@Bean
NTKafkaConsumer<String, Order> ordersConsumer(
        @Qualifier("orders") NtKafkaClientProperties props) { ... }
```

# nt-boot-unleash

Feature flag management with [Unleash](https://www.getunleash.io/). Add the dependency and configure the server — you get an `Unleash` bean ready to inject.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-unleash</artifactId>
</dependency>
```

```yaml
nt:
  unleash:
    api-url: "https://unleash.norsk-tipping.no/api"
    api-token: "${UNLEASH_API_TOKEN}"
    instance-id: "${HOSTNAME}"
```

```kotlin
@Service
class CheckoutService(private val unleash: Unleash) {
    fun checkout(cart: Cart): Receipt {
        if (unleash.isEnabled("new-payment-flow")) {
            return newCheckout(cart)
        }
        return legacyCheckout(cart)
    }
}
```

```java
@Service
public class CheckoutService {
    private final Unleash unleash;

    public CheckoutService(Unleash unleash) {
        this.unleash = unleash;
    }

    public Receipt checkout(Cart cart) {
        if (unleash.isEnabled("new-payment-flow")) {
            return newCheckout(cart);
        }
        return legacyCheckout(cart);
    }
}
```

**How it works under the hood:**
- The client polls toggle config from the server every **15 seconds** and caches locally
- `isEnabled()` evaluates against the local cache — **no network call** per invocation
- If Unleash is down, the client uses the last known config. On a cold start with no server, `isEnabled()` returns `false`

**Gradual rollout** uses a Murmurhash3 of the toggle name + user context. All evaluation is local — no user data is sent to the server.

```kotlin
val context = UnleashContext.builder()
    .userId("customer@example.com")
    .build()

if (unleash.isEnabled("new-payment-flow", context)) {
    // This user consistently gets the new flow
}
```

```java
UnleashContext context = UnleashContext.builder()
    .userId("customer@example.com")
    .build();

if (unleash.isEnabled("new-payment-flow", context)) {
    // This user consistently gets the new flow
}
```

# nt-boot-leader-election

When you run multiple pods, some tasks should only run on **one** pod — scheduled jobs, DB migrations, cleanup tasks. This module uses Kubernetes Lease resources to elect a leader.

```xml
<dependency>
    <groupId>no.norsktipping.boot</groupId>
    <artifactId>nt-boot-leader-election-openshift</artifactId>
</dependency>
```

```yaml
nt:
  leader-election:
    enable-openshift: true
    assume-leader-when-local: true  # for local development
```

```kotlin
@Service
class ScheduledCleanup(
    private val leaderElectionHandler: LeaderElectionHandler
) {
    @Scheduled(fixedDelay = 60_000)
    fun cleanup() {
        if (leaderElectionHandler.isLeader()) {
            // Only one pod executes this
            deleteExpiredRecords()
        }
    }
}
```

```java
@Service
public class ScheduledCleanup {
    private final LeaderElectionHandler leaderElectionHandler;

    public ScheduledCleanup(LeaderElectionHandler leaderElectionHandler) {
        this.leaderElectionHandler = leaderElectionHandler;
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanup() {
        if (leaderElectionHandler.isLeader()) {
            // Only one pod executes this
            deleteExpiredRecords();
        }
    }
}
```

> **Local development:** When running outside Kubernetes, set `assume-leader-when-local: true` so leader-dependent code still executes during development.
