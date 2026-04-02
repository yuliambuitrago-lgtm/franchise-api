package com.buitrago.franchise_api.infrastructure.web.router;

import com.buitrago.franchise_api.infrastructure.web.handler.BranchHandler;
import com.buitrago.franchise_api.infrastructure.web.handler.FranchiseHandler;
import com.buitrago.franchise_api.infrastructure.web.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(
            FranchiseHandler franchiseHandler,
            BranchHandler branchHandler,
            ProductHandler productHandler) {

        return RouterFunctions.route()

                // Franchise endpoints
                .POST("/api/franchises", franchiseHandler::createFranchise)
                .PATCH("/api/franchises/{franchiseId}/name", franchiseHandler::updateFranchiseName)

                // Branch endpoints
                .POST("/api/franchises/{franchiseId}/branches", branchHandler::addBranch)
                .PATCH("/api/franchises/{franchiseId}/branches/{branchId}/name", branchHandler::updateBranchName)

                // Product endpoints
                .POST("/api/franchises/{franchiseId}/branches/{branchId}/products", productHandler::addProduct)
                .DELETE("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}", productHandler::deleteProduct)
                .PATCH("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock", productHandler::updateStock)
                .PATCH("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name", productHandler::updateProductName)

                // Top stock endpoint
                .GET("/api/franchises/{franchiseId}/top-stock", productHandler::getTopStockProductPerBranch)

                .build();
    }
}