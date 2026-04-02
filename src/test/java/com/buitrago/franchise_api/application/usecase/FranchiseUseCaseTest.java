package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id("franchise-1")
                .name("Franquicia Test")
                .branches(new ArrayList<>())
                .build();
    }

    @Test
    void createFranchise_shouldReturnSavedFranchise() {
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.createFranchise(franchise))
                .expectNextMatches(f -> f.getName().equals("Franquicia Test"))
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_shouldReturnUpdatedFranchise() {
        Franchise updated = Franchise.builder()
                .id("franchise-1")
                .name("Nuevo Nombre")
                .branches(new ArrayList<>())
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseUseCase.updateFranchiseName("franchise-1", "Nuevo Nombre"))
                .expectNextMatches(f -> f.getName().equals("Nuevo Nombre"))
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_whenFranchiseNotFound_shouldReturnError() {
        when(franchiseRepository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateFranchiseName("invalid-id", "Nuevo Nombre"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Franchise not found"))
                .verify();
    }
}