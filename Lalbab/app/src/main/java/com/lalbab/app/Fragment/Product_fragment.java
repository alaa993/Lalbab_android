package com.lalbab.app.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import Adapter.Product_adapter;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Category_model;
import com.lalbab.app.Model.Product_model;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.DatabaseHandler;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_fragment extends Fragment {

    private static String TAG = Product_fragment.class.getSimpleName();

    private Button btn_Confirmation;

    private RecyclerView rv_cat;
    private TabLayout tab_cat;

    private List<Category_model> category_modelList = new ArrayList<>();
    private List<String> cat_menu_id = new ArrayList<>();

    private List<Product_model> product_modelList = new ArrayList<>();
    private Product_adapter adapter_product;
    private DatabaseHandler dbcart;

    public Product_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        //setHasOptionsMenu(true);
        btn_Confirmation=(Button)view.findViewById(R.id.bt_Confirmation);
        tab_cat = (TabLayout) view.findViewById(R.id.tab_cat);
        rv_cat = (RecyclerView) view.findViewById(R.id.rv_subcategory);
        rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));
        dbcart = new DatabaseHandler(getActivity());
        String getcat_id = getArguments().getString("cat_id");
        String getcat_title = getArguments().getString("cat_title");

        ((MainActivity) getActivity()).setTitle(getcat_title);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetCategoryRequest(getcat_id);
        }
        btn_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbcart.getCartCount() >0) {
                    Fragment fm = new Cart_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                }else{
                    Toast.makeText(getActivity(), "No item in cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tab_cat.setVisibility(View.GONE);
        tab_cat.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.white));

        tab_cat.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String getcat_id = cat_menu_id.get(tab.getPosition());
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

        return view;
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
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

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

                        adapter_product = new Product_adapter(product_modelList, getActivity() );
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();

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
                                TabLayout.Tab tab = tab_cat.newTab();
                                tab.setText(category_modelList.get(i).getTitle());

                                tab_cat.addTab(tab);
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
