package com.example.foodorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodorder.model.User;

/**
 * 用户表操作：注册、登录、查询、修改联系方式
 */
public class UserDao {

    private final DBHelper helper;

    public UserDao(Context context) {
        helper = DBHelper.getInstance(context);
    }

    /** 注册新用户（顾客），成功返回用户 id，用户名已存在返回 -1 */
    public long register(String username, String password, String phone, String address) {
        if (isExists(username)) {
            return -1;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("phone", phone);
        cv.put("address", address);
        cv.put("role", User.ROLE_USER);
        return db.insert("users", null, cv);
    }

    public boolean isExists(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM users WHERE username = ?",
                new String[]{username});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    /** 登录校验，成功返回 User，失败返回 null */
    public User login(String username, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, username, password, phone, address, role FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        User user = null;
        if (c.moveToFirst()) {
            user = cursorToUser(c);
        }
        c.close();
        return user;
    }

    /** 按 id 查询用户 */
    public User getById(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, username, password, phone, address, role FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)});
        User user = null;
        if (c.moveToFirst()) {
            user = cursorToUser(c);
        }
        c.close();
        return user;
    }

    /** 修改联系方式（手机号 + 地址） */
    public int updateContact(int userId, String phone, String address) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone", phone);
        cv.put("address", address);
        return db.update("users", cv, "id = ?", new String[]{String.valueOf(userId)});
    }

    /** 注册用户总数 */
    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM users", null);
        int n = 0;
        if (c.moveToFirst()) {
            n = c.getInt(0);
        }
        c.close();
        return n;
    }

    private User cursorToUser(Cursor c) {
        return new User(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getInt(5));
    }
}
