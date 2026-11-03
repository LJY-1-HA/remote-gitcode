package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ItemOrderBinding;
import com.example.foodorder.model.Order;
import com.example.foodorder.util.MoneyUtil;
import com.example.foodorder.util.StatusUi;

import java.util.List;

/**
 * 顾客端 - 订单列表适配器
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Holder> {

    public interface OnCancelListener {
        void onCancel(Order order);
    }

    private final List<Order> orders;
    private final OnCancelListener listener;

    public OrderAdapter(List<Order> orders, OnCancelListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Order order = orders.get(position);
        holder.binding.tvOrderNo.setText(String.format("订单号：#%d", order.id));
        holder.binding.tvTime.setText(order.createTime);
        holder.binding.tvStatus.setText(Order.statusText(order.status));
        holder.binding.tvStatus.setBackgroundResource(StatusUi.bg(order.status));
        holder.binding.tvStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), StatusUi.color(order.status)));
        holder.binding.tvTotal.setText("合计：" + MoneyUtil.yuan(order.total));

        StringBuilder sb = new StringBuilder();
        for (com.example.foodorder.model.OrderItem item : order.items) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(item.dishName).append(" x ").append(item.quantity);
        }
        holder.binding.tvItems.setText(sb.toString());

        // 仅“待接单”可取消
        boolean canCancel = order.status == Order.STATUS_PENDING;
        holder.binding.btnCancel.setVisibility(canCancel ? ViewGroup.VISIBLE : ViewGroup.GONE);
        holder.binding.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemOrderBinding binding;

        Holder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
