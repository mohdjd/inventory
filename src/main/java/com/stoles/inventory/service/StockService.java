package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.StockItem;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.StockItemRepository;
import com.stoles.inventory.security.AuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockItemRepository stockRepo;
    private final AuditHelper audit;

    @Transactional(readOnly = true)
    public List<Dtos.StockResponse> findAll() {
        return stockRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.StockResponse create(Dtos.StockRequest req) {
        AppUser actor = audit.currentUser();
        return toResponse(stockRepo.save(StockItem.builder()
                .label(req.getLabel()).fabric(req.getFabric()).size(req.getSize())
                .weight(req.getWeight()).quantity(req.getQuantity())
                .purchaseDate(req.getPurchaseDate())
                .createdBy(actor)
                .build()));
    }

    @Transactional
    public Dtos.StockResponse update(Long id, Dtos.StockRequest req) {
        StockItem s = getOrThrow(id);
        s.setLabel(req.getLabel());
        s.setFabric(req.getFabric());
        s.setSize(req.getSize());
        s.setWeight(req.getWeight());
        s.setQuantity(req.getQuantity());
        s.setPurchaseDate(req.getPurchaseDate());
        // createdBy is NOT updated on edit - it always reflects who created it
        return toResponse(stockRepo.save(s));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        stockRepo.deleteById(id);
    }

    StockItem getOrThrow(Long id) {
        return stockRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("StockItem", id));
    }

    Dtos.StockResponse toResponse(StockItem s) {
        return Dtos.StockResponse.builder()
                .id(s.getId()).label(s.getLabel()).fabric(s.getFabric()).size(s.getSize())
                .weight(s.getWeight()).quantity(s.getQuantity()).purchaseDate(s.getPurchaseDate())
                .createdBy(AuditHelper.displayName(s.getCreatedBy()))
                .createdAt(s.getCreatedAt()).build();
    }
}