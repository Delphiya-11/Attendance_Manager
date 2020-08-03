package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StudentOptionActivity extends AppCompatActivity implements View.OnClickListener {

    Button b9,b10,b11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_option);

        b9=findViewById(R.id.button9);
        b10=findViewById(R.id.button10);
        b11=findViewById(R.id.button11);
        b9.setOnClickListener(this);
        b10.setOnClickListener(this);
        b11.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.exit) {
            finishAffinity();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(b9)) {
            Intent intent = new Intent(StudentOptionActivity.this, ScanActivity.class);
            startActivity(intent);
        }
        else if(view.equals(b10)) {
            Intent intent = new Intent(StudentOptionActivity.this, ViewActivity.class);
            startActivity(intent);
        }
        else if(view.equals(b11)) {
            Intent intent = new Intent(StudentOptionActivity.this, LogoutActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
