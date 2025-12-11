package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.StockIn;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugInfoRepository;
import com.example.drugmanagement.repository.StockInRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/stock-in")
public class StockInController {

    private final StockInRepository stockInRepository;
    private final DrugInfoRepository drugInfoRepository;

    public StockInController(StockInRepository stockInRepository,
                             DrugInfoRepository drugInfoRepository) {
        this.stockInRepository = stockInRepository;
        this.drugInfoRepository = drugInfoRepository;
    }

    private User getCurrentUser(HttpSession session) {
        Object obj = session.getAttribute("currentUser");
        if (obj instanceof User) {
            return (User) obj;
        }
        return null;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        List<StockIn> list = stockInRepository.findAll();
        model.addAttribute("list", list);
        return "stockin/list";
    }

    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        User current = getCurrentUser(session);
        if (current == null) {
            return "redirect:/login";
        }
        List<DrugInfo> drugs = drugInfoRepository.findAll();
        model.addAttribute("stockIn", new StockIn());
        model.addAttribute("drugs", drugs);
        return "stockin/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam("drugId") Long drugId,
                       @RequestParam("quantity") Integer quantity,
                       @RequestParam(value = "price", required = false) String priceStr,
                       @RequestParam(value = "supplier", required = false) String supplier,
                       @RequestParam(value = "batchNo", required = false) String batchNo,
                       @RequestParam(value = "remark", required = false) String remark,
                       HttpSession session,
                       Model model) {
        User current = getCurrentUser(session);
        if (current == null) {
            return "redirect:/login";
        }
        DrugInfo drug = drugInfoRepository.findById(drugId).orElse(null);
        if (drug == null) {
            model.addAttribute("error", "请选择有效的药品");
            model.addAttribute("drugs", drugInfoRepository.findAll());
            model.addAttribute("stockIn", new StockIn());
            return "stockin/form";
        }
        if (quantity == null || quantity <= 0) {
            model.addAttribute("error", "入库数量必须大于 0");
            model.addAttribute("drugs", drugInfoRepository.findAll());
            model.addAttribute("stockIn", new StockIn());
            return "stockin/form";
        }

        StockIn stockIn = new StockIn();
        stockIn.setDrug(drug);
        stockIn.setQuantity(quantity);
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                stockIn.setPrice(new BigDecimal(priceStr.trim()));
            } catch (NumberFormatException e) {
                stockIn.setPrice(null);
            }
        }
        stockIn.setSupplier(supplier);
        stockIn.setBatchNo(batchNo);
        stockIn.setInTime(LocalDateTime.now());
        stockIn.setOperator(current);
        stockIn.setRemark(remark);
        stockInRepository.save(stockIn);

        Integer oldStock = drug.getStock();
        if (oldStock == null) {
            oldStock = 0;
        }
        drug.setStock(oldStock + quantity);
        drugInfoRepository.save(drug);

        return "redirect:/stock-in";
    }
}
