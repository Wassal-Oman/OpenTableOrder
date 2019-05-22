package com.aseela.open_table_order.models;

import java.io.Serializable;

public class Restaurant implements Serializable {
    // attributes
    private String id;
    private String name;
    private String email;
    private String phone;
    private String website;
    private String location;
    private String latitude;
    private String longitude;
    private String image;
    private String image_name;
    private String delivery;

    // constructors
    public Restaurant() {
    }

    public Restaurant(String id, String name, String email, String phone, String website, String location, String latitude, String longitude, String image, String image_name, String delivery) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.image_name = image_name;
        this.delivery = delivery;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
}
