package com.example.attendancemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;

public class StudentRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button b1;
    EditText name,roll,reg,mail,phone;
    boolean checked, checked1;
    AlertDialog.Builder builder;
    String dept,year,t;
    public static String user,pass,m;
    public static final String REGEX_STORE="mydata";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sf;
    private Context mContext;
    String id;
    RequestQueue requestQueue;
    String url;
    JSONArray jsonArray;
    JSONObject ids;
    String m_Text;

    private RelativeLayout rlayout;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);


        rlayout = findViewById(R.id.rlayout);
        animation = AnimationUtils.loadAnimation(this,R.anim.uptodowndiagonal);
        rlayout.setAnimation(animation);

        mContext=this;
        sharedPreferences=this.getSharedPreferences(StudentRegisterActivity.REGEX_STORE,Context.MODE_PRIVATE);

        int xy=Calendar.getInstance().get(Calendar.MONTH)+1;
        int date=Calendar.getInstance().get(Calendar.DATE);
        Calendar cal = Calendar.getInstance();
        int res = cal.getActualMaximum(Calendar.DATE);
        if(xy!=4 && xy!=7) {
            Toast.makeText(this,"Registration Time Over",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,StudentActivity.class);
            startActivity(i);
            finish();
        }
        else if((xy==12 || xy==6) && date==res) {
            SharedPreferences.Editor sp;
            sp=sharedPreferences.edit();
            sp.putInt("re",0);
            sp.commit();
            Toast.makeText(this,"Registration starts tomorrow",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,StudentActivity.class);
            startActivity(i);
            finish();
        }
        else {
            int a=sharedPreferences.getInt("re",0);
            if(a==1) {
                Toast.makeText(this,"Already Registered!",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(this,StudentActivity.class);
                startActivity(i);
                finish();
            }
        }

        b1=findViewById(R.id.button12);
        mail=findViewById(R.id.editText);
        reg=findViewById(R.id.editText5);
        name=findViewById(R.id.editText3);
        roll=findViewById(R.id.editText4);
        phone=findViewById(R.id.editText6);
        b1.setOnClickListener(this);

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

        sharedPreferences=mContext.getSharedPreferences(REGEX_STORE,Context.MODE_PRIVATE);
        sf=sharedPreferences.edit();

        String n=name.getText().toString(); String r=roll.getText().toString();
        String re=reg.getText().toString(); m=mail.getText().toString();
        String p=phone.getText().toString();
        String t=dept+year+"_"+ Calendar.getInstance().get(Calendar.YEAR);

        int sem=0;
        if(((Button)view).getText().toString().equals("VERIFY")) {

            if(TextUtils.isEmpty(m)) {
                mail.setError("Email can't be empty");
            }
            else if(TextUtils.isEmpty(n)) {
                name.setError("Name can't be empty");
            }
            else if(TextUtils.isEmpty(r)) {
                roll.setError("Roll Number can't be empty");
            }
            else if(TextUtils.isEmpty(p)){
                phone.setError("Mobile Number can't be empty");
            }
            else if(TextUtils.isEmpty(re)){
                reg.setError("College Registration Number can't be empty");
            }
            else if(checked==false)
                Toast.makeText(this,"Select Department",Toast.LENGTH_SHORT).show();
            else if(checked1==false) Toast.makeText(this,"Select Year",Toast.LENGTH_SHORT).show();
            else {

                try {
                    user = n.toLowerCase().split(" ")[0] + "_" + dept + "_" + r;
                    pass = n.toLowerCase().substring(0, 3) + "@" + n.toLowerCase().split(" ")[n.split(" ").length - 1].substring(0, 2) + "_" + r;
                    Pattern pat = Pattern.compile("^[0-9]{1,3}$");
                    if (!pat.matcher(r).matches()) {
                        Toast.makeText(this, "Incorrect Roll Number Format", Toast.LENGTH_SHORT).show();

                    } else {

                        pat = Pattern.compile("^[0-9]{2}\\/[0-9]{3}$");
                        if (!pat.matcher(re).matches()) {
                            Toast.makeText(this, "Incorrect Registration Number Format", Toast.LENGTH_SHORT).show();
                        } else {

                            pat = Pattern.compile("(0/91)?[7-9][0-9]{9}");
                            if (!pat.matcher(p).matches()) {
                                Toast.makeText(this, "Incorrect Mobile Number (Enter only 10 digit mobile number)", Toast.LENGTH_SHORT).show();
                            } else {

                                String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

                                Pattern pattern = Pattern.compile(regex);

                                if (!pattern.matcher(m).matches()) {
                                    Toast.makeText(this, "Incorrect Email ID format", Toast.LENGTH_SHORT).show();
                                } else {

                                    Random random = new Random();
                                    id = String.format("%04d", random.nextInt(10000));
                                    requestQueue = Volley.newRequestQueue(this);
                                    JSONObject jsonObject = new JSONObject();
                                    url = "https://attendancestcet.000webhostapp.com/mail.php";
                                    try {
                                        jsonObject.put("otp", id);
                                        jsonObject.put("email", m);
                                        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });

                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                    }

                                    builder = new AlertDialog.Builder(this);
                                    builder.setTitle("Email Verification-Enter OTP");

// Set up the input
                                    final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    builder.setView(input);

// Set up the buttons
                                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            m_Text = input.getText().toString();
                                            if(m_Text.equals("")) {
                                                Toast.makeText(mContext,"You did not enter OTP",Toast.LENGTH_SHORT).show();
                                            }
                                            else if (m_Text.equals(id)) {
                                                b1.setText("SUBMIT");

                                            } else {
                                                Toast.makeText(mContext,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(StudentRegisterActivity.this, StudentRegisterActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });

                                    builder.show();
                                }


                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Name should contain First and Last Name at least", Toast.LENGTH_LONG).show();
                }


            }
        }
        else if(((Button)view).getText().toString().equals("SUBMIT")) {
            if(checked==false || checked1==false || n.equals("") || r.equals("") || re.equals("") || m.equals("") || p.equals(""))
                Toast.makeText(this,"Empty fields!",Toast.LENGTH_SHORT).show();
            else {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                if(Calendar.getInstance().get(Calendar.MONTH)+1==4 || Calendar.getInstance().get(Calendar.MONTH)+1==7) {
                    if (Calendar.getInstance().get(Calendar.MONTH) > 6) {
                        if (Integer.parseInt(year) == 1) sem = 1;
                        else if (Integer.parseInt(year) == 2) sem = 3;
                        else if (Integer.parseInt(year) == 3) sem = 5;
                        else if (Integer.parseInt(year) == 4) sem = 7;
                    } else {
                        if (Integer.parseInt(year) == 1) sem = 2;
                        else if (Integer.parseInt(year) == 2) sem = 4;
                        else if (Integer.parseInt(year) == 3) sem = 6;
                        else if (Integer.parseInt(year) == 4) sem = 8;
                    }

                    try {
                        sf.putInt("roll",Integer.parseInt(r));
                        sf.putString("name",n);
                        sf.putInt("year",Integer.parseInt(year));
                        sf.putString("dept",dept);
                        sf.putInt("sem",sem);
                        sf.putString("regno",re);
                        sf.putString("username",user);
                        sf.putString("password",pass);
                        sf.putInt("hr",0);
                        sf.putInt("min",0);
                        sf.putInt("day",0);
                        sf.commit();

                        requestQueue = Volley.newRequestQueue(this);
                        JSONObject jsonObject = new JSONObject();
                        url = "https://attendancestcet.000webhostapp.com/new.php";
                        try {
                            jsonObject.put("reg",re);
                            jsonObject.put("user", user);
                            jsonObject.put("pass", pass);
                            CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("res","Hey");
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("res","Hi");
                                }
                            });

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                        }

                        requestQueue = Volley.newRequestQueue(this);
                        jsonObject = new JSONObject();
                        url = "https://attendancestcet.000webhostapp.com/register.php";
                        try {
                            jsonObject.put("tab", t);
                            jsonObject.put("roll", r);
                            jsonObject.put("reg_no", re);
                            jsonObject.put("name", n);
                            jsonObject.put("phone", p);
                            jsonObject.put("email", m);
                            CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    sf.putInt("re",1);
                                    sf.commit();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(StudentRegisterActivity.this, SplashRegisterActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    builder.setMessage("Please enter roll number and registration number correctly")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(StudentRegisterActivity.this, StudentRegisterActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Incorrect Roll or Registration Number");
                                    alert.show();
                                }
                            });

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            progressDialog.dismiss();

                        }

                    }
                    catch(Error e) {
                        progressDialog.dismiss();
                        Toast.makeText(this,"You have already registered",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    dept="it";
                    break;
            case R.id.radioButton2:
                if (checked)
                    dept="cse";
                    break;
            case R.id.radioButton3:
                if (checked)
                    dept="ece";
                    break;
            case R.id.radioButton4:
                if (checked)
                    dept="ee";
                    break;
        }

    }

    public void onRadioButtonClicked1(View view) {
        // Is the button now checked?
        checked1 = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButton5:
                if (checked)
                    year="2";
                break;
            case R.id.radioButton6:
                if (checked)
                    year="3";
                break;
            case R.id.radioButton7:
                if (checked)
                    year="1";
                break;
            case R.id.radioButton8:
                if (checked)
                    year="4";
                break;
        }


    }

}
