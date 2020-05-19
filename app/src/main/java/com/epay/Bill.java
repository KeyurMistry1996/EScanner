package com.epay;

/**
 * Created by Harsh on 01-04-2018.
 */

public class Bill{

    public String text, created;


    public Bill(){}

    public Bill(String text, String created) {
        this.text = text;
        this.created = created;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getText() {
        return text;
    }

    public String getCreated() {
        return created;
    }
}