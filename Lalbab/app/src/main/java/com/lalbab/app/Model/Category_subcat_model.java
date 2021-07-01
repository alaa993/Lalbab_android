package com.lalbab.app.Model;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Category_subcat_model {

    String id;
    String title;
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

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
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

    public String getLeval(){
        return leval;
    }

    public String getDescription(){
        return description;
    }

    public String getImage(){
        return image;
    }

    public String getStatus(){
        return status;
    }

    public String getCheckCat(){
        return CheckCat;
    }


    public String getCount(){
        return Count;
    }

    public String getPCount(){
        return PCount;
    }

}
