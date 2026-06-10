package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.WorkType;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.WorkTypeRepository;
import com.stoles.inventory.security.AuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkTypeService {

    private final WorkTypeRepository workTypeRepo;
    private final AuditHelper audit;

    @Transactional(readOnly = true)
    public List<Dtos.WorkTypeResponse> findAll() {
        return workTypeRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.WorkTypeResponse create(Dtos.WorkTypeRequest req) {
        AppUser actor = audit.currentUser();
        return toResponse(workTypeRepo.save(WorkType.builder()
                .name(req.getName()).pricePerPiece(req.getPricePerPiece())
                .createdBy(actor).build()));
    }

    @Transactional
    public Dtos.WorkTypeResponse update(Long id, Dtos.WorkTypeRequest req) {
        WorkType wt = getOrThrow(id);
        wt.setName(req.getName());
        wt.setPricePerPiece(req.getPricePerPiece());
        return toResponse(workTypeRepo.save(wt));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        workTypeRepo.deleteById(id);
    }

    WorkType getOrThrow(Long id) {
        return workTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("WorkType", id));
    }

    Dtos.WorkTypeResponse toResponse(WorkType wt) {
        return Dtos.WorkTypeResponse.builder().id(wt.getId()).name(wt.getName())
                .pricePerPiece(wt.getPricePerPiece())
                .createdBy(AuditHelper.displayName(wt.getCreatedBy()))
                .createdAt(wt.getCreatedAt()).build();
    }

}