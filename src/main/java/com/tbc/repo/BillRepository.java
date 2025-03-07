package com.tbc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tbc.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}