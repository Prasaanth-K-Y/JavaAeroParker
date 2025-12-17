package com.demo.ecommerce.service;

import com.demo.ecommerce.dao.ItemDao;
import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.exception.InsufficientStockException;
import com.demo.ecommerce.exception.ItemNotFoundException;
import com.demo.ecommerce.model.Item;

import com.demo.grpc.notification.NotificationRequest;
import com.demo.grpc.notification.NotificationServiceGrpc.NotificationServiceBlockingStub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemDao itemDao;

    @Mock
    private NotificationServiceBlockingStub notificationStub;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void setup() {
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
    }

    // ---------------------------------------------------------------------
    // TEST 1: Successful order placement
    // ---------------------------------------------------------------------
    @Test
    public void testPlaceOrder_Success() {
        // ARRANGE
        Item item = new Item("Laptop", 10, 1200);
        item.setItemId(101L);

        PlaceOrderRequest request = new PlaceOrderRequest(101L, 3);

        when(itemDao.findById(101L)).thenReturn(item);
        when(itemDao.update(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Item updated = itemService.placeOrder(request);

        // ASSERT
        assertEquals(7, updated.getQuantity());
        verify(notificationStub, never()).notifyInsufficientStock(any());
    }

    // ---------------------------------------------------------------------
    // TEST 2: Item not found
    // ---------------------------------------------------------------------
    @Test
    public void testPlaceOrder_ItemNotFound() {
        PlaceOrderRequest request = new PlaceOrderRequest(999L, 2);

        when(itemDao.findById(999L)).thenReturn(null);

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.placeOrder(request);
        });
    }

    // ---------------------------------------------------------------------
    // TEST 3: Insufficient stock + gRPC notification
    // ---------------------------------------------------------------------
    @Test
    public void testPlaceOrder_InsufficientStock() {
        // ARRANGE
        Item item = new Item("Laptop", 5, 55000);
        item.setItemId(200L);

        PlaceOrderRequest request = new PlaceOrderRequest(200L, 10);

        when(itemDao.findById(200L)).thenReturn(item);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        // ACT & ASSERT
        assertThrows(InsufficientStockException.class, () -> {
            itemService.placeOrder(request);
        });

        // ASSERT that gRPC notification was sent
        verify(notificationStub, times(1))
                .notifyInsufficientStock(any(NotificationRequest.class));
    }

    // ---------------------------------------------------------------------
    // TEST 4: Add new item
    // ---------------------------------------------------------------------
    @Test
    public void testAddNewItem_Success() {
        Item newItem = new Item("Mouse", 15, 500);

        when(itemDao.save(any(Item.class)))
                .thenAnswer(invocation -> {
                    Item saved = invocation.getArgument(0);
                    saved.setItemId(123L); // simulate generated ID
                    return saved;
                });

        Item saved = itemService.addNewItem(newItem);

        assertNotNull(saved.getItemId());
        assertEquals("Mouse", saved.getItemName());
        assertEquals(15, saved.getQuantity());
        assertEquals(500, saved.getPrice());
    }

    // ---------------------------------------------------------------------
    // TEST 5: Validate that price and quantity are integers (no decimals)
    // ---------------------------------------------------------------------
    @Test
    public void testItemWithIntegerPriceAndQuantity() {
        // ARRANGE - Create item with integer values
        Item item = new Item("Keyboard", 10, 1500);
        item.setItemId(150L);

        // ASSERT - Verify integer values (no decimals allowed)
        assertEquals(1500, item.getPrice());
        assertEquals(10, item.getQuantity());

        // ASSERT - Values are whole numbers
        assertTrue(item.getPrice() == Math.floor(item.getPrice()));
        assertTrue(item.getQuantity() == Math.floor(item.getQuantity()));
    }
}
