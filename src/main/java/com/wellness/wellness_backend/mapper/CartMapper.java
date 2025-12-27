package com.wellness.wellness_backend.mapper;

//MAPPER (ENTITY → DTO)

import com.wellness.wellness_backend.dto.*;
import com.wellness.wellness_backend.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toDTO(Cart cart) {

        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        new ProductDTO(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice()
                        ),
                        item.getQuantity(),
                        item.getPriceAtAddTime()
                ))
                .collect(Collectors.toList());

        return new CartDTO(
                cart.getId(),
                cart.getUserEmail(),
                cart.getCreatedAt(),
                items
        );
    }
}
