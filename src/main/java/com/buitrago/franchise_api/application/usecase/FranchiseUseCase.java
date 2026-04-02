package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }

    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .flatMap(franchise -> {
                    franchise.setName(newName);
                    return franchiseRepository.update(franchise);
                });
    }
}