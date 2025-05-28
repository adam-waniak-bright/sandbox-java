# Task 1: Implement a Basic Order Management Feature Using Vertical Slices

**Objective:** Understand how to organize code by feature (vertical slice) rather than by technical layer.

**Description:**
Implement a simple order management feature that allows:
- Creating a new order with order items
- Retrieving order details
- Listing all orders
- Updating order status

**Requirements:**
1. Create a vertical slice architecture where all code related to the order management feature is grouped together
2. Implement the following components within the order management slice:
    - API controllers
    - Domain models (Order, OrderItem, Customer)
    - Business logic (order validation, total calculation)
    - Data access

## Required Domain Model Properties

### Order Properties
```java
public class Order {
    private String id;
    private String customerId;  // Format: "CUST" + 4-8 alphanumeric
    private OrderStatus status;
    private List<OrderItem> items;
    private Long subtotalAmount;  // Amount in cents (e.g., $10.50 = 1050)
    private Long taxAmount;       // Tax amount in cents
    private Long shippingAmount;  // Shipping amount in cents
    private Long totalAmount;     // Total amount in cents
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    
    // Business logic constraints
    public static final int MIN_ITEMS = 1;
    public static final int MAX_ITEMS = 20;
    public static final long MIN_TOTAL_CENTS = 100L;  // $1.00
    public static final double TAX_RATE = 0.08;
    public static final long SHIPPING_THRESHOLD_CENTS = 5000L;  // $50.00
    public static final long SHIPPING_COST_CENTS = 500L;        // $5.00
    
    // Constructors, getters, setters...
}
```

### OrderItem Properties
```java
public class OrderItem {
    private String id;
    private String orderId;
    private String productId;    // Format: "PRD" + 5 digits
    private String productName;
    private Integer quantity;
    private Long unitPriceCents; // Unit price in cents
    private Long lineTotalCents; // Quantity × UnitPrice in cents
    
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 99;
    public static final long MIN_UNIT_PRICE_CENTS = 1L;     // $0.01
    public static final long MAX_UNIT_PRICE_CENTS = 999999L; // $9999.99
    
}
```

### Enums
```java
public enum OrderStatus {
    DRAFT,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

public enum CustomerStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}
```

## Business Logic

### Order Status Flow
- **Valid Transitions:**
    - DRAFT → CONFIRMED, CANCELLED
    - CONFIRMED → SHIPPED, CANCELLED
    - SHIPPED → DELIVERED, CANCELLED
    - DELIVERED → (none - terminal state)
    - CANCELLED → (none - terminal state)

### Order Total Calculation
```
1. Calculate Line Totals: OrderItem.lineTotalCents = quantity × unitPriceCents
2. Calculate Subtotal: Sum of all OrderItem.lineTotalCents
3. Calculate Tax: Math.round(subtotalCents × 0.08) (8% tax rate, rounded to nearest cent)
4. Calculate Shipping: 
   - 500 cents ($5.00) if subtotalCents < 5000 cents ($50.00)
   - 0 cents if subtotalCents >= 5000 cents ($50.00)
5. Calculate Total: subtotalCents + taxCents + shippingCents

Note: All monetary amounts are stored as Long values in cents to avoid 
floating-point precision issues (e.g., $10.50 is stored as 1050L)
```

### Order Validation Rules
1. **Item Count:** Order must have 1-20 order items
2. **Item Quantity:** Each item quantity must be 1-99
3. **Item Price:** Each item unit price must be 1-999999 cents ($0.01-$9999.99)
4. **Customer Status:** Customer must exist and have ACTIVE status
5. **Order Total:** Final total must be at least 100 cents ($1.00)
6. **Product Validation:** ProductId must exist and follow format "PRD" + 5 digits
7. **Duplicate Products:** Cannot have duplicate ProductIds in same order
8. **Monetary Precision:** All amounts must be in cents (Long values) to ensure precision

## API Specification (OpenAPI 3.0)

```yaml
openapi: 3.0.3
info:
  title: Order Management API
  description: RESTful API for managing orders in vertical slice architecture
  version: 1.0.0
  
servers:
  - url: https://api.example.com/v1
    description: Production server
  - url: https://api-staging.example.com/v1
    description: Staging server

paths:
  /orders:
    post:
      summary: Create a new order
      description: Creates a new order in DRAFT status
      operationId: createOrder
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequest'
            example:
              customerId: "CUST1234"
              items:
                - productId: "PRD00001"
                  productName: "Wireless Headphones"
                  quantity: 2
                  unitPriceCents: 7999
                - productId: "PRD00002"
                  productName: "USB Cable"
                  quantity: 1
                  unitPriceCents: 1299
      responses:
        '201':
          description: Order created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderResponse'
        '400':
          description: Invalid request data
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Customer not found or inactive
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
    
    get:
      summary: List all orders
      description: Retrieve a paginated list of orders with optional filtering
      operationId: listOrders
      parameters:
        - name: customerId
          in: query
          description: Filter by customer ID
          required: false
          schema:
            type: string
            pattern: '^CUST[A-Za-z0-9]{4,8}$'
        - name: status
          in: query
          description: Filter by order status
          required: false
          schema:
            $ref: '#/components/schemas/OrderStatus'
        - name: page
          in: query
          description: Page number (1-based)
          required: false
          schema:
            type: integer
            minimum: 1
            default: 1
        - name: limit
          in: query
          description: Number of items per page
          required: false
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
      responses:
        '200':
          description: List of orders
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderListResponse'
        '400':
          description: Invalid query parameters
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /orders/{orderId}:
    get:
      summary: Get order details
      description: Retrieve detailed information about a specific order
      operationId: getOrder
      parameters:
        - name: orderId
          in: path
          required: true
          description: Unique identifier for the order
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Order details
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderResponse'
        '404':
          description: Order not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
    
    patch:
      summary: Update order status
      description: Update the status of an existing order
      operationId: updateOrderStatus
      parameters:
        - name: orderId
          in: path
          required: true
          description: Unique identifier for the order
          schema:
            type: string
            format: uuid
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateOrderStatusRequest'
            example:
              status: "CONFIRMED"
      responses:
        '200':
          description: Order status updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderResponse'
        '400':
          description: Invalid status transition
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Order not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  schemas:
    CreateOrderRequest:
      type: object
      required:
        - customerId
        - items
      properties:
        customerId:
          type: string
          pattern: '^CUST[A-Za-z0-9]{4,8}$'
          description: Customer identifier
          example: "CUST1234"
        items:
          type: array
          minItems: 1
          maxItems: 20
          items:
            $ref: '#/components/schemas/CreateOrderItemRequest'
          description: List of items to include in the order

    CreateOrderItemRequest:
      type: object
      required:
        - productId
        - productName
        - quantity
        - unitPriceCents
      properties:
        productId:
          type: string
          pattern: '^PRD[0-9]{5}

    UpdateOrderStatusRequest:
      type: object
      required:
        - status
      properties:
        status:
          $ref: '#/components/schemas/OrderStatus'

    OrderResponse:
      type: object
      properties:
        id:
          type: string
          format: uuid
          description: Unique identifier for the order
        customerId:
          type: string
          pattern: '^CUST[A-Za-z0-9]{4,8}$'
          description: Customer identifier
        status:
          $ref: '#/components/schemas/OrderStatus'
        items:
          type: array
          items:
            $ref: '#/components/schemas/OrderItemResponse'
        subtotalCents:
          type: integer
          format: int64
          minimum: 0
          description: Subtotal in cents before tax and shipping
        taxCents:
          type: integer
          format: int64
          minimum: 0
          description: Tax amount in cents (8% of subtotal)
        shippingCents:
          type: integer
          format: int64
          minimum: 0
          description: Shipping cost in cents (500 cents or 0 cents)
        totalCents:
          type: integer
          format: int64
          minimum: 100
          description: Total amount in cents (subtotal + tax + shipping)
        createdAt:
          type: string
          format: date-time
          description: Order creation timestamp
        confirmedAt:
          type: string
          format: date-time
          nullable: true
          description: Order confirmation timestamp
        shippedAt:
          type: string
          format: date-time
          nullable: true
          description: Order shipment timestamp
        deliveredAt:
          type: string
          format: date-time
          nullable: true
          description: Order delivery timestamp
        cancelledAt:
          type: string
          format: date-time
          nullable: true
          description: Order cancellation timestamp

    OrderItemResponse:
      type: object
      properties:
        id:
          type: string
          format: uuid
          description: Unique identifier for the order item
        productId:
          type: string
          pattern: '^PRD[0-9]{5}

    OrderListResponse:
      type: object
      properties:
        orders:
          type: array
          items:
            $ref: '#/components/schemas/OrderResponse'
        pagination:
          $ref: '#/components/schemas/PaginationResponse'

    PaginationResponse:
      type: object
      properties:
        page:
          type: integer
          minimum: 1
          description: Current page number
        limit:
          type: integer
          minimum: 1
          description: Items per page
        totalItems:
          type: integer
          minimum: 0
          description: Total number of items
        totalPages:
          type: integer
          minimum: 0
          description: Total number of pages
        hasNext:
          type: boolean
          description: Whether there are more pages
        hasPrevious:
          type: boolean
          description: Whether there are previous pages

    OrderStatus:
      type: string
      enum:
        - DRAFT
        - CONFIRMED
        - SHIPPED
        - DELIVERED
        - CANCELLED
      description: Current status of the order

    ErrorResponse:
      type: object
      required:
        - error
        - message
      properties:
        error:
          type: string
          description: Error code
          example: "INVALID_REQUEST"
        message:
          type: string
          description: Human-readable error message
          example: "Order must have at least 1 item"
        details:
          type: object
          description: Additional error details
          additionalProperties: true
        timestamp:
          type: string
          format: date-time
          description: Error occurrence timestamp
```

## Learning Outcomes
- Understanding how vertical slices differ from traditional layered architecture
- Recognizing the benefits of organizing code by feature
- Implementing cohesive, self-contained features with precise business rules
- Designing RESTful APIs with proper validation and error handling
- Creating domain models that enforce business constraints