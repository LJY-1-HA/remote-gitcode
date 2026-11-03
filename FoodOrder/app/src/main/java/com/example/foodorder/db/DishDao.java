package com.example.foodorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodorder.model.Dish;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品表操作：查询（支持分类 + 关键字过滤）、增删改、销量排行
 */
public class DishDao {

    public static final String CATEGORY_ALL = "全部";

    private final DBHelper helper;

    public DishDao(Context context) {
        helper = DBHelper.getInstance(context);
    }

    /** 顾客端查询：仅上架菜品，category 为 null 或 “全部” 时不限分类 */
    public List<Dish> listOnSale(String category, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT id, name, category, price, emoji, descr, sales, available FROM dishes WHERE available = 1");
        List<String> args = new ArrayList<>();
        if (category != null && !category.isEmpty() && !CATEGORY_ALL.equals(category)) {
            sql.append(" AND category = ?");
            args.add(category);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            args.add("%" + keyword.trim() + "%");
        }
        sql.append(" ORDER BY sales DESC");
        return queryList(sql.toString(), args.toArray(new String[0]));
    }

    /** 管理端查询：全部菜品 */
    public List<Dish> listAll() {
        return queryList("SELECT id, name, category, price, emoji, descr, sales, available FROM dishes ORDER BY id DESC", null);
    }

    public Dish getById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, name, category, price, emoji, descr, sales, available FROM dishes WHERE id = ?",
                new String[]{String.valueOf(id)});
        Dish dish = null;
        if (c.moveToFirst()) {
            dish = cursorToDish(c);
        }
        c.close();
        return dish;
    }

    /** 所有分类（去重，用于顾客端分类栏） */
    public List<String> categories() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT category FROM dishes ORDER BY category", null);
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(c.getString(0));
        }
        c.close();
        return list;
    }

    /** 销量 Top N */
    public List<Dish> topSales(int limit) {
        return queryList("SELECT id, name, category, price, emoji, descr, sales, available FROM dishes ORDER BY sales DESC LIMIT " + limit, null);
    }

    public long insert(Dish dish) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert("dishes", null, toValues(dish));
    }

    public int update(Dish dish) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update("dishes", toValues(dish), "id = ?",
                new String[]{String.valueOf(dish.id)});
    }

    /** 删除菜品（同时清理购物车中的引用，订单明细已做快照不受影响） */
    public int delete(int dishId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("cart", "dish_id = ?", new String[]{String.valueOf(dishId)});
        return db.delete("dishes", "id = ?", new String[]{String.valueOf(dishId)});
    }

    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM dishes", null);
        int n = 0;
        if (c.moveToFirst()) {
            n = c.getInt(0);
        }
        c.close();
        return n;
    }

    private List<Dish> queryList(String sql, String[] args) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        List<Dish> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(cursorToDish(c));
        }
        c.close();
        return list;
    }

    private ContentValues toValues(Dish dish) {
        ContentValues cv = new ContentValues();
        cv.put("name", dish.name);
        cv.put("category", dish.category);
        cv.put("price", dish.price);
        cv.put("emoji", dish.emoji);
        cv.put("descr", dish.descr);
        cv.put("sales", dish.sales);
        cv.put("available", dish.available ? 1 : 0);
        return cv;
    }

    private Dish cursorToDish(Cursor c) {
        return new Dish(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getDouble(3),
                c.getString(4),
                c.getString(5),
                c.getInt(6),
                c.getInt(7) == 1);
    }
}
