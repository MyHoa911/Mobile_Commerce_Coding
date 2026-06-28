package com.lethimyhoa.k2341111e_mobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lethimyhoa.adapters.ProductAdapter;
import com.lethimyhoa.dals.ProductDAO;
import com.lethimyhoa.models.Product;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    ListView lvProduct;
    EditText edtSearchProduct;
    Button btnAddProduct;
    ArrayList<Product> allProducts;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        addViews();
        addEvents();
    }

    private void addViews() {
        lvProduct = findViewById(R.id.lvProduct);
        edtSearchProduct = findViewById(R.id.edtSearchProduct);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        allProducts = ProductDAO.getProducts(this);
        adapter = new ProductAdapter(this, R.layout.product_custom_item);
        adapter.addAll(allProducts);
        lvProduct.setAdapter(adapter);
    }

    private void addEvents() {
        lvProduct.setOnItemClickListener((parent, view, position, id) -> {
            Product p = adapter.getItem(position);
            if (p != null) {
                Toast.makeText(this, "Selected: " + p.getProductName(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddProduct.setOnClickListener(v -> Toast.makeText(this, "Add Product clicked", Toast.LENGTH_SHORT).show());

        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String q = (s == null) ? "" : s.toString().trim().toLowerCase();
                adapter.clear();
                if (q.isEmpty()) {
                    adapter.addAll(allProducts);
                } else {
                    for (Product p : allProducts) {
                        if (p.getProductName() != null && p.getProductName().toLowerCase().contains(q)) {
                            adapter.add(p);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
