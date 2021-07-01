package com.lalbab.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DawaActivity extends AppCompatActivity {
Button btnWhatsApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dawa);
        btnWhatsApp = (Button)findViewById(R.id.btnWhatsApp3);
        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
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
    }
}
