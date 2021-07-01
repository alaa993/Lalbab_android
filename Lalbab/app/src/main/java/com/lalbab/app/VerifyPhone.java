package com.lalbab.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;

import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;
import com.lalbab.app.R;
import com.santalu.maskedittext.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.Session_management;


public class VerifyPhone extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getSimpleName();

    private Button btn_continue, btn_register;

    private TextView  btn_forgot;

    Button bt_code,bt_code2;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String phoneVerificationId;
    private String phoneVerificationIdCode;
    MaskEditText txt_phone , txt_code;
    CountryCodePicker txt_country;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;

    boolean islogin;

    boolean isProveder = false;
    boolean isloginUser = false;
    Boolean isCodeSend = false;
    LinearLayout lay_code;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_verify_phone);





        bt_code = findViewById(R.id.bt_code1);

        txt_code = findViewById(R.id.txt_code1);
        bt_code2 = findViewById(R.id.bt_code2);

        Intent intent = getIntent();
         final String mobile = intent.getStringExtra("mobile");



        fbAuth = FirebaseAuth.getInstance();
        user = fbAuth.getCurrentUser();

        bt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(mobile);
            }
        });

        bt_code2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyPhone.this, ""+user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            }
        });

        bt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyVerificationCode(txt_code.getRawText());
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user!=null){
                    //   txt_status.setText("User logged in "+user.getPhoneNumber());

                    bt_code.setEnabled(false);
                    txt_code.setText("");
                    islogin = true;
                    String Login_Sussess = getResources().getString(R.string.login_sussess);
                    Toast.makeText(getApplicationContext(), Login_Sussess, Toast.LENGTH_SHORT).show();


                } else{

                }
            }
        };
        login(mobile);

    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        user = task.getResult().getUser();

                    }catch (Exception e){e.printStackTrace();}

                    if (user!= null){
                        sussesLogin();
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
                        sussesLogin();
                    }else {
                        filedLogin();
                    }
                    //filedLogin();
                }
            }
        });
    }
    private void filedLogin() {
        Toast.makeText(this, "filedLogin", Toast.LENGTH_SHORT).show();
    }
    private void filedLogin1() {
        Toast.makeText(this, "This number is not registered, you must create a new account.", Toast.LENGTH_SHORT).show();
    }
    private void sussesLogin() {
        if (user!= null) {

            makeLoginRequest(user.getPhoneNumber(), user.getUid());

        }
    }



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
            Toast.makeText(VerifyPhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
/////////////////////////////////////////////////////////




    private void makeLoginRequest(String email, final String password) {

        // Tag used to cancel the request
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

                        Session_management sessionManagement = new Session_management(VerifyPhone.this);
                        sessionManagement.createLoginSession(user_id,user_email,user_fullname,user_phone,user_image,"","","","",password, statusu , id_catagore);

                        Intent i = new Intent(VerifyPhone.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(VerifyPhone.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VerifyPhone.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }




}
