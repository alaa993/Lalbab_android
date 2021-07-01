package com.lalbab.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class phoneVerfication extends AppCompatActivity {
    private final int REQUESR_LOG = 1000;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0 ;
    private static String TAG = RegisterActivity.class.getSimpleName();
    String et_name;
    String mobile;
    String stat="";
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        et_name = intent.getStringExtra("mobile");

//        final String mobile = intent.getStringExtra("mobile");



            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder().setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build())).setLogo(R.drawable.ccp_down_arrow).setTheme(R.style.AppTheme).build(),REQUESR_LOG);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESR_LOG) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    checkloginvsregister(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    return;
                } else {
                    if (response == null) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "NO internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Unkonw erorrs", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }





    private void makeRegisterRequest(final String name, String mobile,
                                     final String email, final String password) {

        // Tag used to cancel the request
        String tag_json_obj = "json_register_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }



        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", name);
        params.put("user_mobile", mobile);
        params.put("user_email", email);
        params.put("password", password);
        //  params.put("user_phone", user_phone);
        // params.put("user_uid", user_uid);
        params.put("user_phone", mobile);
        params.put("user_uid", "test");

        final String statusu = "1";
        final String finalUser_uid = user_uid;
        final String finalUser_phone = user_phone;
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.REGISTER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Session_management sessionManagement = new Session_management(phoneVerfication.this);
                        sessionManagement.createLoginSession(finalUser_uid,email,name, finalUser_phone,"","","","","",password, statusu , "0");
                        Toast.makeText(phoneVerfication.this,"تم بنجاح تسجيل حساب جديد", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(phoneVerfication.this,MainActivity.class);
                        startActivity(i);
                        finish();


                    } else {
                        String error = response.getString("error");
                        Toast.makeText(phoneVerfication.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(phoneVerfication.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeLoginRequest(String email, final String password) {

        // Tag used to cancel the request
        Toast.makeText(this, "login request sended", Toast.LENGTH_SHORT).show();
        String tag_json_obj = "json_login_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("user_email", email);
        params.put("password", password);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        JSONObject obj = response.getJSONObject("data");
                        String user_id = obj.getString("user_id");
                        String user_fullname = obj.getString("user_fullname");
                        String user_email = obj.getString("user_email");
                        String user_phone = obj.getString("user_phone");
                        String user_image = obj.getString("user_image");
                        String id_catagore = obj.getString("id_catagore");
                        String statusu = "0";
                        if (obj.has("status"))
                            statusu = obj.getString("status");
                        Log.e("statusu", ":"+statusu);

                        Session_management sessionManagement = new Session_management(phoneVerfication.this);
                        sessionManagement.createLoginSession(user_id,user_email,user_fullname,user_phone,user_image,"","","","",password, statusu , id_catagore);

                        Intent i = new Intent(phoneVerfication.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(phoneVerfication.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(phoneVerfication.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void checkloginvsregister(String email, final String password) {

        // Tag used to cancel the request
        Toast.makeText(this, "login request sended", Toast.LENGTH_SHORT).show();
        String tag_json_obj = "json_login_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("user_email", email);
        params.put("password", password);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.CHECK_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    stat = response.getString("responcee");


                    if (stat.equals("L")) {
                        Toast.makeText(phoneVerfication.this, stat, Toast.LENGTH_SHORT).show();
                        makeLoginRequest(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                        return;
                    }
                    else if (stat.equals("R")){

                        Intent intent = new Intent(phoneVerfication.this,RegisterActivity.class);
                        intent.putExtra("mobile", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        intent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        finish();
                        return;
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
                    Toast.makeText(phoneVerfication.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}

