package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugCategory;
import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.repository.DrugCategoryRepository;
import com.example.drugmanagement.repository.DrugInfoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drugs")
public class DrugApiController {

    private final DrugInfoRepository drugInfoRepository;
    private final DrugCategoryRepository drugCategoryRepository;

    public DrugApiController(DrugInfoRepository drugInfoRepository,
                             DrugCategoryRepository drugCategoryRepository) {
        this.drugInfoRepository = drugInfoRepository;
        this.drugCategoryRepository = drugCategoryRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam(value = "keyword", required = false) String keyword) {
        List<DrugInfo> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = drugInfoRepository.findByNameContaining(keyword.trim());
        } else {
            list = drugInfoRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody DrugRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()
                || request.getCategoryId() == null) {
            body.put("message", "名称和分类不能为空");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        DrugCategory category = drugCategoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            body.put("message", "无效的分类");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        DrugInfo drug = new DrugInfo();
        applyRequestToEntity(request, drug, category, true);
        DrugInfo saved = drugInfoRepository.save(drug);
        return new ResponseEntity<>(toDto(saved), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody DrugRequest request) {
        Map<String, Object> body = new HashMap<>();
        DrugInfo drug = drugInfoRepository.findById(id).orElse(null);
        if (drug == null) {
            body.put("message", "药品不存在");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        DrugCategory category = drug.getCategory();
        if (request.getCategoryId() != null) {
            category = drugCategoryRepository.findById(request.getCategoryId()).orElse(null);
            if (category == null) {
                body.put("message", "无效的分类");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
        }
        applyRequestToEntity(request, drug, category, false);
        DrugInfo saved = drugInfoRepository.save(drug);
        return new ResponseEntity<>(toDto(saved), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<>();
        try {
            drugInfoRepository.deleteById(id);
            body.put("message", "删除成功");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            body.put("message", "删除失败，可能有入库/出库记录正在使用该药品");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, Object> toDto(DrugInfo drug) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", drug.getId());
        map.put("name", drug.getName());
        map.put("categoryId", drug.getCategory() != null ? drug.getCategory().getId() : null);
        map.put("categoryName", drug.getCategory() != null ? drug.getCategory().getName() : null);
        map.put("specification", drug.getSpecification());
        map.put("unit", drug.getUnit());
        map.put("price", drug.getPrice() != null ? drug.getPrice() : BigDecimal.ZERO);
        map.put("stock", drug.getStock() != null ? drug.getStock() : 0);
        map.put("manufacturer", drug.getManufacturer());
        map.put("productionDate", drug.getProductionDate() != null ? drug.getProductionDate().toString() : null);
        map.put("expiryDate", drug.getExpireDate() != null ? drug.getExpireDate().toString() : null);
        map.put("status", drug.getStatus());
        map.put("createTime", drug.getCreateTime() != null ? drug.getCreateTime().toString() : null);
        return map;
    }

    private void applyRequestToEntity(DrugRequest request, DrugInfo drug, DrugCategory category, boolean isCreate) {
        drug.setName(request.getName());
        drug.setCategory(category);
        drug.setSpecification(request.getSpecification());
        drug.setUnit(request.getUnit());

        if (request.getPrice() != null) {
            drug.setPrice(request.getPrice());
        } else if (isCreate) {
            drug.setPrice(null);
        }

        if (request.getStock() != null) {
            drug.setStock(request.getStock());
        } else if (isCreate && drug.getStock() == null) {
            drug.setStock(0);
        }

        drug.setManufacturer(request.getManufacturer());

        if (request.getProductionDate() != null && !request.getProductionDate().trim().isEmpty()) {
            try {
                drug.setProductionDate(LocalDate.parse(request.getProductionDate().trim()));
            } catch (Exception e) {
                drug.setProductionDate(null);
            }
        } else if (isCreate) {
            drug.setProductionDate(null);
        }

        if (request.getExpiryDate() != null && !request.getExpiryDate().trim().isEmpty()) {
            try {
                drug.setExpireDate(LocalDate.parse(request.getExpiryDate().trim()));
            } catch (Exception e) {
                drug.setExpireDate(null);
            }
        } else if (isCreate) {
            drug.setExpireDate(null);
        }

        if (request.getStatus() != null) {
            drug.setStatus(request.getStatus());
        } else if (isCreate) {
            drug.setStatus(1);
        }

        if (isCreate || drug.getCreateTime() == null) {
            drug.setCreateTime(LocalDateTime.now());
        }
    }

    public static class DrugRequest {
        private Long id;
        private String name;
        private Long categoryId;
        private String specification;
        private String unit;
        private BigDecimal price;
        private Integer stock;
        private String manufacturer;
        private String productionDate;
        private String expiryDate;
        private Integer status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getSpecification() {
            return specification;
        }

        public void setSpecification(String specification) {
            this.specification = specification;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(String productionDate) {
            this.productionDate = productionDate;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
