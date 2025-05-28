# Sandbox Java Project

## Overview

This is an educational sandbox project designed to help team members improve their skills in modern software architecture and testing practices. The project serves as a playground for implementing and experimenting with concepts such as:

- Vertical slices architecture
- CQRS (Command Query Responsibility Segregation)
- Proper class separation and single responsibility
- Testing without mocks
- Integration testing with test containers
- Value classes and domain/entity object separation

## Purpose

The primary purpose of this project is educational. It provides a structured environment where team members can:

1. Learn about advanced architectural patterns and testing strategies
2. Implement these concepts in a real Spring Boot application
3. Experiment with different approaches without affecting production code
4. Develop skills that can be applied to production projects

## Getting Started

### Prerequisites

- Java 21
- Gradle
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Setup

1. Clone this repository
2. Open the project in your IDE
3. Run `./gradlew build` to ensure everything is working correctly

## Educational Tasks

The file [TASKS.md](TASKS.md) contains a series of educational tasks designed to progressively build knowledge and skills. Each task:

- Has clear objectives and learning outcomes
- Builds upon previous tasks
- Focuses on specific architectural or testing concepts
- Includes detailed requirements

## Project Structure

This is a standard Spring Boot application with the following structure:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── acti/
│   │           └── sandboxjava/
│   │               └── SandboxJavaApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/
            └── acti/
                └── sandboxjava/
                    └── SandboxJavaApplicationTests.java
```

As you work through the tasks, you'll expand this structure to implement various features and architectural patterns.

## Resources

Here are some resources that may be helpful when working on the tasks:

- [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html) by Martin Fowler
- [Vertical Slice Architecture](https://jimmybogard.com/vertical-slice-architecture/) by Jimmy Bogard
- [Testcontainers for Java](https://www.testcontainers.org/)
- [Testing Without Mocks](https://www.jamesshore.com/v2/blog/2018/testing-without-mocks) by James Shore
- [Value Objects and Domain-Driven Design](https://martinfowler.com/bliki/ValueObject.html) by Martin Fowler
