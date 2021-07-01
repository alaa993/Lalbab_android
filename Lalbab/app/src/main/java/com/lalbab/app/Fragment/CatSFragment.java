package com.lalbab.app.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lalbab.app.AppController;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.MainActivity;
import com.lalbab.app.Model.Category_model;
import com.lalbab.app.R;
import com.lalbab.app.util.ConnectivityReceiver;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.RecyclerTouchListener;
import com.lalbab.app.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Cat_adapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CatSFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CatSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatSFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static String TAG = CatSFragment.class.getSimpleName();
    private RecyclerView rv_items;
    //private RelativeLayout rl_view_all;
    private Session_management sessionManagement;
    private List<Category_model> category_modelList = new ArrayList<>();
    private Cat_adapter adapter;
    private boolean isSubcat = false;
    public CatSFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatSFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CatSFragment newInstance(String param1, String param2) {
        CatSFragment fragment = new CatSFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BaseURL.loadLocale(getActivity());
        View view = inflater.inflate(R.layout.fragment_cat, container, false);
        setHasOptionsMenu(true);
        sessionManagement = new Session_management(this.getActivity());
        String getcat_id = getArguments().getString("cat_id");
        String getcat_title = getArguments().getString("cat_title");

        ((MainActivity) getActivity()).setTitle(getcat_title);
        ((MainActivity) getActivity()).updateHeader();


        // handle the touch event if true

        rv_items = (RecyclerView) view.findViewById(R.id.rv_cats);
        rv_items.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_items.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        if (ConnectivityReceiver.isConnected()) {

            makeGetCategoryRequest("52",getcat_id);
        }

        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String getid = category_modelList.get(position).getId();
                String getcat_title = category_modelList.get(position).getTitle();

                BaseURL.CURENT_CATAGORY_POSTION = position;


                Bundle args = new Bundle();
                android.app.Fragment fm = new Product_fragment();
                args.putString("cat_id", getid);
                args.putString("cat_title", getcat_title);
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



    private void makeGetCategoryRequest(String parent_id,String parent_cat) {

        // Tag used to cancel the request
        String tag_json_obj = "json_category_req";

        isSubcat = false;

        Map<String, String> params = new HashMap<String, String>();
        if (parent_id != null && parent_id != "") {
            params.put("parent", parent_id);
            params.put("catS", parent_cat);
            isSubcat = true;
        }

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY1_URL, params, new Response.Listener<JSONObject>() {

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
                        BaseURL.category_modelList = category_modelList;

                        adapter = new Cat_adapter(category_modelList);
                        rv_items.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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

    public interface OnFragmentInteractionListener {
    }
}
