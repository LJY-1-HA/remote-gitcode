package com.example.foodorder.model;

/**
 * 购物车条目（查询时联表带出菜品快照信息）
 */
public class CartItem {
    public int dishId;
    public String name;
    public double price;
    public String emoji;
    public int quantity;

    public CartItem(int dishId, String name, double price, String emoji, int quantity) {
        this.dishId = dishId;
        this.name = name;
        this.price = price;
        this.emoji = emoji;
        this.quantity = quantity;
    }

    /** 小计金额 */
    public double subtotal() {
        return price * quantity;
    }
}
