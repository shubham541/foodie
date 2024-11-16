package com.bits.foodie.test.service;

import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.mappers.MenuMapper;
import com.bits.foodie.model.Menu;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.service.impl.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.bits.foodie.utility.FoodieConstants.RESTAURANT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceImplTest {

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MenuMapper menuMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRestaurant() {
        RestaurantDTO restaurantDTO = new RestaurantDTO(
                "Test Restaurant",
                "test@example.com",
                "password",
                "123 Street",
                "ROLE_RESTAURANT",
                "Indian",
                true,
                null,
                null,
                List.of("Zone 1"));

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseDTO response = restaurantService.addRestaurant(restaurantDTO);

        assertNotNull(response);
        assertEquals("Registration Successful", response.getMessage());

        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void testLoginRestaurant_Success() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password", RESTAURANT);
        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                "test@example.com",
                "encodedPassword",
                "123 Street",
                "RESTAURANT",
                "Indian",
                true,
                null,
                null,
                List.of("Zone 1"));

        when(restaurantRepository.findByRestaurantEmail("test@example.com")).thenReturn(restaurant);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(restaurantRepository.findOneByRestaurantEmailAndPassword("test@example.com", "encodedPassword"))
                .thenReturn(Optional.of(restaurant));

        ResponseDTO response = restaurantService.loginRestaurant(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Login Success", response.getMessage());
    }

    @Test
    void testFetchRestaurantById() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        when(restaurantRepository.findByRestaurantId(1)).thenReturn(restaurant);

        Restaurant fetchedRestaurant = restaurantService.fetchRestaurantById(1);

        assertNotNull(fetchedRestaurant);
        assertEquals(1, fetchedRestaurant.getRestaurantId());
    }

    @Test
    void testUpdateRestaurant() {
        RestaurantDTO restaurantDTO = new RestaurantDTO(
                "Updated Restaurant",
                null,
                null,
                "456 Avenue",
                null,
                null,
                true,
                null,
                null,
                List.of("Zone 1"));

        Restaurant restaurant = new Restaurant(
                "Test Restaurant",
                "test@example.com",
                "encodedPassword",
                "123 Street",
                "ROLE_RESTAURANT",
                "Indian",
                true,
                null,
                null,
                List.of("Zone 1"));

        when(restaurantRepository.findByRestaurantId(1)).thenReturn(restaurant);

        Restaurant updatedRestaurant = restaurantService.updateRestaurant(1, restaurantDTO);

        assertNotNull(updatedRestaurant);
        assertEquals("Updated Restaurant", updatedRestaurant.getRestaurantName());
        assertEquals("456 Avenue", updatedRestaurant.getRestaurantAdd());
        assertEquals("Zone 1", updatedRestaurant.getDeliveryZones().getFirst());

        verify(restaurantRepository, times(1)).save(updatedRestaurant);
    }

    @Test
    void testUpdateMenu() {
        MenuDTO menuDTO = new MenuDTO();
        Menu menu = new Menu();
        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(menu);

        when(restaurantRepository.findByRestaurantId(1)).thenReturn(restaurant);
        when(menuMapper.mapMenuDTOtoMenu(menuDTO)).thenReturn(menu);

        Menu updatedMenu = restaurantService.updateMenu(1, menuDTO);

        assertNotNull(updatedMenu);
        assertEquals(menu, updatedMenu);

        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    void testFetchMenu() {
        Menu menu = new Menu();
        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(menu);

        when(restaurantRepository.findByRestaurantId(1)).thenReturn(restaurant);

        Menu fetchedMenu = restaurantService.fetchMenu(1);

        assertNotNull(fetchedMenu);
        assertEquals(menu, fetchedMenu);
    }

    @Test
    void testViewOrders() {
        Order order1 = new Order();
        order1.setRestaurantId(1);
        Order order2 = new Order();
        order2.setRestaurantId(2);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = restaurantService.viewOrders(1);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(1, orders.getFirst().getRestaurantId());
    }

    @Test
    void testDeactivateRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        restaurant.setActive(true);

        when(restaurantRepository.findByRestaurantId(1)).thenReturn(restaurant);

        restaurantService.deactivateRestaurant(1);

        assertFalse(restaurant.isActive());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    void testSearchRestaurant() {
        List<Restaurant> restaurants = new ArrayList<>();
        when(restaurantRepository.searchRestaurants("Test", "Indian", true, "Paneer"))
                .thenReturn(restaurants);

        List<Restaurant> result = restaurantService.searchRestaurant("Test", "Indian", true, "Paneer");

        assertNotNull(result);
        assertEquals(restaurants, result);
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setRestaurantId(1);
        order.setOrderId(100);
        order.setStatus("ORDER_PLACED");

        when(orderRepository.findByOrderId(100)).thenReturn(order);

        Order updatedOrder = restaurantService.updateOrderStatus(1, 100, "DELIVERED");

        assertNotNull(updatedOrder);
        assertEquals("DELIVERED", updatedOrder.getStatus());

        verify(orderRepository, times(1)).save(order);
    }
}
