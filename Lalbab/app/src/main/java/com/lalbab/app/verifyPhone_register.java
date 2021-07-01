package com.lalbab.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
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
import java.util.concurrent.TimeUnit;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.util.CustomVolleyJsonRequest;

public class verifyPhone_register extends AppCompatActivity {
    private static String TAG = RegisterActivity.class.getSimpleName();

    private String et_name;
    private Button btn_register;
    private TextView tv_name;


    private FirebaseAuth fbAuth;
    private FirebaseUser user;

    boolean isLocated;
    boolean isProveder =false;
    boolean isCodeSend = false;
    boolean isloginUser = false;


    private String phoneVerificationId;
    private String phoneVerificationIdCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String mVerificationId;

    private String phone = "";
    MaskEditText txt_phone , txt_code;

    Button bt_code,bt_code0;

    CountryCodePicker txt_country;
    LinearLayout lay_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseURL.loadLocale(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verify_phone_register);



        bt_code = findViewById(R.id.bt_code3);
        bt_code0 = findViewById(R.id.bt_code0);
        txt_code=findViewById(R.id.txt_code3);
        Intent intent = getIntent();
        final String mobile = intent.getStringExtra("mobile");
         et_name = intent.getStringExtra("getname");




        fbAuth = FirebaseAuth.getInstance();
        user = fbAuth.getCurrentUser();

        bt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                verifyVerificationCode(txt_code.getRawText());
            }
        });
        bt_code0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(mobile);
            }
        });
        login(mobile);
    }
/////////////////////////////////
    private void login(String mobile) {
        isCodeSend = false;
        isloginUser = true;


        Toast.makeText(this, "(" + mobile +  ")", Toast.LENGTH_SHORT).show();

        Log.e("login", "" + mobile +"user");


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                txt_code.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verifyPhone_register.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    ///////////////////////////////////////////



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        user = task.getResult().getUser();


                    }catch (Exception e){e.printStackTrace();}

                    if (user != null){
                        regesterSusses();
                        //   regesterUser(user.getPhoneNumber());
                    }else {
                        filedLogin();
                    }

                }else {
                    try {
                        task.getException();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (user!= null){
                        regesterSusses();
                    }else {
                        filedLogin();
                    }
                }
            }
        });
    }

    private void regesterSusses() {
        String getphone = user.getPhoneNumber().toString();
        String getname = et_name;
        String getpassword = user.getUid().toString();
        makeRegisterRequest(getname, getphone, "", getpassword);

    }

    private void filedLogin() {
        String Login_filed = getResources().getString(R.string.login_filed);
        Toast.makeText(getApplicationContext(), Login_filed, Toast.LENGTH_SHORT).show();
    }


///////////this is update///////////

    /**
     * Method to make json object request where json response starts wtih
     */
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
                        Session_management sessionManagement = new Session_management(verifyPhone_register.this);
                        sessionManagement.createLoginSession(finalUser_uid,email,name, finalUser_phone,"","","","","",password, statusu , "0");
                        Toast.makeText(verifyPhone_register.this,"تم بنجاح تسجيل حساب جديد", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(verifyPhone_register.this,MainActivity.class);
                        startActivity(i);
                        finish();


                    } else {
                        String error = response.getString("error");
                        Toast.makeText(verifyPhone_register.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(verifyPhone_register.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
