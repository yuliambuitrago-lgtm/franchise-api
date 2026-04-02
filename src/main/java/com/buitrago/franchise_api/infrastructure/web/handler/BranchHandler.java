package com.buitrago.franchise_api.infrastructure.web.handler;

import com.buitrago.franchise_api.application.usecase.BranchUseCase;
import com.buitrago.franchise_api.domain.model.Branch;
import com.buitrago.franchise_api.infrastructure.web.dto.BranchRequest;
import com.buitrago.franchise_api.infrastructure.web.dto.UpdateNameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .flatMap(dto -> branchUseCase.addBranch(franchiseId,
                        Branch.builder()
                                .name(dto.getName())
                                .build()
                ))
                .flatMap(branch -> ServerResponse.ok().bodyValue(branch))
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> branchUseCase.updateBranchName(franchiseId, branchId, dto.getName()))
                .flatMap(branch -> ServerResponse.ok().bodyValue(branch))
                .onErrorResume(e -> resolveError(e));
    }

    private Mono<ServerResponse> resolveError(Throwable e) {
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return ServerResponse.notFound().build();
        }
        return ServerResponse.badRequest().bodyValue(e.getMessage());
    }
}