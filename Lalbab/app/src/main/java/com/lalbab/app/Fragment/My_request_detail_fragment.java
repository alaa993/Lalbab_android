package com.lalbab.app.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapter.My_order_detail_adapter;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Delivery_address_model;
import com.lalbab.app.Model.My_order_detail_model;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonArrayRequest;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.Session_management;

/**
 * Created by Rajesh Dabhi on 30/6/2017.
 */

public class My_request_detail_fragment extends Fragment {

    private static String TAG = My_request_detail_fragment.class.getSimpleName();

    private TextView tv_date, tv_total, tv_delivery_charge , tv_note  , tv_note_sel , tv_order_phone , text_name;
    private Button btn_cancle;
    private RecyclerView rv_detail_order;
    String location_id  = "";

    private String sale_id;

    private List<My_order_detail_model> my_order_detail_modelList = new ArrayList<>();

    public My_request_detail_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_my_request_detail, container, false);

        text_name = (TextView) view.findViewById(R.id.text_name);
        tv_date = (TextView) view.findViewById(R.id.tv_order_Detail_date);
        tv_note = (TextView) view.findViewById(R.id.tv_note);
        tv_note_sel = (TextView) view.findViewById(R.id.tv_note_sel);

        tv_delivery_charge = (TextView) view.findViewById(R.id.tv_order_Detail_deli_charge);
        tv_order_phone = (TextView) view.findViewById(R.id.tv_order_phone);
        tv_total = (TextView) view.findViewById(R.id.tv_order_Detail_total);
        btn_cancle = (Button) view.findViewById(R.id.btn_order_detail_cancle);
        rv_detail_order = (RecyclerView) view.findViewById(R.id.rv_order_detail);

        rv_detail_order.setLayoutManager(new LinearLayoutManager(getActivity()));

        sale_id = getArguments().getString("sale_id");
        String total_rs = getArguments().getString("total");
        String date = getArguments().getString("date");
        String note = getArguments().getString("note");
        final String note_sel = getArguments().getString("note_sel");
        final String status = getArguments().getString("status");
        location_id = getArguments().getString("location_id");

        String address = getArguments().getString("address");
        String phone = getArguments().getString("phone");
        String name_reseve = getArguments().getString("name_reseve");



        tv_delivery_charge.setText(getResources().getString(R.string.address) +" : " +address );
        tv_order_phone.setText(getResources().getString(R.string.phone_number) +" : " +phone );
        text_name.setText(""+name_reseve );


        //  makeGetAddressRequest();



        /*
        if (status.equals("0")) {
            btn_cancle.setVisibility(View.VISIBLE);
        } else {
            btn_cancle.setVisibility(View.GONE);
        }
        */

        long timestamp = Long.parseLong(date);


        tv_total.setText(total_rs);
        tv_date.setText(getResources().getString(R.string.date) + getDate(timestamp).toString());
        if (note_sel.length()>0 ){
            if (tv_note_sel.getVisibility()!=View.VISIBLE){
                tv_note_sel.setVisibility(View.VISIBLE);
            }
            tv_note_sel.setText(getResources().getString(R.string.note_sel) +""+ note_sel);
        }else {
            if (tv_note_sel.getVisibility()!=View.GONE){
                tv_note_sel.setVisibility(View.GONE);
            }
        }

        if (note.length()>0 ){
            if (tv_note.getVisibility()!=View.VISIBLE){
                tv_note.setVisibility(View.VISIBLE);
            }
            tv_note.setText(getResources().getString(R.string.note_user) +""+ note);
        }else {
            if (tv_note.getVisibility()!=View.GONE){
                tv_note.setVisibility(View.GONE);
            }
        }
       // tv_time.setText(getResources().getString(R.string.time) + time);
       // tv_delivery_charge.setText(getResources().getString(R.string.address) +" : " +address );

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetOrderDetailRequest(sale_id);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductDetail(status , note_sel);
            }
        });

        return view;
    }

    private String getDate(long time) {
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time * 1000L);
            String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

            return date;
        }catch (Exception e){
            return "";
        }

    }
    private List<Delivery_address_model> delivery_address_modelList = new ArrayList<>();


    private void makeGetAddressRequest() {
        Session_management sessionManagement = new Session_management(getActivity());
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        // Tag used to cancel the request
        String tag_json_obj = "json_get_address_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }



        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        delivery_address_modelList.clear();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Delivery_address_model>>() {
                        }.getType();

                        delivery_address_modelList = gson.fromJson(response.getString("data"), listType);

                        String address = "";
                        String phone = "";

                        for (int i=0 ;i<delivery_address_modelList.size() ;i++){
                            if (delivery_address_modelList.get(i).getLocation_id().equals(location_id)){
                                address = delivery_address_modelList.get(i).getPincode() + " " + delivery_address_modelList.get(i).getHouse_no() ;
                                phone = delivery_address_modelList.get(i).getReceiver_mobile() ;
                            }
                        }

                        tv_delivery_charge.setText(getResources().getString(R.string.address) +" : " +address );
                        tv_order_phone.setText(getResources().getString(R.string.phone_number) +" : " +phone );



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
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    int pos_change = 0;
    List<String> list ;

    private void showProductDetail(String status ,String note_sel) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_request_order);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        Spinner spinner_order = (Spinner) dialog.findViewById(R.id.spinner_order);
        final EditText txt_seler = (EditText) dialog.findViewById(R.id.txt_seler);
        Button bt_edit = (Button) dialog.findViewById(R.id.bt_edit);

        if (note_sel.length()>0){
            txt_seler.setText("" + note_sel);
        }

        String pending = getResources().getString(R.string.pending);
        String Conferm = getResources().getString(R.string.confirm);
        String delivered = getResources().getString(R.string.delivered);
        String Cancel = getResources().getString(R.string.cancle);
        String Delete = getResources().getString(R.string.delete);

        list = new ArrayList<String>();
        list.add(pending);
        list.add(Conferm);
        list.add(delivered);
        list.add(Cancel);
        //list.add(Delete);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_order.setAdapter(adapter);

        spinner_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pos_change = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_order.setSelection(Integer.parseInt(status));

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editOrder(txt_seler.getText().toString());
                dialog.cancel();
            }
        });

    }

    private void editOrder(String note_sel ) {
        Toast.makeText(getActivity(), "" + note_sel + " " + list.get(pos_change), Toast.LENGTH_SHORT).show();
        //sale_id;
       // BaseURL.CONFERM_ORDER_URL
        makeGetOrderConfermRequest(sale_id ,note_sel  );

    }


    // alertdialog for cancle order
    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(getResources().getString(R.string.cancle_order_note));
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Session_management sessionManagement = new Session_management(getActivity());
                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    makeDeleteOrderRequest(sale_id, user_id);
                }

                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Method to make json array request where json response starts wtih
     */


    private void makeGetOrderConfermRequest(final String sale_id , String note_sel) {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("note_sel", note_sel);
        params.put("status", ""+pos_change);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.CONFERM_ORDER_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Toast.makeText(getActivity(), "("+response.toString() + ")", Toast.LENGTH_SHORT).show();
                makeGetOrderDetailRequest(sale_id);

                /*
                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }
                */

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


    private void makeGetOrderDetailRequest(String sale_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.ORDER_DETAIL_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
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
    private void makeDeleteOrderRequest(String sale_id, String user_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_delete_order_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("user_id", user_id);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.DELETE_ORDER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("message");
                        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).onBackPressed();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
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
