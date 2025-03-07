package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tbc.entity.ProductEntity;
import com.tbc.repo.BrandRepository;
import com.tbc.repo.CategoryRepository;
import com.tbc.repo.ProductRepo;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @GetMapping
    public String getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String query,
            Model model,
            @ModelAttribute("successMessage") String successMessage) {
        List<ProductEntity> products;
        if (query != null && !query.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(query);
        } else if (categoryId != null && brandId != null) {
            products = productRepository.findByCategoryIdAndBrandId(categoryId, brandId);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (brandId != null) {
            products = productRepository.findByBrandId(brandId);
        } else {
            products = productRepository.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("successMessage", successMessage);
        return "product-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "product-form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute ProductEntity product, RedirectAttributes redirectAttributes) {
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "product-form";
        }
        return "redirect:/products";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductEntity updatedProduct, RedirectAttributes redirectAttributes) {
        Optional<ProductEntity> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            ProductEntity product = existingProduct.get();
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setQuantity(updatedProduct.getQuantity());
            product.setCategory(updatedProduct.getCategory());
            product.setBrand(updatedProduct.getBrand());
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        }
        return "redirect:/products";
    }
}