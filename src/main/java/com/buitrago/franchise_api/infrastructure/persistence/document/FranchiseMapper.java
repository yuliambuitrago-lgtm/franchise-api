package com.buitrago.franchise_api.infrastructure.persistence.document;

import com.buitrago.franchise_api.domain.model.Branch;
import com.buitrago.franchise_api.domain.model.Franchise;
import com.buitrago.franchise_api.domain.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FranchiseMapper {

    public static Franchise toDomain(FranchiseDocument doc) {
        return Franchise.builder()
                .id(doc.getId())
                .name(doc.getName())
                .branches(mapBranchesToDomain(doc.getBranches()))
                .build();
    }

    public static FranchiseDocument toDocument(Franchise franchise) {
        return FranchiseDocument.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .branches(mapBranchesToDocument(franchise.getBranches()))
                .build();
    }

    private static List<Branch> mapBranchesToDomain(List<BranchDocument> docs) {
        if (docs == null) return Collections.emptyList();
        return docs.stream()
                .map(doc -> Branch.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .products(mapProductsToDomain(doc.getProducts()))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<BranchDocument> mapBranchesToDocument(List<Branch> branches) {
        if (branches == null) return Collections.emptyList();
        return branches.stream()
                .map(branch -> BranchDocument.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .products(mapProductsToDocument(branch.getProducts()))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<Product> mapProductsToDomain(List<ProductDocument> docs) {
        if (docs == null) return Collections.emptyList();
        return docs.stream()
                .map(doc -> Product.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .stock(doc.getStock())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ProductDocument> mapProductsToDocument(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(product -> ProductDocument.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .stock(product.getStock())
                        .build())
                .collect(Collectors.toList());
    }
}