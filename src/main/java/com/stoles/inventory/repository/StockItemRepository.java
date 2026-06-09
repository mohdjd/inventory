package com.stoles.inventory.repository;

import com.stoles.inventory.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long> {

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockItem s")
    Integer sumTotalQuantity();
}
