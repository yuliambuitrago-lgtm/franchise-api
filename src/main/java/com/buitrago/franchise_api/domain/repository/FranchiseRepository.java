package com.buitrago.franchise_api.domain.repository;

import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.model.ProductByBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Flux<Franchise> findAll();
    Mono<Franchise> update(Franchise franchise);
    Mono<ProductByBranch> findTopStockProductByBranch(String franchiseId, String branchId);
    Flux<ProductByBranch> findTopStockProductPerBranch(String franchiseId);
}