package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugCategory;
import com.example.drugmanagement.entity.DrugInfo;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugCategoryRepository;
import com.example.drugmanagement.repository.DrugInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/drugs")
public class DrugController {

    private final DrugInfoRepository drugInfoRepository;
    private final DrugCategoryRepository categoryRepository;

    public DrugController(DrugInfoRepository drugInfoRepository,
                          DrugCategoryRepository categoryRepository) {
        this.drugInfoRepository = drugInfoRepository;
        this.categoryRepository = categoryRepository;
    }

    private User getCurrentUser(HttpSession session) {
        Object obj = session.getAttribute("currentUser");
        if (obj instanceof User) {
            return (User) obj;
        }
        return null;
    }

    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       Model model,
                       HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        List<DrugInfo> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = drugInfoRepository.findByNameContaining(keyword.trim());
        } else {
            list = drugInfoRepository.findAll();
        }
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        return "drug/list";
    }

    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        List<DrugCategory> categories = categoryRepository.findAll();
        model.addAttribute("drug", new DrugInfo());
        model.addAttribute("categories", categories);
        return "drug/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        Optional<DrugInfo> optional = drugInfoRepository.findById(id);
        if (!optional.isPresent()) {
            return "redirect:/drugs";
        }
        List<DrugCategory> categories = categoryRepository.findAll();
        model.addAttribute("drug", optional.get());
        model.addAttribute("categories", categories);
        return "drug/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam String name,
                       @RequestParam("categoryId") Long categoryId,
                       @RequestParam(value = "specification", required = false) String specification,
                       @RequestParam(value = "unit", required = false) String unit,
                       @RequestParam(value = "price", required = false) String priceStr,
                       @RequestParam(value = "stock", required = false) String stockStr,
                       @RequestParam(value = "manufacturer", required = false) String manufacturer,
                       @RequestParam(value = "productionDate", required = false) String productionDateStr,
                       @RequestParam(value = "expireDate", required = false) String expireDateStr,
                       @RequestParam(value = "status", required = false) Integer status,
                       HttpSession session,
                       Model model) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        Optional<DrugCategory> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            model.addAttribute("error", "请选择有效的药品分类");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("drug", new DrugInfo());
            return "drug/form";
        }

        DrugInfo drug;
        if (id != null) {
            Optional<DrugInfo> optional = drugInfoRepository.findById(id);
            if (optional.isPresent()) {
                drug = optional.get();
            } else {
                drug = new DrugInfo();
                drug.setCreateTime(LocalDateTime.now());
            }
        } else {
            drug = new DrugInfo();
            drug.setCreateTime(LocalDateTime.now());
        }

        drug.setName(name);
        drug.setCategory(categoryOptional.get());
        drug.setSpecification(specification);
        drug.setUnit(unit);

        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                drug.setPrice(new BigDecimal(priceStr.trim()));
            } catch (NumberFormatException e) {
                drug.setPrice(null);
            }
        } else {
            drug.setPrice(null);
        }

        if (stockStr != null && !stockStr.trim().isEmpty()) {
            try {
                drug.setStock(Integer.parseInt(stockStr.trim()));
            } catch (NumberFormatException e) {
                drug.setStock(0);
            }
        } else if (drug.getStock() == null) {
            drug.setStock(0);
        }

        drug.setManufacturer(manufacturer);

        if (productionDateStr != null && !productionDateStr.trim().isEmpty()) {
            try {
                drug.setProductionDate(LocalDate.parse(productionDateStr.trim()));
            } catch (Exception e) {
                drug.setProductionDate(null);
            }
        } else {
            drug.setProductionDate(null);
        }

        if (expireDateStr != null && !expireDateStr.trim().isEmpty()) {
            try {
                drug.setExpireDate(LocalDate.parse(expireDateStr.trim()));
            } catch (Exception e) {
                drug.setExpireDate(null);
            }
        } else {
            drug.setExpireDate(null);
        }

        if (status == null) {
            status = 1;
        }
        drug.setStatus(status);

        drugInfoRepository.save(drug);
        return "redirect:/drugs";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        try {
            drugInfoRepository.deleteById(id);
        } catch (Exception e) {
            // ignore for now
        }
        return "redirect:/drugs";
    }
}
