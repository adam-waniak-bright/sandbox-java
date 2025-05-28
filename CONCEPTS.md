# Key Concepts Reference Guide

This document provides an overview of the key architectural and testing concepts that are covered in the educational tasks.

## Vertical Slices Architecture

### What is it?
Vertical slices architecture organizes code by feature (or "slice") rather than by technical layer. Each slice contains all the components needed to implement a specific feature, from the UI to the database.

### Key characteristics:
- **Feature-focused**: Code is organized around features rather than technical concerns
- **Self-contained**: Each slice contains all the components needed for a feature
- **Reduced coupling**: Slices have minimal dependencies on other slices
- **Improved cohesion**: Related code is kept together

### Benefits:
- Easier to understand the codebase by feature
- Simpler to make changes to a feature without affecting others
- More natural alignment with how business stakeholders think about the system
- Facilitates parallel development by different teams

### Comparison with traditional layered architecture:

**Traditional Layered Architecture:**
```
├── Controllers/
│   ├── UserController.java
│   ├── RoleController.java
│   └── PermissionController.java
├── Services/
│   ├── UserService.java
│   ├── RoleService.java
│   └── PermissionService.java
├── Repositories/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── PermissionRepository.java
└── Models/
    ├── User.java
    ├── Role.java
    └── Permission.java
```

**Vertical Slices Architecture:**
```
├── Users/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   └── User.java
├── Roles/
│   ├── RoleController.java
│   ├── RoleService.java
│   ├── RoleRepository.java
│   └── Role.java
└── Permissions/
    ├── PermissionController.java
    ├── PermissionService.java
    ├── PermissionRepository.java
    └── Permission.java
```

## CQRS (Command Query Responsibility Segregation)

### What is it?
CQRS is a pattern that separates read and write operations into different models. Commands (writes) and queries (reads) use different data models, which can be optimized for their specific purposes.

### Key components:
- **Commands**: Represent intentions to change the state of the system
- **Command Handlers**: Process commands and apply changes to the system
- **Queries**: Represent requests for information
- **Query Handlers**: Process queries and return data
- **Models**: Separate models for commands and queries

### Benefits:
- Optimized data models for different operations
- Improved scalability by separating read and write workloads
- Better handling of complex business logic
- Clearer separation of concerns

### Simple CQRS example:

**Command side:**
```java
// Command
public class CreateUserCommand {
    private String username;
    private String email;
    private String role;
    // getters, setters
}

// Command Handler
public class CreateUserCommandHandler {
    private final UserRepository repository;

    public void handle(CreateUserCommand command) {
        User user = new User(
            UUID.randomUUID(),
            command.getUsername(),
            command.getEmail(),
            command.getRole()
        );
        repository.save(user);
    }
}
```

**Query side:**
```java
// Query
public class GetUserQuery {
    private UUID id;
    // getters, setters
}

// Query Handler
public class GetUserQueryHandler {
    private final UserReadRepository repository;

    public UserDto handle(GetUserQuery query) {
        return repository.findById(query.getId())
            .map(this::mapToDto)
            .orElseThrow(() -> new UserNotFoundException(query.getId()));
    }

    private UserDto mapToDto(UserReadModel user) {
        // mapping logic
    }
}
```

## Single Responsibility Principle

### What is it?
The Single Responsibility Principle (SRP) states that a class should have only one reason to change, meaning it should have only one responsibility.

### Key characteristics:
- **Focused classes**: Each class does one thing and does it well
- **Clear boundaries**: Responsibilities are clearly defined
- **High cohesion**: Related functionality is grouped together
- **Low coupling**: Minimal dependencies between classes

### Benefits:
- Easier to understand and maintain
- More testable code
- Reduced risk when making changes
- More reusable components

### Example of applying SRP:

**Before (multiple responsibilities):**
```java
public class UserService {
    private final UserRepository repository;

    public User createUser(String username, String email, String role) {
        // Validation
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }

        // Creation
        User user = new User(UUID.randomUUID(), username, email, role);

        // Persistence
        repository.save(user);

        // Notification
        sendNewUserNotification(user);

        return user;
    }

    private void sendNewUserNotification(User user) {
        // Email sending logic
    }
}
```

**After (single responsibility):**
```java
// Validation
public class UserValidator {
    public void validate(CreateUserCommand command) {
        if (command.getUsername() == null || command.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (command.getEmail() == null || !command.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }
    }
}

// Creation
public class UserFactory {
    public User createUser(String username, String email, String role) {
        return new User(UUID.randomUUID(), username, email, role);
    }
}

// Command Handler (orchestration)
public class CreateUserCommandHandler {
    private final UserValidator validator;
    private final UserFactory factory;
    private final UserRepository repository;
    private final UserNotifier notifier;

    public void handle(CreateUserCommand command) {
        validator.validate(command);

        User user = factory.createUser(
            command.getUsername(),
            command.getEmail(),
            command.getRole()
        );

        repository.save(user);

        notifier.notifyAboutNewUser(user);
    }
}

// Notification
public class UserNotifier {
    public void notifyAboutNewUser(User user) {
        // Email sending logic
    }
}
```

## Testing Without Mocks

### What is it?
Testing without mocks (or with minimal mocking) involves using real implementations of dependencies rather than mock objects. This approach often uses in-memory implementations or test doubles that have real behavior.

### Key strategies:
- **In-memory implementations**: Create lightweight implementations of interfaces for testing
- **Test-specific implementations**: Implement interfaces with test-specific behavior
- **Real objects**: Use actual implementations where possible
- **Hexagonal architecture**: Design your system with ports and adapters to facilitate testing

### Benefits:
- Tests that better reflect real behavior
- Less brittle tests that don't break when implementation details change
- More confidence in test results
- Tests that catch integration issues earlier

### Example:

**Traditional approach with mocks:**
```java
@Test
void createUser_ValidData_UserCreated() {
    // Arrange
    UserRepository mockRepository = mock(UserRepository.class);
    UserService service = new UserService(mockRepository);

    // Act
    service.createUser("testuser", "test@example.com", "USER");

    // Assert
    verify(mockRepository).save(any(User.class));
}
```

**Testing without mocks:**
```java
@Test
void createUser_ValidData_UserCreated() {
    // Arrange
    UserRepository repository = new InMemoryUserRepository();
    UserService service = new UserService(repository);

    // Act
    User user = service.createUser("testuser", "test@example.com", "USER");

    // Assert
    Optional<User> savedUser = repository.findById(user.getId());
    assertTrue(savedUser.isPresent());
    assertEquals("testuser", savedUser.get().getUsername());
}

// In-memory implementation
class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    // Other methods...
}
```

## Integration Testing with Testcontainers

### What is it?
Testcontainers is a Java library that provides lightweight, throwaway instances of common databases, message brokers, or anything else that can run in a Docker container. It allows you to write integration tests that use real dependencies.

### Key features:
- **Real databases**: Test against the same database technology used in production
- **Isolated environments**: Each test runs in its own container
- **Automatic cleanup**: Containers are disposed of after tests complete
- **Support for various technologies**: Databases, message brokers, web browsers, etc.
- **Full application testing**: Test through web controllers with the entire application running

### Benefits:
- Tests that accurately reflect production behavior
- Catching database-specific issues early
- No need to maintain separate test databases
- Consistent test environments across development machines
- End-to-end testing that verifies the entire request flow

### Repository-level testing example:

```java
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        // Configure Spring datasource to use the container
        DataSource dataSource = DataSourceBuilder.create()
            .url(postgres.getJdbcUrl())
            .username(postgres.getUsername())
            .password(postgres.getPassword())
            .build();

        // Create repository with the configured datasource
        repository = new JdbcUserRepository(dataSource);
    }

    @Test
    void saveAndRetrieveUser() {
        // Arrange
        User user = new User(UUID.randomUUID(), "testuser", "test@example.com", "USER");

        // Act
        repository.save(user);
        Optional<User> retrieved = repository.findById(user.getId());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("testuser", retrieved.get().getUsername());
    }
}
```

### Full application testing through web controllers:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndGetUser() {
        // Arrange
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "USER");

        // Act - Create user
        ResponseEntity<UserResponse> createResponse = restTemplate.postForEntity(
            "/api/users",
            request,
            UserResponse.class
        );

        // Assert - User created successfully
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("testuser", createResponse.getBody().getUsername());

        // Act - Get user
        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
            "/api/users/" + createResponse.getBody().getId(),
            UserResponse.class
        );

        // Assert - User retrieved successfully
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("testuser", getResponse.getBody().getUsername());
        assertEquals("test@example.com", getResponse.getBody().getEmail());
        assertEquals("USER", getResponse.getBody().getRole());
    }
}
```

This approach offers several advantages:
- Tests the entire application stack from HTTP request to database and back
- Verifies that controllers, services, and repositories work together correctly
- Catches issues in request/response mapping
- Tests actual HTTP endpoints as clients would use them
- Ensures configuration is correct for the entire application


## Conclusion

These concepts form the foundation of modern software architecture and testing practices. By understanding and applying these concepts, you'll be able to build more maintainable, testable, and scalable applications.

Remember that these patterns are tools, not rules. Use them when they provide value to your specific context, and adapt them as needed to fit your requirements.
