package com.lethimyhoa.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lethimyhoa.k2341111e_mobile.R;
import com.lethimyhoa.models.Employee;

public class EmployeeAdapter extends ArrayAdapter<Employee> {

    Activity context;
    int resource;

    public EmployeeAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater(); //nhân bản giao diện tại vị trí thứ position
        View customView = inflater.inflate(this.resource, null);
        //lấy employee ở vị trí position ra
        Employee emp=this.getItem(position);
        //show employee into GUI
        TextView txtId=customView.findViewById(R.id.txtId);
        TextView txtName=customView.findViewById(R.id.txtName);
        TextView txtPhone=customView.findViewById(R.id.Phone);
        txtId.setText(emp.getEmployeeId());
        txtName.setText(emp.getEmployeeName());
        txtPhone.setText(emp.getPhoneNumber());

        ImageView imgCall=customView.findViewById(R.id.imgCall);
        ImageView imgSms=customView.findViewById(R.id.imgSms);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCall=new Intent(Intent.ACTION_DIAL);
                Uri uriCall= Uri.parse("tel:"+emp.getPhoneNumber());
                intentCall.setData(uriCall);
                context.startActivity(intentCall);
            }
        });
        imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return customView; //phải return customerView
    }
}
