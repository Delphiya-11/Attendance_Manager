package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class ScanActivity extends AppCompatActivity implements LocationListener {

    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;
    LocationManager locationManager;
    public static String lat;
    public static String lon;
    public static int hr, mn, dy;
    SharedPreferences sf;

    public static TextView tvresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvresult = findViewById(R.id.tvresult);
        sf=this.getSharedPreferences(StudentRegisterActivity.REGEX_STORE,Context.MODE_PRIVATE);

        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hr= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                mn= Calendar.getInstance().get(Calendar.MINUTE);
                dy= Calendar.getInstance().get(Calendar.DATE);
                if(dy==sf.getInt("day",0)) {
                    if((hr==sf.getInt("hr",0)+1 && mn>=(sf.getInt("min",0)+60)%60+1) || hr>=sf.getInt("hr",0)+2) {
                        Intent intent = new Intent(ScanActivity.this, ScancamActivity.class);
                        tvresult.setText("");
                        startActivity(intent);
                    }
                    else {
                        tvresult.setText("Come back after 1 hour");
                    }
                }
                else {
                    Intent intent = new Intent(ScanActivity.this, ScancamActivity.class);
                    tvresult.setText("");
                    startActivity(intent);
                }

            }
        });


        dl = findViewById(R.id.drawerLayout);
        nv = findViewById(R.id.navigationView);

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
                        startActivity(new Intent(ScanActivity.this, StudentOptionActivity.class));
                        break;
                    case R.id.scan:
                        startActivity(new Intent(ScanActivity.this, ScanActivity.class));
                        break;
                    case R.id.view:
                        startActivity(new Intent(ScanActivity.this, ViewActivity.class));
                        break;
                    default: return(true);
                }
                return true;
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GPS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.exit) {
            finishAffinity();
            System.exit(0);
        }
        else if(item.getItemId()==R.id.logout) {
            Intent intent = new Intent(ScanActivity.this, LogoutActivity.class);
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

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat=String.valueOf(location.getLatitude());
        lon=String.valueOf(location.getLongitude());

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}
