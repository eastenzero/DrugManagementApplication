package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.DrugCategory;
import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.DrugCategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final DrugCategoryRepository categoryRepository;

    public CategoryController(DrugCategoryRepository categoryRepository) {
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
    public String list(Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        List<DrugCategory> list = categoryRepository.findAll();
        model.addAttribute("list", list);
        return "category/list";
    }

    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        model.addAttribute("category", new DrugCategory());
        return "category/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        Optional<DrugCategory> optional = categoryRepository.findById(id);
        if (!optional.isPresent()) {
            return "redirect:/categories";
        }
        model.addAttribute("category", optional.get());
        return "category/form";
    }

    @PostMapping("/save")
    public String save(DrugCategory formCategory, HttpSession session, Model model) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        DrugCategory category;
        if (formCategory.getId() != null) {
            Optional<DrugCategory> optional = categoryRepository.findById(formCategory.getId());
            if (!optional.isPresent()) {
                category = new DrugCategory();
                category.setCreateTime(LocalDateTime.now());
            } else {
                category = optional.get();
            }
        } else {
            category = new DrugCategory();
            category.setCreateTime(LocalDateTime.now());
        }
        category.setName(formCategory.getName());
        category.setDescription(formCategory.getDescription());
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        if (getCurrentUser(session) == null) {
            return "redirect:/login";
        }
        try {
            categoryRepository.deleteById(id);
            return "redirect:/categories";
        } catch (Exception e) {
            List<DrugCategory> list = categoryRepository.findAll();
            model.addAttribute("list", list);
            model.addAttribute("error", "删除失败，可能有药品正在使用该分类");
            return "category/list";
        }
    }
}
