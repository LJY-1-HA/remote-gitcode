package com.example.foodorder.model;

/**
 * 菜品实体：available = 1 上架中，0 已下架
 */
public class Dish {
    public int id;
    public String name;
    public String category;
    public double price;
    public String emoji;
    public String descr;
    public int sales;
    public boolean available = true;

    public Dish() {
    }

    public Dish(int id, String name, String category, double price, String emoji, String descr, int sales, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.emoji = emoji;
        this.descr = descr;
        this.sales = sales;
        this.available = available;
    }
}
