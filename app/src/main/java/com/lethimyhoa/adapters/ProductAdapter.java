package com.lethimyhoa.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.lethimyhoa.k2341111e_mobile.R;
import com.lethimyhoa.models.Product;

public class ProductAdapter extends ArrayAdapter<Product> {
    Activity context;
    int resource;

    public ProductAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View customView = inflater.inflate(this.resource, null);

        TextView txtId = customView.findViewById(R.id.txtProductId);
        TextView txtName = customView.findViewById(R.id.txtProductName);
        TextView txtInfo = customView.findViewById(R.id.txtProductInfo);

        Product p = getItem(position);
        txtId.setText(p.getProductId());
        txtName.setText(p.getProductName());
        txtInfo.setText("Price: " + p.getPrice() + "$ | Stock: " + p.getQuantity());

        return customView;
    }
}
