package com.lalbab.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.santalu.maskedittext.MaskEditText;

import com.lalbab.app.Config.BaseURL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = LoginActivity.class.getSimpleName();

    private Button btn_continue, btn_register;

    private TextView  btn_forgot;

    Button bt_code;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        BaseURL.loadLocale(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        lay_code = (LinearLayout) findViewById(R.id.lay_code);

        btn_continue = (Button) findViewById(R.id.btnWhatsapp);
        btn_register = (Button) findViewById(R.id.btnRegister);
        btn_forgot = (TextView) findViewById(R.id.btnForgot);

        bt_code = findViewById(R.id.bt_code);
        txt_phone = findViewById(R.id.txt_phone);
        txt_code = findViewById(R.id.txt_code);
        txt_country = findViewById(R.id.txt_country);

        btn_continue.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);

        fbAuth = FirebaseAuth.getInstance();
        user = fbAuth.getCurrentUser();



    }



    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnWhatsapp) {
            //attemptLogin();
            //login();
            Intent startlogin = new Intent(LoginActivity.this, phoneVerfication.class);
            startActivity(startlogin);

        } else if (id == R.id.btnRegister) {
            Intent startlogin = new Intent(LoginActivity.this, phoneVerfication.class);
            startActivity(startlogin);
        }else if (id == R.id.btnForgot) {
            Intent startRegister = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(startRegister);
        }
    }

    private void login() {
        String mobile = txt_country.getFullNumberWithPlus()+ txt_phone.getRawText();
        if(mobile.isEmpty() || mobile.length() < 10){
            txt_phone.setError("Enter a valid mobile");
            txt_phone.requestFocus();
            return;
        }

        Intent intent = new Intent(LoginActivity.this, VerifyPhone.class);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }

        /*if (txt_phone.getRawText().length() == 10 ) {

            Toast.makeText(this, "(" + phoneNumber + ")", Toast.LENGTH_SHORT).show();

            Log.e("login", "" + phoneNumber);
            setUpVerifcationCallback();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, verificationStateChangedCallbacks);
        }else {
            String error_phone = getResources().getString(R.string.error_phone);
            Toast.makeText(this, "" + error_phone , Toast.LENGTH_SHORT).show();

        }*/




}
