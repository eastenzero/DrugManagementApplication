package com.example.drugmanagement.repository;

import com.example.drugmanagement.entity.StockOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockOutRepository extends JpaRepository<StockOut, Long> {
}
