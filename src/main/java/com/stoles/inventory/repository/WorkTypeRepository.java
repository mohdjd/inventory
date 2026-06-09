package com.stoles.inventory.repository;

import com.stoles.inventory.entity.WorkType;
import com.stoles.inventory.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {

    Optional<WorkType> findByNameIgnoreCase(String name);
}
