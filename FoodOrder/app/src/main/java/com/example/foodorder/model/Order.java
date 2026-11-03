package com.example.foodorder.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体及订单状态流转
 * 状态流转：待接单 → 制作中 → 配送中 → 已完成；待接单可取消
 */
public class Order {
    public static final int STATUS_PENDING = 0;     // 待接单
    public static final int STATUS_COOKING = 1;     // 制作中
    public static final int STATUS_DELIVERING = 2;  // 配送中
    public static final int STATUS_DONE = 3;        // 已完成
    public static final int STATUS_CANCELED = -1;   // 已取消

    public int id;
    public int userId;
    public String username;      // 下单顾客（管理端联表查询时填充）
    public double total;
    public int status = STATUS_PENDING;
    public String address;
    public String phone;
    public String remark;
    public String createTime;
    public List<OrderItem> items = new ArrayList<>();

    /** 状态中文描述 */
    public static String statusText(int status) {
        switch (status) {
            case STATUS_PENDING: return "待接单";
            case STATUS_COOKING: return "制作中";
            case STATUS_DELIVERING: return "配送中";
            case STATUS_DONE: return "已完成";
            case STATUS_CANCELED: return "已取消";
            default: return "未知";
        }
    }
}
