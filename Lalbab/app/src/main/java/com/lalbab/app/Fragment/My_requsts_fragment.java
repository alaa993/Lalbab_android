package com.lalbab.app.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.My_order_adapter;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.My_order_model;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonArrayRequest;
import com.lalbab.app.util.RecyclerTouchListener;
import com.lalbab.app.util.Session_management;

/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class My_requsts_fragment extends Fragment {

    private static String TAG = My_requsts_fragment.class.getSimpleName();

    private RecyclerView rv_myorder;

    private List<My_order_model> my_order_modelList = new ArrayList<>();

    public My_requsts_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_my_requests, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.my_order));

        // handle the touch event if true
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // check user can press back button or not
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    Fragment fm = new Home_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

        rv_myorder = (RecyclerView) view.findViewById(R.id.rv_myorder);
        rv_myorder.setLayoutManager(new LinearLayoutManager(getActivity()));

        Session_management sessionManagement = new Session_management(getActivity());
        String cat_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID_CATAGORE);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetOrderRequest(cat_id);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        // recyclerview item click listener
        rv_myorder.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_myorder, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String sale_id = my_order_modelList.get(position).getSale_id();
                String seler_id = my_order_modelList.get(position).getSeler_id();
                String date = my_order_modelList.get(position).getOn_date();
                String note = my_order_modelList.get(position).getNote();
                String note_sel = my_order_modelList.get(position).getNote_sel();
                String time = my_order_modelList.get(position).getDelivery_time_from()+"-"+ my_order_modelList.get(position).getDelivery_time_to();
                String total = my_order_modelList.get(position).getTotal_amount();
                String status = my_order_modelList.get(position).getStatus();
                String deli_charge = my_order_modelList.get(position).getDelivery_charge();
                String address = my_order_modelList.get(position).getDelivery_address() ;
                String location_id = my_order_modelList.get(position).getLocation_id();
                String phone = my_order_modelList.get(position).getPhone();
                String name_reseve = my_order_modelList.get(position).getName_resever();

                Bundle args = new Bundle();
                Fragment fm = new My_request_detail_fragment();
                args.putString("sale_id", sale_id);
                args.putString("seler_id", seler_id);
                args.putString("date", date);
                args.putString("note", note);
                args.putString("note_sel", note_sel);
                args.putString("time", time);
                args.putString("total", total);
                args.putString("status", status);
                args.putString("deli_charge", deli_charge);
                args.putString("address", address);
                args.putString("phone", phone);
                args.putString("name_reseve", name_reseve);
                args.putString("location_id", location_id);





                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetOrderRequest(String cat_id) {


        // Tag used to cancel the request
        String tag_json_obj = "json_requests_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("cat_id", cat_id);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.GET_REQUESTS_URL, params, new Response.Listener<JSONArray>() {




            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_model>>() {}.getType();

                my_order_modelList = gson.fromJson(response.toString(), listType);

                My_order_adapter adapter = new My_order_adapter(my_order_modelList);
                rv_myorder.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if(my_order_modelList.isEmpty()){
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

}
