package com.lalbab.app.Fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.AdapterProductsName;
import Adapter.Product_adapter_shop;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Category_model;
import com.lalbab.app.Model.List_Product_model;
import com.lalbab.app.Model.Product_model;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonRequest;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_fragment_shop extends Fragment {

    private static String TAG = Product_fragment_shop.class.getSimpleName();

    private RecyclerView rv_cat;
    private TabLayout tab_cat;
    Button bt_add_product;
    int curent_catagore = 0;
    int curent_type = 0;

    private List<Category_model> category_modelList = new ArrayList<>();
    private List<String> cat_menu_id = new ArrayList<>();

    private List<Product_model> product_modelList = new ArrayList<>();
    private List<List_Product_model> list_product_modelList = new ArrayList<>();

    private Product_adapter_shop adapter_product_shop;
    LinearLayout view_add;

    private Spinner title;
    String title_p;
    Button bt_close , bt_add;;
    int position_product ;
    EditText txt_price;


    public Product_fragment_shop() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BaseURL.loadLocale(getActivity());
        View view = inflater.inflate(R.layout.fragment_product_shop, container, false);
        //setHasOptionsMenu(true);

        tab_cat = (TabLayout) view.findViewById(R.id.tab_cat);
        txt_price = (EditText) view.findViewById(R.id.txt_price);
        bt_add = (Button) view.findViewById(R.id.bt_add);
        bt_close = (Button) view.findViewById(R.id.bt_close);
        title = view.findViewById(R.id.title);
        view_add = (LinearLayout) view.findViewById(R.id.view_add);
        bt_add_product = (Button) view.findViewById(R.id.bt_add_product);
        rv_cat = (RecyclerView) view.findViewById(R.id.rv_subcategory);
        rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));

        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title_p = String.valueOf(list_product_modelList.get(position).getId());
                position_product = position;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_price.getText().length()>0) {

                    makeAddProductRequest();
                }else {

                }

             //   Toast.makeText(getActivity(), ""+title_p +" "+position_product , Toast.LENGTH_SHORT).show();
            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( view_add.getVisibility()!=View.GONE)
                    view_add.setVisibility(View.GONE);

            }
        });


        String getcat_id = getArguments().getString("cat_id");
        String getcat_title = getArguments().getString("cat_title");

        ((MainActivity) getActivity()).setTitle(getcat_title);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetCategoryRequest(getcat_id);
        }

        tab_cat.setVisibility(View.GONE);
        tab_cat.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.white));

        tab_cat.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String getcat_id = cat_menu_id.get(tab.getPosition());

                curent_catagore = Integer.parseInt(getcat_id);
                curent_type = tab.getPosition();

                int i = tab.getPosition();
                if (i==0){
                   // tab.getCustomView().setBackgroundColor(Color.parseColor("#1bdc84"));
                    tab_cat.setSelectedTabIndicatorColor(Color.parseColor("#1bdc84"));
                    //tab.parent.setTabTextColors(ColorStateList.valueOf(Color.BLACK));

                }if (i==1){
                   // tab.getCustomView().setBackgroundColor(Color.parseColor("#09c6df"));
                    tab_cat.setSelectedTabIndicatorColor(Color.parseColor("#000000"));

                }if (i==2){
                 //   tab.getCustomView().setBackgroundColor(Color.parseColor("#6855c8"));
                    tab_cat.setSelectedTabIndicatorColor(Color.parseColor("#ff0023"));

                }

                if (ConnectivityReceiver.isConnected()) {
                    makeGetProductRequest(getcat_id);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /*String getcat_id = cat_menu_id.get(tab.getPosition());
                tab_cat.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.white));

                if (ConnectivityReceiver.isConnected()) {
                    makeGetProductRequest(getcat_id);
                }*/
            }
        });

        bt_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPrudoct();

            }
        });



        return view;
    }

    private void addPrudoct() {
      //  Toast.makeText(getActivity().getBaseContext(), "curent_catagore "+ curent_catagore + " curent_type:" + curent_type, Toast.LENGTH_SHORT).show();


        makeGetListProductRequest(curent_catagore , curent_type);
    }


    private void makeGetListProductRequest(int cat_id ,int curent_type) {

        // Tag used to cancel the request
        String tag_json_obj = "json_list_product_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }

        curent_type = curent_type + 1;

        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", ""+cat_id);
        params.put("id_type", ""+curent_type);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
           //     BaseURL.GET_LIST_PRODUCT_URL, params, new Response.Listener<JSONObject>() {
                BaseURL.GET_LIST_USER_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<List_Product_model>>() {
                        }.getType();

                        Log.e("list_product_modelList:", ":"+response.getString("data"));

                        list_product_modelList = gson.fromJson(response.getString("data"), listType);

                        if (getActivity() != null) {
                            if (list_product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (list_product_modelList.size() > 0){
                            if (view_add.getVisibility()!=View.VISIBLE)
                                view_add.setVisibility(View.VISIBLE);

                            AdapterProductsName adapter_name = new AdapterProductsName(getActivity(),  R.layout.item_pruduct_name ,  R.id.title_pp, list_product_modelList );
                            title.setAdapter(adapter_name);

                        }


                    }else{
                        Toast.makeText(getActivity(), ""+response.getString("data"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("list_product_modelList", ""+e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void makeAddProductRequest() {

        // Tag used to cancel the request
        String tag_json_obj = "json_add_product_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }

        String prod_title = ""+list_product_modelList.get(position_product).getName_ar();
        String prod_title_en = list_product_modelList.get(position_product).getName_en();
        String prod_title_ku = ""+list_product_modelList.get(position_product).getName_ku();
        String parent = ""+curent_catagore;
        String prod_status = "1";
        String product_description = "";
        String unit = "kg";
        String price = ""+txt_price.getText().toString();
        String qty = "1";
        String product_image = ""+list_product_modelList.get(position_product).getImg();

        Map<String, String> params = new HashMap<String, String>();
        params.put("prod_title", prod_title);
        params.put("prod_title_en", prod_title_en);
        params.put("prod_title_ku", prod_title_ku);
        params.put("product_image", product_image);
        params.put("parent", parent);
        params.put("prod_status", prod_status);
        params.put("product_description", product_description);
        params.put("unit", unit);
        params.put("price", price);
        params.put("qty", qty);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        String msg = response.getString("data");

                        Log.e("msg", ""+msg);
                        Toast.makeText(getActivity(), ""+msg, Toast.LENGTH_SHORT).show();

                    }else {
                        String msg = response.getString("error");

                        Log.e("msg", ""+msg);
                        Toast.makeText(getActivity(), ""+msg, Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetProductRequest(String cat_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_All_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                        product_modelList = gson.fromJson(response.getString("data"), listType);

                        adapter_product_shop = new Product_adapter_shop(product_modelList, getActivity() , curent_catagore );
                        rv_cat.setAdapter(adapter_product_shop);
                        adapter_product_shop.notifyDataSetChanged();

                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeGetCategoryRequest(final String parent_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_category_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", parent_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category_model>>() {
                        }.getType();

                        category_modelList = gson.fromJson(response.getString("data"), listType);

                        if (!category_modelList.isEmpty()) {
                            tab_cat.setVisibility(View.VISIBLE);

                            cat_menu_id.clear();
                            for (int i = 0; i < category_modelList.size(); i++) {
                                cat_menu_id.add(category_modelList.get(i).getId());
                                tab_cat.addTab(tab_cat.newTab().setText(category_modelList.get(i).getTitle()));
                            }
                        } else {
                            makeGetProductRequest(parent_id);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }




}
