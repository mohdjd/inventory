package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.*;
import com.stoles.inventory.exception.BusinessException;
import com.stoles.inventory.exception.ResourceNotFoundException;
import com.stoles.inventory.repository.DispatchRepository;
import com.stoles.inventory.security.AuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepo;
    private final StockService stockService;
    private final WorkerService workerService;
    private final WorkTypeService workTypeService;
    private final PaymentService paymentService;
    private final AuditHelper audit;

    @Transactional(readOnly = true)
    public List<Dtos.DispatchResponse> findAll(Long workerId, String search) {
        return dispatchRepo.search(workerId, search).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<Dtos.DispatchResponse> findPending() {
        return dispatchRepo.findByStatusNot(DispatchStatus.COMPLETED).stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dtos.DispatchResponse create(Dtos.DispatchRequest req) {
        StockItem stock = stockService.getOrThrow(req.getStockItemId());
        Worker worker = workerService.getOrThrow(req.getWorkerId());
        WorkType wt = workTypeService.getOrThrow(req.getWorkTypeId());
        BigDecimal price = req.getPricePerPiece() != null ? req.getPricePerPiece() : wt.getPricePerPiece();
        AppUser actor = audit.currentUser();
        return toResponse(dispatchRepo.save(Dispatch.builder()
                .stockItem(stock).worker(worker).workType(wt).pricePerPiece(price)
                .sentQty(req.getSentQty()).receivedQty(0).sentDate(req.getSentDate())
                .status(DispatchStatus.PENDING).createdBy(actor).build()));
    }

    @Transactional
    public Dtos.DispatchResponse recordReceipt(Long id, Dtos.ReceiveRequest req) {
        Dispatch d = getOrThrow(id);
        if (d.getStatus() == DispatchStatus.COMPLETED)
            throw new BusinessException("Dispatch #" + id + " is already completed.");
        int newTotal = d.getReceivedQty() + req.getQuantity();
        if (newTotal > d.getSentQty())
            throw new BusinessException("Cannot receive " + req.getQuantity()
                    + " - only " + (d.getSentQty() - d.getReceivedQty()) + " pending." );
        AppUser actor = audit.currentUser();
        d.setReceivedQty(newTotal);
        d.setReceivedDate(req.getReceivedDate());
        d.setReceivedBy(actor); // stamp who recorded the receipt
        d.setStatus(newTotal >= d.getSentQty() ? DispatchStatus.COMPLETED : DispatchStatus.PARTIAL);
        return toResponse(dispatchRepo.save(d));
    }

    @Transactional(readOnly = true)
    public List<Dtos.WorkerSummaryResponse> workerSummary() {
        return dispatchRepo.workerSummary().stream().map(p -> {
            BigDecimal earned = dispatchRepo.totalEarnedByWorker(p.getWorkerId());
            BigDecimal paid = paymentService.totalPaidToWorker(p.getWorkerId());
            Worker w = workerService.getOrThrow(p.getWorkerId());
            return Dtos.WorkerSummaryResponse.builder()
                    .workerId(p.getWorkerId()).workerName(p.getWorkerName()).phone(w.getPhone())
                    .totalSent(p.getTotalSent()).totalReceived(p.getTotalReceived())
                    .pending(p.getTotalSent() - p.getTotalReceived()).totalJobs(p.getTotalJobs())
                    .totalEarned(earned).totalPaid(paid).outstanding(earned.subtract(paid))
                    .build();
        }).toList();
    }

    private Dispatch getOrThrow(Long id) {
        return dispatchRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", id));

    }

    public Dtos.DispatchResponse toResponse(Dispatch d) {
        int pending = d.getSentQty() - d.getReceivedQty();
        BigDecimal payable = d.getPricePerPiece().multiply(BigDecimal.valueOf(d.getReceivedQty()));
        return Dtos.DispatchResponse.builder()
                .id(d.getId()).stockItemId(d.getStockItem().getId())
                .label(d.getStockItem().getLabel()).fabric(d.getStockItem().getFabric())
                .size(d.getStockItem().getSize()).weight(d.getStockItem().getWeight())
                .workerId(d.getWorker().getId()).workerName(d.getWorker().getName())
                .workTypeId(d.getWorkType().getId()).workTypeName(d.getWorkType().getName())
                .pricePerPiece(d.getPricePerPiece()).sentQty(d.getSentQty())
                .receivedQty(d.getReceivedQty()).pendingQty(pending)
                .sentDate(d.getSentDate()).receivedDate(d.getReceivedDate())
                .status(d.getStatus()).totalPayable(payable)
                .createdBy(AuditHelper.displayName(d.getCreatedBy()))
                .receivedBy(AuditHelper.displayName(d.getReceivedBy()))
                .createdAt(d.getCreatedAt()).build();
    }
}
