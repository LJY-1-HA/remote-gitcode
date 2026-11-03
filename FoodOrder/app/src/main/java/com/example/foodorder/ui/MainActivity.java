package com.example.foodorder.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityMainBinding;
import com.example.foodorder.db.CartDao;
import com.example.foodorder.fragment.CartFragment;
import com.example.foodorder.fragment.MenuFragment;
import com.example.foodorder.fragment.OrdersFragment;
import com.example.foodorder.fragment.ProfileFragment;
import com.example.foodorder.util.SessionManager;
import com.google.android.material.badge.BadgeDrawable;

/**
 * 顾客端主页：底部导航切换 点餐 / 购物车 / 订单 / 我的
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TAB = "tab";
    public static final int TAB_MENU = 0;
    public static final int TAB_CART = 1;
    public static final int TAB_ORDERS = 2;
    public static final int TAB_PROFILE = 3;

    private static final int[] MENU_IDS = {
            R.id.nav_menu, R.id.nav_cart, R.id.nav_orders, R.id.nav_profile};

    private ActivityMainBinding binding;
    private SessionManager session;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        userId = session.getUserId();
        if (userId == -1) {
            goLogin();
            return;
        }

        binding.navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_menu) {
                switchFragment(new MenuFragment());
            } else if (id == R.id.nav_cart) {
                switchFragment(new CartFragment());
            } else if (id == R.id.nav_orders) {
                switchFragment(new OrdersFragment());
            } else if (id == R.id.nav_profile) {
                switchFragment(new ProfileFragment());
            }
            return true;
        });

        // 默认显示点餐页（或从结算页带过来的指定 Tab）
        if (savedInstanceState == null) {
            handleIntent(getIntent(), true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 下单成功后从结算页跳回“订单”页
        handleIntent(intent, false);
    }

    private void handleIntent(Intent intent, boolean setDefault) {
        int tab = intent.getIntExtra(EXTRA_TAB, -1);
        if (tab < 0 || tab >= MENU_IDS.length) {
            tab = TAB_MENU;
        }
        binding.navView.setSelectedItemId(MENU_IDS[tab]);
        if (setDefault && tab == TAB_MENU) {
            // setSelectedItemId 不会触发首次回调，这里手动切换
            switchFragment(new MenuFragment());
        }
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    /** 更新购物车 Tab 上的数量角标，供各 Fragment 调用 */
    public void notifyCartChanged() {
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (userId == -1) {
            return;
        }
        int count = new CartDao(this).countByUser(userId);
        if (count > 0) {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(count);
        } else {
            binding.navView.removeBadge(R.id.nav_cart);
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
