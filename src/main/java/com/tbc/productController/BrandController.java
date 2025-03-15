package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tbc.entity.Brand;
import com.tbc.repo.BrandRepository;
import com.tbc.services.LogoService;

@Controller
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;
    
    @Autowired
    private LogoService logoService;

    @GetMapping
    public String getAllBrands(Model model, @ModelAttribute("successMessage") String successMessage) {
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("successMessage", successMessage);
        return "brand-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brand-form";
    }

    @PostMapping
    public String createBrand(@ModelAttribute Brand brand, RedirectAttributes redirectAttributes) {
    	// Fetch the logo URL using LogoService
        String logoUrl = logoService.getBrandLogo(brand.getName());
        brand.setLogoUrl(logoUrl);
        
        brandRepository.save(brand);
        redirectAttributes.addFlashAttribute("successMessage", "Brand added successfully!");
        return "redirect:/brands";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Brand deleted successfully!");
        }
        return "redirect:/brands";
    }
}