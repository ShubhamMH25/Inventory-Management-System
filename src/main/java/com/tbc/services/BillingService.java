package com.tbc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Bill createBill(Long customerId, String customerName, Integer customerAge, String customerPhone,
                           List<Long> productIds, List<Integer> quantities) throws IllegalArgumentException {
        // Handle customer
        Customer customer = handleCustomer(customerId, customerName, customerAge, customerPhone);

        // Validate products and quantities
        if (productIds == null || quantities == null || productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Please add at least one product with a valid quantity.");
        }

        // Create bill and bill items
        Bill bill = new Bill();
        bill.setCustomer(customer);
        List<BillItem> items = new ArrayList<>();
        double totalPrice = 0;

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int quantity = quantities.get(i);

            var productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty() || quantity <= 0) {
                throw new IllegalArgumentException("Invalid product or quantity.");
            }

            Product product = productOpt.get();
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock for " + product.getName() + ".");
            }

            // Create bill item
            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            items.add(item);

            // Update product quantity
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            // Update total price
            totalPrice += product.getPrice() * quantity;
        }

        bill.setItems(items);
        bill.setTotalPrice(totalPrice);
        return billRepository.save(bill);
    }

    private Customer handleCustomer(Long customerId, String customerName, Integer customerAge, String customerPhone) {
        if (customerId != null) {
            var existingCustomer = customerRepository.findById(customerId);
            if (existingCustomer.isEmpty()) {
                throw new IllegalArgumentException("Invalid customer selected.");
            }
            return existingCustomer.get();
        } else {
            if (customerName == null || customerName.isEmpty() || customerAge == null || customerPhone == null || customerPhone.isEmpty()) {
                throw new IllegalArgumentException("Please provide all customer details or select an existing customer.");
            }
            Customer customer = new Customer();
            customer.setName(customerName);
            customer.setAge(customerAge);
            customer.setPhone(customerPhone);
            return customerRepository.save(customer);
        }
    }

    public List<String> checkLowStockAlerts() {
        List<String> alerts = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getQuantity() < 5) { // Threshold for low stock
                alerts.add("Low stock alert: " + product.getName() + " has only " + product.getQuantity() + " units left.");
            }
        }
        return alerts;
    }
}
