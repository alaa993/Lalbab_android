package com.lalbab.app.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.List;
import java.util.Locale;

import com.lalbab.app.Model.Category_model;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class BaseURL {

    public static final String PREFS_NAME = "GroceryLoginPrefs";

    public static final String PREFS_NAME2 = "GroceryLoginPrefs2";

    public static final String IS_LOGIN = "isLogin";

    public static final String KEY_NAME = "user_fullname";

    public static final String KEY_EMAIL = "user_email";

    public static final String KEY_ID = "user_id";

    public static final String KEY_MOBILE = "user_phone";

    public static final String KEY_IMAGE = "user_image";

    public static final String KEY_PINCODE = "pincode";

    public static final String KEY_SOCITY_ID = "Socity_id";

    public static final String KEY_SOCITY_NAME = "socity_name";

    public static final String KEY_HOUSE = "house_no";

    public static final String KEY_DATE = "date";

    public static final String KEY_TIME = "time";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ID_CATAGORE = "id_catagore";
    public static String CurentIDCatagore = "0";
    public static String CurentIDProduct = "0";
    public static int CURENT_CATAGORY_POSTION = 0;
    public static List<Category_model> category_modelList;


    //public static String BASE_URL = "http://iclauncher.com/grocery/";
    public static String BASE_URL = "https://www.lellbab.com/";
    //public static String BASE_URL = "http://23.229.0.42/plesk-site-preview/www.lellbab.com/https/23.229.0.42/";
    // public static String BASE_URL = "http://192.168.123.101/grocery/";

    public static String IMG_SLIDER_URL = BASE_URL + "uploads/sliders/";

    public static String IMG_CATEGORY_URL = BASE_URL + "uploads/category/";

    public static String IMG_PRODUCT_URL = BASE_URL + "uploads/products/";

    public static String IMG_PROFILE_URL = BASE_URL + "uploads/profile/";


    public static String GET_SLIDER_URL = BASE_URL + "index.php/api/get_sliders";

    public static String get_DelevaryCharge = BASE_URL + "index.php/api/get_DelevaryCharge";
    public static String GET_CATEGORY_URL = BASE_URL + "index.php/api/get_categories";
    public static String GET_CATEGORY1_URL = BASE_URL + "index.php/api/get_categories1";

    public static String GET_CATEGORYS_URL = BASE_URL + "index.php/api/get_categoriesS";

    public static String GET_PRODUCT_URL = BASE_URL + "index.php/api/get_products";
    public static String GET_PRODUCT_All_URL = BASE_URL + "index.php/api/get_products_all";

    public static String GET_LIST_PRODUCT_URL = BASE_URL + "index.php/api/get_list_products";

    public static String GET_LIST_USER_PRODUCT_URL = BASE_URL + "index.php/api/get_list_products_user";

    public static String CONFERM_ORDER_URL = BASE_URL + "index.php/api/confirm_order";

    public static String GET_ABOUT_URL = BASE_URL + "index.php/api/aboutus";

    public static String GET_SUPPORT_URL = BASE_URL + "index.php/api/support";

    public static String GET_TERMS_URL = BASE_URL + "index.php/api/terms";

    public static String GET_TIME_SLOT_URL = BASE_URL + "index.php/api/get_time_slot";

    public static String LOGIN_URL = BASE_URL + "index.php/api/login";
    public static String CHECK_URL = BASE_URL + "index.php/api/checklogin";
    public static String REGISTER_URL = BASE_URL + "index.php/api/signup";

    public static String GET_SOCITY_URL = BASE_URL + "index.php/api/get_society";

    public static String EDIT_PROFILE_URL = BASE_URL + "index.php/api/update_userdata";

    public static String ADD_ORDER_URL = BASE_URL + "index.php/api/send_order";

    public static String ADD_PRODUCT_URL = BASE_URL + "index.php/api/add_products";
    public static String EDIT_PRODUCT_URL = BASE_URL + "index.php/api/edit_products";

    public static String GET_ORDER_URL = BASE_URL + "index.php/api/my_orders";
    public static String LANGUAGE = "ar";


    public static String GET_REQUESTS_URL = BASE_URL + "index.php/api/my_requests";

    public static String ORDER_DETAIL_URL = BASE_URL + "index.php/api/order_details";

    public static String DELETE_ORDER_URL = BASE_URL + "index.php/api/cancel_order";

    public static String GET_LIMITE_SETTING_URL = BASE_URL + "index.php/api/get_limit_settings";

    public static String ADD_ADDRESS_URL = BASE_URL + "index.php/api/add_address";

    public static String GET_ADDRESS_URL = BASE_URL + "index.php/api/get_address";

    public static String FORGOT_URL = BASE_URL + "index.php/api/forgot_password";

    public static String JSON_RIGISTER_FCM = BASE_URL + "index.php/api/register_fcm";

    public static String CHANGE_PASSWORD_URL = BASE_URL + "index.php/api/change_password";

    public static String DELETE_ADDRESS_URL = BASE_URL + "index.php/api/delete_address";

    public static String EDIT_ADDRESS_URL = BASE_URL + "index.php/api/edit_address";


    // global topic to receive app wide push notifications

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String PUSH_NOTIFICATION = "pushNotification";


    public static void setLocale(String lang , Context context){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        if (lang.contains("ku")) {
            configuration.setLayoutDirection(new Locale("ar"));
        }else {
            configuration.setLayoutDirection(new Locale(lang));
        }

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("lang", lang);
        editor.apply();
    }

    public static void  loadLocale(Context context){
        SharedPreferences pref = context.getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = pref.getString("lang", "ar");
        LANGUAGE = lang;
        setLocale(lang,context);
    }


}
