package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.StockIn;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugInfoRepository;
import com.example.drugmanagement.repository.StockInRepository;
import com.example.drugmanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-in")
public class StockInApiController {

    private final StockInRepository stockInRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final UserRepository userRepository;

    public StockInApiController(StockInRepository stockInRepository,
                                DrugInfoRepository drugInfoRepository,
                                UserRepository userRepository) {
        this.stockInRepository = stockInRepository;
        this.drugInfoRepository = drugInfoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        List<StockIn> list = stockInRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody StockInRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request == null || request.getDrugId() == null || request.getQuantity() == null
                || request.getQuantity() <= 0) {
            body.put("message", "药品和数量不能为空");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        DrugInfo drug = drugInfoRepository.findById(request.getDrugId()).orElse(null);
        if (drug == null) {
            body.put("message", "无效的药品");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        StockIn stockIn = new StockIn();
        stockIn.setDrug(drug);
        stockIn.setQuantity(request.getQuantity());
        if (request.getPrice() != null) {
            stockIn.setPrice(request.getPrice());
        }
        stockIn.setSupplier(request.getSupplier());
        stockIn.setBatchNo(request.getBatchNumber());
        stockIn.setInTime(LocalDateTime.now());
        if (request.getOperator() != null && !request.getOperator().trim().isEmpty()) {
            User operator = userRepository.findByUsername(request.getOperator().trim());
            stockIn.setOperator(operator);
        }
        stockIn.setRemark(request.getRemark());
        StockIn saved = stockInRepository.save(stockIn);

        Integer oldStock = drug.getStock();
        if (oldStock == null) {
            oldStock = 0;
        }
        drug.setStock(oldStock + request.getQuantity());
        drugInfoRepository.save(drug);

        return new ResponseEntity<>(toDto(saved), HttpStatus.OK);
    }

    private Map<String, Object> toDto(StockIn stockIn) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", stockIn.getId());
        map.put("drugId", stockIn.getDrug() != null ? stockIn.getDrug().getId() : null);
        map.put("drugName", stockIn.getDrug() != null ? stockIn.getDrug().getName() : null);
        map.put("quantity", stockIn.getQuantity());
        map.put("price", stockIn.getPrice() != null ? stockIn.getPrice() : BigDecimal.ZERO);
        map.put("supplier", stockIn.getSupplier());
        map.put("batchNumber", stockIn.getBatchNo());
        map.put("createTime", stockIn.getInTime() != null ? stockIn.getInTime().toString() : null);
        map.put("operator", stockIn.getOperator() != null ? stockIn.getOperator().getUsername() : null);
        map.put("remark", stockIn.getRemark());
        return map;
    }

    public static class StockInRequest {
        private Long drugId;
        private Integer quantity;
        private BigDecimal price;
        private String supplier;
        private String batchNumber;
        private String remark;
        private String operator;

        public Long getDrugId() {
            return drugId;
        }

        public void setDrugId(Long drugId) {
            this.drugId = drugId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getSupplier() {
            return supplier;
        }

        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }
}
