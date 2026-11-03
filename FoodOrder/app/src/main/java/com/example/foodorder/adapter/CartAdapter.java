package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.databinding.ItemCartBinding;
import com.example.foodorder.model.CartItem;
import com.example.foodorder.util.MoneyUtil;

import java.util.List;

/**
 * 购物车列表适配器
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Holder> {

    public interface Callback {
        void onPlus(CartItem item);

        void onMinus(CartItem item);

        void onDelete(CartItem item);
    }

    private final List<CartItem> items;
    private final Callback callback;

    public CartAdapter(List<CartItem> items, Callback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CartItem item = items.get(position);
        holder.binding.tvEmoji.setText(item.emoji);
        holder.binding.tvName.setText(item.name);
        holder.binding.tvPrice.setText(MoneyUtil.yuan(item.price));
        holder.binding.tvQty.setText(String.valueOf(item.quantity));

        holder.binding.btnPlus.setOnClickListener(v -> {
            if (callback != null) callback.onPlus(item);
        });
        holder.binding.btnMinus.setOnClickListener(v -> {
            if (callback != null) callback.onMinus(item);
        });
        holder.binding.btnDelete.setOnClickListener(v -> {
            if (callback != null) callback.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemCartBinding binding;

        Holder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
