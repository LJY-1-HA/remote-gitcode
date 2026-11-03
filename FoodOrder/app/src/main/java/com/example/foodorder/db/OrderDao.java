package com.example.foodorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.Order;
import com.example.foodorder.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 订单表操作：下单（事务）、查询、取消、状态推进、统计
 */
public class OrderDao {

    private final DBHelper helper;

    public OrderDao(Context context) {
        helper = DBHelper.getInstance(context);
    }

    /**
     * 下单（事务）：
     * 1. 写入订单主表；2. 写入订单明细；3. 累加菜品销量；4. 清空购物车
     * 返回新订单 id，失败返回 -1
     */
    public long place(int userId, String address, String phone, String remark, List<CartItem> cartItems) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.subtotal();
            }
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // 1. 订单主表
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("total", total);
            cv.put("status", Order.STATUS_PENDING);
            cv.put("address", address);
            cv.put("phone", phone);
            cv.put("remark", remark);
            cv.put("create_time", now);
            long orderId = db.insert("orders", null, cv);
            if (orderId == -1) {
                return -1;
            }

            // 2. 明细 + 3. 销量
            for (CartItem item : cartItems) {
                ContentValues itemCv = new ContentValues();
                itemCv.put("order_id", orderId);
                itemCv.put("dish_id", item.dishId);
                itemCv.put("dish_name", item.name);
                itemCv.put("price", item.price);
                itemCv.put("quantity", item.quantity);
                db.insert("order_items", null, itemCv);

                db.execSQL("UPDATE dishes SET sales = sales + ? WHERE id = ?",
                        new Object[]{item.quantity, item.dishId});
            }

            // 4. 清空购物车
            db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});

            db.setTransactionSuccessful();
            return orderId;
        } finally {
            db.endTransaction();
        }
    }

    /** 顾客端：查询某用户订单（含明细），最新在前 */
    public List<Order> listByUser(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, user_id, total, status, address, phone, remark, create_time " +
                "FROM orders WHERE user_id = ? ORDER BY id DESC", new String[]{String.valueOf(userId)});
        List<Order> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(cursorToOrder(c));
        }
        c.close();
        loadItems(db, list);
        return list;
    }

    /** 管理端：查询全部订单（联表带出顾客用户名），最新在前 */
    public List<Order> listAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT o.id, o.user_id, o.total, o.status, o.address, o.phone, o.remark, o.create_time, u.username " +
                "FROM orders o LEFT JOIN users u ON o.user_id = u.id ORDER BY o.id DESC", null);
        List<Order> list = new ArrayList<>();
        while (c.moveToNext()) {
            Order order = cursorToOrder(c);
            order.username = c.getString(8);
            list.add(order);
        }
        c.close();
        loadItems(db, list);
        return list;
    }

    /** 取消订单（仅待接单状态可取消） */
    public int cancel(int orderId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", Order.STATUS_CANCELED);
        return db.update("orders", cv, "id = ? AND status = ?",
                new String[]{String.valueOf(orderId), String.valueOf(Order.STATUS_PENDING)});
    }

    /** 商家推进状态：待接单 → 制作中 → 配送中 → 已完成 */
    public int nextStatus(int orderId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE orders SET status = status + 1 WHERE id = ? AND status < ?",
                new Object[]{orderId, Order.STATUS_DONE});
        return 1;
    }

    /** 今日订单数 */
    public int countToday() {
        String today = todayPrefix();
        return countBySql("SELECT COUNT(*) FROM orders WHERE create_time LIKE ?", new String[]{today});
    }

    /** 今日营业额（不含已取消订单） */
    public double incomeToday() {
        String today = todayPrefix();
        return sumBySql("SELECT IFNULL(SUM(total), 0) FROM orders WHERE status != ? AND create_time LIKE ?",
                new String[]{String.valueOf(Order.STATUS_CANCELED), today});
    }

    public int countAll() {
        return countBySql("SELECT COUNT(*) FROM orders", null);
    }

    public double incomeAll() {
        return sumBySql("SELECT IFNULL(SUM(total), 0) FROM orders WHERE status != ?",
                new String[]{String.valueOf(Order.STATUS_CANCELED)});
    }

    private String todayPrefix() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + "%";
    }

    private int countBySql(String sql, String[] args) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        int n = 0;
        if (c.moveToFirst()) {
            n = c.getInt(0);
        }
        c.close();
        return n;
    }

    private double sumBySql(String sql, String[] args) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        double v = 0;
        if (c.moveToFirst()) {
            v = c.getDouble(0);
        }
        c.close();
        return v;
    }

    /** 批量加载订单明细 */
    private void loadItems(SQLiteDatabase db, List<Order> orders) {
        for (Order order : orders) {
            Cursor c = db.rawQuery("SELECT order_id, dish_id, dish_name, price, quantity " +
                            "FROM order_items WHERE order_id = ?",
                    new String[]{String.valueOf(order.id)});
            while (c.moveToNext()) {
                order.items.add(new OrderItem(c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3), c.getInt(4)));
            }
            c.close();
        }
    }

    private Order cursorToOrder(Cursor c) {
        Order order = new Order();
        order.id = c.getInt(0);
        order.userId = c.getInt(1);
        order.total = c.getDouble(2);
        order.status = c.getInt(3);
        order.address = c.getString(4);
        order.phone = c.getString(5);
        order.remark = c.getString(6);
        order.createTime = c.getString(7);
        return order;
    }
}
