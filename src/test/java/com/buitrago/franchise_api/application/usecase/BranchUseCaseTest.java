package com.buitrago.franchise_api.application.usecase;

import com.buitrago.franchise_api.domain.model.Branch;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    private Franchise franchise;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id("branch-1")
                .name("Sucursal Norte")
                .products(new ArrayList<>())
                .build();

        franchise = Franchise.builder()
                .id("franchise-1")
                .name("Franquicia Test")
                .branches(new ArrayList<>(List.of(branch)))
                .build();
    }

    @Test
    void addBranch_shouldReturnBranchWithId() {
        Branch newBranch = Branch.builder()
                .name("Sucursal Sur")
                .products(new ArrayList<>())
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(branchUseCase.addBranch("franchise-1", newBranch))
                .expectNextMatches(b -> b.getId() != null && b.getName().equals("Sucursal Sur"))
                .verifyComplete();
    }

    @Test
    void addBranch_whenFranchiseNotFound_shouldReturnError() {
        when(franchiseRepository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.addBranch("invalid-id", branch))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Franchise not found"))
                .verify();
    }

    @Test
    void updateBranchName_shouldReturnBranchWithNewName() {
        Branch updatedBranch = Branch.builder()
                .id("branch-1")
                .name("Sucursal Actualizada")
                .products(new ArrayList<>())
                .build();

        Franchise updatedFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Franquicia Test")
                .branches(new ArrayList<>(List.of(updatedBranch)))
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(branchUseCase.updateBranchName("franchise-1", "branch-1", "Sucursal Actualizada"))
                .expectNextMatches(b -> b.getName().equals("Sucursal Actualizada"))
                .verifyComplete();
    }

    @Test
    void updateBranchName_whenFranchiseNotFound_shouldReturnError() {
        when(franchiseRepository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateBranchName("invalid-id", "branch-1", "Nuevo Nombre"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Franchise not found"))
                .verify();
    }
}