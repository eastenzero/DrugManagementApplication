package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.StockOut;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugInfoRepository;
import com.example.drugmanagement.repository.StockOutRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/stock-out")
public class StockOutController {

    private final StockOutRepository stockOutRepository;
    private final DrugInfoRepository drugInfoRepository;

    public StockOutController(StockOutRepository stockOutRepository,
                              DrugInfoRepository drugInfoRepository) {
        this.stockOutRepository = stockOutRepository;
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
        List<StockOut> list = stockOutRepository.findAll();
        model.addAttribute("list", list);
        return "stockout/list";
    }

    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        User current = getCurrentUser(session);
        if (current == null) {
            return "redirect:/login";
        }
        List<DrugInfo> drugs = drugInfoRepository.findAll();
        model.addAttribute("stockOut", new StockOut());
        model.addAttribute("drugs", drugs);
        return "stockout/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam("drugId") Long drugId,
                       @RequestParam("quantity") Integer quantity,
                       @RequestParam(value = "reason", required = false) String reason,
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
            model.addAttribute("stockOut", new StockOut());
            return "stockout/form";
        }
        if (quantity == null || quantity <= 0) {
            model.addAttribute("error", "出库数量必须大于 0");
            model.addAttribute("drugs", drugInfoRepository.findAll());
            model.addAttribute("stockOut", new StockOut());
            return "stockout/form";
        }
        Integer stock = drug.getStock();
        if (stock == null) {
            stock = 0;
        }
        if (quantity > stock) {
            model.addAttribute("error", "库存不足，当前库存为 " + stock);
            model.addAttribute("drugs", drugInfoRepository.findAll());
            model.addAttribute("stockOut", new StockOut());
            return "stockout/form";
        }

        StockOut stockOut = new StockOut();
        stockOut.setDrug(drug);
        stockOut.setQuantity(quantity);
        stockOut.setReason(reason);
        stockOut.setOutTime(LocalDateTime.now());
        stockOut.setOperator(current);
        stockOut.setRemark(remark);
        stockOutRepository.save(stockOut);

        drug.setStock(stock - quantity);
        drugInfoRepository.save(drug);

        return "redirect:/stock-out";
    }
}
