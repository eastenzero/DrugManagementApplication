package com.example.drugmanagement.repository;

import com.example.drugmanagement.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugCategoryRepository extends JpaRepository<DrugCategory, Long> {
}
