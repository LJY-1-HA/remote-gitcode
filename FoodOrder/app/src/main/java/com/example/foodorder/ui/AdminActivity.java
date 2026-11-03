package com.example.foodorder.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityAdminBinding;
import com.example.foodorder.fragment.DishManageFragment;
import com.example.foodorder.fragment.OrderManageFragment;
import com.example.foodorder.fragment.StatsFragment;

/**
 * 管理端主页：底部导航切换 菜品管理 / 订单管理 / 数据统计
 */
public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dish_manage) {
                switchFragment(new DishManageFragment());
            } else if (id == R.id.nav_order_manage) {
                switchFragment(new OrderManageFragment());
            } else if (id == R.id.nav_stats) {
                switchFragment(new StatsFragment());
            }
            return true;
        });

        if (savedInstanceState == null) {
            switchFragment(new DishManageFragment());
        }
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
    }
}
