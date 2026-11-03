package com.example.foodorder.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.databinding.ItemOrderAdminBinding;
import com.example.foodorder.model.Order;
import com.example.foodorder.model.OrderItem;
import com.example.foodorder.util.MoneyUtil;
import com.example.foodorder.util.StatusUi;

import java.util.List;

/**
 * 管理端 - 订单列表适配器（接单 / 出餐 / 送达 / 取消）
 */
public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.Holder> {

    public interface Callback {
        void onAction(Order order);   // 推进到下一状态

        void onCancel(Order order);   // 取消订单
    }

    private final List<Order> orders;
    private final Callback callback;

    public AdminOrderAdapter(List<Order> orders, Callback callback) {
        this.orders = orders;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderAdminBinding binding = ItemOrderAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Order order = orders.get(position);
        holder.binding.tvOrderNo.setText(String.format("订单号：#%d", order.id));
        holder.binding.tvStatus.setText(Order.statusText(order.status));
        holder.binding.tvStatus.setBackgroundResource(StatusUi.bg(order.status));
        holder.binding.tvStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), StatusUi.color(order.status)));

        String phone = order.phone == null || order.phone.isEmpty() ? "未留电话" : order.phone;
        holder.binding.tvUser.setText(String.format("顾客：%s（%s）",
                order.username == null ? "未知用户" : order.username, phone));
        holder.binding.tvAddress.setText(String.format("配送至：%s",
                order.address == null || order.address.isEmpty() ? "未填写地址" : order.address));

        StringBuilder sb = new StringBuilder();
        for (OrderItem item : order.items) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(item.dishName).append(" x ").append(item.quantity);
        }
        holder.binding.tvItems.setText(sb.toString());

        if (order.remark != null && !order.remark.isEmpty()) {
            holder.binding.tvRemark.setVisibility(ViewGroup.VISIBLE);
            holder.binding.tvRemark.setText("备注：" + order.remark);
        } else {
            holder.binding.tvRemark.setVisibility(ViewGroup.GONE);
        }

        holder.binding.tvTotal.setText("合计：" + MoneyUtil.yuan(order.total));

        // 取消：仅待接单
        boolean canCancel = order.status == Order.STATUS_PENDING;
        holder.binding.btnCancel.setVisibility(canCancel ? ViewGroup.VISIBLE : ViewGroup.GONE);
        holder.binding.btnCancel.setOnClickListener(v -> {
            if (callback != null) callback.onCancel(order);
        });

        // 推进状态按钮：待接单→接单，制作中→出餐完成，配送中→确认送达
        String nextLabel;
        switch (order.status) {
            case Order.STATUS_PENDING:
                nextLabel = "接单";
                break;
            case Order.STATUS_COOKING:
                nextLabel = "出餐完成";
                break;
            case Order.STATUS_DELIVERING:
                nextLabel = "确认送达";
                break;
            default:
                nextLabel = null;
                break;
        }
        if (nextLabel != null) {
            holder.binding.btnNext.setVisibility(ViewGroup.VISIBLE);
            holder.binding.btnNext.setText(nextLabel);
            holder.binding.btnNext.setOnClickListener(v -> {
                if (callback != null) callback.onAction(order);
            });
        } else {
            holder.binding.btnNext.setVisibility(ViewGroup.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemOrderAdminBinding binding;

        Holder(ItemOrderAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
