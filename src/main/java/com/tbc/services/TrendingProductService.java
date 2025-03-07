package com.tbc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tbc.dto.TrendingProductDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrendingProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    public List<TrendingProductDTO> getTrendingProducts() {
        // Call the YouTube Top Search Terms API
        String url = "https://youtube-top-search-terms.p.rapidapi.com/api/v1/top-search-terms?term=electronics";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-RapidAPI-Key", rapidApiKey);
        headers.put("X-RapidAPI-Host", rapidApiHost);

        try {
            // Fetch trending search terms
            Map<String, Object> response = restTemplate.getForObject(url, Map.class, headers);
            List<String> searchTerms = (List<String>) response.get("terms");

            // Filter for electronics-related terms (mobile phones, headphones, laptops)
            List<String> electronicsTerms = searchTerms.stream()
                    .filter(term -> term.toLowerCase().contains("phone") ||
                            term.toLowerCase().contains("headphone") ||
                            term.toLowerCase().contains("laptop"))
                    .collect(Collectors.toList());

            // Map to DTOs with placeholder image URLs
            List<TrendingProductDTO> trendingProducts = new ArrayList<>();
            for (String term : electronicsTerms) {
                TrendingProductDTO dto = new TrendingProductDTO();
                dto.setName(term);
                // Placeholder image (you'd fetch real images using an API like Google Custom Search)
                dto.setImageUrl("/images/trending-product-placeholder.png");
                trendingProducts.add(dto);
            }

            // Limit to top 5 trending products
            return trendingProducts.stream()
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Fallback to an empty list if the API call fails
            return Collections.emptyList();
        }
    }
}