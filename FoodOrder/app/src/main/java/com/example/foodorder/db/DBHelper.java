package com.example.foodorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.foodorder.model.Dish;

/**
 * 数据库帮助类：建表 + 预置种子数据（管理员账号、示例菜品）
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "food_order.db";
    public static final int DB_VERSION = 1;

    private static volatile DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
                }
            }
        }
        return instance;
    }

    private DBHelper(@Nullable Context context, @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "phone TEXT DEFAULT '', " +
                "address TEXT DEFAULT '', " +
                "role INTEGER DEFAULT 0)");

        // 菜品表
        db.execSQL("CREATE TABLE dishes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT DEFAULT '推荐', " +
                "price REAL DEFAULT 0, " +
                "emoji TEXT DEFAULT '🍽️', " +
                "descr TEXT DEFAULT '', " +
                "sales INTEGER DEFAULT 0, " +
                "available INTEGER DEFAULT 1)");

        // 购物车表（同一用户同一菜品唯一）
        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "dish_id INTEGER NOT NULL, " +
                "quantity INTEGER DEFAULT 1, " +
                "UNIQUE(user_id, dish_id))");

        // 订单表
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "total REAL DEFAULT 0, " +
                "status INTEGER DEFAULT 0, " +
                "address TEXT DEFAULT '', " +
                "phone TEXT DEFAULT '', " +
                "remark TEXT DEFAULT '', " +
                "create_time TEXT DEFAULT '')");

        // 订单明细表（名称、价格做快照）
        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "dish_id INTEGER, " +
                "dish_name TEXT, " +
                "price REAL, " +
                "quantity INTEGER)");

        seed(db);
    }

    /** 预置数据：管理员 + 示例顾客 + 示例菜品 */
    private void seed(SQLiteDatabase db) {
        // 管理员：admin / 123456
        insertUser(db, "admin", "123456", "13800000000", "餐厅后台", 1);
        // 示例顾客：user / 123456
        insertUser(db, "user", "123456", "13900000000", "幸福小区 3 号楼 502", 0);

        insertDish(db, new Dish(0, "宫保鸡丁", "炒菜", 28, "🥘", "经典川菜，花生与鸡丁的绝配", 156, true));
        insertDish(db, new Dish(0, "鱼香肉丝", "炒菜", 26, "🍲", "咸甜酸辣，下饭神器", 132, true));
        insertDish(db, new Dish(0, "番茄炒蛋", "炒菜", 16, "🍅", "酸甜可口，家常味道", 98, true));
        insertDish(db, new Dish(0, "红烧牛肉面", "主食", 22, "🍜", "浓汤牛腩，劲道面条", 187, true));
        insertDish(db, new Dish(0, "扬州炒饭", "主食", 18, "🍚", "粒粒分明，配料十足", 165, true));
        insertDish(db, new Dish(0, "麻辣香锅", "炒菜", 45, "🍲", "荤素自选，麻辣鲜香", 76, true));
        insertDish(db, new Dish(0, "香辣炸鸡", "小吃", 22, "🍗", "外酥里嫩，越吃越香", 143, true));
        insertDish(db, new Dish(0, "黄金薯条", "小吃", 12, "🍟", "酥脆可口，配番茄酱", 121, true));
        insertDish(db, new Dish(0, "现烤蛋挞", "甜品", 8, "🥧", "酥皮嫩心，现烤出炉", 87, true));
        insertDish(db, new Dish(0, "提拉米苏", "甜品", 18, "🍰", "意式经典，入口即化", 65, true));
        insertDish(db, new Dish(0, "珍珠奶茶", "饮品", 15, "🧋", "醇香奶茶，Q 弹珍珠", 210, true));
        insertDish(db, new Dish(0, "冰鲜柠檬水", "饮品", 10, "🍋", "酸甜清爽，解腻必备", 178, true));
    }

    private void insertUser(SQLiteDatabase db, String username, String password,
                            String phone, String address, int role) {
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("phone", phone);
        cv.put("address", address);
        cv.put("role", role);
        db.insert("users", null, cv);
    }

    private void insertDish(SQLiteDatabase db, Dish dish) {
        ContentValues cv = new ContentValues();
        cv.put("name", dish.name);
        cv.put("category", dish.category);
        cv.put("price", dish.price);
        cv.put("emoji", dish.emoji);
        cv.put("descr", dish.descr);
        cv.put("sales", dish.sales);
        cv.put("available", dish.available ? 1 : 0);
        db.insert("dishes", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 课程项目：升级时删表重建（数据会清空）
        String[] tables = {"order_items", "orders", "cart", "dishes", "users"};
        for (String table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }
}
