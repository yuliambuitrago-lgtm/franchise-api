package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Branch;
import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.model.Product;
import com.buitrago.franchise_api.domain.model.ProductByBranch;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("product-1")
                .name("Producto A")
                .stock(100)
                .build();

        branch = Branch.builder()
                .id("branch-1")
                .name("Sucursal Norte")
                .products(new ArrayList<>(List.of(product)))
                .build();

        franchise = Franchise.builder()
                .id("franchise-1")
                .name("Franquicia Test")
                .branches(new ArrayList<>(List.of(branch)))
                .build();
    }

    @Test
    void addProduct_shouldReturnProductWithId() {
        Product newProduct = Product.builder()
                .name("Producto B")
                .stock(50)
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(productUseCase.addProduct("franchise-1", "branch-1", newProduct))
                .expectNextMatches(p -> p.getId() != null && p.getName().equals("Producto B"))
                .verifyComplete();
    }

    @Test
    void addProduct_whenFranchiseNotFound_shouldReturnError() {
        when(franchiseRepository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.addProduct("invalid-id", "branch-1", product))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Franchise not found"))
                .verify();
    }

    @Test
    void deleteProduct_shouldCompleteSuccessfully() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(productUseCase.deleteProduct("franchise-1", "branch-1", "product-1"))
                .verifyComplete();
    }

    @Test
    void updateStock_shouldReturnProductWithNewStock() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(productUseCase.updateStock("franchise-1", "branch-1", "product-1", 999))
                .expectNextMatches(p -> p.getStock() == 999)
                .verifyComplete();
    }

    @Test
    void updateProductName_shouldReturnProductWithNewName() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(productUseCase.updateProductName("franchise-1", "branch-1", "product-1", "Nuevo Nombre"))
                .expectNextMatches(p -> p.getName().equals("Nuevo Nombre"))
                .verifyComplete();
    }

    @Test
    void getTopStockProductPerBranch_shouldReturnTopProducts() {
        Product topProductDetail = Product.builder()
                .id("product-1")
                .name("Producto A")
                .stock(100)
                .build();

        ProductByBranch topProduct = ProductByBranch.builder()
                .branchName("Sucursal Norte")
                .product(topProductDetail)
                .build();

        when(franchiseRepository.findTopStockProductPerBranch("franchise-1"))
                .thenReturn(Flux.just(topProduct));

        StepVerifier.create(productUseCase.getTopStockProductPerBranch("franchise-1"))
                .expectNextMatches(p -> p.getBranchName().equals("Sucursal Norte") &&
                        p.getProduct().getStock() == 100)
                .verifyComplete();
    }
}