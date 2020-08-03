package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import io.realm.Realm;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScancamActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    SharedPreferences sf;
    SharedPreferences.Editor sp;
    int k;
    RequestQueue queue;
    String url;
    StringRequest stringRequest;
    CustomJsonObjectRequest jsonObjectRequest;

    //camera permission is needed.

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        queue = Volley.newRequestQueue(this);
        sf=this.getSharedPreferences(StudentRegisterActivity.REGEX_STORE,Context.MODE_PRIVATE);
        sp=sf.edit();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public void handleResult(final me.dm7.barcodescanner.zbar.Result result) {
        // Do something with the result here
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        try {
            final String t = result.getContents().split("_")[0];

            String a = ScanActivity.lat.substring(0, 5), b = ScanActivity.lon.substring(0, 5);

            //if (a.equals("22.53") && b.equals("88.32")) {
            if (a.equals("22.50") && b.equals("88.34")) {

                url = "https://attendancestcet.000webhostapp.com/qrcheck.php?sub="+t;
                try {
                    StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    try {
                                        k = Integer.parseInt(response);
                                        if (Integer.parseInt(result.getContents().split("_")[3]) > k) {

                                            url = "https://attendancestcet.000webhostapp.com/qrstore.php?k=" + (k + 1) + "&sub=" + t;
                                            try {
                                                stringRequest = new StringRequest(Request.Method.GET, url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                Log.i("hey", "yo");
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                    }
                                                });
                                                queue.add(stringRequest);
                                            } catch (Exception e) {
                                            }

                                            Log.v("kkkk", result.getContents()); // Prints scan results
                                            Log.v("uuuu", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
                                            String prev = result.getContents().split("_")[1], cur = result.getContents().split("_")[2];
                                            int r = sf.getInt("roll", 0);
                                            JSONObject jsonObject = new JSONObject();
                                            String url = "https://attendancestcet.000webhostapp.com/qrattendance.php";
                                            try {
                                                jsonObject.put("tab", t);
                                                jsonObject.put("roll", r);
                                                jsonObject.put("prev", prev);
                                                jsonObject.put("cur", cur);
                                                jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        sp.putInt("hr", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                                                        sp.putInt("min", Calendar.getInstance().get(Calendar.MINUTE));
                                                        sp.putInt("day", Calendar.getInstance().get(Calendar.DATE));
                                                        sp.commit();
                                                        progressDialog.dismiss();
                                                        ScanActivity.tvresult.setText("Attendance given");

                                                        Log.i("hello", "yo");
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                        progressDialog.dismiss();
                                                        ScanActivity.tvresult.setText("Error");

                                                    }
                                                });
                                                queue.add(jsonObjectRequest);


                                            } catch (Exception e) {

                                            }

                                            //ScanActivity.tvresult.setText("Error");
                                            onBackPressed();

                                            // If you would like to resume scanning, call this method below:
                                            //mScannerView.resumeCameraPreview(this);
                                        } else {
                                            progressDialog.dismiss();
                                            ScanActivity.tvresult.setText("Improper attempt");
                                            finish();
                                        }


                                    }
                                    catch(Exception e) {
                                        progressDialog.dismiss();
                                        ScanActivity.tvresult.setText("Invalid QR Code");
                                        finish();
                                    }
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(strRequest);
                } catch (Exception e) {
                }

            } else {
                progressDialog.dismiss();
                ScanActivity.tvresult.setText("Outside College Boundaries");
                finish();
            }
        }
        catch(Exception e) {
            progressDialog.dismiss();
            ScanActivity.tvresult.setText("Invalid QR Code");
            finish();
        }
    }

}
