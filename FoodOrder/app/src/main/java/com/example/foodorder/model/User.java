package com.example.foodorder.model;

/**
 * 用户实体：role = 0 普通用户（顾客），role = 1 管理员
 */
public class User {
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    public int id;
    public String username;
    public String password;
    public String phone;
    public String address;
    public int role = ROLE_USER;

    public User() {
    }

    public User(int id, String username, String password, String phone, String address, int role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }
}
