package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.StockOut;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugInfoRepository;
import com.example.drugmanagement.repository.StockOutRepository;
import com.example.drugmanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-out")
public class StockOutApiController {

    private final StockOutRepository stockOutRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final UserRepository userRepository;

    public StockOutApiController(StockOutRepository stockOutRepository,
                                 DrugInfoRepository drugInfoRepository,
                                 UserRepository userRepository) {
        this.stockOutRepository = stockOutRepository;
        this.drugInfoRepository = drugInfoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        List<StockOut> list = stockOutRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody StockOutRequest request) {
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
        Integer stock = drug.getStock();
        if (stock == null) {
            stock = 0;
        }
        if (request.getQuantity() > stock) {
            body.put("message", "库存不足，当前库存为 " + stock);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        StockOut stockOut = new StockOut();
        stockOut.setDrug(drug);
        stockOut.setQuantity(request.getQuantity());
        stockOut.setReason(request.getReason());
        stockOut.setOutTime(LocalDateTime.now());
        if (request.getOperator() != null && !request.getOperator().trim().isEmpty()) {
            User operator = userRepository.findByUsername(request.getOperator().trim());
            stockOut.setOperator(operator);
        }
        stockOut.setRemark(request.getRemark());
        StockOut saved = stockOutRepository.save(stockOut);

        drug.setStock(stock - request.getQuantity());
        drugInfoRepository.save(drug);

        return new ResponseEntity<>(toDto(saved), HttpStatus.OK);
    }

    private Map<String, Object> toDto(StockOut stockOut) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", stockOut.getId());
        map.put("drugId", stockOut.getDrug() != null ? stockOut.getDrug().getId() : null);
        map.put("drugName", stockOut.getDrug() != null ? stockOut.getDrug().getName() : null);
        map.put("quantity", stockOut.getQuantity());
        map.put("reason", stockOut.getReason());
        map.put("createTime", stockOut.getOutTime() != null ? stockOut.getOutTime().toString() : null);
        map.put("operator", stockOut.getOperator() != null ? stockOut.getOperator().getUsername() : null);
        map.put("remark", stockOut.getRemark());
        return map;
    }

    public static class StockOutRequest {
        private Long drugId;
        private Integer quantity;
        private String reason;
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

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
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
