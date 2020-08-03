package com.example.attendancemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerifyActivity extends AppCompatActivity implements SubjectAdapter.OnNoteListener, AdapterView.OnItemSelectedListener {

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    TextView textView, textView2, textView3;
    Spinner spinner;
    RadioGroup radioGroup;
    ProgressBar progressBar;

    List<String> semester;
    ArrayAdapter<String> dataAdapter;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Subject> subjectList;
    private RecyclerView.Adapter adapter;

    Subject subject;
    Context mContext;

    JSONArray jsonArray;
    JSONObject jsonObject;

    String[] subid;
    String[] subname;

    public String item, dept, sid, sname, tid, tname, tpwd, prev, curr;
    public int sem, r, pop, did, day_count, deptid;
    int noOfAttendance;
    int[] previous;
    public List<String> categories = new ArrayList<String>();
    public long reference;

    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

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

        //progressBar=findViewById(R.id.progressBar);

        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.nav_header_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
        TextView nameView = (TextView) hView.findViewById(R.id.nav_header_textView);
        nameView.setText(tname);

        spinner=findViewById(R.id.spinner);

        semester=new ArrayList<>();
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semester);

        semester.add("Select..");
        semester.add("Semester 1");
        semester.add("Semester 2");
        semester.add("Semester 3");
        semester.add("Semester 4");
        semester.add("Semester 5");
        semester.add("Semester 6");
        semester.add("Semester 7");
        semester.add("Semester 8");

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(this);

        mList = findViewById(R.id.main_list);

        subjectList = new ArrayList<>();
        adapter = new SubjectAdapter(getApplicationContext(), subjectList, this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        dl = findViewById(R.id.drawerLayout);
        nv = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, dl, R.string.open_menu, R.string.close_menu);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
        getSupportActionBar().setTitle("Attendance Verification");

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.check_give:
                        Intent intent=new Intent(VerifyActivity.this, ActionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("DeptId", deptid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.year:
                        Intent intent1=new Intent(VerifyActivity.this, YearActivity.class);
                        Bundle bundle1=new Bundle();
                        bundle1.putInt("DeptId", deptid);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case R.id.subject:
                        Intent intent2=new Intent(VerifyActivity.this, SubjectActivity.class);
                        Bundle bundle2=new Bundle();
                        bundle2.putInt("DeptId", deptid);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case R.id.verify:
                        Intent intent3=new Intent(VerifyActivity.this, VerifyActivity.class);
                        Bundle bundle3=new Bundle();
                        bundle3.putInt("DeptId", deptid);
                        intent3.putExtras(bundle3);
                        startActivity(intent3);
                        break;
                    case R.id.percentage:
                        Intent intent4=new Intent(VerifyActivity.this, PercentageActivity.class);
                        Bundle bundle4=new Bundle();
                        bundle4.putInt("DeptId", deptid);
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        break;
                    case R.id.department:
                        Intent intent5=new Intent(VerifyActivity.this, DepartmentActivity.class);
                        startActivity(intent5);
                        break;
                    default: return(true);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item))
            return true;
        else if(item.getItemId()==R.id.menu_settings) {
            Intent intent=new Intent(VerifyActivity.this,SettingsActivity.class);
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

    public void alertOneButton() {
        new AlertDialog.Builder(VerifyActivity.this)
                .setTitle("Error")
                .setMessage("Give proper input to proceed")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void getAll(){
        subjectList.clear();
        final List<Subject> list=new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (deptid==11){
            String url;
            if(tid.equals("mdu") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_subject.php?dept=11";
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_subject.php?dept=11&tid="+tid;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==21){
            String url;
            if(tid.equals("aks") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_subject.php?dept=21";
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_subject.php?dept=21&tid="+tid;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==31){
            String url;
            if(tid.equals("pc") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_subject.php?dept=31";
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_subject.php?dept=31&tid="+tid;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==121){
            String url;
            if(tid.equals("prp") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_subject.php?dept=121";
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_subject.php?dept=121&tid="+tid;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }
                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }

    private void getData(int sem) {
        subjectList.clear();
        final List<Subject> list=new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (deptid==11){
            String url;
            if(tid.equals("mdu") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_sem_subject.php?dept=11&sem="+sem;
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_sem_subject.php?dept=11&tid="+tid+"&sem="+sem;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==21){
            String url;
            if(tid.equals("aks") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_sem_subject.php?dept=21&sem="+sem;
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_sem_subject.php?dept=21&tid="+tid+"&sem="+sem;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==31){
            String url;
            if(tid.equals("pc") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_sem_subject.php?dept=31&sem="+sem;
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_sem_subject.php?dept=31&tid="+tid+"&sem="+sem;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else if (deptid==121){
            String url;
            if(tid.equals("prp") || tid.equals("sg")){
                url ="https://attendancestcet.000webhostapp.com/hod_sem_subject.php?dept=121&sem="+sem;
            }
            else {
                url ="https://attendancestcet.000webhostapp.com/show_sem_subject.php?dept=121&tid="+tid+"&sem="+sem;
            }
            RequestQueue queue = Volley.newRequestQueue(VerifyActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                jsonArray = new JSONArray(response);

                                for (int i=0;i<jsonArray.length();i++){
                                    jsonObject = jsonArray.getJSONObject(i);

                                    subject = new Subject();
                                    subject.setSid(jsonObject.getString("sid"));
                                    subject.setSname(jsonObject.getString("sname"));

                                    list.add(subject);
                                }

                                subjectList.addAll(list);
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
                    Toast.makeText(getApplicationContext(),"Cannot find any subjects", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }

    @Override
    public void onNoteClick(int position) {
        sid=subjectList.get(position).getSid();
        sname=subjectList.get(position).getSname();

        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyActivity.this);
        builder.setTitle("Enter Number of Students");

        LinearLayout layout = new LinearLayout(VerifyActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(400,100);

        final EditText number = new EditText(VerifyActivity.this);
        number.setHint("No. of Students");
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        //number.setLayoutParams(lparams);
        layout.addView(number);

        // pass null as the parent view because its going in the dialog layout
        builder.setView(layout)

                // action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // your sign in code here
                        //progressBar.setVisibility(View.VISIBLE);
                        String strNum=number.getText().toString();
                        noOfAttendance=Integer.parseInt(strNum);

                        RequestQueue queue1 = Volley.newRequestQueue(VerifyActivity.this);
                        String url1 ="https://attendancestcet.000webhostapp.com/get_dates.php?sid="+sid;
                        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.

                                        try {
                                            jsonArray = new JSONArray(response);
                                            jsonObject = jsonArray.getJSONObject(0);
                                            prev=jsonObject.getString("prev");
                                            curr=jsonObject.getString("curr");

                                            RequestQueue queue2 = Volley.newRequestQueue(VerifyActivity.this);
                                            String url2 = "https://attendancestcet.000webhostapp.com/pop_daycount.php?sid="+sid;
                                            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            // Display the first 500 characters of the response string.

                                                            try {
                                                                jsonArray = new JSONArray(response);
                                                                jsonObject=jsonArray.getJSONObject(0);
                                                                pop=jsonObject.getInt("pop");
                                                                day_count=jsonObject.getInt("day_count");

                                                                if (noOfAttendance!=pop){
                                                                    RequestQueue requestQueue= Volley.newRequestQueue(VerifyActivity.this);
                                                                    JSONObject jsonObject=new JSONObject();
                                                                    String url3 ="https://attendancestcet.000webhostapp.com/reduce.php";
                                                                    try {
                                                                        jsonObject.put("sid", sid);
                                                                        jsonObject.put("prev", prev);
                                                                        jsonObject.put("curr", curr);
                                                                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                Log.i("Response", "onResponse: "+response.toString());

                                                                            }
                                                                        }, new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                //progressBar.setVisibility(View.INVISIBLE);
                                                                                Log.i("Error", "onErrorResponse: "+error.toString());
                                                                                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                                        requestQueue.add(jsonObjectRequest);
                                                                        //progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(getApplicationContext(),"Attendance of all students has been cancelled!!!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    catch (Exception e) {
                                                                        //progressBar.setVisibility(View.INVISIBLE);
                                                                        //Toast.makeText(getApplicationContext(),"Entering catch", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                else {
                                                                    //progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(getApplicationContext(),"Attendance is perfect!!!",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            catch (Exception e) { }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getApplicationContext(),"Try again properly!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            queue2.add(stringRequest2);
                                        }
                                        catch (Exception e) { }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),"Error in fetching data!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue1.add(stringRequest1);

                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove the dialog from the screen
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();

        if (item.equals("Select..")){
            textView3.setText("All");
        }
        else{
            textView3.setText(item);
        }

        if (item.equals("Select..")){
            getAll();
        }
        else if (item.equals("Semester 1")){
            getData(1);
        }
        else if (item.equals("Semester 2")){
            getData(2);
        }
        else if (item.equals("Semester 3")){
            getData(3);
        }
        else if (item.equals("Semester 4")){
            getData(4);
        }
        else if(item.equals("Semester 5")){
            getData(5);
        }
        else if(item.equals("Semester 6")){
            getData(6);
        }
        else if (item.equals("Semester 7")){
            getData(7);
        }
        else if (item.equals("Semester 8")){
            getData(8);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
