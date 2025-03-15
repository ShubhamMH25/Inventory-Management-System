package com.tbc.dto;



import java.util.List;

public class BillRequestDTO {
    private Long customerId;
    private List<BillItemDTO> items;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<BillItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BillItemDTO> items) {
        this.items = items;
    }

    public static class BillItemDTO {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}