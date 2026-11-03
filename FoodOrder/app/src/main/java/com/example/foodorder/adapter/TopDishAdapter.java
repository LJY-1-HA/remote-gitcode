package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.databinding.ItemTopDishBinding;
import com.example.foodorder.model.Dish;
import com.example.foodorder.util.MoneyUtil;

import java.util.List;
import java.util.Locale;

/**
 * 管理端 - 销量排行适配器（数据统计页）
 */
public class TopDishAdapter extends RecyclerView.Adapter<TopDishAdapter.Holder> {

    private final List<Dish> dishes;

    public TopDishAdapter(List<Dish> dishes) {
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTopDishBinding binding = ItemTopDishBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Dish dish = dishes.get(position);
        holder.binding.tvRank.setText(String.valueOf(position + 1));
        holder.binding.tvName.setText(dish.emoji + " " + dish.name);
        holder.binding.tvCount.setText(String.format(Locale.CHINA, "已售 %d 份", dish.sales));
        holder.binding.tvRevenue.setText(MoneyUtil.yuan(dish.price * dish.sales));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemTopDishBinding binding;

        Holder(ItemTopDishBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
