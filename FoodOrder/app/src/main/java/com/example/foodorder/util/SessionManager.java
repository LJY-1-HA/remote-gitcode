package com.example.foodorder.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 登录会话管理：基于 SharedPreferences 记录当前登录用户 id，实现自动登录
 */
public class SessionManager {

    private static final String PREF_NAME = "food_order_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** 登录成功后保存会话 */
    public void save(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    /** 当前登录用户 id，未登录返回 -1 */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /** 退出登录 */
    public void clear() {
        prefs.edit().clear().apply();
    }
}
