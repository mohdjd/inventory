package com.stoles.inventory.service;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.DispatchStatus;
import com.stoles.inventory.repository.DispatchRepository;
import com.stoles.inventory.repository.PaymentRepository;
import com.stoles.inventory.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StockItemRepository stockRepo;
    private final DispatchRepository dispatchRepo;
    private final PaymentRepository paymentRepo;
    private final DispatchService dispatchService;

    @Transactional(readOnly = true)
    public Dtos.DashboardResponse summary() {
        int totalStock = stockRepo.sumTotalQuantity();
        int totalSent = dispatchRepo.sumTotalSent();
        int totalReceived = dispatchRepo.sumTotalReceived();
        BigDecimal totalEarned = dispatchRepo.findAll().stream()
                .map(d -> d.getPricePerPiece().multiply(BigDecimal.valueOf(d.getReceivedQty())))
                .reduce(BigDecimal.ZERO, BigDecimal :: add);
        BigDecimal totalPaid = paymentRepo.totalPaidAll();
        List<Dtos.DispatchResponse> recent = dispatchRepo
                .findByStatusOrderBySentDateDesc(DispatchStatus.PENDING)
                .stream().limit(5).map(dispatchService :: toResponse). toList();
        return Dtos.DashboardResponse.builder()
                .totalStockPieces(totalStock).totalSent(totalSent).totalReceived(totalReceived)
                .totalPendingAtWorker(totalSent - totalReceived).availableStock(totalStock - totalSent)
                .totalEarned(totalEarned).totalPaid(totalPaid)
                .totalOutstanding(totalEarned.subtract(totalPaid))
                .workerSummary(dispatchService.workerSummary()).recentDispatches(recent)
                .build();
    }
}
