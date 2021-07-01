package com.lalbab.app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.lalbab.app.Config.BaseURL;

/**
 * Created by Rajesh Dabhi on 24/6/2017.
 */

public class Category_model {

    String id;
    String title;
    String title_ku;
    String title_en;
    String slug;
    String parent;
    String catS;
    String leval;
    String description;
    String image;
    String status;

    String CheckCat;
    String Count;
    String PCount;

    @SerializedName("sub_cat")
    ArrayList<Category_subcat_model> category_sub_datas;

    public String getId(){
        return id;
    }

    public String getTitle(){
        if (BaseURL.LANGUAGE.contains("ar")){
            return title;
        }else if (BaseURL.LANGUAGE.contains("ku")){
            return title_ku;
        }else if (BaseURL.LANGUAGE.contains("en")){
            return title_en;
        }
        return title;
    }

    public String getTitle_ku() {
        return title_ku;
    }

    public void setTitle_ku(String title_ku) {
        this.title_ku = title_ku;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getSlug(){
        return slug;
    }

    public String getParent(){
        return parent;
    }

    public String getParentCat(){
        return catS;
    }

    public String getCheckCat(){
        return CheckCat;
    }

    public String getLeval(){
        return leval;
    }

    public String getDescription(){
        return description;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getStatus(){
        return status;
    }




    public String getCount(){
        return Count;
    }

    public String getPCount(){
        return PCount;
    }

    public ArrayList<Category_subcat_model> getCategory_sub_datas(){
        return category_sub_datas;
    }

}
