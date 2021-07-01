package com.lalbab.app.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.Model.Category_model;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.DatabaseHandler;
import com.lalbab.app.util.Session_management;

import Adapter.Home_adapter;

/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class Delivery_payment_detail_fragment extends Fragment {

    private static String TAG = Delivery_payment_detail_fragment.class.getSimpleName();

    private TextView tv_timeslot, tv_address, tv_item, tv_total , txt_note;
    private Button btn_order;
    private ProgressBar progressBar;

    private String getlocation_id = "";
    //private String gettime = "";
   // private String getdate = "";
    private String getnote = "";
    private String getnote_sel = "";
    private String getuser_id = "";
    private String seler_id = "";
    private int deli_charges;

    private DatabaseHandler db_cart;
    private Session_management sessionManagement;

    public Delivery_payment_detail_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment_detail));

        db_cart = new DatabaseHandler(getActivity());
        sessionManagement = new Session_management(getActivity());

        tv_timeslot = (TextView) view.findViewById(R.id.textTimeSlot);
        tv_address = (TextView) view.findViewById(R.id.txtAddress);
        tv_total = (TextView) view.findViewById(R.id.txtTotal);
        txt_note = (TextView) view.findViewById(R.id.txt_note);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        btn_order = (Button) view.findViewById(R.id.buttonContinue);

        getnote = getArguments().getString("txt_note");
        getlocation_id = getArguments().getString("location_id");
        deli_charges = 0;
        String getaddress = getArguments().getString("address");

        tv_timeslot.setText("" +getnote);
        tv_address.setText(getaddress);

        makeGetDelavaryCharge();

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    attemptOrder();
                } else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
            }
        });

        return view;
    }

    private void attemptOrder() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(100);
        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }


        // retrive data from cart database
        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();

        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);

                JSONObject jObjP = new JSONObject();

                try {
                    jObjP.put("product_id", map.get("product_id"));
                    jObjP.put("qty", map.get("qty"));
                    jObjP.put("unit_value", map.get("unit_value"));
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    jObjP.put("user_phone", user_phone);
                    jObjP.put("user_uid", user_uid);

                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
            seler_id = BaseURL.CurentIDCatagore;
           // seler_id = modelList.get(position).getCategory_id();

            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "from:" + "" + "\ndate:" + "" +
                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + "\ndata:" + passArray.toString());

                makeAddOrderRequest(getnote,getnote_sel,  getuser_id , seler_id, getlocation_id, passArray);
            }
        }
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeAddOrderRequest(String note , String note_sel, String userid, String seler_id,
                                     String location, JSONArray passArray) {

        // Tag used to cancel the request
        String tag_json_obj = "json_add_order_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("note", note);
        params.put("note_sel", note_sel);
        params.put("user_id", userid);
        params.put("seler_id", seler_id);
        params.put("location", location);
        params.put("data", passArray.toString());
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("data");

                        db_cart.clearCart();
                        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());

                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

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


    private void makeAddProductRequest(String note , String note_sel, String userid, String seler_id,
                                     String location, JSONArray passArray) {

        // Tag used to cancel the request
        progressBar.setProgress(0);
        String tag_json_obj = "json_add_product_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }

        String prod_title = "ar";
        String prod_title_en = "en";
        String prod_title_ku = "ku";
        String parent = "47";
        String prod_status = "1";
        String product_description = "";
        String unit = "1";
        String price = "1000";
        String qty = "1";

        Map<String, String> params = new HashMap<String, String>();
        params.put("prod_title", prod_title);
        params.put("prod_title_en", prod_title_en);
        params.put("prod_title_ku", prod_title_ku);
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

                        db_cart.clearCart();
                        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());

                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

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
    private void makeGetDelavaryCharge() {

        // Tag used to cancel the request
        String tag_json_obj = "json_category_req";



        JsonArrayRequest req = new JsonArrayRequest(BaseURL.get_DelevaryCharge,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                try {


                        JSONObject jsonObject = (JSONObject) response
                               .get(0) ;
                        String inh = jsonObject.getString("delivery_charge");

                        tv_total.setText(getResources().getString(R.string.tv_cart_item) + db_cart.getCartCount() + "\n" +
                                getResources().getString(R.string.tv_cart_item) + inh + "\n" +
                                getResources().getString(R.string.total_amount) +
                                db_cart.getTotalAmount() +" "+ getResources().getString(R.string.currency));

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
        AppController.getInstance().addToRequestQueue(req);
    }


}
