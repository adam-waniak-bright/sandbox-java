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
│   ├── ProductController.java
│   ├── OrderController.java
│   └── UserController.java
├── Services/
│   ├── ProductService.java
│   ├── OrderService.java
│   └── UserService.java
├── Repositories/
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   └── UserRepository.java
└── Models/
    ├── Product.java
    ├── Order.java
    └── User.java
```

**Vertical Slices Architecture:**
```
├── Products/
│   ├── ProductController.java
│   ├── ProductService.java
│   ├── ProductRepository.java
│   └── Product.java
├── Orders/
│   ├── OrderController.java
│   ├── OrderService.java
│   ├── OrderRepository.java
│   └── Order.java
└── Users/
    ├── UserController.java
    ├── UserService.java
    ├── UserRepository.java
    └── User.java
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
public class CreateProductCommand {
    private String name;
    private String description;
    private BigDecimal price;
    // getters, setters
}

// Command Handler
public class CreateProductCommandHandler {
    private final ProductRepository repository;
    
    public void handle(CreateProductCommand command) {
        Product product = new Product(
            UUID.randomUUID(),
            command.getName(),
            command.getDescription(),
            command.getPrice()
        );
        repository.save(product);
    }
}
```

**Query side:**
```java
// Query
public class GetProductQuery {
    private UUID id;
    // getters, setters
}

// Query Handler
public class GetProductQueryHandler {
    private final ProductReadRepository repository;
    
    public ProductDto handle(GetProductQuery query) {
        return repository.findById(query.getId())
            .map(this::mapToDto)
            .orElseThrow(() -> new ProductNotFoundException(query.getId()));
    }
    
    private ProductDto mapToDto(ProductReadModel product) {
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
public class ProductService {
    private final ProductRepository repository;
    
    public Product createProduct(String name, String description, BigDecimal price) {
        // Validation
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        // Creation
        Product product = new Product(UUID.randomUUID(), name, description, price);
        
        // Persistence
        repository.save(product);
        
        // Notification
        sendNewProductNotification(product);
        
        return product;
    }
    
    private void sendNewProductNotification(Product product) {
        // Email sending logic
    }
}
```

**After (single responsibility):**
```java
// Validation
public class ProductValidator {
    public void validate(CreateProductCommand command) {
        if (command.getName() == null || command.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (command.getPrice() == null || command.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }
}

// Creation
public class ProductFactory {
    public Product createProduct(String name, String description, BigDecimal price) {
        return new Product(UUID.randomUUID(), name, description, price);
    }
}

// Command Handler (orchestration)
public class CreateProductCommandHandler {
    private final ProductValidator validator;
    private final ProductFactory factory;
    private final ProductRepository repository;
    private final ProductNotifier notifier;
    
    public void handle(CreateProductCommand command) {
        validator.validate(command);
        
        Product product = factory.createProduct(
            command.getName(),
            command.getDescription(),
            command.getPrice()
        );
        
        repository.save(product);
        
        notifier.notifyAboutNewProduct(product);
    }
}

// Notification
public class ProductNotifier {
    public void notifyAboutNewProduct(Product product) {
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
void createProduct_ValidData_ProductCreated() {
    // Arrange
    ProductRepository mockRepository = mock(ProductRepository.class);
    ProductService service = new ProductService(mockRepository);
    
    // Act
    service.createProduct("Test Product", "Description", new BigDecimal("10.00"));
    
    // Assert
    verify(mockRepository).save(any(Product.class));
}
```

**Testing without mocks:**
```java
@Test
void createProduct_ValidData_ProductCreated() {
    // Arrange
    ProductRepository repository = new InMemoryProductRepository();
    ProductService service = new ProductService(repository);
    
    // Act
    Product product = service.createProduct("Test Product", "Description", new BigDecimal("10.00"));
    
    // Assert
    Optional<Product> savedProduct = repository.findById(product.getId());
    assertTrue(savedProduct.isPresent());
    assertEquals("Test Product", savedProduct.get().getName());
}

// In-memory implementation
class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> products = new HashMap<>();
    
    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }
    
    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
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

### Benefits:
- Tests that accurately reflect production behavior
- Catching database-specific issues early
- No need to maintain separate test databases
- Consistent test environments across development machines

### Example:

```java
@Testcontainers
class ProductRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    private ProductRepository repository;
    
    @BeforeEach
    void setUp() {
        // Configure Spring datasource to use the container
        DataSource dataSource = DataSourceBuilder.create()
            .url(postgres.getJdbcUrl())
            .username(postgres.getUsername())
            .password(postgres.getPassword())
            .build();
        
        // Create repository with the configured datasource
        repository = new JdbcProductRepository(dataSource);
    }
    
    @Test
    void saveAndRetrieveProduct() {
        // Arrange
        Product product = new Product(UUID.randomUUID(), "Test Product", "Description", new BigDecimal("10.00"));
        
        // Act
        repository.save(product);
        Optional<Product> retrieved = repository.findById(product.getId());
        
        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Test Product", retrieved.get().getName());
    }
}
```

## Event Sourcing

### What is it?
Event Sourcing is a pattern where changes to the application state are stored as a sequence of events. Instead of storing the current state, the system records all events that led to that state and can rebuild the state by replaying those events.

### Key components:
- **Events**: Immutable records of something that happened in the system
- **Event Store**: Persistent storage for events
- **Aggregates**: Domain objects that handle commands and emit events
- **Projections**: Build read models from events for querying

### Benefits:
- Complete audit trail of all changes
- Ability to reconstruct past states
- Natural fit with CQRS
- Temporal queries (what was the state at a specific time)

### Simple Event Sourcing example:

```java
// Event
public class ProductCreatedEvent {
    private final UUID productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    // constructor, getters
}

// Aggregate
public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<Object> uncommittedEvents = new ArrayList<>();
    
    public static Product create(String name, String description, BigDecimal price) {
        Product product = new Product();
        UUID id = UUID.randomUUID();
        
        ProductCreatedEvent event = new ProductCreatedEvent(id, name, description, price);
        product.applyEvent(event);
        product.uncommittedEvents.add(event);
        
        return product;
    }
    
    private void applyEvent(ProductCreatedEvent event) {
        this.id = event.getProductId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.price = event.getPrice();
    }
    
    public List<Object> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }
    
    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }
}

// Event Store
public class EventStore {
    private final Map<UUID, List<Object>> eventStreams = new HashMap<>();
    
    public void saveEvents(UUID aggregateId, List<Object> events) {
        List<Object> eventStream = eventStreams.computeIfAbsent(aggregateId, k -> new ArrayList<>());
        eventStream.addAll(events);
    }
    
    public List<Object> getEvents(UUID aggregateId) {
        return eventStreams.getOrDefault(aggregateId, Collections.emptyList());
    }
}

// Command Handler
public class CreateProductCommandHandler {
    private final EventStore eventStore;
    
    public void handle(CreateProductCommand command) {
        Product product = Product.create(
            command.getName(),
            command.getDescription(),
            command.getPrice()
        );
        
        eventStore.saveEvents(product.getId(), product.getUncommittedEvents());
        product.clearUncommittedEvents();
    }
}
```

## Conclusion

These concepts form the foundation of modern software architecture and testing practices. By understanding and applying these concepts, you'll be able to build more maintainable, testable, and scalable applications.

Remember that these patterns are tools, not rules. Use them when they provide value to your specific context, and adapt them as needed to fit your requirements.