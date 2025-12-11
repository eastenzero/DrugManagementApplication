package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugCategory;
import com.example.drugmanagement.repository.DrugCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final DrugCategoryRepository categoryRepository;

    public CategoryApiController(DrugCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<DrugCategory> list() {
        return categoryRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            body.put("message", "分类名称不能为空");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        DrugCategory category = new DrugCategory();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setCreateTime(LocalDateTime.now());
        DrugCategory saved = categoryRepository.save(category);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Map<String, Object> body = new HashMap<>();
        DrugCategory category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            body.put("message", "分类不存在");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            category.setName(request.getName().trim());
        }
        category.setDescription(request.getDescription());
        DrugCategory saved = categoryRepository.save(category);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<>();
        try {
            categoryRepository.deleteById(id);
            body.put("message", "删除成功");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            body.put("message", "删除失败，可能有药品正在使用该分类");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    public static class CategoryRequest {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
