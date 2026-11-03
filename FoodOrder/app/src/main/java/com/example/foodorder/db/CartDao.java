package com.example.foodorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodorder.model.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车表操作：加购、数量增减、删除、清空、合计
 */
public class CartDao {

    private final DBHelper helper;

    public CartDao(Context context) {
        helper = DBHelper.getInstance(context);
    }

    /** 查询某用户的购物车（联表带出菜品信息） */
    public List<CartItem> listByUser(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT c.dish_id, d.name, d.price, d.emoji, c.quantity " +
                        "FROM cart c JOIN dishes d ON c.dish_id = d.id " +
                        "WHERE c.user_id = ? ORDER BY c.id DESC",
                new String[]{String.valueOf(userId)});
        List<CartItem> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(new CartItem(c.getInt(0), c.getString(1), c.getDouble(2), c.getString(3), c.getInt(4)));
        }
        c.close();
        return list;
    }

    /** 加入购物车：已存在则数量 +1 */
    public void add(int userId, int dishId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int qty = getQuantity(db, userId, dishId);
        if (qty > 0) {
            ContentValues cv = new ContentValues();
            cv.put("quantity", qty + 1);
            db.update("cart", cv, "user_id = ? AND dish_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(dishId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("dish_id", dishId);
            cv.put("quantity", 1);
            db.insert("cart", null, cv);
        }
    }

    /** 数量 -1，减到 0 自动移除 */
    public void decrease(int userId, int dishId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int qty = getQuantity(db, userId, dishId);
        if (qty <= 1) {
            remove(userId, dishId);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("quantity", qty - 1);
            db.update("cart", cv, "user_id = ? AND dish_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(dishId)});
        }
    }

    public void remove(int userId, int dishId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("cart", "user_id = ? AND dish_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(dishId)});
    }

    /** 清空购物车（下单成功后调用） */
    public void clear(int userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});
    }

    /** 购物车商品总件数（用于底部角标） */
    public int countByUser(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IFNULL(SUM(quantity), 0) FROM cart WHERE user_id = ?",
                new String[]{String.valueOf(userId)});
        int n = 0;
        if (c.moveToFirst()) {
            n = c.getInt(0);
        }
        c.close();
        return n;
    }

    /** 购物车总金额 */
    public double totalByUser(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(d.price * c.quantity), 0) FROM cart c JOIN dishes d ON c.dish_id = d.id WHERE c.user_id = ?",
                new String[]{String.valueOf(userId)});
        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    private int getQuantity(SQLiteDatabase db, int userId, int dishId) {
        Cursor c = db.rawQuery("SELECT quantity FROM cart WHERE user_id = ? AND dish_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(dishId)});
        int qty = 0;
        if (c.moveToFirst()) {
            qty = c.getInt(0);
        }
        c.close();
        return qty;
    }
}
