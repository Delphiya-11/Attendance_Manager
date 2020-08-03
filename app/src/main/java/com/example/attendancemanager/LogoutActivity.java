package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class LogoutActivity extends AppCompatActivity {

    Handler handler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        sharedPreferences=this.getSharedPreferences(StudentRegisterActivity.REGEX_STORE, Context.MODE_PRIVATE);
        sp=sharedPreferences.edit();

        sp.putInt("login",0);
        sp.commit();

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LogoutActivity.this,StudentActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);

    }
}
