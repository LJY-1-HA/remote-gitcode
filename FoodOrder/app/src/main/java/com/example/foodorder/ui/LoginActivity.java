package com.example.foodorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.databinding.ActivityLoginBinding;
import com.example.foodorder.db.UserDao;
import com.example.foodorder.model.User;
import com.example.foodorder.util.SessionManager;

/**
 * 登录页：校验账号密码，按角色进入顾客端 / 管理端
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = new UserDao(this);
        session = new SessionManager(this);

        // 自动登录：本地已保存会话则直接进入对应端
        int userId = session.getUserId();
        if (userId != -1) {
            User user = userDao.getById(userId);
            if (user != null) {
                route(user);
                return;
            } else {
                session.clear(); // 会话失效（如数据库被清空）
            }
        }

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDao.login(username, password);
        if (user == null) {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        session.save(user.id);
        route(user);
    }

    /** 按角色进入对应主页：管理员 → 管理端，顾客 → 顾客端 */
    private void route(User user) {
        Intent intent;
        if (user.role == User.ROLE_ADMIN) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
