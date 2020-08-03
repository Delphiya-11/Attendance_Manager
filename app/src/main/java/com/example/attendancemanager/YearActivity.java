package com.example.attendancemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Calendar;

public class YearActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    Switch simpleSwitch;
    EditText tableName;
    ProgressBar progressBar;
    Button go;
    TextView textView1, textView2;
    TextInputLayout til;
    CardView cardView1, cardView2;

    String tName;
    String tid, tname, tpwd;
    int did, deptid;

    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        spref=getSharedPreferences("Login", MODE_PRIVATE);
        tid=spref.getString("Username","");
        tname=spref.getString("Name","");
        tpwd=spref.getString("Password","");
        did=spref.getInt("DeptId",0);

        Bundle b = getIntent().getExtras();
        deptid=b.getInt("DeptId");

        /*Bundle b = getIntent().getExtras();
        tid= b.getString("Tid");
        tname=b.getString("Tname");
        tpwd=b.getString("Tpwd");
        did=b.getInt("Did");*/

        cardView1=findViewById(R.id.cardView5);
        cardView2=findViewById(R.id.cardView7);

        textView1=findViewById(R.id.textView4);
        textView2=findViewById(R.id.textView5);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.nav_header_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YearActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
        TextView nameView = (TextView) hView.findViewById(R.id.nav_header_textView);
        nameView.setText(tname);

        til=findViewById(R.id.textInputLayout);

        progressBar=findViewById(R.id.progressBar);

        simpleSwitch=findViewById(R.id.switch1);

        go=findViewById(R.id.go);

        //result=findViewById(R.id.result);

        tableName=findViewById(R.id.tableName);
        tableName.requestFocus();

        dl = findViewById(R.id.drawerLayout);
        nv = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, dl, R.string.open_menu, R.string.close_menu);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellow)));
        getSupportActionBar().setTitle("Year");

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.check_give:
                        Intent intent=new Intent(YearActivity.this, ActionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("DeptId", deptid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.year:
                        Intent intent1=new Intent(YearActivity.this, YearActivity.class);
                        Bundle bundle1=new Bundle();
                        bundle1.putInt("DeptId", deptid);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case R.id.subject:
                        Intent intent2=new Intent(YearActivity.this, SubjectActivity.class);
                        Bundle bundle2=new Bundle();
                        bundle2.putInt("DeptId", deptid);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case R.id.verify:
                        Intent intent3=new Intent(YearActivity.this, VerifyActivity.class);
                        Bundle bundle3=new Bundle();
                        bundle3.putInt("DeptId", deptid);
                        intent3.putExtras(bundle3);
                        startActivity(intent3);
                        break;
                    case R.id.percentage:
                        Intent intent4=new Intent(YearActivity.this, PercentageActivity.class);
                        Bundle bundle4=new Bundle();
                        bundle4.putInt("DeptId", deptid);
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        break;
                    case R.id.department:
                        Intent intent5=new Intent(YearActivity.this, DepartmentActivity.class);
                        startActivity(intent5);
                        break;
                    default: return(true);
                }
                return true;
            }
        });

        if(tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
            til.setEnabled(true);
            go.setEnabled(true);
            go.setOnClickListener(this);
        }
        else {
            til.setEnabled(false);
            go.setEnabled(false);
            Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
        }

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    simpleSwitch.setText("Add Year");
                    tableName.setText("");
                    textView2.setText("");
                    if(tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
                        go.setEnabled(true);
                        tableName.requestFocus();
                    }
                    else {
                        go.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    simpleSwitch.setText("Delete Year");
                    tableName.setText("");
                    textView2.setText("");
                    if(tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
                        go.setEnabled(true);
                        tableName.requestFocus();
                    }
                    else {
                        go.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item))
            return true;
        else if(item.getItemId()==R.id.menu_settings) {
            Intent intent=new Intent(YearActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newm, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        String strTN = tableName.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(strTN)) {
            alertOneButton();
            //tableName.setError("Roll can't be empty");
        }
        else {
            if (simpleSwitch.isChecked()){
                new AlertDialog.Builder(YearActivity.this)
                        .setTitle("Add Year")
                        .setMessage("Do you want to add the year?")
                        .setIcon(R.drawable.ic_question)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    //@TargetApi(11)
                                    public void onClick(DialogInterface dialog, int id) {
                                        String strTN = tableName.getText().toString().toLowerCase();
                                        int year = Calendar.getInstance().get(Calendar.YEAR);
                                        tName = strTN + "_" + String.valueOf(year);
                                        textView2.setText(tName);
                                        progressBar.setVisibility(View.VISIBLE);

                                        RequestQueue requestQueue= Volley.newRequestQueue(YearActivity.this);
                                        JSONObject jsonObject=new JSONObject();
                                        String url ="https://attendancestcet.000webhostapp.com/add_year.php";
                                        try {
                                            jsonObject.put("tab", tName);
                                            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.i("Response", "onResponse: "+response.toString());
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Log.i("Error", "onErrorResponse: "+error.toString());
                                                }
                                            });

                                            requestQueue.add(jsonObjectRequest);
                                        }
                                        catch (Exception e) { }

                                        //result.setText(tName);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), tName+" Added!", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            //@TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
            }
            else{
                new AlertDialog.Builder(YearActivity.this)
                        .setTitle("Delete Year")
                        .setMessage("Do you want to delete the year?")
                        .setIcon(R.drawable.ic_question)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    //@TargetApi(11)
                                    public void onClick(DialogInterface dialog, int id) {
                                        String strTN = tableName.getText().toString().toLowerCase();
                                        int year = Calendar.getInstance().get(Calendar.YEAR);
                                        tName = strTN + "_" + String.valueOf(year);
                                        textView2.setText(tName);

                                        progressBar.setVisibility(View.VISIBLE);
                                        RequestQueue requestQueue= Volley.newRequestQueue(YearActivity.this);
                                        JSONObject jsonObject=new JSONObject();
                                        String url ="https://attendancestcet.000webhostapp.com/delete_year.php";
                                        try {
                                            jsonObject.put("tab", tName);
                                            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.i("Response", "onResponse: "+response.toString());
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Log.i("Error", "onErrorResponse: "+error.toString());
                                                }
                                            });

                                            requestQueue.add(jsonObjectRequest);
                                        }
                                        catch (Exception e) { }

                                        //result.setText(tName);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), tName+" Deleted!", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            //@TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
            }
        }
    }

    public void alertOneButton() {
        new AlertDialog.Builder(YearActivity.this)
                .setTitle("Error")
                .setMessage("Give proper input to proceed")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
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
