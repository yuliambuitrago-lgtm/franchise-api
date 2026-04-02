package com.buitrago.franchise_api.infrastructure.web.handler;

import com.buitrago.franchise_api.application.usecase.FranchiseUseCase;
import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.infrastructure.web.dto.FranchiseRequest;
import com.buitrago.franchise_api.infrastructure.web.dto.UpdateNameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(dto -> franchiseUseCase.createFranchise(
                        Franchise.builder()
                                .name(dto.getName())
                                .build()
                ))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> resolveError(e));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> franchiseUseCase.updateFranchiseName(franchiseId, dto.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> resolveError(e));
    }

    private Mono<ServerResponse> resolveError(Throwable e) {
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return ServerResponse.notFound().build();
        }
        return ServerResponse.badRequest().bodyValue(e.getMessage());
    }
}