package com.lalbab.app.Model;

/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class My_order_model {

    String sale_id;
    String seler_id;
    String user_id;
    String on_date;
    String delivery_time_from;
    String delivery_time_to;
    String status;
    String note;
    String note_sel;
    String is_paid;
    String total_amount;
    String total_kg;
    String total_items;
    String socity_id;
    String delivery_address;
    String location_id;
    String delivery_charge;
    String phone;
    String name_resever;
    

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName_resever() {
        return name_resever;
    }

    public void setName_resever(String name_resever) {
        this.name_resever = name_resever;
    }

    public String getSeler_id() {
        return seler_id;
    }

    public void setSeler_id(String seler_id) {
        this.seler_id = seler_id;
    }

    public String getNote_sel() {
        return note_sel;
    }

    public void setNote_sel(String note_sel) {
        this.note_sel = note_sel;
    }

    public String getSale_id(){
        return sale_id;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getOn_date(){
        return on_date;
    }

    public String getDelivery_time_from(){
        return delivery_time_from;
    }

    public String getDelivery_time_to(){
        return delivery_time_to;
    }

    public String getStatus(){
        return status;
    }

    public String getNote(){
        return note;
    }

    public String getIs_paid(){
        return is_paid;
    }

    public String getTotal_amount(){
        return total_amount;
    }

    public String getTotal_kg(){
        return total_kg;
    }

    public String getTotal_items(){
        return total_items;
    }

    public String getSocity_id(){
        return socity_id;
    }

    public String getDelivery_address(){
        return delivery_address;
    }

    public String getLocation_id(){
        return location_id;
    }

    public String getDelivery_charge(){
        return delivery_charge;
    }

}
