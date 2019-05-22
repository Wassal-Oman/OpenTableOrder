package com.aseela.open_table_order.models;

import java.io.Serializable;

public class Meal implements Serializable {

    // attributes
    private String id;
    private String name;
    private String description;
    private String price;
    private String image;
    private String image_name;
    private String restaurant_id;

    // constructors
    public Meal() {
    }

    public Meal(String id, String name, String description, String price, String image, String image_name, String restaurant_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.image_name = image_name;
        this.restaurant_id = restaurant_id;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}
