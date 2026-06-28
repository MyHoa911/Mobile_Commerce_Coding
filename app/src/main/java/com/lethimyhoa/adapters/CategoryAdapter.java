package com.lethimyhoa.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lethimyhoa.k2341111e_mobile.R;
import com.lethimyhoa.models.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    Activity context;
    int resource;
    public CategoryAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View custom = inflater.inflate(resource, null);
        Category category = getItem(position);
        TextView txtCategory = custom.findViewById(R.id.txtCategory);
        TextView txtCategoryName = custom.findViewById(R.id.txtCategoryName);

        txtCategory.setText(category.getCategoryId());
        txtCategoryName.setText(category.getCategoryName());
        return custom;
    }
}
