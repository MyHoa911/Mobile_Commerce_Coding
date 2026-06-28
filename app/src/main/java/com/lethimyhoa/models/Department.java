package com.lethimyhoa.models;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Department {
    private String departmentId;
    private String departmentName;
    private ArrayList<Employee> listOfEmployees;

    public Department(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        //this.listOfEmployees = listOfEmployees;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Department() {

    }
    @NonNull
    @Override
    public String toString() {
        return this.departmentName;
    }
    //danh sách  ít
    public void addEmployee(Employee emp) {
        this.listOfEmployees.add(emp);
    }
    //danh sách nhiều
    public void addListEmployee(ArrayList<Employee> listEmp) {
        this.listOfEmployees.addAll(listEmp);
    }
    public ArrayList<Employee> getListOfEmployees() {
        return listOfEmployees;

    }
}
