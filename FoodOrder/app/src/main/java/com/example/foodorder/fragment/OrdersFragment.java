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

import com.example.foodorder.adapter.OrderAdapter;
import com.example.foodorder.databinding.FragmentOrdersBinding;
import com.example.foodorder.db.OrderDao;
import com.example.foodorder.model.Order;
import com.example.foodorder.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 顾客端 - 我的订单：查看历史订单，待接单可取消
 */
public class OrdersFragment extends Fragment implements OrderAdapter.OnCancelListener {

    private FragmentOrdersBinding binding;
    private OrderDao orderDao;
    private int userId = -1;
    private final List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderDao = new OrderDao(requireContext());
        userId = new SessionManager(requireContext()).getUserId();
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
        orders.addAll(orderDao.listByUser(userId));
        binding.rvOrders.setAdapter(new OrderAdapter(orders, this));
        binding.tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
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
