package com.bits.foodie.dto;

import com.bits.foodie.model.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuDTO {
    private int menuId;
    private List<ItemDTO> items;
}
