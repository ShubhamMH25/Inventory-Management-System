package com.tbc.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LogoService {

    @Autowired
    private RestTemplate restTemplate;

        public String getBrandLogo(String brandName) {
            String url = "https://en.wikipedia.org/w/api.php?action=query&titles=" + brandName + 
                         "&prop=pageimages&format=json&pithumbsize=500";

            String response = restTemplate.getForObject(url, String.class);

            JSONObject jsonObject = new JSONObject(response);
            JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");

            String logoPath = pages.keys().next();
            JSONObject logoData = pages.getJSONObject(logoPath);

            return "https://upload.wikimedia.org/wikipedia/commons/" + 
                   logoData.optString("pageimage");
    }
}
