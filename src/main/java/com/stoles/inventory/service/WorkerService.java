package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.Worker;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.WorkerRepository;
import com.stoles.inventory.security.AuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepo;
    private final AuditHelper audit;

    @Transactional(readOnly = true)
    public List<Dtos.WorkerResponse> findAll() {
        return workerRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.WorkerResponse create(Dtos.WorkerRequest req) {
        AppUser actor = audit.currentUser();
        return toResponse(workerRepo.save(Worker.builder()
                .name(req.getName()).phone(req.getPhone()).address(req.getAddress())
                .createdBy(actor).build()));
    }

    @Transactional
    public Dtos.WorkerResponse update(Long id, Dtos.WorkerRequest req) {
        Worker w = getOrThrow(id);
        w.setName(req.getName());
        w.setPhone(req.getPhone());
        w.setAddress(req.getAddress());
        return toResponse(workerRepo.save(w));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        workerRepo.deleteById(id);
    }

    Worker getOrThrow(Long id) {
        return workerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worker", id));
    }

    Dtos.WorkerResponse toResponse(Worker w) {
        return Dtos.WorkerResponse.builder().id(w.getId()).name(w.getName())
                .phone(w.getPhone()).address(w.getAddress())
                .createdBy(AuditHelper.displayName(w.getCreatedBy()))
                .createdAt(w.getCreatedAt()).build();
    }
}