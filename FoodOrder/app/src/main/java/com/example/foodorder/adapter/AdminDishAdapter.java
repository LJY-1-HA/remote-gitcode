package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.databinding.ItemDishAdminBinding;
import com.example.foodorder.model.Dish;
import com.example.foodorder.util.MoneyUtil;

import java.util.List;
import java.util.Locale;

/**
 * 管理端 - 菜品列表适配器（编辑 / 删除）
 */
public class AdminDishAdapter extends RecyclerView.Adapter<AdminDishAdapter.Holder> {

    public interface Callback {
        void onEdit(Dish dish);

        void onDelete(Dish dish);
    }

    private final List<Dish> dishes;
    private final Callback callback;

    public AdminDishAdapter(List<Dish> dishes, Callback callback) {
        this.dishes = dishes;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDishAdminBinding binding = ItemDishAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Dish dish = dishes.get(position);
        holder.binding.tvEmoji.setText(dish.emoji);
        holder.binding.tvName.setText(dish.name);
        holder.binding.tvCategory.setText(dish.category);
        holder.binding.tvSales.setText(String.format(Locale.CHINA, "月售 %d", dish.sales));
        holder.binding.tvPrice.setText(MoneyUtil.yuan(dish.price));
        holder.binding.tvOffShelf.setVisibility(dish.available ? ViewGroup.GONE : ViewGroup.VISIBLE);

        holder.binding.btnEdit.setOnClickListener(v -> {
            if (callback != null) callback.onEdit(dish);
        });
        holder.binding.btnDelete.setOnClickListener(v -> {
            if (callback != null) callback.onDelete(dish);
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemDishAdminBinding binding;

        Holder(ItemDishAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
