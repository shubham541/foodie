package com.bits.foodie.mappers;

import com.bits.foodie.dto.ItemDTO;
import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.model.Item;
import com.bits.foodie.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuMapper {
    @Autowired
    ItemMapper itemMapper;

    public Menu mapMenuDTOtoMenu(MenuDTO menuDTO) {
        Menu menu = new Menu();
        menu.setMenuId(menuDTO.getMenuId());

        List<Item> items = menuDTO.getItems().stream()
                .map(itemDTO -> {
                    Item item = itemMapper.mapItemDTOtoItem(itemDTO);
                    item.setMenu(menu);  // Set the back-reference to Menu
                    return item;
                })
                .collect(Collectors.toList());

        menu.setItems(items);
        return menu;
    }

    public MenuDTO mapMenutoMenuDTO(Menu menu) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menu.getMenuId());

        List<ItemDTO> itemDTOs = menu.getItems().stream()
                .map(itemMapper::mapItemToItemDTO)
                .collect(Collectors.toList());

        menuDTO.setItems(itemDTOs);
        return menuDTO;
    }
}
