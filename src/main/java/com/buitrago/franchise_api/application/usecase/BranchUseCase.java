package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Branch;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> addBranch(String franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    branch.setId(UUID.randomUUID().toString());
                    if (branch.getProducts() == null) {
                        branch.setProducts(new ArrayList<>());
                    }
                    if (franchise.getBranches() == null) {
                        franchise.setBranches(new ArrayList<>());
                    }
                    franchise.getBranches().add(branch);
                    return franchiseRepository.update(franchise)
                            .thenReturn(branch);
                });
    }

    public Mono<Branch> updateBranchName(String franchiseId, String branchId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            .setName(newName);
                    return franchiseRepository.update(franchise)
                            .flatMap(updated -> Mono.just(
                                    updated.getBranches().stream()
                                            .filter(b -> b.getId().equals(branchId))
                                            .findFirst()
                                            .orElseThrow(() -> new RuntimeException("Branch not found"))
                            ));
                });
    }
}