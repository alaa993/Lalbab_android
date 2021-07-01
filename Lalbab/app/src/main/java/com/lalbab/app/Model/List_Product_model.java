package com.lalbab.app.Model;

/**
 * Created by Rajesh Dabhi on 24/6/2017.
 */

public class List_Product_model {

    String id;
    String name_ar;
    String name_ku;
    String name_en;
    String type_p;
    String img;

    public List_Product_model() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getName_ku() {
        return name_ku;
    }

    public void setName_ku(String name_ku) {
        this.name_ku = name_ku;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getType_p() {
        return type_p;
    }

    public void setType_p(String type_p) {
        this.type_p = type_p;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
