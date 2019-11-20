package com.example.myapplication;

public class Response {
    private String message;
    private String path;

    public Response(String id, String text) {
        this.message = id;
        this.path = text;
    }

    public String getMessage(){
        return message;
    }

    public String getPath(){
        return path;
    }
}
