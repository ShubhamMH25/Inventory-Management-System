package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tbc.dto.CustomerDTO;
import com.tbc.entity.Customer;
import com.tbc.repo.CustomerRepository;
import com.tbc.services.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerApiController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerService.getCustomerRepository().findAll();
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            Customer customer = customerService.createCustomer(customerDTO);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}