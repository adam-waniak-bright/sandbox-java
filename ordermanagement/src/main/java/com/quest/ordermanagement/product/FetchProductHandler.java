package com.quest.ordermanagement.product;

import static com.quest.ordermanagement.product.domain.repo.InMemoryProductRepository.createProducts;

import com.quest.ordermanagement.product.error.ProductNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FetchProductHandler {
    public void verifyProductExists(String productId) {
        createProducts().stream()
                .filter(product -> productId.equals(product.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }
}
