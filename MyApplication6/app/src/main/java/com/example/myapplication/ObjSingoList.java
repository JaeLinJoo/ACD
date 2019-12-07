package com.example.myapplication;



public class ObjSingoList {

    String objective;
    String name;
    int[] img;
    String id;
    String reportmsg;

    public String getObjective() {
        return objective;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    public ObjSingoList(String name, String id, String objective) {
        this.objective = objective;
        this.name = name;
        this.id = id;
    }


}
