package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lethimyhoa.models.Category;
import com.lethimyhoa.dals.CategoryDAO;

public class CategoryNewActivity extends AppCompatActivity {
    EditText edtCategoryId, edtCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_new);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtCategoryName = findViewById(R.id.edtCategoryName);
    }

    public void processSaveCategory(View view) {
        Category category=new Category();
        category.setCategoryId(edtCategoryId.getText().toString());
        category.setCategoryName(edtCategoryName.getText().toString());

        long result = CategoryDAO.insertCategory(this, category);
        Intent intent = getIntent();

        if (result > 0) {
            setResult(3, intent);
            finish();
        } else {
            Toast.makeText(this, "Lưu danh mục thất bại", Toast.LENGTH_LONG).show();
        }

    }

    public void processCancelCategory(View view) {
        Intent intent=getIntent();
        //assume 2 is CANCEL
        setResult(2,intent);
        finish();
    }
}
