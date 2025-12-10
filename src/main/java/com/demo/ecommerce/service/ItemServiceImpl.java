package com.demo.ecommerce.service;

import com.demo.ecommerce.dao.ItemDao;
import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.exception.InsufficientStockException;
import com.demo.ecommerce.exception.ItemAlreadyExistsException;
import com.demo.ecommerce.exception.ItemNotFoundException;
import com.demo.ecommerce.model.Item;

// gRPC
import com.demo.grpc.notification.NotificationRequest;
import com.demo.grpc.notification.NotificationServiceGrpc.NotificationServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.List;
import java.util.Comparator;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Service("fastItemService")
public class ItemServiceImpl implements ItemService {

    private static final Logger logger =
            LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemDao itemDao;

    @GrpcClient("notification-service")
    private NotificationServiceBlockingStub notificationStub;

    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    // --------------------------
    // PLACE ORDER
    // --------------------------
    @Override
    @Transactional
    public Item placeOrder(PlaceOrderRequest orderRequest) {

        Item item = itemDao.findById(orderRequest.itemId());
        if (item == null) {
            throw new ItemNotFoundException("Item not found: " + orderRequest.itemId());
        }

        if (orderRequest.quantity() <= item.getQuantity()) {

            item.setQuantity(item.getQuantity() - orderRequest.quantity());
            return itemDao.update(item);
        }

        // ------------ SEND GRPC NOTIFICATION -----------
        String username = "UNKNOWN_USER";
        try {
            Object principal =
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            }
        } catch (Exception ignored) {}

        try {
            String failedOrderId = UUID.randomUUID().toString();

            NotificationRequest grpcRequest = NotificationRequest.newBuilder()
                    .setItemId(orderRequest.itemId())
                    .setOrderId(failedOrderId)
                    .setRequestedQty(orderRequest.quantity())
                    .setUserId(username)
                    .build();

            notificationStub.notifyInsufficientStock(grpcRequest);

        } catch (Exception e) {
            logger.error("FAILED to send gRPC notification: {}", e.getMessage());
        }

        throw new InsufficientStockException(
                "GRPC SENT.Insufficient stock. Requested = " + orderRequest.quantity() +
                        ", Available = " + item.getQuantity()
        );
    }

    // --------------------------
    // ADD NEW ITEM
    // --------------------------
    @Override
    @Transactional
    public Item addNewItem(Item newItem) {

        if (newItem.getItemId() == null || newItem.getItemId().isBlank()) {
            throw new IllegalArgumentException("Item ID is required.");
        }

        if (itemDao.exists(newItem.getItemId())) {
            throw new ItemAlreadyExistsException(
                    "Item already exists: " + newItem.getItemId());
        }

        return itemDao.save(newItem);
    }

    // --------------------------
    // GET ITEM BY ID
    // --------------------------
    @Override
    public Item getItemById(String itemId) {

        Item item = itemDao.findById(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item not found: " + itemId);
        }

        return item;
    }

    @Override
    public List<Item> getLowStockItems() {
        return itemDao.findAll().stream() 
            .filter(item -> item.getQuantity() < 5)   // lambda
            .sorted(Comparator.comparingInt(Item::getQuantity))
            .toList();
}
    @Override
    public List<Item> findAll() {
        return itemDao.findAll();
    }

}
