# Educational Tasks for Team Development - Order Management Domain

This document outlines a series of educational tasks designed to help the team understand and implement key software architecture and testing concepts:
- Vertical slices architecture
- CQRS (Command Query Responsibility Segregation)
- Proper class separation and single responsibility
- Testing without mocks
- Integration testing with test containers
- Value classes and domain/entity object separation

## Task Progression

The tasks are organized in a logical progression, with each task building upon the knowledge and implementation from previous tasks.

## Task 1: Implement a Basic Order Management Feature Using Vertical Slices

[Task 1](./Task1.md)

## Task 2: Refactor to CQRS Pattern

**Objective:** Learn how to separate read and write operations using the CQRS pattern.

**Description:**
Refactor the order management feature to implement CQRS with enhanced business logic.

**Requirements:**
1. Create command classes for:
   - CreateOrderCommand
   - UpdateOrderStatusCommand
   - AddOrderItemCommand
   - RemoveOrderItemCommand
   - ApplyDiscountCommand
2. Create query classes for:
   - GetOrderQuery
   - ListOrdersQuery
   - GetOrdersByCustomerQuery
   - GetOrdersByStatusQuery
   - GetOrdersByDateRangeQuery
3. Implement corresponding command and query handlers

**Enhanced Business Logic:**

**Discount Application Rules:**
- Discount codes are case-insensitive alphanumeric strings (6-12 characters)
- Each discount code can only be used once per customer
- Discount types and calculations:
   - PERCENTAGE: Discount = Subtotal × (DiscountPercentage / 100), max $100.00
   - FIXED_AMOUNT: Discount = DiscountAmount, cannot exceed Subtotal
   - FREE_SHIPPING: Shipping cost becomes $0.00, no other discount applied
- Discounts cannot be applied to orders with status SHIPPED or DELIVERED
- Valid discount codes with exact rules:
   - "WELCOME10": 10% off, valid for customers with 0 previous orders
   - "SAVE5": $5.00 off, valid for all customers
   - "FREESHIP": Free shipping, valid for orders with subtotal $25-$49.99


**Learning Outcomes:**
- Understanding the separation of concerns in CQRS
- Implementing command and query models with precise business logic
- Recognizing when and why to use CQRS in business-heavy domains

## Task 3: Implement Single Responsibility Classes

**Objective:** Learn how to design classes that have a single responsibility and are easily testable.

**Requirements:**
1. Create validator classes with exact validation rules
2. Implement mapper classes for transforming between different models
3. Create focused service classes with precisely defined responsibilities

**Precisely Defined Service Classes:**

**OrderCalculationService:**
```
CalculateSubtotal(List<OrderItem> items):
  return Sum of (item.Quantity × item.UnitPrice)

CalculateTax(decimal subtotal):
  return subtotal × 0.08

CalculateShipping(decimal subtotal):
  if subtotal < 50.00 then return 5.00
  else return 0.00

ApplyDiscount(decimal subtotal, string discountCode, int customerOrderCount):
  switch discountCode.ToUpper():
    case "WELCOME10":
      if customerOrderCount > 0 then throw InvalidOperationException
      return Math.Min(subtotal × 0.10, 100.00)
    case "SAVE5":
      return Math.Min(5.00, subtotal)
    case "FREESHIP":
      if subtotal < 25.00 or subtotal >= 50.00 then throw InvalidOperationException
      return 0.00 // This affects shipping, not subtotal
    default:
      throw ArgumentException("Invalid discount code")
```

**OrderStatusValidator:**
```
ValidateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus):
  valid transitions:
    DRAFT → CONFIRMED, CANCELLED
    CONFIRMED → SHIPPED, CANCELLED
    SHIPPED → DELIVERED, CANCELLED
    DELIVERED → (none)
    CANCELLED → (none)
  
  if transition not in valid transitions then
    throw InvalidStatusTransitionException
```

**Learning Outcomes:**
- Understanding the Single Responsibility Principle with concrete examples
- Implementing precise business rules in focused classes
- Creating testable, maintainable service classes

## Task 4: Implement Testing Without Mocks

**Objective:** Learn how to write tests that don't rely heavily on mocks.

**Requirements:**
1. Create in-memory implementations for repositories
2. Write tests using real business logic classes
3. Implement comprehensive test scenarios

**Precisely Defined Test Scenarios:**

**OrderCalculationService Tests:**
```
Test Case 1: Basic Calculation
  Items: [{ quantity: 2, unitPrice: 10.00 }, { quantity: 1, unitPrice: 15.00 }]
  Expected: Subtotal = 35.00, Tax = 2.80, Shipping = 5.00, Total = 42.80

Test Case 2: Free Shipping Threshold
  Items: [{ quantity: 5, unitPrice: 10.00 }]
  Expected: Subtotal = 50.00, Tax = 4.00, Shipping = 0.00, Total = 54.00

Test Case 3: WELCOME10 Discount
  Subtotal: 100.00, Customer Order Count: 0, Discount Code: "WELCOME10"
  Expected: Discount = 10.00, Total after discount = 97.80

Test Case 4: WELCOME10 Invalid for Existing Customer
  Subtotal: 100.00, Customer Order Count: 3, Discount Code: "WELCOME10"
  Expected: InvalidOperationException

Test Case 5: FREESHIP Discount Invalid Range
  Subtotal: 20.00, Discount Code: "FREESHIP"
  Expected: InvalidOperationException
```

**Order Status Transition Tests:**
```
Test Case 1: Valid Transitions
  DRAFT → CONFIRMED: Should succeed
  CONFIRMED → SHIPPED: Should succeed
  SHIPPED → DELIVERED: Should succeed

Test Case 2: Invalid Transitions
  DELIVERED → SHIPPED: Should throw InvalidStatusTransitionException
  CANCELLED → CONFIRMED: Should throw InvalidStatusTransitionException

Test Case 3: Cancellation from Any State
  DRAFT → CANCELLED: Should succeed
  CONFIRMED → CANCELLED: Should succeed and release inventory
  SHIPPED → CANCELLED: Should succeed and release inventory
```

**Learning Outcomes:**
- Understanding how to test business logic without extensive mocking
- Implementing comprehensive test scenarios with precise expectations
- Creating reliable tests that validate actual business behavior

## Task 5: Implement Integration Tests with Testcontainers

**Objective:** Learn how to write integration tests using Testcontainers for realistic testing environments.

**Requirements:**
1. Set up Testcontainers for PostgreSQL
2. Implement end-to-end workflow tests
3. Test complex business scenarios with real data persistence

**Precisely Defined Integration Test Scenarios:**

**Complete Order Lifecycle Test:**
```
1. Create order with items: ProductA (qty: 2), ProductB (qty: 1)
2. Verify order status = DRAFT
3. Confirm order (DRAFT → CONFIRMED)
4. Ship order (CONFIRMED → SHIPPED)
5. Deliver order (SHIPPED → DELIVERED)
6. Verify order completed
```

**Discount Application Integration Test:**
```
1.. Create order with subtotal $100.00
2. Apply "WELCOME10" discount
3. Verify final total = $102.80 (subtotal: $90.00, tax: $7.20, shipping: $5.00)
4. Complete order
5. Create second order, attempt "WELCOME10" again
6. Verify discount application fails for existing customer
```

**Learning Outcomes:**
- Testing complete business workflows with real database persistence
- Validating complex business rule interactions
- Ensuring data consistency across service boundaries

## Task 6: Implement Value Classes and Domain/Entity Object Separation

**Objective:** Learn how to create value classes and separate domain objects from entity objects.

**Requirements:**
1. Create immutable value classes with precise behavior
2. Separate rich domain models from persistence entities
3. Implement exact mapping logic

**Precisely Defined Value Classes:**

**Money Value Class:**
```
Money:
  Properties: Amount (decimal), Currency (string)
  
  Validation:
    Amount must be >= 0.00
    Amount must have maximum 2 decimal places
    Currency must be 3-character ISO code (USD, EUR, etc.)
  
  Operations:
    Add(Money other): Must have same currency, return new Money
    Subtract(Money other): Must have same currency, result >= 0.00
    Multiply(decimal factor): factor must be > 0, return new Money
    
  Formatting:
    ToString(): "$X.XX" for USD, "€X.XX" for EUR
```

**OrderStatus Value Class:**
```
OrderStatus:
  Valid Values: DRAFT, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
  
  Methods:
    CanTransitionTo(OrderStatus newStatus): 
      Implements exact transition rules from Task 3
    
    GetNextValidStatuses(): 
      Returns list of valid next statuses
    
    IsTerminal(): 
      Returns true for DELIVERED and CANCELLED
```

**OrderId Value Class:**
```
OrderId:
  Properties: Value (Guid)
  
  Validation:
    Value cannot be Guid.Empty
  
  Methods:
    ToString(): Returns Value.ToString()
    Equals(): Value-based equality
    
  Static Methods:
    NewOrderId(): Generates new Guid
    FromString(string): Parses Guid from string
```

**CustomerId Value Class:**
```
CustomerId:
  Properties: Value (string)
  
  Validation:
    Value must be 8-12 alphanumeric characters
    Must start with "CUST"
    
  Example valid IDs: "CUST1234", "CUST567890AB"
```

**ProductId Value Class:**
```
ProductId:
  Properties: Value (string)
  
  Validation:
    Value must be exactly 8 characters
    Format: "PRD" + 5 digits
    
  Example valid IDs: "PRD00001", "PRD99999"
```

**Domain Model with Business Logic:**
```
Order Domain Model:
  Properties:
    OrderId Id
    CustomerId CustomerId  
    List<OrderItem> Items
    OrderStatus Status
    Money Subtotal
    Money Tax
    Money Shipping
    Money Discount
    Money Total
    DateTime CreatedAt
    DateTime? ConfirmedAt
    DateTime? ShippedAt
    DateTime? DeliveredAt
  
  Business Methods:
    AddItem(ProductId productId, int quantity, Money unitPrice):
      Validates quantity (1-99) and price ($0.01-$9999.99)
      Prevents duplicate products
      Recalculates totals
      
    RemoveItem(ProductId productId):
      Removes item and recalculates totals
      
    UpdateStatus(OrderStatus newStatus):
      Validates transition using OrderStatus.CanTransitionTo()
      Updates timestamp fields
      
    ApplyDiscount(string discountCode, int customerOrderCount):
      Uses OrderCalculationService logic
      Updates discount and total
      
    CalculateTotals():
      Recalculates all totals using precise formulas from Task 1
      
  Invariants:
    Items.Count must be 1-20
    Total must equal Subtotal + Tax + Shipping - Discount
    Status transitions must be valid
    Timestamps must be consistent with status
```

**Entity Classes for Persistence:**
```
OrderEntity:
  Id (Guid)
  CustomerId (string)
  Status (string)
  SubtotalAmount (Integer)
  SubtotalCurrency (string)
  TaxAmount (decimal)
  ShippingAmount (Integer)
  DiscountAmount (Integer)
  TotalAmount (Integer)
  CreatedAt (DateTime)
  ConfirmedAt (DateTime?)
  ShippedAt (DateTime?)
  DeliveredAt (DateTime?)
  
OrderItemEntity:
  Id (Guid)
  OrderId (Guid)
  ProductId (string)
  Quantity (int)
  UnitPriceAmount (integer)
  UnitPriceCurrency (string)
```

**Mapping Rules:**
```
Domain to Entity:
  Money objects → split into Amount/Currency fields
  Value objects → unwrap to primitive values
  Collections → separate entity tables
  
Entity to Domain:
  Combine Amount/Currency → Money objects
  Validate and wrap primitives → Value objects
  Load related entities → populate collections
  Apply business rules during reconstruction
```

**Learning Outcomes:**
- Creating value objects with precise validation and behavior
- Implementing immutable domain models with rich business logic
- Separating domain concerns from persistence concerns
- Understanding the complexity of object-relational mapping

## Task 7: Create a Subscription Management Feature Using All Concepts

**Objective:** Apply all learned concepts to implement a new feature from scratch.

**Description:**
Implement a subscription management feature with precisely defined business rules.

**Requirements:**
Apply all previous concepts to implement subscription management with these exact specifications:

**Subscription Business Rules:**

**Billing Cycles:**
- MONTHLY: Bills every 30 days
- QUARTERLY: Bills every 90 days
- ANNUAL: Bills every 365 days

**Subscription Status Flow:**
- PENDING → ACTIVE (after first successful payment)
- ACTIVE → PAUSED (customer request, max 90 days)
- PAUSED → ACTIVE (customer request)
- ACTIVE → CANCELLED (customer request)
- PENDING → CANCELLED (failed payment after 3 attempts)
- CANCELLED cannot transition to any other status

**Pricing Rules:**
```
Base Price Calculation:
  Monthly: $29.99
  Quarterly: $79.99 (11% discount from 3×monthly)
  Annual: $299.99 (17% discount from 12×monthly)

Loyalty Discount:
  After 12 consecutive months: 5% discount
  After 24 consecutive months: 10% discount
  After 36 consecutive months: 15% discount (maximum)

Proration for Changes:
  Upgrade (Monthly→Quarterly): Immediate charge for difference
  Downgrade (Annual→Monthly): Credit applied to future bills
  
  Formula: 
    DaysRemaining = DaysInCurrentPeriod - DaysUsed
    ProrationAmount = (NewPrice - OldPrice) × (DaysRemaining / DaysInCurrentPeriod)
```

**Payment Processing Rules:**
```
Payment Retry Logic:
  Attempt 1: Immediate
  Attempt 2: After 3 days
  Attempt 3: After 7 days
  After 3 failures: Cancel subscription

Failed Payment Handling:
  Send notification after each failed attempt
  Pause subscription after 2nd failure
  Cancel subscription after 3rd failure
```

**Subscription Modification Rules:**
```
Pause Rules:
  Can pause ACTIVE subscriptions only
  Maximum pause duration: 90 days
  Automatic resume after 90 days
  Billing date shifts by pause duration
  
Cancel Rules:
  Can cancel ACTIVE or PAUSED subscriptions
  Immediate cancellation: No refund, service until period end
  Scheduled cancellation: Set to cancel at period end
  
Upgrade/Downgrade Rules:
  Changes take effect immediately with proration
  New billing cycle starts from change date
  Cannot change billing cycle if subscription ends within 30 days
```

**Value Classes to Implement:**
```
SubscriptionId: "SUB" + 8 digits
BillingCycle: MONTHLY, QUARTERLY, ANNUAL with day calculations
SubscriptionStatus: With transition validation
Money: Enhanced with proration calculations
PaymentMethod: Credit card with validation
```

**Command/Query Implementation:**
- CreateSubscriptionCommand
- PauseSubscriptionCommand
- ResumeSubscriptionCommand
- CancelSubscriptionCommand
- ChangeSubscriptionPlanCommand
- ProcessSubscriptionPaymentCommand
- GetActiveSubscriptionsQuery
- GetSubscriptionPaymentHistoryQuery

**Integration Test Scenarios:**
```
Scenario 1: Complete Subscription Lifecycle
  1. Create monthly subscription ($29.99)
  2. Process first payment successfully → ACTIVE
  3. Wait 30 days, process renewal payment
  4. Upgrade to quarterly with proration
  5. Pause subscription for 30 days
  6. Resume subscription
  7. Cancel subscription

Scenario 2: Failed Payment Handling
  1. Create subscription with failing payment method
  2. Verify 3 payment attempts with correct timing
  3. Verify subscription cancelled after 3rd failure
  4. Verify customer notifications sent

Scenario 3: Loyalty Discount Application
  1. Create subscription, maintain for 12 months
  2. Verify 5% discount applied on 13th month
  3. Continue to 24 months, verify 10% discount
  4. Pause subscription, verify loyalty preserved
```

**Learning Outcomes:**
- Implementing complex business workflows with precise rules
- Managing time-based business logic and recurring processes
- Applying all architectural patterns cohesively
- Handling real-world subscription business complexity

## Conclusion

These tasks provide precisely defined business rules that eliminate ambiguity while teaching modern software architecture patterns. Each rule includes exact calculations, validation criteria, and expected behaviors that students can implement and test definitively.

The progression from simple order management to complex subscription handling demonstrates how architectural patterns scale with business complexity, while the precise specifications ensure consistent implementation and reliable testing.


Ideas for next quests:

Observability (terraform, actuator)
CDKTF usage
Containerization (docker)
Github workflows
dependency injection