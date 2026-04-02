package com.buitrago.franchise_api.infrastructure.web.handler;

import com.buitrago.franchise_api.application.usecase.ProductUseCase;
import com.buitrago.franchise_api.domain.model.Product;
import com.buitrago.franchise_api.infrastructure.web.dto.ProductRequest;
import com.buitrago.franchise_api.infrastructure.web.dto.UpdateNameRequest;
import com.buitrago.franchise_api.infrastructure.web.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .flatMap(dto -> productUseCase.addProduct(franchiseId, branchId,
                        Product.builder()
                                .name(dto.getName())
                                .stock(dto.getStock())
                                .build()
                ))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return productUseCase.deleteProduct(franchiseId, branchId, productId)
                .then(ServerResponse.noContent().build())
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> productUseCase.updateStock(franchiseId, branchId, productId, dto.getStock()))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> productUseCase.updateProductName(franchiseId, branchId, productId, dto.getName()))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> getTopStockProductPerBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return productUseCase.getTopStockProductPerBranch(franchiseId)
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products))
                .onErrorResume(e -> resolveError(e));
    }

    private Mono<ServerResponse> resolveError(Throwable e) {
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return ServerResponse.notFound().build();
        }
        return ServerResponse.badRequest().bodyValue(e.getMessage());
    }
}