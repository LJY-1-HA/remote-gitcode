package com.example.foodorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.databinding.ActivityCheckoutBinding;
import com.example.foodorder.db.CartDao;
import com.example.foodorder.db.OrderDao;
import com.example.foodorder.db.UserDao;
import com.example.foodorder.model.CartItem;
import com.example.foodorder.model.User;
import com.example.foodorder.util.MoneyUtil;
import com.example.foodorder.util.SessionManager;

import java.util.List;
import java.util.Locale;

/**
 * 确认订单页：填写配送信息 → 提交订单（事务）→ 跳转订单页
 */
public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CartDao cartDao;
    private OrderDao orderDao;
    private UserDao userDao;
    private int userId = -1;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartDao = new CartDao(this);
        orderDao = new OrderDao(this);
        userDao = new UserDao(this);
        userId = new SessionManager(this).getUserId();

        // 购物车为空直接返回
        cartItems = cartDao.listByUser(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "购物车是空的", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showItems();
        prefillUserInfo();

        binding.tvBack.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> submit());
    }

    /** 商品清单 + 底部合计 */
    private void showItems() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        double total = 0;
        for (CartItem item : cartItems) {
            sb.append(item.name).append(" x ").append(item.quantity)
                    .append("　").append(MoneyUtil.yuan(item.subtotal())).append("\n");
            count += item.quantity;
            total += item.subtotal();
        }
        binding.tvItems.setText(sb.toString().trim());
        binding.tvTotal.setText("合计：" + MoneyUtil.yuan(total));
        binding.tvCountHint.setText(String.format(Locale.CHINA, "共 %d 件 · 免配送费", count));
    }

    /** 用账户里保存的手机号 / 地址预填 */
    private void prefillUserInfo() {
        User user = userDao.getById(userId);
        if (user != null) {
            binding.etPhone.setText(user.phone);
            binding.etAddress.setText(user.address);
        }
    }

    private void submit() {
        String address = binding.etAddress.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String remark = binding.etRemark.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请填写收货地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写联系电话", Toast.LENGTH_SHORT).show();
            return;
        }

        long orderId = orderDao.place(userId, address, phone, remark, cartItems);
        if (orderId == -1) {
            Toast.makeText(this, "下单失败，请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        // 顺手把联系方式保存到账户，下次自动带出
        userDao.updateContact(userId, phone, address);

        new AlertDialog.Builder(this)
                .setTitle("下单成功 🎉")
                .setMessage("订单已提交，等待商家接单\n订单号：#" + orderId)
                .setCancelable(false)
                .setPositiveButton("查看订单", (d, w) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(MainActivity.EXTRA_TAB, MainActivity.TAB_ORDERS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
