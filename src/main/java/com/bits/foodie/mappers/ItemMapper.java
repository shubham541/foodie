package com.bits.foodie.mappers;

import com.bits.foodie.dto.ItemDTO;
import com.bits.foodie.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemDTO mapItemToItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemId(item.getItemId());
        itemDTO.setItemName(item.getItemName());
        itemDTO.setItemPrice(item.getItemPrice());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setAvailable(item.isAvailable());
        return itemDTO;

    }

    public Item mapItemDTOtoItem(ItemDTO itemDTO) {
        Item item = new Item();
        item.setItemId(itemDTO.getItemId());
        item.setItemName(itemDTO.getItemName());
        item.setItemPrice(itemDTO.getItemPrice());
        item.setDescription(itemDTO.getDescription());
        item.setAvailable(itemDTO.isAvailable());
        return item;

    }
}
