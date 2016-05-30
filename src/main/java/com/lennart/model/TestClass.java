package com.lennart.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.lennart.jsonview.Views;

/**
 * Created by LPO10346 on 30-5-2016.
 */
public class TestClass {

//    @JsonView(Views.Public.class)
//    private int a = 8;

    @JsonView(Views.Public.class)
    private String fName;

    @JsonView(Views.Public.class)
    private String lName;

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }



}
