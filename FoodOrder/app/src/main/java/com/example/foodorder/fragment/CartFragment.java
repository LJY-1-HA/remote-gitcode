package com.example.foodorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorder.adapter.CartAdapter;
import com.example.foodorder.databinding.FragmentCartBinding;
import com.example.foodorder.db.CartDao;
import com.example.foodorder.model.CartItem;
import com.example.foodorder.ui.CheckoutActivity;
import com.example.foodorder.ui.MainActivity;
import com.example.foodorder.util.MoneyUtil;
import com.example.foodorder.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 顾客端 - 购物车：数量增减、删除、结算
 */
public class CartFragment extends Fragment implements CartAdapter.Callback {

    private FragmentCartBinding binding;
    private CartDao cartDao;
    private int userId = -1;
    private final List<CartItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartDao = new CartDao(requireContext());
        userId = new SessionManager(requireContext()).getUserId();

        binding.rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.btnCheckout.setOnClickListener(v -> {
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "购物车是空的，先去点餐吧", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), CheckoutActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从结算页返回 / 增删商品后刷新
        load();
    }

    private void load() {
        if (binding == null) {
            return;
        }
        items.clear();
        items.addAll(cartDao.listByUser(userId));
        binding.rvCart.setAdapter(new CartAdapter(items, this));
        binding.tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

        double total = cartDao.totalByUser(userId);
        int count = cartDao.countByUser(userId);
        binding.tvTotal.setText("合计：" + MoneyUtil.yuan(total));
        binding.tvCountHint.setText(String.format(Locale.CHINA, "共 %d 件 · 免配送费", count));
    }

    @Override
    public void onPlus(CartItem item) {
        cartDao.add(userId, item.dishId);
        afterChanged();
    }

    @Override
    public void onMinus(CartItem item) {
        cartDao.decrease(userId, item.dishId);
        afterChanged();
    }

    @Override
    public void onDelete(CartItem item) {
        cartDao.remove(userId, item.dishId);
        Toast.makeText(requireContext(), "已移除：" + item.name, Toast.LENGTH_SHORT).show();
        afterChanged();
    }

    private void afterChanged() {
        load();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).notifyCartChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
