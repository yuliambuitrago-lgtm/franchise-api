package com.buitrago.franchise_api.infrastructure.persistence.repository;

import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.model.Product;
import com.buitrago.franchise_api.domain.model.ProductByBranch;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import com.buitrago.franchise_api.infrastructure.persistence.document.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryImpl implements FranchiseRepository {

    private final FranchiseMongoRepository mongoRepository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        franchise.setId(UUID.randomUUID().toString());
        if (franchise.getBranches() != null) {
            franchise.getBranches().forEach(branch -> {
                branch.setId(UUID.randomUUID().toString());
                if (branch.getProducts() != null) {
                    branch.getProducts().forEach(product ->
                            product.setId(UUID.randomUUID().toString()));
                }
            });
        }
        return mongoRepository.save(FranchiseMapper.toDocument(franchise))
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepository.findById(id)
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return mongoRepository.findAll()
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return mongoRepository.save(FranchiseMapper.toDocument(franchise))
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<ProductByBranch> findTopStockProductByBranch(String franchiseId, String branchId) {
        return findById(franchiseId)
                .flatMap(franchise -> Flux.fromIterable(franchise.getBranches())
                        .filter(branch -> branch.getId().equals(branchId))
                        .next()
                        .flatMap(branch -> Flux.fromIterable(branch.getProducts())
                                .sort(Comparator.comparingInt(Product::getStock).reversed())
                                .next()
                                .map(product -> ProductByBranch.builder()
                                        .branchName(branch.getName())
                                        .product(product)
                                        .build())));
    }

    @Override
    public Flux<ProductByBranch> findTopStockProductPerBranch(String franchiseId) {
        return findById(franchiseId)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches())
                        .flatMap(branch -> Flux.fromIterable(branch.getProducts())
                                .sort(Comparator.comparingInt(Product::getStock).reversed())
                                .next()
                                .map(product -> ProductByBranch.builder()
                                        .branchName(branch.getName())
                                        .product(product)
                                        .build())));
    }
}