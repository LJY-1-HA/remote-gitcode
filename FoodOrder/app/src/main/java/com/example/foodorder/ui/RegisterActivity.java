package com.example.foodorder.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.databinding.ActivityRegisterBinding;
import com.example.foodorder.db.UserDao;

/**
 * 注册页：注册顾客账号（管理员账号由系统预置，不可注册）
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = new UserDao(this);

        binding.tvBack.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String confirm = binding.etConfirm.getText().toString();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "密码至少 6 位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = userDao.register(username, password, phone, address);
        if (id == -1) {
            Toast.makeText(this, "用户名已存在，请换一个", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_LONG).show();
        finish();
    }
}
