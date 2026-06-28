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
import com.lethimyhoa.models.DataWarehouse;
import com.lethimyhoa.models.Order;

public class OrderAdapter extends ArrayAdapter<Order> {
    Activity context;
    int resource;
    public OrderAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View custom = inflater.inflate(resource, null);
        Order order = getItem(position);
        TextView txtOrderId = custom.findViewById(R.id.txtOrderId);
        TextView txtOrderDate = custom.findViewById(R.id.txtOrderDate);
        TextView txtStatus = custom.findViewById(R.id.txtStatus);
        TextView txtOrderTotal = custom.findViewById(R.id.txtOrderTotal);
        txtOrderId.setText(order.getOrderId().toString());
        txtOrderDate.setText(order.getOrderDate().toString());
        txtStatus.setText(order.getOrderStatus().getDescription().toString());
        txtOrderTotal.setText(String.valueOf(DataWarehouse.sumOfMoney(order)));

        switch (order.getOrderStatus()) {
            case COMPLETED:
                txtStatus.setTextColor(context.getColor(R.color.order_completed));
                break;
            case NOT_YET_PAYMENT:
                txtStatus.setTextColor(context.getColor(R.color.order_not_yet_payment));
                break;
            case GOING_LOGISTICS:
                txtStatus.setTextColor(context.getColor(R.color.order_going_logistics));
                break;
            case CUSTOMER_COMPLAIN:
                txtStatus.setTextColor(context.getColor(R.color.order_customer_complain));
                break;
        }
        return custom;
    }
}
