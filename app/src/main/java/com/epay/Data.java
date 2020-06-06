package com.epay;

import com.google.firebase.database.DataSnapshot;

public class Data {
    public Data(String Created, String Name,String Text) {

        this.Name = Name;
        this.Text = Text;
        this.Created = Created;
    }

    public Data() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }



    private String Name;
    private String Text;
    private String Created;

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        this.Created = created;
    }
}
