package com.example.foodorder.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorder.R;
import com.example.foodorder.adapter.DishAdapter;
import com.example.foodorder.databinding.FragmentMenuBinding;
import com.example.foodorder.db.CartDao;
import com.example.foodorder.db.DishDao;
import com.example.foodorder.model.Dish;
import com.example.foodorder.ui.MainActivity;
import com.example.foodorder.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 顾客端 - 点餐页：搜索 + 分类筛选 + 菜品列表 + 加购
 */
public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private DishDao dishDao;
    private CartDao cartDao;
    private int userId = -1;

    private String selectedCategory = DishDao.CATEGORY_ALL;
    private String keyword = "";
    private final List<Dish> dishes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dishDao = new DishDao(requireContext());
        cartDao = new CartDao(requireContext());
        userId = new SessionManager(requireContext()).getUserId();

        binding.rvDishes.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 搜索框
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                load();
            }
        });

        buildCategoryChips();
        load();
    }

    /** 生成“全部 + 各分类”标签 */
    private void buildCategoryChips() {
        List<String> categories = new ArrayList<>();
        categories.add(DishDao.CATEGORY_ALL);
        categories.addAll(dishDao.categories());

        binding.chipContainer.removeAllViews();
        for (String category : categories) {
            TextView chip = new TextView(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(lp);
            chip.setPadding(dp(14), dp(5), dp(14), dp(5));
            chip.setText(category);
            chip.setTextSize(13);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                refreshChipStyles();
                load();
            });
            binding.chipContainer.addView(chip);
        }
        refreshChipStyles();
    }

    /** 刷新标签选中样式 */
    private void refreshChipStyles() {
        for (int i = 0; i < binding.chipContainer.getChildCount(); i++) {
            TextView chip = (TextView) binding.chipContainer.getChildAt(i);
            boolean selected = chip.getText().toString().equals(selectedCategory);
            chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_normal);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    selected ? R.color.white : R.color.text_secondary));
            chip.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    /** 按当前分类 + 关键字加载菜品 */
    private void load() {
        if (binding == null) {
            return;
        }
        dishes.clear();
        dishes.addAll(dishDao.listOnSale(selectedCategory, keyword));
        binding.rvDishes.setAdapter(new DishAdapter(dishes, this::addToCart));
        binding.tvEmpty.setVisibility(dishes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /** 加购并刷新购物车角标 */
    private void addToCart(Dish dish) {
        cartDao.add(userId, dish.id);
        Toast.makeText(requireContext(), "已加入购物车：" + dish.name, Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).notifyCartChanged();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
