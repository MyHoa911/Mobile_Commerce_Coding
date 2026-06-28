package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lethimyhoa.adapters.CategoryAdapter;
import com.lethimyhoa.dals.CategoryDAO;
import com.lethimyhoa.models.Category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    ListView lvCategory;
    ArrayList<Category> categories;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        lvCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                processDeleteCategory(i);
                return false;
            }
        });
    }

    private void processDeleteCategory(int i) {
        Toast.makeText(this, "Remove category id="+i, Toast.LENGTH_LONG).show();
        Category category = categories.get(i);
        long result = CategoryDAO.deleteCategory(this, category);
        if(result>0)
        {
            categories = CategoryDAO.getCategories(this);
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();
        }
    }

    private void addViews() {
        lvCategory = findViewById(R.id.listViewCategory);
        categories = CategoryDAO.getCategories(this);
        adapter = new CategoryAdapter(this, R.layout.category_custom_item);
        adapter.addAll(categories);
        lvCategory.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.mnu_category_add_new)
        {
            Intent intent=new Intent(this, CategoryNewActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==2)
        {
            //là user chọn cacel --> làm thinh k làm j hết
        }
        else if(requestCode==1 && resultCode==3)
        {
            //là user chọn yes --> ta cần cập nhật lại giao diện
            categories = CategoryDAO.getCategories(this);
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();
        }
    }
}
