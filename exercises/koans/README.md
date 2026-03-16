# Spring Koans

A series of failing tests that teach Spring Boot concepts. Work through them in order — each koan builds on the previous one.

## Requirements

- Java 21+
- An IDE with Kotlin support (IntelliJ recommended)

## How to Work

Run a single koan at a time:

```bash
./mvnw test -Dtest="*.Koan1*"
```

Read the test, read the error, fix the source code. When all tests in a koan pass, move to the next one.

Run all tests to see your overall progress:

```bash
./mvnw test
```

## Koans

| # | Topic | What you do |
|---|-------|-------------|
| 1 | **IoC** | Add `@Component` to make a class a Spring bean |
| 2 | **Dependency Injection** | Inject one bean into another via constructor |
| 3 | **Interfaces** | Program to an interface, not an implementation |
| 4 | **@Bean** | Declare an `ObjectMapper` bean in a `@Configuration` class |
| 5 | **@Qualifier** | Disambiguate when multiple beans match |
| 6 | **Configuration** | Use `@ConfigurationProperties` and profiles |
| 7 | **Testing** | Write a `@WebMvcTest` and a `@DataJpaTest` |
| 8 | **Mini-Project** | Build a complete REST API from scratch |

## Tips

- Each koan has `TODO` comments telling you what to change
- Tests are read-only (except Koan 7 where you write the test body)
- The mini-project package starts empty — you create everything
- If stuck, the presentation slides cover the same concepts
