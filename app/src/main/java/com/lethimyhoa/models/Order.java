package com.lethimyhoa.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order implements Serializable {
    private String orderId;
    private String customerId;
    private String employeeId;
    private Date orderDate;
    private OrderStatus orderStatus;
    //static khởi tạo 1 lần duy nhất
    static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");


    public Order() {
    }

    public Order(String orderId, String customerId, String employeeId, Date orderDate, OrderStatus orderStatus) {
        this(orderId,customerId,employeeId,orderDate);
        this.orderStatus = orderStatus;
    }

    public Order(String orderId, String customerId, String employeeId, Date orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        String data=orderId+"\t"+sdf.format(orderDate)+"\t"+DataWarehouse.sumOfMoney(this);
        return data;
    }
}
