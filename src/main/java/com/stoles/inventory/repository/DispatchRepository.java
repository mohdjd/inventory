package com.stoles.inventory.repository;


import com.stoles.inventory.entity.Dispatch;
import com.stoles.inventory.entity.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    List<Dispatch> findByWorkerIdOrderBySentDateDesc(Long workerId);
    List<Dispatch> findByStatusNot(DispatchStatus status);
    List<Dispatch> findByStatusOrderBySentDateDesc(DispatchStatus status);

    @Query("SELECT COALESCE(SUM(d.sentQty), 0) FROM Dispatch d")
    Integer sumTotalSent();

    @Query("SELECT COALESCE(SUM(d.receivedQty), 0) FROM Dispatch d")
    Integer sumTotalReceived();

    @Query("""
    SELECT d FROM Dispatch d
    JOIN FETCH d.worker w
    JOIN FETCH d.workType wt
    JOIN FETCH d.stockItem si
    WHERE (:workerId IS NULL OR w.id = :workerId)
    AND (:search IS NULL OR :search = '' OR
            LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(wt.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(si.fabric) LIKE LOWER(CONCAT('%', :search, '%')))
    ORDER BY d.sentDate DESC
    """)
    List<Dispatch> search(@Param("workerId") Long workerId, @Param("search") String search);

    // Total earned per worker (receivedQty * pricePerPiece)
    @Query("SELECT COALESCE(SUM(d.receivedQty * d.pricePerPiece), 0) FROM Dispatch d WHERE d.worker.id = :workerId")
    java.math.BigDecimal totalEarnedByWorker(@Param("workerId") Long workerId);

    @Query("""  
    SELECT d.worker.id AS workerId, d.worker.name AS workerName,
        COALESCE(SUM(d.sentQty), 0) AS totalSent,
        COALESCE(SUM(d.receivedQty), 0) AS totalReceived,
        COUNT(d.id) AS totalJobs
    FROM Dispatch d
    GROUP BY d.worker.id, d.worker.name
    """)
    List<WorkerSummaryProjection> workerSummary();

    interface WorkerSummaryProjection {
        Long getWorkerId();
        String getWorkerName();
        Integer getTotalSent();
        Integer getTotalReceived();
        Long getTotalJobs();
    }
}
