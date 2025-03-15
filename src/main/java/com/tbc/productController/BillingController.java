package com.tbc.productController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tbc.dto.BillRequestDTO;
import com.tbc.entity.Bill;
import com.tbc.services.BillingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public String showBillingLandingPage() {
        return "redirect:/billing/new";
    }

    @GetMapping("/new")
    public String showBillingForm(Model model) {
        model.addAttribute("customers", billingService.getCustomerRepository().findAll());
        model.addAttribute("products", billingService.getProductRepository().findAll());
        model.addAttribute("bills", billingService.getBillRepository().findAll());

        List<String> lowStockAlerts = billingService.checkLowStockAlerts();
        if (!lowStockAlerts.isEmpty()) {
            model.addAttribute("lowStockAlerts", lowStockAlerts);
        }

        return "billing-form";
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBill(@RequestBody BillRequestDTO billRequestDTO) {
        try {
            Bill bill = billingService.createBill(billRequestDTO);
            messagingTemplate.convertAndSend("/topic/bills", bill);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("billId", bill.getId());
            List<String> lowStockAlerts = billingService.checkLowStockAlerts();
            if (!lowStockAlerts.isEmpty()) {
                response.put("lowStockAlerts", lowStockAlerts);
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/invoice/{id}")
    public String showInvoice(@PathVariable Long id, Model model) {
        var billOpt = billingService.getBillRepository().findById(id);
        if (billOpt.isEmpty()) {
            return "redirect:/billing/new";
        }
        model.addAttribute("bill", billOpt.get());
        return "invoice";
    }
}