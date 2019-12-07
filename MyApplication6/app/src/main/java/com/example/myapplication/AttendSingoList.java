package com.example.myapplication;

public class AttendSingoList {

    String name;
    String time;
    String date;
    String user;
    String reportmsg;
    int[] img;


    public AttendSingoList(String name, String date, String user) {
        this.name = name;
        this.date = date;
        this.user = user;
    }


    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }


}
