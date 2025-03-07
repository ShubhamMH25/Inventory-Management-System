package com.tbc.dto;

import lombok.Data;

@Data
public class TrendingProductDTO {
    private String name;
    private String imageUrl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
    
    
}