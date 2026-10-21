package com.bits.foodie.web;

import com.bits.foodie.dto.CatalogDtos.MenuDTO;
import com.bits.foodie.dto.CatalogDtos.RestaurantDTO;
import com.bits.foodie.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Public, read-only browsing. */
@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final CatalogService catalog;

    public RestaurantController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public List<RestaurantDTO> list() {
        return catalog.listActive();
    }

    @GetMapping("/{id}")
    public RestaurantDTO get(@PathVariable Long id) {
        return catalog.get(id);
    }

    @GetMapping("/{id}/menu")
    public MenuDTO menu(@PathVariable Long id) {
        return catalog.menu(id);
    }
}
