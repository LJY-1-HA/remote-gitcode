package com.example.foodorder.util;

import com.example.foodorder.R;
import com.example.foodorder.model.Order;

/**
 * 订单状态 → 标签背景 / 文字颜色 资源映射
 */
public final class StatusUi {

    private StatusUi() {
    }

    public static int bg(int status) {
        switch (status) {
            case Order.STATUS_PENDING: return R.drawable.bg_status_pending;
            case Order.STATUS_COOKING: return R.drawable.bg_status_cooking;
            case Order.STATUS_DELIVERING: return R.drawable.bg_status_delivering;
            case Order.STATUS_DONE: return R.drawable.bg_status_done;
            default: return R.drawable.bg_status_canceled;
        }
    }

    public static int color(int status) {
        switch (status) {
            case Order.STATUS_PENDING: return R.color.status_pending;
            case Order.STATUS_COOKING: return R.color.status_cooking;
            case Order.STATUS_DELIVERING: return R.color.status_delivering;
            case Order.STATUS_DONE: return R.color.status_done;
            default: return R.color.status_canceled;
        }
    }
}
