package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class MainActivityx extends AppCompatActivity {


    private EditText et;
    private EditText et2;
    private Button button;
    private RequestQueue requestQueue;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainx);
        et=findViewById(R.id.editText);
        et2=findViewById(R.id.editText2);
        button=findViewById(R.id.button);
        requestQueue= Volley.newRequestQueue(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://attendancestcet.000webhostapp.com/login_teachers.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++) {
                                String s;
                                s = jsonArray.getJSONObject(i).getString("tpwd");
                                String id=jsonArray.getJSONObject(i).getString("tid");
                                if (s.equals(et2.getText().toString()) && id.equals(et.getText().toString())) {
                                    String tid = et.getText().toString();
                                    Intent intent = new Intent(MainActivityx.this, Main2Activity.class);
                                    intent.putExtra("Data", tid);
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }else if(s.equals(et2.getText().toString()) || id.equals(et.getText().toString()))
                                    Toast.makeText(getApplicationContext(), "Wrong credentials!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e)
                        {

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(stringRequest);

            }
        });

    }

    public void check(View view) {
        if(et.getText().toString().equals("admin") && et2.getText().toString().equals("1234")){
            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
            String tid = et.getText().toString();
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra("Data", tid);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong credentials!",Toast.LENGTH_SHORT).show();
        }
    }
}
