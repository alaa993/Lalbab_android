package com.lalbab.app.Model;

import com.lalbab.app.Config.BaseURL;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_model {

    String product_id;
    String product_name;
    String product_name_ku;
    String product_name_en;
    String product_description;
    String product_image;
    String category_id;
    String parent_id;
    String in_stock;
    String price;
    String unit_value;
    String unit;
    String increament;
    String title;

    public String getProduct_name_ku() {
        return product_name_ku;
    }

    public void setProduct_name_ku(String product_name_ku) {
        this.product_name_ku = product_name_ku;
    }

    public String getProduct_name_en() {
        return product_name_en;
    }

    public void setProduct_name_en(String product_name_en) {
        this.product_name_en = product_name_en;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {

        if (BaseURL.LANGUAGE.contains("ar")){
           return product_name;
        }else if (BaseURL.LANGUAGE.contains("ku")){
            return product_name_ku;
        }else if (BaseURL.LANGUAGE.contains("en")){
            return product_name_en;
        }
            return product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getIn_stock() {
        return in_stock;
    }

    public String getPrice() {
        return price;
    }

    public String getUnit_value() {
        return unit_value;
    }

    public String getUnit() {
        return unit;
    }


    public String getIncreament() {
        return increament;
    }

    public String getTitle() {
        return title;
    }

}
