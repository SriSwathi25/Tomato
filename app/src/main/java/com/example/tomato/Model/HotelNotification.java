package com.example.tomato.Model;

public class HotelNotification {
    private String Message;
    private String time;
    public HotelNotification(){

    }


    public String getMessage() {
        return Message;
    }

    public String getTime() {
        return time;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
