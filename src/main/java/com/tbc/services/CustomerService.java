package com.tbc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tbc.dto.CustomerDTO;
import com.tbc.entity.Customer;
import com.tbc.repo.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getName() == null || customerDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setAge(customerDTO.getAge());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setAddress(customerDTO.getAddress());

        return customerRepository.save(customer);
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }
}