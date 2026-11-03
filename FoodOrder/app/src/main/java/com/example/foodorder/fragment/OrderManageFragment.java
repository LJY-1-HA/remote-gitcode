package com.example.foodorder.fragment;

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

import com.example.foodorder.adapter.AdminOrderAdapter;
import com.example.foodorder.databinding.FragmentOrderManageBinding;
import com.example.foodorder.db.OrderDao;
import com.example.foodorder.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端 - 订单管理：查看全部订单，接单 → 出餐 → 送达，可取消
 */
public class OrderManageFragment extends Fragment implements AdminOrderAdapter.Callback {

    private FragmentOrderManageBinding binding;
    private OrderDao orderDao;
    private final List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderManageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderDao = new OrderDao(requireContext());
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
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
        orders.clear();
        orders.addAll(orderDao.listAll());
        binding.rvOrders.setAdapter(new AdminOrderAdapter(orders, this));
        binding.tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onAction(Order order) {
        orderDao.nextStatus(order.id);
        String next = Order.statusText(order.status + 1);
        Toast.makeText(requireContext(), "已更新为：" + next, Toast.LENGTH_SHORT).show();
        load();
    }

    @Override
    public void onCancel(Order order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("取消订单")
                .setMessage("确定要取消该订单吗？")
                .setPositiveButton("确定取消", (d, w) -> {
                    orderDao.cancel(order.id);
                    Toast.makeText(requireContext(), "订单已取消", Toast.LENGTH_SHORT).show();
                    load();
                })
                .setNegativeButton("再想想", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
