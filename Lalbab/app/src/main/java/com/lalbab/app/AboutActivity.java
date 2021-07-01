package com.lalbab.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lalbab.app.R;

import androidx.appcompat.app.AppCompatActivity;


import com.lalbab.app.Config.BaseURL;

public class AboutActivity extends AppCompatActivity {
    Button bt_close ;
    TextView address ;
    TextView phone ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseURL.loadLocale(this);
        setContentView(R.layout.activity_about);

        bt_close = findViewById(R.id.bt_close);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void gps(){

    }
}
