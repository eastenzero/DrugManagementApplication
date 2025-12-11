package com.example.drugmanagement.repository;

import com.example.drugmanagement.entity.StockIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockInRepository extends JpaRepository<StockIn, Long> {
}
