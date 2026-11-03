package com.example.foodorder.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityDishEditBinding;
import com.example.foodorder.db.DishDao;
import com.example.foodorder.model.Dish;

import java.util.Arrays;

/**
 * 菜品新增 / 编辑页（管理端）
 */
public class DishEditActivity extends AppCompatActivity {

    public static final String EXTRA_DISH_ID = "dish_id";

    private ActivityDishEditBinding binding;
    private DishDao dishDao;
    private ArrayAdapter<String> categoryAdapter;
    private int dishId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDishEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dishDao = new DishDao(this);
        dishId = getIntent().getIntExtra(EXTRA_DISH_ID, -1);

        // 分类下拉框
        String[] categories = getResources().getStringArray(R.array.dish_categories);
        categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Arrays.asList(categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategory.setAdapter(categoryAdapter);

        if (dishId >= 0) {
            binding.tvTitle.setText("编辑菜品");
            fillForm(dishDao.getById(dishId));
        }

        binding.tvBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void fillForm(Dish dish) {
        if (dish == null) {
            Toast.makeText(this, "菜品不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.etName.setText(dish.name);
        binding.etPrice.setText(String.valueOf(dish.price));
        binding.etEmoji.setText(dish.emoji);
        binding.etDesc.setText(dish.descr);
        binding.swAvailable.setChecked(dish.available);

        int position = categoryAdapter.getPosition(dish.category);
        if (position >= 0) {
            binding.spCategory.setSelection(position);
        }
    }

    private void save() {
        String name = binding.etName.getText().toString().trim();
        String priceText = binding.etPrice.getText().toString().trim();
        String emoji = binding.etEmoji.getText().toString().trim();
        String desc = binding.etDesc.getText().toString().trim();
        String category = (String) binding.spCategory.getSelectedItem();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入菜品名称", Toast.LENGTH_SHORT).show();
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的价格", Toast.LENGTH_SHORT).show();
            return;
        }
        if (price < 0) {
            Toast.makeText(this, "价格不能为负数", Toast.LENGTH_SHORT).show();
            return;
        }
        if (emoji.isEmpty()) {
            emoji = "🍽️";
        }

        Dish dish = new Dish();
        dish.id = dishId;
        dish.name = name;
        dish.category = category == null ? "推荐" : category;
        dish.price = price;
        dish.emoji = emoji;
        dish.descr = desc;
        dish.available = binding.swAvailable.isChecked();

        if (dishId >= 0) {
            dishDao.update(dish);
            Toast.makeText(this, "修改已保存", Toast.LENGTH_SHORT).show();
        } else {
            dishDao.insert(dish);
            Toast.makeText(this, "菜品已添加", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
