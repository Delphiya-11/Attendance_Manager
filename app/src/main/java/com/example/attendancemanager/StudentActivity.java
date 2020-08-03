package com.example.attendancemanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {

    Button submit, home;
    ImageButton register;
    EditText et1,et2;
    String user,pwd;
    AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sp;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        context=this;
        sharedPreferences=context.getSharedPreferences(StudentRegisterActivity.REGEX_STORE,Context.MODE_PRIVATE);
        sp=sharedPreferences.edit();

        submit=findViewById(R.id.button6);
        register=findViewById(R.id.button8);
        home=findViewById(R.id.button4);
        et1=findViewById(R.id.editText);
        et2=findViewById(R.id.editText2);
        submit.setOnClickListener(this);
        register.setOnClickListener(this);
        home.setOnClickListener(this);
        builder = new AlertDialog.Builder(this);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        if(view.equals(submit)) {
            user = et1.getText().toString();
            pwd = et2.getText().toString();
            if(TextUtils.isEmpty(user)) {
                et1.setError("Username can't be empty");
            }
            else if(TextUtils.isEmpty(pwd)){
                et2.setError("Password can't be empty");
            }
            else {

                try {

                    String us = sharedPreferences.getString("username", null);
                    String pw = sharedPreferences.getString("password", null);
                    if ((user.equals(us) && pwd.equals(pw)) || (user.equals("admin") && pwd.equals("admin"))) {
                        sp.putInt("login",1);
                        sp.commit();
                        Intent i = new Intent(this, StudentOptionActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StudentActivity.this, StudentActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(this,"Register first!",Toast.LENGTH_SHORT).show();
                }

            }
        }
        else if(view.equals(register)) {
            Intent intent = new Intent(StudentActivity.this, StudentRegisterActivity.class);
            startActivity(intent);
        }
        else if(view.equals(home)) {
            Intent intent = new Intent(StudentActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
