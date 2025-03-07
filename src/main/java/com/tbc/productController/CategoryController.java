package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tbc.entity.CategoriesEntity;
import com.tbc.repo.CategoryRepository;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String getAllCategories(Model model, @ModelAttribute("successMessage") String successMessage) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("successMessage", successMessage);
        return "category-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoriesEntity());
        return "category-form";
    }

    @PostMapping
    public String createCategory(@ModelAttribute CategoriesEntity category, RedirectAttributes redirectAttributes) {
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        }
        return "redirect:/categories";
    }
}