package com.acti.sandboxjava.domain;

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