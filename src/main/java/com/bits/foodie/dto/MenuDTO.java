package com.bits.foodie.dto;

import com.bits.foodie.model.Item;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuDTO {

    private int menuId;

    @NotEmpty(message = "Menu must contain at least one item")
    private List<ItemDTO> items;
}
