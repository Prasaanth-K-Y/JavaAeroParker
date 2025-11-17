package com.demo.ecommerce.service;

import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.exception.InsufficientStockException;
import com.demo.ecommerce.exception.ItemNotFoundException;
import com.demo.ecommerce.model.Item;
import com.demo.ecommerce.repository.ItemRepository;

// gRPC
import com.demo.grpc.notification.NotificationRequest;
import com.demo.grpc.notification.NotificationServiceGrpc.NotificationServiceBlockingStub;

import org.junit.*;
import org.junit.runner.RunWith;

import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

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

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
    }

    // ---------------------------------------------------------------------
    // TEST 1: Successful order placement
    // ---------------------------------------------------------------------
    @Test
    public void testPlaceOrder_Success() {

        // ARRANGE
        Item item =new Item("ITM200", "Laptop", 5, 55000.0);
        item.setPrice(90000.0);

        PlaceOrderRequest request = new PlaceOrderRequest("I101", 3);

        when(itemRepository.findById("I101")).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
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
    @Test(expected = ItemNotFoundException.class)
    public void testPlaceOrder_ItemNotFound() {

        PlaceOrderRequest request = new PlaceOrderRequest("BAD_ID", 2);

        when(itemRepository.findById("BAD_ID")).thenReturn(Optional.empty());

        itemService.placeOrder(request);
    }

    // ---------------------------------------------------------------------
    // TEST 3: Insufficient stock + gRPC notification
    // ---------------------------------------------------------------------
    @Test(expected = InsufficientStockException.class)
    public void testPlaceOrder_InsufficientStock() {

        // ARRANGE
        Item item = new Item("ITM200", "Laptop", 5, 55000.0)
;
        item.setPrice(55000.0);

        PlaceOrderRequest request = new PlaceOrderRequest("ITM200", 10);

        when(itemRepository.findById("ITM200")).thenReturn(Optional.of(item));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        // ACT
        try {
            itemService.placeOrder(request);
        } finally {
            // ASSERT
            verify(notificationStub, times(1))
                    .notifyInsufficientStock(any(NotificationRequest.class));
        }
    }
}
