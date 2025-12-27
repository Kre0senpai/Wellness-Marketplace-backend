package com.wellness.wellness_backend.dto;

import jakarta.validation.constraints.Min;

public class UpdateQuantityRequest {

    @Min(1)
    private int quantity;

    public UpdateQuantityRequest() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
