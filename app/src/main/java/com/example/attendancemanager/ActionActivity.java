package com.example.attendancemanager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActionActivity extends AppCompatActivity implements SubjectAdapter.OnNoteListener, AdapterView.OnItemSelectedListener {

    ListView listView;
    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    ProgressBar progressBar;
    TextView textView, textView2, textView3;
    Spinner spinner;

    List<String> semester;
    ArrayAdapter<String> dataAdapter;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Subject> subjectList;
    private RecyclerView.Adapter adapter;

    Subject subject;
    Context mContext;

    DownloadManager downloadManager;

    JSONArray jsonArray;
    JSONObject jsonObject;

    String tid, tname, tpwd;
    String[] subid;
    String[] subname;

    public String item, dept, sid, sname, current_date;
    public int sem, r, pop, did, day_count, deptid;
    int noOfAttendance;
    public long reference;

    Cursor cursor;
    String file;

    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        spref=getSharedPreferences("Login", MODE_PRIVATE);
        tid=spref.getString("Username","");
        tname=spref.getString("Name","");
        tpwd=spref.getString("Password","");
        did=spref.getInt("DeptId",0);

        Bundle b = getIntent().getExtras();
        deptid=b.getInt("DeptId");

        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.nav_header_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionActivity.this,SettingsActivity.class);
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

        //progressBar=findViewById(R.id.progressBar);

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        getSupportActionBar().setTitle("Attendance");

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.check_give:
                        Intent intent=new Intent(ActionActivity.this, ActionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("DeptId", deptid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.year:
                        Intent intent1=new Intent(ActionActivity.this, YearActivity.class);
                        Bundle bundle1=new Bundle();
                        bundle1.putInt("DeptId", deptid);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case R.id.subject:
                        Intent intent2=new Intent(ActionActivity.this, SubjectActivity.class);
                        Bundle bundle2=new Bundle();
                        bundle2.putInt("DeptId", deptid);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case R.id.verify:
                        Intent intent3=new Intent(ActionActivity.this, VerifyActivity.class);
                        Bundle bundle3=new Bundle();
                        bundle3.putInt("DeptId", deptid);
                        intent3.putExtras(bundle3);
                        startActivity(intent3);
                        break;
                    case R.id.percentage:
                        Intent intent4=new Intent(ActionActivity.this, PercentageActivity.class);
                        Bundle bundle4=new Bundle();
                        bundle4.putInt("DeptId", deptid);
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        break;
                    case R.id.department:
                        Intent intent5=new Intent(ActionActivity.this, DepartmentActivity.class);
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
            Intent intent=new Intent(ActionActivity.this,SettingsActivity.class);
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
        new AlertDialog.Builder(ActionActivity.this)
                .setTitle("Error")
                .setMessage("Give proper input to proceed")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    private int checkStatus(long downloadReference) {
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(downloadReference);
        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(myDownloadQuery);
        if (cursor.moveToFirst()) {
            //checkStatus(cursor);

            //column for status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            //column for reason code if the download failed or paused
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);
            //get the download filename
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            String filename = cursor.getString(filenameIndex);

            String statusText = "";
            String reasonText = "";

            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    statusText = "STATUS_FAILED";
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                    }
                    break;
                case DownloadManager.STATUS_PAUSED:
                    statusText = "STATUS_PAUSED";
                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonText = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            reasonText = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonText = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonText = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = "STATUS_PENDING";
                    break;
                case DownloadManager.STATUS_RUNNING:
                    statusText = "STATUS_RUNNING";
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = "STATUS_SUCCESSFUL";
                    reasonText = "Filename:\n" + filename;
                    break;
            }


            /*Toast toast = Toast.makeText(ActionActivity.this,
                    statusText + "\n" +
                            reasonText,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();*/
            return status;
        }
        return 0;
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
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

        new AlertDialog.Builder(ActionActivity.this)
                .setTitle("Check or Give Attendance")
                .setMessage("Do you want to check attendance or give attendance?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton("CHECK",
                        new DialogInterface.OnClickListener() {
                            //@TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                //progressBar.setVisibility(View.VISIBLE);
                                RequestQueue queue1 = Volley.newRequestQueue(ActionActivity.this);
                                String url1 = "https://attendancestcet.000webhostapp.com/pop_daycount.php?sid="+sid;
                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Display the first 500 characters of the response string.

                                                try {
                                                    jsonArray = new JSONArray(response);
                                                    jsonObject=jsonArray.getJSONObject(0);
                                                    pop=jsonObject.getInt("pop");
                                                    day_count=jsonObject.getInt("day_count");

                                                    RequestQueue queue = Volley.newRequestQueue(ActionActivity.this);
                                                    String url ="https://attendancestcet.000webhostapp.com/attendance_csv.php?tab="+sid+"&day_count="+day_count;
                                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    // Display the first 500 characters of the response string.
                                                                    try{
                                                                        //jsonArray = new JSONArray(response);

                                                                        File direct = new File(Environment.getExternalStorageDirectory() + "/attendance_files");

                                                                        if (!direct.exists()) {
                                                                            direct.mkdirs();
                                                                        }

                                                                        downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                                                                        Uri uri= Uri.parse("https://attendancestcet.000webhostapp.com/attendance_"+sid+".csv");
                                                                        DownloadManager.Request request=new DownloadManager.Request(uri);
                                                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                                        request.setDestinationInExternalFilesDir(ActionActivity.this,Environment.DIRECTORY_DOWNLOADS,"attendance_"+sid+".csv");
                                                                        request.setVisibleInDownloadsUi(true);
                                                                        reference=downloadManager.enqueue(request);

                                                                        int k=checkStatus(reference);
                                                                        Log.i("Error", "onErrorResponse: "+k);
                                                                        //progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(getApplicationContext(),"File Downloading!!!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    catch (Exception e){
                                                                        //progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(getApplicationContext(),"Cannot download file!!!", Toast.LENGTH_SHORT).show();

                                                                        //Toast.makeText(getApplicationContext(),file, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.i("Error", "onErrorResponse: "+error.toString());
                                                            //progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(),"Cannot download file :(", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    queue.add(stringRequest);
                                                }
                                                catch (Exception e) { }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"Cannot find any subjects :(", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                queue1.add(stringRequest1);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("GIVE", new DialogInterface.OnClickListener() {
                    //@TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActionActivity.this);
                        builder.setTitle("Enter Roll Number");

                        LinearLayout layout = new LinearLayout(ActionActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(10,10);

                        final EditText roll = new EditText(ActionActivity.this);
                        roll.setHint("Roll Number");
                        roll.setInputType(InputType.TYPE_CLASS_NUMBER);
                        layout.addView(roll);

                        // pass null as the parent view because its going in the dialog layout
                        builder.setView(layout)

                                // action buttons
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // your sign in code here
                                        //progressBar.setVisibility(View.VISIBLE);
                                        String strRoll=roll.getText().toString();
                                        r=Integer.parseInt(strRoll);

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMddyyyy");
                                        Calendar cal = Calendar.getInstance();
                                        current_date=dateFormat.format(cal.getTime());

                                        RequestQueue queue1 = Volley.newRequestQueue(ActionActivity.this);
                                        String url1 ="https://attendancestcet.000webhostapp.com/fetch_today.php?tab="+sid+"&roll="+r+"&col="+current_date;
                                        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // Display the first 500 characters of the response string.

                                                        try {
                                                            jsonArray = new JSONArray(response);
                                                            jsonObject = jsonArray.getJSONObject(0);
                                                            noOfAttendance=jsonObject.getInt("col");
                                                            //Toast.makeText(getApplicationContext(),String.valueOf(noOfAttendance), Toast.LENGTH_SHORT).show();

                                                            RequestQueue queue2 = Volley.newRequestQueue(ActionActivity.this);
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

                                                                                RequestQueue requestQueue= Volley.newRequestQueue(ActionActivity.this);
                                                                                JSONObject jsonObject=new JSONObject();
                                                                                String url3 ="https://attendancestcet.000webhostapp.com/give_attendance.php";
                                                                                try {
                                                                                    jsonObject.put("tab", sid);
                                                                                    jsonObject.put("roll", r);
                                                                                    jsonObject.put("col", current_date);
                                                                                    jsonObject.put("pop",pop+1);
                                                                                    jsonObject.put("att", noOfAttendance+1);
                                                                                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
                                                                                        @Override
                                                                                        public void onResponse(JSONObject response) {
                                                                                            Log.i("Response", "onResponse: "+response.toString());
                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            Log.i("Error", "onErrorResponse: "+error.toString());
                                                                                            //progressBar.setVisibility(View.INVISIBLE);
                                                                                        }
                                                                                    });

                                                                                    requestQueue.add(jsonObjectRequest);
                                                                                }
                                                                                catch (Exception e) {
                                                                                    //progressBar.setVisibility(View.INVISIBLE);
                                                                                    Toast.makeText(getApplicationContext(),"Try again properly!!!",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                //progressBar.setVisibility(View.INVISIBLE);
                                                                                Toast.makeText(getApplicationContext()," Attendance Given!!!", Toast.LENGTH_SHORT).show();
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
                                                        catch (Exception e) {
                                                            //progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(),"Try again properly!!!",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                //progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getApplicationContext(),"Try again properly!!!",Toast.LENGTH_SHORT).show();
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
                        dialog.cancel();
                    }
                }).show();
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

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(),"Select an item",Toast.LENGTH_SHORT).show();
    }
}