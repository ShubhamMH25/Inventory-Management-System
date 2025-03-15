package com.tbc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbc.dto.BillRequestDTO;
import com.tbc.entity.Bill;
import com.tbc.entity.BillItem;
import com.tbc.entity.Customer;
import com.tbc.entity.ProductEntity;
import com.tbc.repo.BillRepository;
import com.tbc.repo.CustomerRepository;
import com.tbc.repo.ProductRepo;

import java.util.ArrayList;
import java.util.List;
@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepo productRepository;

    @Transactional
    public Bill createBill(BillRequestDTO billRequestDTO) throws IllegalArgumentException {
        // Validate customer
        Long customerId = billRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        var customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid customer selected");
        }
        Customer customer = customerOpt.get();

        // Validate items
        List<BillRequestDTO.BillItemDTO> items = billRequestDTO.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one product is required");
        }

        Bill bill = new Bill();
        bill.setCustomer(customer);
        List<BillItem> billItems = new ArrayList<>();
        double totalPrice = 0;

        for (BillRequestDTO.BillItemDTO itemDTO : items) {
            Long productId = itemDTO.getProductId();
            int quantity = itemDTO.getQuantity();

            var productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty() || quantity <= 0) {
                throw new IllegalArgumentException("Invalid product or quantity");
            }

            ProductEntity product = productOpt.get();
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock for " + product.getName());
            }

            BillItem billItem = new BillItem();
            billItem.setBill(bill);
            billItem.setProduct(product);
            billItem.setQuantity(quantity);
            billItem.setPrice(product.getPrice());
            billItems.add(billItem);

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            totalPrice += product.getPrice() * quantity;
        }

        bill.setItems(billItems);
        bill.setTotalPrice(totalPrice);
        return billRepository.save(bill);
    }

    public List<String> checkLowStockAlerts() {
        List<String> alerts = new ArrayList<>();
        List<ProductEntity> products = productRepository.findAll();
        for (ProductEntity product : products) {
            if (product.getQuantity() < 5) {
                alerts.add("Low stock alert: " + product.getName() + " has only " + product.getQuantity() + " units left.");
            }
        }
        return alerts;
    }

    public BillRepository getBillRepository() {
        return billRepository;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public ProductRepo getProductRepository() {
        return productRepository;
    }
}