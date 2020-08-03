package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewActivity extends AppCompatActivity {

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    RecyclerView mRecyclerView;
    SharedPreferences sf;
    int sem,roll,dep=0;
    String dept;
    JSONArray jsonArray;
    JSONObject ids;
    String id,prev; int count;
    Realm realm;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        dl = findViewById(R.id.drawerLayout);
        nv = findViewById(R.id.navigationView);

        sf=this.getSharedPreferences(StudentRegisterActivity.REGEX_STORE,MODE_PRIVATE);

        sem=sf.getInt("sem",0);
        roll=sf.getInt("roll",0);
        dept=sf.getString("dept",null);

        if(dept.equals("cse")) dep=11;
        else if(dept.equals("ece")) dep=31;
        else if(dept.equals("it")) dep=21;
        else if(dept.equals("ee")) dep=121;

        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<pojo> result = realm.where(pojo.class).findAll();
                    result.deleteAllFromRealm();
                }
            });

        }
        catch(Exception e) {}

        mcontext=this;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://attendancestcet.000webhostapp.com/fetch.php?sem="+sem+"&dept="+dep+"&roll="+roll;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){

                                ids = jsonArray.getJSONObject(i);
                                id = ids.getString("sid");
                                prev=ids.getString("prev");
                                try {
                                    count = Integer.parseInt(ids.getString("c"));
                                }
                                catch (Exception e) {
                                    continue;
                                }
                                Realm realm=Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        pojo db = realm.where(pojo.class).equalTo("sid", id).findFirst();
                                        if(db == null) {
                                            db = realm.createObject(pojo.class, id);
                                        }
                                        db.setPrev(prev);
                                        db.setCount(count);
                                    }
                                });
                                realm.close();
                            }

                        }
                        catch (Exception e) {}

                        mRecyclerView=findViewById(R.id.personRecycler);
                        realm = Realm.getDefaultInstance();
                        RealmResults<pojo> res = realm.where(pojo.class).findAll();
                        MyAdapter myAdapter = new MyAdapter(res,mcontext);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
                        mRecyclerView.setAdapter(myAdapter);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("hey","It's an Error!");
                Toast.makeText(ViewActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        queue.add(stringRequest);

        //ActionBarDrawerToggle is initialized to sync drawer open and closed states
        toggle = new ActionBarDrawerToggle(this, dl, R.string.open_menu, R.string.close_menu);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        //The Hamburger icon is applied to the action bar for working with the nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.home:
                        startActivity(new Intent(ViewActivity.this, StudentOptionActivity.class));
                        break;
                    case R.id.scan:
                        startActivity(new Intent(ViewActivity.this, ScanActivity.class));
                        break;
                    case R.id.view:
                        startActivity(new Intent(ViewActivity.this, ViewActivity.class));
                        break;
                    default: return(true);
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.exit) {
            finishAffinity();
            System.exit(0);
        }
        else if(item.getItemId()==R.id.logout) {
            Intent intent = new Intent(ViewActivity.this, LogoutActivity.class);
            startActivity(intent);
            finish();
        }
        else if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu, menu);
        return true;
    }
}
