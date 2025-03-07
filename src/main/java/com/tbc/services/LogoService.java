package com.tbc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LogoService {

    @Autowired
    private RestTemplate restTemplate;

    public String fetchLogoUrl(String brandName) {
        // Convert brand name to a domain (e.g., "Apple" -> "apple.com")
        String domain = brandName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com";
        String clearbitUrl = "https://logo.clearbit.com/" + domain;

        try {
            // Check if the logo exists by making a HEAD request
            restTemplate.headForHeaders(clearbitUrl);
            return clearbitUrl;
        } catch (Exception e) {
            // Fallback to a default placeholder if the logo isn't found
            return "/images/placeholder-logo.png";
        }
    }
}
