package com.lalbab.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.net.URI;

public class ContactUs extends AppCompatActivity {
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    private Button btn_call, btn_Whatsapp;
    private Button btn_call1, btn_Whatsapp1;
    private Button btn_mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        btn_call = findViewById(R.id.btnCall);
        btn_Whatsapp = findViewById(R.id.btnWhatsapp);

        btn_call1 = findViewById(R.id.btnCall1);
        btn_Whatsapp1 = findViewById(R.id.btnWhatsapp1);

        btn_mail = findViewById(R.id.btnMail);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = "+9647503886300";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        btn_Whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String smsNumber = "+9647503886300"; //without '+'
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "+9647503886300")));
                if (getPackageManager().resolveActivity(intent, 0) != null) {
                    startActivity(intent);
                } else {

                }
            }
        });

        btn_call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = "+9647723888300";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

            }
        });

        btn_Whatsapp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsNumber = "+9647723888300"; //without '+'
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s", "+9647723888300")));
                if (getPackageManager().resolveActivity(intent, 0) != null) {
                    startActivity(intent);
                } else {

                }

            }
        });

        btn_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:info@lalbab.net"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

}
