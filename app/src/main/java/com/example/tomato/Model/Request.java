package com.example.tomato.Model;

public class Request {
    public Request(){};
    private String hotel_address;
    private String hotel_name;
    private String hotel_email;
    private String request_id;
    private String menu_id;
    private String user_id;
    private String hotel_phone;
    private String hotel_pincode;

    public String  getHotel_phone() {
        return hotel_phone;
    }

    public String getHotel_pincode() {
        return hotel_pincode;
    }

    public void setHotel_pincode(String hotel_pincode) {
        this.hotel_pincode = hotel_pincode;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setHotel_phone(String hotel_phone) {
        this.hotel_phone = hotel_phone;
    }

    public void setHotel_address(String hotel_address) {
        this.hotel_address = hotel_address;
    }

    public String getHotel_address() {
        return hotel_address;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public void setHotel_email(String hotel_email) {
        this.hotel_email = hotel_email;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }


    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }


    public String getHotel_email() {
        return hotel_email;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public String getRequest_id() {
        return request_id;
    }

}
