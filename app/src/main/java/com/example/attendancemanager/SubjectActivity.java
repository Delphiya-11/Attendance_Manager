package com.example.attendancemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.navigation.NavigationView;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity implements View.OnClickListener, SubjectAdapter.OnNoteListener, AdapterView.OnItemSelectedListener {

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    TextView textView, textView2, textView3;
    Spinner spinner;;
    ProgressBar progressBar;
    JSONArray jsonArray;
    JSONObject jsonObject;
    Toolbar toolbar;
    FloatingActionMenu fabMenu;
    com.github.clans.fab.FloatingActionButton add, upload;

    List<String> semester;
    ArrayAdapter<String> dataAdapter;

    String[] subid;
    String[] subname;

    Intent myFileIntent;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Subject> subjectList;
    private RecyclerView.Adapter adapter;

    Subject subject;
    Context mContext;

    public String item, dept, tid, tname, tpwd, url, path, file, sid, sname;
    public int sem, did, len, deptid;
    public List<String> categories = new ArrayList<String>();

    private static final String TAG="SubjectActivity";
    private static final int REQUEST_CODE=43;

    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        spref=getSharedPreferences("Login", MODE_PRIVATE);
        tid=spref.getString("Username","");
        tname=spref.getString("Name","");
        tpwd=spref.getString("Password","");
        did=spref.getInt("DeptId",0);

        Bundle b = getIntent().getExtras();
        deptid=b.getInt("DeptId");

        textView = findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.nav_header_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubjectActivity.this,SettingsActivity.class);
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

        add=findViewById(R.id.addButton);
        add.setOnClickListener(this);
        upload=findViewById(R.id.uploadButton);
        upload.setOnClickListener(this);

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
        getSupportActionBar().setTitle("Subject");

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.check_give:
                        Intent intent=new Intent(SubjectActivity.this, ActionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("DeptId", deptid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.year:
                        Intent intent1=new Intent(SubjectActivity.this, YearActivity.class);
                        Bundle bundle1=new Bundle();
                        bundle1.putInt("DeptId", deptid);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case R.id.subject:
                        Intent intent2=new Intent(SubjectActivity.this, SubjectActivity.class);
                        Bundle bundle2=new Bundle();
                        bundle2.putInt("DeptId", deptid);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case R.id.verify:
                        Intent intent3=new Intent(SubjectActivity.this, VerifyActivity.class);
                        Bundle bundle3=new Bundle();
                        bundle3.putInt("DeptId", deptid);
                        intent3.putExtras(bundle3);
                        startActivity(intent3);
                        break;
                    case R.id.percentage:
                        Intent intent4=new Intent(SubjectActivity.this, PercentageActivity.class);
                        Bundle bundle4=new Bundle();
                        bundle4.putInt("DeptId", deptid);
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        break;
                    case R.id.department:
                        Intent intent5=new Intent(SubjectActivity.this, DepartmentActivity.class);
                        startActivity(intent5);
                        break;
                    default: return(true);
                }
                return true;
            }
        });
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
            RequestQueue queue = Volley.newRequestQueue(SubjectActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item))
            return true;
        else if(item.getItemId()==R.id.menu_settings) {
            Intent intent=new Intent(SubjectActivity.this,SettingsActivity.class);
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

    private void startSearch(){
        //progressBar.setVisibility(View.VISIBLE);
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if(data!=null){
                Uri uri=data.getData();
                path=uri.getPath();

                String s1[]=path.split("/");
                len=s1.length;
                file=s1[len-1].trim();

                String s2[]=file.split("\\.");
                String sid=s2[0].trim().toUpperCase();

                try {
                    CSVReader reader = new CSVReader(new FileReader(file));
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                       Toast.makeText(getApplicationContext(),"Data: "+nextLine[0]+" "+nextLine[1],Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("Error: ","onFileError: "+e.toString());
                    Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
                }

                //progressBar.setVisibility(View.VISIBLE);
//                RequestQueue requestQueue= Volley.newRequestQueue(this);
//                JSONObject jsonObject=new JSONObject();
//                String url ="https://attendancestcet.000webhostapp.com/upload_student.php";
//                try {
//                    jsonObject.put("csvData", path);
//                    jsonObject.put("sid", sid);
//                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.i("Response", "onResponse: "+response.toString());
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.i("Error", "onErrorResponse: "+error.toString());
//                            //progressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    requestQueue.add(jsonObjectRequest);
//                    //progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(getApplicationContext(),"Exiting try",Toast.LENGTH_SHORT).show();
//                }
//                catch (Exception e) {
//                    //progressBar.setVisibility(View.INVISIBLE);
//                    Toast.makeText(getApplicationContext(),"Entered catch",Toast.LENGTH_SHORT).show();
//                }
            }
        }

        finish();
        startActivity(getIntent());
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1001:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Permission granted!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Permission not granted!!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }*/

    public void alertOneButton() {
        new AlertDialog.Builder(SubjectActivity.this)
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
    public void onNoteClick(int position) {
        sid=subjectList.get(position).getSid();
        sname=subjectList.get(position).getSname();

        new AlertDialog.Builder(SubjectActivity.this)
                .setTitle("Delete Subject")
                .setMessage("Do you want to delete the subject?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            //@TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                if (tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
                                    //progressBar.setVisibility(View.VISIBLE);
                                    RequestQueue requestQueue= Volley.newRequestQueue(SubjectActivity.this);
                                    JSONObject jsonObject=new JSONObject();
                                    String url ="https://attendancestcet.000webhostapp.com/delete_subject.php";
                                    try {
                                        jsonObject.put("sid", sid);
                                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
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
                                    }
                                    catch (Exception e) { }

                                    //progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),sid+" Deleted!", Toast.LENGTH_SHORT).show();

                                    finish();
                                    startActivity(getIntent());
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                                }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addButton:
                if (tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
                    new AlertDialog.Builder(SubjectActivity.this)
                            .setTitle("Add Subject")
                            .setIcon(R.drawable.ic_question)
                            .setMessage("Do you want to add a subject?")
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SubjectActivity.this);
                                            builder.setTitle("Enter Subject Details");

                                            LinearLayout layout = new LinearLayout(SubjectActivity.this);
                                            layout.setOrientation(LinearLayout.VERTICAL);

                                            final EditText teacherId = new EditText(SubjectActivity.this);
                                            teacherId.setHint("Teacher ID");
                                            teacherId.setInputType(InputType.TYPE_CLASS_TEXT);
                                            layout.addView(teacherId);

                                            final EditText semester = new EditText(SubjectActivity.this);
                                            semester.setHint("Semester");
                                            semester.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            layout.addView(semester);

                                            final EditText subjectId = new EditText(SubjectActivity.this);
                                            subjectId.setHint("Subject ID");
                                            subjectId.setInputType(InputType.TYPE_CLASS_TEXT);
                                            layout.addView(subjectId);

                                            final EditText subjectName = new EditText(SubjectActivity.this);
                                            subjectName.setHint("Subject Name");
                                            subjectName.setInputType(InputType.TYPE_CLASS_TEXT);
                                            layout.addView(subjectName);

                                            builder.setView(layout)
                                                    .setPositiveButton("ADD",
                                                            new DialogInterface.OnClickListener() {
                                                                //@TargetApi(11)
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    String strTI=teacherId.getText().toString().toLowerCase();
                                                                    int s=Integer.parseInt(semester.getText().toString());
                                                                    String strSI=subjectId.getText().toString().toUpperCase();
                                                                    String strSN=subjectName.getText().toString();

                                                                    RequestQueue requestQueue= Volley.newRequestQueue(SubjectActivity.this);
                                                                    JSONObject jsonObject=new JSONObject();
                                                                    String url ="https://attendancestcet.000webhostapp.com/add_subject.php";
                                                                    try {
                                                                        jsonObject.put("tab", "subjects");
                                                                        jsonObject.put("sid", strSI);
                                                                        jsonObject.put("sname", strSN);
                                                                        jsonObject.put("tid",strTI);
                                                                        jsonObject.put("sem", s);
                                                                        jsonObject.put("dept", did);
                                                                        jsonObject.put("pop",0);
                                                                        jsonObject.put("day_count",0);
                                                                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                Log.i("Response", "onResponse: "+response.toString());

                                                                            }
                                                                        }, new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Log.i("Error", "onErrorResponse: "+error.toString());
                                                                            }
                                                                        });

                                                                        requestQueue.add(jsonObjectRequest);
                                                                    }
                                                                    catch (Exception e) { }

                                                                    Toast.makeText(getApplicationContext(), strSI+" Added!", Toast.LENGTH_SHORT).show();

                                                                    new AlertDialog.Builder(SubjectActivity.this)
                                                                            .setTitle("Upload Student Details")
                                                                            .setMessage("Do you want to upload csv file of the subject added?")
                                                                            .setIcon(R.drawable.ic_question)
                                                                            .setPositiveButton("YES",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            startSearch();
                                                                                        }
                                                                                    })
                                                                            .setNegativeButton("NO",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            finish();
                                                                                            startActivity(getIntent());
                                                                                            dialog.cancel();
                                                                                        }
                                                                                    }).show();

                                                                    dialog.cancel();
                                                                }
                                                            })
                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                        //@TargetApi(11)
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }
                                    })
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.uploadButton:
                if (tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("prp")){
                    new AlertDialog.Builder(SubjectActivity.this)
                            .setTitle("Upload Student Details")
                            .setMessage("Do you want to upload csv file of the subject added?")
                            .setIcon(R.drawable.ic_question)
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startSearch();
                                        }
                                    })
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            startActivity(getIntent());
                                            dialog.cancel();
                                        }
                                    }).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
