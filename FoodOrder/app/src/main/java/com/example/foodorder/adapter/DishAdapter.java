package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.databinding.ItemDishBinding;
import com.example.foodorder.model.Dish;
import com.example.foodorder.util.MoneyUtil;

import java.util.List;
import java.util.Locale;

/**
 * 顾客端 - 菜品列表适配器
 */
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.Holder> {

    public interface OnAddCartListener {
        void onAddCart(Dish dish);
    }

    private final List<Dish> dishes;
    private final OnAddCartListener listener;

    public DishAdapter(List<Dish> dishes, OnAddCartListener listener) {
        this.dishes = dishes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDishBinding binding = ItemDishBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Dish dish = dishes.get(position);
        holder.binding.tvEmoji.setText(dish.emoji);
        holder.binding.tvName.setText(dish.name);
        holder.binding.tvDesc.setText(dish.descr);
        holder.binding.tvSales.setText(String.format(Locale.CHINA, "月售 %d", dish.sales));
        holder.binding.tvPrice.setText(MoneyUtil.yuan(dish.price));

        // 上架可加购，下架显示“已售罄”
        boolean onSale = dish.available;
        holder.binding.btnAdd.setVisibility(onSale ? ViewGroup.VISIBLE : ViewGroup.GONE);
        holder.binding.tvSoldOut.setVisibility(onSale ? ViewGroup.GONE : ViewGroup.VISIBLE);

        holder.binding.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddCart(dish);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemDishBinding binding;

        Holder(ItemDishBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
