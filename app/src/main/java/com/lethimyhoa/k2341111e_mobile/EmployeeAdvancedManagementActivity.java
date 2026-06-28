package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lethimyhoa.adapters.EmployeeAdapter;
import com.lethimyhoa.models.Department;
import com.lethimyhoa.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvancedManagementActivity extends AppCompatActivity {
    ListView lvEmployee;
    ArrayList<Employee> listEmployee;
    EmployeeAdapter adapterEmployee;
    Spinner spDepartment;
    ArrayList<Department> listDepartment;
    ArrayAdapter<Department>adapterDepartment; //muốn đẹp thìdooiri thành DeapartmentAdapter
    ImageView imgAddEmployee, imgEditEmployee, imgDeleteEmployee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_advanced_management);
        addViews();
        sampleData(); //giả lập CSDL
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterEmployee.clear();
                if (i == 0) {
                    for (int j = 1; j < listDepartment.size(); j++) {
                        adapterEmployee.addAll(listDepartment.get(j).getListOfEmployees());
                    }
                } else {
                    Department selectedDepartment = listDepartment.get(i);
                    adapterEmployee.addAll(selectedDepartment.getListOfEmployees());
                }
                adapterEmployee.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        imgAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deptIndex = spDepartment.getSelectedItemPosition();
                Intent intentAdd = new Intent(EmployeeAdvancedManagementActivity.this, AddEmployeeActivity.class);
                intentAdd.putExtra("DEPT_INDEX", deptIndex);
                startActivityForResult(intentAdd, 888);
            }
        });
    }


    private void sampleData() {
        if (listDepartment == null) {
            listDepartment = new ArrayList<>();
        }
        Department d0 = new Department("-1", "------ALL------");
        Department d1 = new Department("d1", "Phòng hành chính");
        Department d2 = new Department("d2", "Phòng nhân sự");
        Department d3 = new Department("d3", "Phòng tài chính");
        Department d4 = new Department("d4", "Phòng kỹ thuật");
        
        listDepartment.add(d0); // Thêm d0 vào index 0
        listDepartment.add(d1);
        listDepartment.add(d2);
        listDepartment.add(d3);
        listDepartment.add(d4);

        d1.addEmployee(new Employee("e1", "tèo", "0348707251", "Hà Nội"));

        ArrayList<Employee>list1=new ArrayList<>();
        list1.add(new Employee("e2", "tý", "083652816", "TP. HCM"));
        list1.add(new Employee("e3", "bin", "0826151936", "Đà Nẵng"));

        d2.addListEmployee(list1);

        d4.addEmployee(new Employee("e4","minh","083232344","Hải Phòng"));
        d4.addEmployee(new Employee("e5","linh","0844222344","Huế"));
        d4.addEmployee(new Employee("e6","khanh","0493432344","Cần Thơ"));
        d4.addEmployee(new Employee("e7","ha","083232344","Nha Trang"));
        d4.addEmployee(new Employee("e8","nhi","083232344","Đà Lạt"));

        adapterDepartment.notifyDataSetChanged();
    }

    private void addViews() {
        lvEmployee = findViewById(R.id.lvEmployee);
        listEmployee = new ArrayList<>();
        listEmployee.add(new Employee("e1", "tèo", "0348707251", "Hà Nội"));
        listEmployee.add(new Employee("e2", "tý", "083652816", "TP. HCM"));
        listEmployee.add(new Employee("e3", "bin", "0826151936", "Đà Nẵng"));

        adapterEmployee = new EmployeeAdapter(this, R.layout.employee_custom_item);
        lvEmployee.setAdapter(adapterEmployee);

        adapterEmployee.addAll(listEmployee);
        adapterEmployee.notifyDataSetChanged();

        spDepartment=findViewById(R.id.spDepartment);
        listDepartment=new ArrayList<>();
        adapterDepartment=new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,listDepartment);

        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(adapterDepartment);

        imgAddEmployee=findViewById(R.id.imgAddEmployee);
        imgEditEmployee=findViewById(R.id.imgEditEmployee);
        imgDeleteEmployee=findViewById(R.id.imgDeleteEmployee);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888 && resultCode == 888 && data != null) {
            Employee emp = (Employee) data.getSerializableExtra("NEW_EMPLOYEE");
            int deptIndex = data.getIntExtra("DEPT_INDEX", 0);

            if (emp != null && deptIndex > 0 && deptIndex < listDepartment.size()) {
                listDepartment.get(deptIndex).addEmployee(emp);
            }

            int currentIndex = spDepartment.getSelectedItemPosition();
            adapterEmployee.clear();
            if (currentIndex == 0) {
                for (int j = 1; j < listDepartment.size(); j++) {
                    adapterEmployee.addAll(listDepartment.get(j).getListOfEmployees());
                }
            } else {
                adapterEmployee.addAll(listDepartment.get(currentIndex).getListOfEmployees());
            }
            adapterEmployee.notifyDataSetChanged();
        }
    }
}