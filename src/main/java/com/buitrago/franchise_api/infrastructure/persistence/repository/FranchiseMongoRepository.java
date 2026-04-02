package com.buitrago.franchise_api.infrastructure.persistence.repository;

import com.buitrago.franchise_api.infrastructure.persistence.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}