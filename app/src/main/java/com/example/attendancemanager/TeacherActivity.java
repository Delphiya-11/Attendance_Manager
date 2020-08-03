package com.example.attendancemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

public class TeacherActivity extends AppCompatActivity {

    EditText username, password;
    Button submit;
    TextInputLayout til, til1;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String tid, tname, tpwd;
    int did;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        sharedPreferences=this.getSharedPreferences("Login", 0);
        if (sharedPreferences.contains("Username") && sharedPreferences.contains("Password")){
            //Toast.makeText(getApplicationContext(),"Logged in as: "+sharedPreferences.getString("Name",""),Toast.LENGTH_SHORT).show();
            moveToActionActivity();
        }

        getSupportActionBar().hide();

        til=findViewById(R.id.textInputLayout);
        til1=findViewById(R.id.textInputLayout1);

        submit=findViewById(R.id.submit);

        username= findViewById(R.id.userName);
        password= findViewById(R.id.passWord);

        progressBar=findViewById(R.id.progressBar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String strUserName = username.getText().toString();
                final String strPassWord = password.getText().toString();

                if(TextUtils.isEmpty(strUserName) && TextUtils.isEmpty(strPassWord)) {
                    username.setError("Username can't be empty");
                    password.setError("Password can't be empty");
                }
                else if(TextUtils.isEmpty(strUserName)) {
                    username.setError("Username can't be empty");
                }
                else if(TextUtils.isEmpty(strPassWord)){
                    password.setError("Password can't be empty");
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    RequestQueue queue1 = Volley.newRequestQueue(TeacherActivity.this);
                    String url1 ="https://attendancestcet.000webhostapp.com/get_pwd.php?tid="+strUserName;
                    StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.

                                    try {
                                        jsonArray = new JSONArray(response);
                                        jsonObject = jsonArray.getJSONObject(0);
                                        tid=strUserName;
                                        tname =jsonObject.getString("tname");
                                        tpwd =jsonObject.getString("tpwd");
                                        did =jsonObject.getInt("did");

                                        if (strPassWord.equals(tpwd)){
                                            SharedPreferences.Editor ed=sharedPreferences.edit();
                                            ed.putString("Username",strUserName);
                                            ed.putString("Name",tname);
                                            ed.putString("Password",strPassWord);
                                            ed.putInt("DeptId",did);
                                            ed.commit();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getApplicationContext(),"Successfully Logged In!!!", Toast.LENGTH_SHORT).show();
                                            moveToActionActivity();
                                        }
                                        else{
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getApplicationContext(),"Invalid Username or, Password!!!", Toast.LENGTH_SHORT).show();
                                            username.setText("");
                                            password.setText("");
                                            username.requestFocus();
                                        }
                                    }
                                    catch (Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(),"Invalid Username or, Password!!!", Toast.LENGTH_SHORT).show();
                                        username.setText("");
                                        password.setText("");
                                        username.requestFocus();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Cannot log in!!! Check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue1.add(stringRequest1);
                }
            }
        });
    }

    private void moveToActionActivity(){
        Intent intent=new Intent(TeacherActivity.this, DepartmentActivity.class);
        /*Bundle bundle=new Bundle();
        bundle.putString("Tid", tid);
        bundle.putString("Tname", tname);
        bundle.putString("Tpwd", tpwd);
        bundle.putInt("Did", did);
        intent.putExtras(bundle);*/
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(TeacherActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }


}