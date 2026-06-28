package com.lethimyhoa.models;

public enum OrderStatus {
    ALL("Tất cả các loại hóa đơn"),
    COMPLETED("Các hóa đơn đã hoàn tất hành trình"),
    NOT_YET_PAYMENT("Hóa đơn chưa thanh toán"),
    GOING_LOGISTICS("Hóa đơn đang xử lý logistics"),
    CUSTOMER_COMPLAIN("Hóa đơn bị KH la lối");
    private String description;

    private OrderStatus(String description) {
        this.description = description;
    }

    //trả về mô tả
    public String getDescription() {
        return this.description;
    }
}
