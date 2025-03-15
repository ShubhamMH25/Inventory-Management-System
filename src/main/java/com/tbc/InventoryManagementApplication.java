package com.tbc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.tbc.entity.Brand;
import com.tbc.entity.CategoriesEntity;
import com.tbc.entity.Customer;
import com.tbc.repo.BrandRepository;
import com.tbc.repo.CategoryRepository;
import com.tbc.repo.CustomerRepository;
import com.tbc.services.LogoService;

@SpringBootApplication
public class InventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner initData(CategoryRepository categoryRepository, BrandRepository brandRepository,LogoService logoService,CustomerRepository customerRepository) {
        return args -> {
            // Seed Categories
            if (categoryRepository.count() == 0) {
                String[] categories = {"Laptops", "Headphones", "Phones"};
                for (String name : categories) {
                    CategoriesEntity category = new CategoriesEntity();
                    category.setName(name);
                    categoryRepository.save(category);
                }
            }

            // Seed Brands
            if (brandRepository.count() == 0) {
                String[] brands = {"Apple", "Bose"};
                for (String name : brands) {
                    Brand brand = new Brand();
                    brand.setName(name);
                    String logoUrl = logoService.getBrandLogo(name);
                    brand.setLogoUrl(logoUrl);
                    brandRepository.save(brand);
                }
            }
            
         // Seed Customers
           if(customerRepository.count()==0) {
                Object[][] customers = {
                    {"John Doe", 30, "john.doe@example.com", "123-456-7890"},
                    {"Jane Smith", 25, "jane.smith@example.com", "987-654-3210"}
                };
                for (Object[] customerData : customers) {
                    Customer customer = new Customer();
                    customer.setName((String) customerData[0]);
                    customer.setAge((Integer) customerData[1]);
                    customer.setEmail((String) customerData[2]);
                    customer.setPhone((String) customerData[3]);
                    customerRepository.save(customer);
                }
            }
            
        };
    }
}
