package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tbc.entity.Bill;
import com.tbc.entity.BillItem;
import com.tbc.entity.Customer;
import com.tbc.entity.ProductEntity;
import com.tbc.repo.BillRepository;
import com.tbc.repo.CustomerRepository;
import com.tbc.repo.ProductRepo;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepo productRepository;

    @GetMapping
    public String getAllBills(Model model, @ModelAttribute("successMessage") String successMessage) {
        model.addAttribute("bills", billRepository.findAll());
        model.addAttribute("successMessage", successMessage);
        return "billing-list";
    }

    @GetMapping("/new")
    public String showBillingForm(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "billing-form";
    }

    @PostMapping
    public String createBill(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer customerAge,
            @RequestParam(required = false) String customerPhone,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("quantities") List<Integer> quantities,
            RedirectAttributes redirectAttributes) {
        // Handle customer
        Customer customer;
        if (customerId != null) {
            var existingCustomer = customerRepository.findById(customerId);
            if (existingCustomer.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid customer selected.");
                return "redirect:/billing/new";
            }
            customer = existingCustomer.get();
        } else {
            if (customerName == null || customerName.isEmpty() || customerAge == null || customerPhone == null || customerPhone.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please provide all customer details or select an existing customer.");
                return "redirect:/billing/new";
            }
            customer = new Customer();
            customer.setName(customerName);
            customer.setAge(customerAge);
            customer.setPhone(customerPhone);
            customerRepository.save(customer);
        }

        // Validate products and quantities
        if (productIds == null || quantities == null || productIds.size() != quantities.size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please add at least one product with a valid quantity.");
            return "redirect:/billing/new";
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
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid product or quantity.");
                return "redirect:/billing/new";
            }

            ProductEntity product = productOpt.get();
            if (product.getQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock for " + product.getName() + ".");
                return "redirect:/billing/new";
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
        billRepository.save(bill);

        return "redirect:/billing/invoice/" + bill.getId();
    }

    @GetMapping("/invoice/{id}")
    public String showInvoice(@PathVariable Long id, Model model) {
        var billOpt = billRepository.findById(id);
        if (billOpt.isEmpty()) {
            return "redirect:/billing";
        }
        model.addAttribute("bill", billOpt.get());
        return "invoice";
    }
}