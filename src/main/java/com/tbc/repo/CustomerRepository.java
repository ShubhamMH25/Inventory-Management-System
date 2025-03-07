package com.tbc.repo;



import org.springframework.data.jpa.repository.JpaRepository;

import com.tbc.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
