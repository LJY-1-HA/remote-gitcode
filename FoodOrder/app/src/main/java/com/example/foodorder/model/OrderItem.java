package com.example.foodorder.model;

/**
 * 订单明细：下单时对菜品名称、价格做快照，菜品后续修改不影响历史订单
 */
public class OrderItem {
    public int orderId;
    public int dishId;
    public String dishName;
    public double price;
    public int quantity;

    public OrderItem() {
    }

    public OrderItem(int orderId, int dishId, String dishName, double price, int quantity) {
        this.orderId = orderId;
        this.dishId = dishId;
        this.dishName = dishName;
        this.price = price;
        this.quantity = quantity;
    }
}
