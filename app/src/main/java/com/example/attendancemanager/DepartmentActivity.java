package com.example.attendancemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DepartmentActivity extends AppCompatActivity implements DepartmentAdapter.OnNoteListener{

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    SharedPreferences spref;
    TextView textView;
    String tid, tname, tpwd, item;
    int did;

    JSONArray jsonArray;
    JSONObject jsonObject;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Department> departmentList;
    private RecyclerView.Adapter adapter;

    Department department;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        spref=getSharedPreferences("Login", MODE_PRIVATE);
        tid=spref.getString("Username","");
        tname=spref.getString("Name","");
        tpwd=spref.getString("Password","");
        did=spref.getInt("DeptId",0);

        textView=findViewById(R.id.textView);

        mList = findViewById(R.id.main_list);

        departmentList = new ArrayList<>();
        adapter = new DepartmentAdapter(getApplicationContext(), departmentList, this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple)));
        getSupportActionBar().setTitle("Departments");

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_settings) {
            Intent intent=new Intent(DepartmentActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        else{
            DepartmentActivity.this.finish();
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
    public void onBackPressed() {
        //super.onBackPressed();
        //Write your code here
        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(DepartmentActivity.this, MainActivity.class);
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

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String url="https://attendancestcet.000webhostapp.com/departments.php";
        RequestQueue queue = Volley.newRequestQueue(DepartmentActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        try {
                            jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++){
                                jsonObject = jsonArray.getJSONObject(i);

                                department = new Department();
                                department.setId(jsonObject.getInt("did"));
                                department.setDept(jsonObject.getString("dname"));
                                department.setHod(jsonObject.getString("hod"));

                                departmentList.add(department);
                            }

                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Cannot find any Departments", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onNoteClick(int position) {
        if(tid.equals("sg")){
            Intent intent=new Intent(DepartmentActivity.this, ActionActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("DeptId", departmentList.get(position).getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if (did==departmentList.get(position).getId()){
            Intent intent=new Intent(DepartmentActivity.this, ActionActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("DeptId", departmentList.get(position).getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"Permission not granted",Toast.LENGTH_SHORT).show();
        }
    }
}
