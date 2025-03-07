package com.tbc.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbc.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
