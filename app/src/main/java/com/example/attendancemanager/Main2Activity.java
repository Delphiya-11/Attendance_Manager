package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et3;
    private TextView tv;
    private Button btn;
    private String date;
    private RequestQueue requestQueue;
    public String subid;
    public String prev;

    DatabaseHelper dbHelp;
    ArrayList arrayList;

    JSONArray jsonArray;
    JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Subject, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        et3 = findViewById(R.id.editText3);
        tv = findViewById(R.id.textView);
        btn = findViewById(R.id.button2);
        Calendar calendar = Calendar.getInstance();
        date = DateFormat.getDateInstance().format(calendar.getTime());
        prev="Jan012020";




        //Changing date format

        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
        Date sourceDate = null;
        try {
            sourceDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat target = new SimpleDateFormat("MMMddyyyy");
        date = target.format(sourceDate);

        tv.setText(date);
        et3.setText(getIntent().getStringExtra("Data"));


        //database helper
        dbHelp = new DatabaseHelper(Main2Activity.this);
        arrayList = dbHelp.getAllText();

        requestQueue = Volley.newRequestQueue(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting previous date from curr column of subjects table
                RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);
                String url1 ="https://attendancestcet.000webhostapp.com/get_prev.php?sid="+subid;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.

                                try {
                                    jsonArray = new JSONArray(response);
                                    jsonObject=jsonArray.getJSONObject(0);
                                    prev=jsonObject.getString("curr");

                                    JSONObject jsonObject = new JSONObject();
                                    String url = "https://attendancestcet.000webhostapp.com/att_date_entry.php";
                                    try {
                                        jsonObject.put("date", date);
                                        jsonObject.put("subid", subid);
                                        jsonObject.put("prev", prev);

                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.i("Response", "onResponse: " + response.toString());

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.i("Error", "onErrorResponse: " + error.toString());
                                                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                    }

                                    String mergedata = ( subid+"_" + prev + "_" + (date));
                                    Toast.makeText(getApplicationContext(),prev, Toast.LENGTH_SHORT).show();
                                    Intent intent2 = new Intent(Main2Activity.this, Main3Activity.class);
                                    intent2.putExtra("Data", mergedata);
                                    startActivity(intent2);
                                }
                                catch (Exception e) { }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Cannot fetch data from curr column", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);

                //Merging the data and sending it to next activity

                /*String mergedata = (subid + "_" + (tv.getText().toString()));
                Intent intent2 = new Intent(Main2Activity.this, Main3Activity.class);
                intent2.putExtra("Data", mergedata);
                startActivity(intent2);*/

                // Inserting SQLite code on click of Generate button
                String text = tv.getText().toString();
                if (!text.isEmpty()) {
                    dbHelp.addText(text);
                    Toast.makeText(getApplicationContext(), "Date inserted", Toast.LENGTH_SHORT).show();
                    arrayList.clear();
                    arrayList.addAll(dbHelp.getAllText());
                }

            }


            //Spinner item select function







/*
    public void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
                int day = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int year = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        datePickerDialog.show();
        String date =  day + "/" + month + "/" + year;
        tv.setText(date);
    }
*/


        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subid = parent.getItemAtPosition(position).toString();
    }

    //not used yet

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
