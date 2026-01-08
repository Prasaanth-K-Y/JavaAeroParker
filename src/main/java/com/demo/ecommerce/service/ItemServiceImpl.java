package com.demo.ecommerce.service;

import com.demo.ecommerce.dao.ItemDao;
import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.exception.*;
import com.demo.ecommerce.model.Item;

import com.demo.grpc.notification.NotificationRequest;
import com.demo.grpc.notification.NotificationServiceGrpc.NotificationServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.Comparator;

@Service("fastItemService")
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final NotificationServiceBlockingStub notificationStub;

    public ItemServiceImpl(ItemDao itemDao,
                          @GrpcClient("notification-service") NotificationServiceBlockingStub notificationStub) {
        this.itemDao = itemDao;
        this.notificationStub = notificationStub;
    }

    // ------------------- PLACE ORDER -------------------
    @Override
    @Transactional
    public Item placeOrder(PlaceOrderRequest orderRequest) {

        Item item = itemDao.findById(orderRequest.itemId());
        if (item == null) {
            throw new ItemNotFoundException("Item not found: " + orderRequest.itemId());
        }

        if (orderRequest.quantity() <= item.getQuantity()) {
            item.setQuantity(item.getQuantity() - orderRequest.quantity());
            return itemDao.update(item);   // Transaction auto-commits
        }

        boolean grpcSuccess = sendInsufficientStockNotification(orderRequest, item);
        String grpcStatus = grpcSuccess ? "GRPC SENT" : "GRPC FAILED";

        throw new InsufficientStockException(
                grpcStatus + ". Insufficient stock. Requested = " + orderRequest.quantity() +
                        ", Available = " + item.getQuantity()
        );
    }

    private boolean sendInsufficientStockNotification(PlaceOrderRequest req, Item item) {
        String username = getAuthenticatedUsername();
        String failedOrderId = UUID.randomUUID().toString();

        NotificationRequest grpcReq = NotificationRequest.newBuilder()
                .setItemId(String.valueOf(req.itemId()))
                .setOrderId(failedOrderId)
                .setRequestedQty(req.quantity())
                .setUserId(username)
                .build();

        try {
            notificationStub.notifyInsufficientStock(grpcReq);
            System.out.println("GRPC SUCCESS: Insufficient stock notification sent for item " + item.getItemName());
            return true;
        } catch (Exception e) {
            System.err.println("GRPC FAILED: Unable to send notification - " + e.getMessage());
            return false;
        }
    }

    private String getAuthenticatedUsername() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "UNKNOWN_USER";
        } catch (Exception ignored) {
            return "UNKNOWN_USER";
        }
    }

    // ------------------- ADD ITEM -------------------

    @Override
    @Transactional
    public Item addNewItem(Item newItem) {

    if (newItem.getItemId() != null) {
        throw new IllegalArgumentException("New items must NOT provide itemId. It is auto-generated.");
    }

    // Check if item with same name already exists
    Item existingItem = itemDao.findByName(newItem.getItemName());
    if (existingItem != null) {
        throw new ItemAlreadyExistsException("Item with name '" + newItem.getItemName() + "' already exists with ID: " + existingItem.getItemId());
    }

    return itemDao.save(newItem);
}


    // ------------------- GETTERS -------------------
    @Override
    public Item getItemById(Long itemId) {
        Item item = itemDao.findById(itemId);

        if (item == null) {
            throw new ItemNotFoundException("Item not found: " + itemId);
        }
        return item;
    }

    @Override
    public List<Item> getLowStockItems() {
        return itemDao.findAll().stream()
                .filter(item -> item.getQuantity() < 5)
                .sorted(Comparator.comparingInt(Item::getQuantity))
                .toList();
    }

    @Override
    public List<Item> findAll() {
        return itemDao.findAll();
    }

    // ------------------- ORDER PROCESSING (for JSP page) -------------------
    @Transactional
    public Item processOrderWithLowStockCheck(Long itemId, int quantity) {
        Item item = itemDao.findById(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item not found: " + itemId);
        }

        // Check if requested quantity exceeds available stock
        if (quantity > item.getQuantity()) {
            // Send insufficient stock notification via gRPC and track success/failure
            boolean grpcSuccess = sendInsufficientStockNotification(
                new PlaceOrderRequest(itemId, quantity),
                item
            );

            String grpcStatus = grpcSuccess ? "GRPC SENT" : "GRPC FAILED";
            throw new InsufficientStockException(
                grpcStatus + ". Insufficient stock. Requested = " + quantity +
                ", Available = " + item.getQuantity()
            );
        }

        // Update quantity - no gRPC notification for successful orders
        item.setQuantity(item.getQuantity() - quantity);
        Item updatedItem = itemDao.update(item);

        return updatedItem;
    }

}
