package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbc.entity.ProductEntity;
import com.tbc.repo.ProductRepo;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductApiController {

    @Autowired
    private ProductRepo productRepository;

    @GetMapping("/products")
    public List<ProductEntity> getProducts() {
        return productRepository.findAll();
    }
}
