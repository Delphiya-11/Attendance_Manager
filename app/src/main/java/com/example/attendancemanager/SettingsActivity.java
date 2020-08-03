package com.example.attendancemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView listView;
    ScrollView scrollView;
    SharedPreferences spref;
    TextView textView, tv;
    CardView cardView;
    ImageView imageView1, imageView2;

    String menuItems[]={"Account Details", "Close", "Logout"};
    int icons[]={R.drawable.ic_details,R.drawable.ic_close,R.drawable.ic_logout};

    public String tid, tname, tpwd;
    public int did;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spref=getSharedPreferences("Login", MODE_PRIVATE);
        tid=spref.getString("Username","");
        tname=spref.getString("Name","");
        tpwd=spref.getString("Password","");
        did=spref.getInt("DeptId",0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.deep_blue)));
        getSupportActionBar().setTitle("Settings");

        imageView1=findViewById(R.id.imageView);
        imageView2=findViewById(R.id.imageView1);

        imageView2.setOnClickListener(this);

        cardView=findViewById(R.id.cardView);
        textView=findViewById(R.id.textView);

        listView=findViewById(R.id.listView1);
        CustomAdapter customAdapter = new CustomAdapter(SettingsActivity.this, menuItems, icons);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(this);

        scrollView=findViewById(R.id.scrollView1);
        tv=findViewById(R.id.textViewWithScroll);

        textView.setText(tname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView1:
                alertWebView();
                break;
            default:
                break;
        }
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        if (position==0){
            alertScrollView();
        }
        else if (position==1){
            alertTwoButtons();
        }
        else if (position==2){
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Logout")
                    .setMessage("Do you want to logout?")
                    .setIcon(R.drawable.ic_logout)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                //@TargetApi(11)
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor sprefEditor=spref.edit();
                                    sprefEditor.clear();
                                    sprefEditor.commit();
                                    Toast.makeText(getApplicationContext(),"Logging Out!!!",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(SettingsActivity.this,TeacherActivity.class);
                                    startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SettingsActivity.this.finish();
        return true;
    }

    public void alertScrollView() {

        /*
         * Inflate the XML view.
         *
         * @activity_main is in res/layout/scroll_text.xml
         */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

        // textViewWithScroll is the name of our TextView on scroll_text.xml
        tv = myScrollView.findViewById(R.id.textViewWithScroll);

        // Initializing a blank textview so that we can just append a text later
        tv.setText("");

        /*
         * Display the text 10 times so that it will exceed the device screen
         * height and be able to scroll
         */
        tv.append("Name: "+tname+"\n\n");
        tv.append("Username: "+tid+"\n\n");
        if (tid.equals("sg")){
            tv.append("Designation: Principal\n\n");
        }
        else if (tid.equals("mdu") || tid.equals("aks") || tid.equals("pc") || tid.equals("pp")){
            tv.append("Designation: HOD\n\n");
        }
        else{
            tv.append("Designation: Asst. Prof.\n\n");
        }
        if (did==11){
            tv.append("Department: CSE\n\n");
        }
        else if (did==121){
            tv.append("Department: IT\n\n");
        }
        else if (did==21){
            tv.append("Department: EE\n\n");
        }
        else if (did==31){
            tv.append("Department: ECE\n\n");
        }

        new AlertDialog.Builder(SettingsActivity.this).setView(myScrollView)
                .setTitle("Account Details")
                .setIcon(R.drawable.ic_person)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    //@TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();

    }

    public void alertTwoButtons() {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Close app")
                .setMessage("Do you want to exit?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            //@TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();
                                System.exit(0);
                                //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
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

    public void alertWebView() {

        // WebView is created programatically here.
        WebView myWebView = new WebView(SettingsActivity.this);
        myWebView.loadUrl("https://github.com/");

        /*
         * This part is needed so it won't ask the user to open another browser.
         */
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        new AlertDialog.Builder(SettingsActivity.this).setView(myWebView)
                .setTitle("Go to GitHub")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    //@TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }
}
