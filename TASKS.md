# Educational Tasks for Team Development

This document outlines a series of educational tasks designed to help the team understand and implement key software architecture and testing concepts:
- Vertical slices architecture
- CQRS (Command Query Responsibility Segregation)
- Proper class separation and single responsibility
- Testing without mocks
- Integration testing with test containers

## Task Progression

The tasks are organized in a logical progression, with each task building upon the knowledge and implementation from previous tasks.

## Task 1: Implement a Basic Product Catalog Feature Using Vertical Slices

**Objective:** Understand how to organize code by feature (vertical slice) rather than by technical layer.

**Description:**
Implement a simple product catalog feature that allows:
- Adding a new product
- Retrieving product details
- Listing all products

**Requirements:**
1. Create a vertical slice architecture where all code related to the product catalog feature is grouped together
2. Implement the following components within the product catalog slice:
   - API controllers
   - Domain models
   - Business logic
   - Data access

**Learning Outcomes:**
- Understanding how vertical slices differ from traditional layered architecture
- Recognizing the benefits of organizing code by feature
- Implementing cohesive, self-contained features

## Task 2: Refactor to CQRS Pattern

**Objective:** Learn how to separate read and write operations using the CQRS pattern.

**Description:**
Refactor the product catalog feature to implement CQRS:
- Create separate command and query models
- Implement command handlers for write operations
- Implement query handlers for read operations

**Requirements:**
1. Create command classes for:
   - CreateProductCommand
   - UpdateProductCommand
   - DeleteProductCommand
2. Create query classes for:
   - GetProductQuery
   - ListProductsQuery
3. Implement corresponding command and query handlers
4. Update the API controllers to use the command and query handlers

**Learning Outcomes:**
- Understanding the separation of concerns in CQRS
- Implementing command and query models
- Recognizing when and why to use CQRS

## Task 3: Implement Single Responsibility Classes

**Objective:** Learn how to design classes that have a single responsibility and are easily testable.

**Description:**
Refine the CQRS implementation by ensuring each class has a single responsibility:
- Extract validation logic into separate validator classes
- Create dedicated mappers for transforming between different models
- Implement service classes with focused responsibilities

**Requirements:**
1. Create validator classes for commands
2. Implement mapper classes for transforming between:
   - Command models and domain models
   - Domain models and response DTOs
3. Ensure each class has a clear, single responsibility

**Learning Outcomes:**
- Understanding the Single Responsibility Principle
- Recognizing the benefits of small, focused classes
- Implementing a design that's easy to extend and maintain

## Task 4: Implement Testing Without Mocks

**Objective:** Learn how to write tests that don't rely heavily on mocks.

**Description:**
Create tests for the product catalog feature that minimize the use of mocks:
- Use in-memory repositories for testing
- Implement test-specific implementations of interfaces
- Use real objects where possible

**Requirements:**
1. Create an in-memory implementation of the product repository
2. Write tests that use the in-memory repository instead of mocking
3. Implement tests for command and query handlers using real dependencies

**Learning Outcomes:**
- Understanding the drawbacks of excessive mocking
- Implementing more realistic tests
- Creating test-specific implementations that improve test reliability

## Task 5: Implement Integration Tests with Testcontainers

**Objective:** Learn how to write integration tests using Testcontainers for realistic testing environments.

**Description:**
Create integration tests that use Testcontainers to test the product catalog feature with a real database:
- Set up Testcontainers for a PostgreSQL database
- Implement repository tests against the containerized database
- Create end-to-end tests for the API endpoints

**Requirements:**
1. Add Testcontainers dependencies to the project
2. Configure a PostgreSQL container for testing
3. Implement repository tests that use the containerized database
4. Create end-to-end tests for the API endpoints

**Learning Outcomes:**
- Understanding the benefits of testing with real dependencies
- Implementing tests that closely resemble the production environment
- Using Testcontainers to create isolated, reproducible test environments

## Task 6: Implement Event Sourcing (Advanced)

**Objective:** Learn how to implement event sourcing as an extension of CQRS.

**Description:**
Extend the product catalog feature to use event sourcing:
- Store product events instead of current state
- Rebuild product state from events
- Implement event handlers for different event types

**Requirements:**
1. Create event classes for product-related events
2. Implement an event store
3. Update command handlers to emit events
4. Create projections that build read models from events

**Learning Outcomes:**
- Understanding the concept of event sourcing
- Implementing an event-driven architecture
- Recognizing the benefits and trade-offs of event sourcing

## Task 7: Create a New Feature Using All Concepts

**Objective:** Apply all learned concepts to implement a new feature from scratch.

**Description:**
Implement an order management feature that allows:
- Creating new orders
- Updating order status
- Retrieving order details
- Listing orders with filtering

**Requirements:**
1. Implement the feature using vertical slices architecture
2. Apply CQRS to separate read and write operations
3. Design classes with single responsibilities
4. Write tests without excessive mocking
5. Create integration tests with Testcontainers

**Learning Outcomes:**
- Applying all learned concepts in a cohesive manner
- Implementing a complete feature using best practices
- Understanding how different architectural patterns work together

## Conclusion

These tasks are designed to progressively build knowledge and skills in modern software architecture and testing practices. Each task builds upon the previous ones, allowing team members to gradually apply more advanced concepts as they become comfortable with the fundamentals.

The focus throughout is on creating maintainable, testable code that separates concerns appropriately and can be easily extended as requirements change.