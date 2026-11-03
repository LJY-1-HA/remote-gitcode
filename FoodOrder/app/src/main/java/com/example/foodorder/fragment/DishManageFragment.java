package com.example.foodorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorder.adapter.AdminDishAdapter;
import com.example.foodorder.databinding.FragmentDishManageBinding;
import com.example.foodorder.db.DishDao;
import com.example.foodorder.model.Dish;
import com.example.foodorder.ui.DishEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端 - 菜品管理：新增、编辑、删除菜品
 */
public class DishManageFragment extends Fragment implements AdminDishAdapter.Callback {

    private FragmentDishManageBinding binding;
    private DishDao dishDao;
    private final List<Dish> dishes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDishManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dishDao = new DishDao(requireContext());
        binding.rvDishes.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), DishEditActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从编辑页返回后刷新列表
        load();
    }

    private void load() {
        if (binding == null) {
            return;
        }
        dishes.clear();
        dishes.addAll(dishDao.listAll());
        binding.rvDishes.setAdapter(new AdminDishAdapter(dishes, this));
        binding.tvEmpty.setVisibility(dishes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(Dish dish) {
        Intent intent = new Intent(requireContext(), DishEditActivity.class);
        intent.putExtra(DishEditActivity.EXTRA_DISH_ID, dish.id);
        startActivity(intent);
    }

    @Override
    public void onDelete(Dish dish) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除菜品")
                .setMessage("确定要删除「" + dish.name + "」吗？\n历史订单不受影响。")
                .setPositiveButton("删除", (d, w) -> {
                    dishDao.delete(dish.id);
                    Toast.makeText(requireContext(), "已删除：" + dish.name, Toast.LENGTH_SHORT).show();
                    load();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
