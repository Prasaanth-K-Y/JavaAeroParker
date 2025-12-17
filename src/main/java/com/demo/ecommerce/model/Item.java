package com.demo.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "SpringItems")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO INCREMENT
    @Column(name = "item_id")
    private Long itemId;

    @NotBlank(message = "Item name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Item name must contain only alphabets and spaces")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Positive(message = "Quantity must be greater than 0")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be a whole number without decimals")
    @Column(name = "quantity")
    private int quantity;

    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 0, message = "Price must be a whole number without decimals")
    @Column(name = "price")
    private int price;

    public Item() {}

    // Constructor without ID (ID is auto-generated)
    public Item(String itemName, int quantity, int price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    // GETTERS & SETTERS
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
