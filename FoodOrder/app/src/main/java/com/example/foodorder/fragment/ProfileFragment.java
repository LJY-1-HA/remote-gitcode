package com.example.foodorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.foodorder.R;
import com.example.foodorder.databinding.FragmentProfileBinding;
import com.example.foodorder.db.UserDao;
import com.example.foodorder.model.User;
import com.example.foodorder.ui.LoginActivity;
import com.example.foodorder.util.SessionManager;

/**
 * 顾客端 - 我的：查看账号信息、维护联系方式、退出登录
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserDao userDao;
    private SessionManager session;
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userDao = new UserDao(requireContext());
        session = new SessionManager(requireContext());
        userId = session.getUserId();

        binding.btnSaveInfo.setOnClickListener(v -> saveInfo());
        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {
        if (binding == null) {
            return;
        }
        User user = userDao.getById(userId);
        if (user == null) {
            return;
        }
        binding.tvUsername.setText(user.username);
        binding.tvRole.setText(user.role == User.ROLE_ADMIN ? R.string.role_admin : R.string.role_user);
        binding.etPhone.setText(user.phone);
        binding.etAddress.setText(user.address);
    }

    private void saveInfo() {
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        userDao.updateContact(userId, phone, address);
        Toast.makeText(requireContext(), "联系方式已保存", Toast.LENGTH_SHORT).show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("退出", (d, w) -> {
                    session.clear();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
