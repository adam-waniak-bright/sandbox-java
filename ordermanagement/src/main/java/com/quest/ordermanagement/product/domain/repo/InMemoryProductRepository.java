package com.quest.ordermanagement.product.domain.repo;

import com.quest.ordermanagement.product.domain.Product;
import java.util.List;

public class InMemoryProductRepository {
    public static List<Product> createProducts() {
        return List.of(
                new Product("PRD12345", "Product 1", "Description 1", 100L),
                new Product("PRD12346", "Product 2", "Description 2", 200L),
                new Product("PRD12347", "Product 3", "Description 3", 300L),
                new Product("PRD12348", "Product 4", "Description 4", 400L));
    }
}
