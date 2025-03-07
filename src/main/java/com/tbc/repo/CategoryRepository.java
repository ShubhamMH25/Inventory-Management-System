package com.tbc.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbc.entity.CategoriesEntity;

public interface CategoryRepository extends JpaRepository<CategoriesEntity, Long> {
}
