package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class MyItem {
    private Bitmap icon;
    private String name;
    private String contents;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
