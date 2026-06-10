package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.Payment;
import com.stoles.inventory.entity.Worker;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.PaymentRepository;
import com.stoles.inventory.repository.WorkerRepository;
import com.stoles.inventory.security.AuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final WorkerRepository workerRepo;
    private final AuditHelper audit;

    @Transactional(readOnly = true)
    public List<Dtos.PaymentResponse> filter(Long workerId, LocalDate from, LocalDate to) {
        return paymentRepo.filter(workerId, from, to).stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.PaymentResponse create(Dtos.PaymentRequest req) {
        Worker w = workerRepo.findById(req.getWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker", req.getWorkerId()));
        AppUser actor = audit.currentUser();
        Payment p = Payment.builder()
                .worker(w).amount(req.getAmount()).paymentDate(req.getPaymentDate())
                .paymentMode(req.getPaymentMode()).referenceNo(req.getReferenceNo())
                .remarks(req.getRemarks()).createdBy(actor).build();
        return toResponse(paymentRepo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentRepo.existsById(id)) throw new ResourceNotFoundException("Payment", id);
        paymentRepo.deleteById(id);
    }

    BigDecimal totalPaidToWorker(Long workerId) {
        return paymentRepo.totalPaidToWorker(workerId);
    }

    Dtos.PaymentResponse toResponse(Payment p) {
        return Dtos.PaymentResponse.builder()
                .id(p.getId()).workerId(p.getWorker().getId()).workerName(p.getWorker().getName())
                .amount(p.getAmount()).paymentDate(p.getPaymentDate()).paymentMode(p.getPaymentMode())
                .referenceNo(p.getReferenceNo()).remarks(p.getRemarks())
                .createdBy(AuditHelper.displayName(p.getCreatedBy()))
                .createdAt(p.getCreatedAt()).build();
    }

}
