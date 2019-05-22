package com.aseela.open_table_order.models;

public class Order {

    // attributes
    private String customer_id;
    private String customer_name;
    private String customer_email;
    private String customer_phone;
    private String meal_id;
    private String meal_name;
    private String meal_price;
    private String total_price;
    private String order_count;
    private String order_date;
    private String delivery_type;

    // constructors
    public Order() {
    }

    public Order(String customer_id, String customer_name, String customer_email, String customer_phone, String meal_id, String meal_name, String meal_price, String total_price, String order_count, String order_date, String delivery_type) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_phone = customer_phone;
        this.meal_id = meal_id;
        this.meal_name = meal_name;
        this.meal_price = meal_price;
        this.total_price = total_price;
        this.order_count = order_count;
        this.order_date = order_date;
        this.delivery_type = delivery_type;
    }

    // getters and setters
    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public String getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(String meal_price) {
        this.meal_price = meal_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getOrder_count() {
        return order_count;
    }

    public void setOrder_count(String order_count) {
        this.order_count = order_count;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }
}
