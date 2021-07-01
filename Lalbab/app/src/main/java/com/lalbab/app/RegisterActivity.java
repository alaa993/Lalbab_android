package com.lalbab.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.lalbab.app.R;
import com.lalbab.app.util.Session_management;
import com.santalu.maskedittext.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.util.CustomVolleyJsonRequest;

public class RegisterActivity extends AppCompatActivity {

    private static String TAG = RegisterActivity.class.getSimpleName();

    private EditText  et_name;
    private Button btn_register;
    private TextView  tv_name;


    private FirebaseAuth fbAuth;
    private FirebaseUser user;

    boolean isLocated;
    boolean isProveder =false;
    boolean isCodeSend = false;


    private String phoneVerificationId;
    private String phoneVerificationIdCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    String mobile = "";
    String uid = "";
    String namee="";
    private String phone = "";
    MaskEditText txt_phone , txt_code;

    Button bt_code;

    CountryCodePicker txt_country;
    LinearLayout lay_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        BaseURL.loadLocale(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        uid = intent.getStringExtra("uid");

        et_name = (EditText) findViewById(R.id.et_reg_name);
        namee=et_name.getText().toString();
        tv_name = (TextView) findViewById(R.id.tv_reg_name);
        btn_register = (Button) findViewById(R.id.btnRegister);


        lay_code = findViewById(R.id.lay_code);
        txt_phone = findViewById(R.id.txt_phone);
        txt_code = findViewById(R.id.txt_code);
        bt_code = findViewById(R.id.bt_code);
        txt_country = findViewById(R.id.txt_country);



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // login();

                makeRegisterRequest(et_name.getText().toString(), mobile,
                        "", uid);



            }
        });



    }


    private void makeRegisterRequest(final String name, final String mobile,
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

        params.put("user_email", email);
        params.put("password", password);
        //  params.put("user_phone", user_phone);
        // params.put("user_uid", user_uid);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);

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
                        makeLoginRequest(mobile, uid);

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(RegisterActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
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

                        Session_management sessionManagement = new Session_management(RegisterActivity.this);
                        sessionManagement.createLoginSession(user_id,user_email,user_fullname,user_phone,user_image,"","","","",password, statusu , id_catagore);

                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(RegisterActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
