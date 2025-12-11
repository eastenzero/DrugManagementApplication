package com.example.drugmanagement.repository;

import com.example.drugmanagement.entity.DrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugInfoRepository extends JpaRepository<DrugInfo, Long> {

    List<DrugInfo> findByNameContaining(String name);
}
