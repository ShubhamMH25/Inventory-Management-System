package com.tbc.productController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tbc.repo.BrandRepository;
import com.tbc.repo.CategoryRepository;
import com.tbc.repo.ProductRepo;
import com.tbc.services.TrendingProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;
    
    @Autowired
    private TrendingProductService trendingProductService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalBrands", brandRepository.count());
        model.addAttribute("trendingProducts", trendingProductService.getTrendingProducts());
        return "home";
    }
}