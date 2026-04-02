package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Product;
import com.buitrago.franchise_api.domain.model.ProductByBranch;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Product> addProduct(String franchiseId, String branchId, Product product) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    Product builtProduct = buildProduct(product);  // ← guardamos el producto con id
                    franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            .getProducts()
                            .add(builtProduct);
                    return franchiseRepository.update(franchise)
                            .thenReturn(builtProduct);  // ← retornamos el producto con id
                });
    }

    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            .getProducts()
                            .removeIf(p -> p.getId().equals(productId));
                    return franchiseRepository.update(franchise).then();
                });
    }

    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, int newStock) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    Product product = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            .getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    product.setStock(newStock);
                    return franchiseRepository.update(franchise)
                            .thenReturn(product);
                });
    }

    public Mono<Product> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    Product product = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            .getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    product.setName(newName);
                    return franchiseRepository.update(franchise)
                            .thenReturn(product);
                });
    }

    public Flux<ProductByBranch> getTopStockProductPerBranch(String franchiseId) {
        return franchiseRepository.findTopStockProductPerBranch(franchiseId);
    }

    private Product buildProduct(Product product) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(product.getName())
                .stock(product.getStock())
                .build();
    }
}