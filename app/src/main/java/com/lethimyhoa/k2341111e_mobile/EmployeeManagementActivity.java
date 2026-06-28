package com.lethimyhoa.k2341111e_mobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EmployeeManagementActivity extends AppCompatActivity {

    ListView lvEmployee;
    ArrayList<String> listEmployee;
    ArrayAdapter<String> adapterEmployee;
    EditText edtEmployeeId, edtEmployeeName, edtPhoneNumber;
    int selectedPosition = -1;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_management);

        preferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE);

        addViews();
        addEvents();

        // Khôi phục vị trí cũ
        selectedPosition = preferences.getInt("last_pos", -1);
        if (selectedPosition != -1 && selectedPosition < listEmployee.size()) {
            displayEmployeeInfo(selectedPosition);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        lvEmployee = findViewById(R.id.lvEmployee);
        edtEmployeeId = findViewById(R.id.edtEmployeeId);
        edtEmployeeName = findViewById(R.id.edtEmployeeName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);

        listEmployee = new ArrayList<>();
        listEmployee.add("e1-tèo-0348707251");
        listEmployee.add("e2-tý-083652816");
        listEmployee.add("e3-bin-0826151936");

        // Custom Adapter để bôi vàng dòng chọn
        adapterEmployee = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listEmployee) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == selectedPosition) {
                    view.setBackgroundColor(Color.YELLOW);
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return view;
            }
        };
        lvEmployee.setAdapter(adapterEmployee);
    }

    private void addEvents() {
        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPosition = i;
                displayEmployeeInfo(i);
                // Lưu vị trí ngay lập tức
                preferences.edit().putInt("last_pos", i).apply();
                adapterEmployee.notifyDataSetChanged();
            }
        });
    }

    private void displayEmployeeInfo(int position) {
        String text = listEmployee.get(position);
        String[] arr = text.split("-");
        if (arr.length >= 3) {
            edtEmployeeId.setText(arr[0]);
            edtEmployeeName.setText(arr[1]);
            edtPhoneNumber.setText(arr[2]);
        }
    }

    public void saveEmployee(View view) {
        String id = edtEmployeeId.getText().toString().trim();
        String name = edtEmployeeName.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Please enter ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = id + "-" + name + "-" + phone;
        int foundIndex = -1;

        for (int i = 0; i < listEmployee.size(); i++) {
            if (listEmployee.get(i).startsWith(id + "-")) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1) {
            listEmployee.set(foundIndex, text);
            Toast.makeText(this, "Employee Updated", Toast.LENGTH_SHORT).show();
        } else {
            listEmployee.add(text);
            Toast.makeText(this, "Employee Added", Toast.LENGTH_SHORT).show();
        }

        adapterEmployee.notifyDataSetChanged();
        clearInputs();
    }

    private void clearInputs() {
        edtEmployeeId.setText("");
        edtEmployeeName.setText("");
        edtPhoneNumber.setText("");
        edtEmployeeId.requestFocus();
        selectedPosition = -1;
        preferences.edit().putInt("last_pos", -1).apply();
        adapterEmployee.notifyDataSetChanged();
    }

    public void deleteEmployee(View view) {
        if (selectedPosition == -1) {
            Toast.makeText(this, "Please select an employee to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete this employee?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listEmployee.remove(selectedPosition);
                adapterEmployee.notifyDataSetChanged();
                clearInputs();
                Toast.makeText(EmployeeManagementActivity.this, "Employee Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void closeEmployeeActivity(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        ImageView imgYes = dialog.findViewById(R.id.imgYes);
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);

        imgYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

