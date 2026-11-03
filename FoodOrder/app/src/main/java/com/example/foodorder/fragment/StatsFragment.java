package com.example.foodorder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorder.adapter.TopDishAdapter;
import com.example.foodorder.databinding.FragmentStatsBinding;
import com.example.foodorder.db.DishDao;
import com.example.foodorder.db.OrderDao;
import com.example.foodorder.db.UserDao;
import com.example.foodorder.util.MoneyUtil;

/**
 * 管理端 - 数据统计：今日 / 累计经营数据 + 销量排行
 */
public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private OrderDao orderDao;
    private DishDao dishDao;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderDao = new OrderDao(requireContext());
        dishDao = new DishDao(requireContext());
        userDao = new UserDao(requireContext());
        binding.rvTopDishes.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (binding == null) {
            return;
        }
        binding.tvTodayOrders.setText(String.valueOf(orderDao.countToday()));
        binding.tvTodayIncome.setText(MoneyUtil.yuan(orderDao.incomeToday()));
        binding.tvTotalOrders.setText(String.valueOf(orderDao.countAll()));
        binding.tvTotalIncome.setText(MoneyUtil.yuan(orderDao.incomeAll()));
        binding.tvUserCount.setText(String.valueOf(userDao.count()));
        binding.tvDishCount.setText(String.valueOf(dishDao.count()));
        binding.rvTopDishes.setAdapter(new TopDishAdapter(dishDao.topSales(5)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
