package com.tbc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbc.entity.ProductEntity;

public interface ProductRepo extends JpaRepository<ProductEntity, Long>{
	List<ProductEntity> findByNameContainingIgnoreCase(String name);
    List<ProductEntity> findByCategoryId(Long categoryId);
    List<ProductEntity> findByBrandId(Long brandId);
    List<ProductEntity> findByCategoryIdAndBrandId(Long categoryId, Long brandId);
}
