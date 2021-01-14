package com.htwberlin.geolist.gui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.htwberlin.geolist.R;
import com.htwberlin.geolist.gui.TaskViewAdapter;
import com.htwberlin.geolist.gui.fragments.ListFragment;
import com.htwberlin.geolist.gui.fragments.MapFragment;
import com.htwberlin.geolist.location.services.LocationService;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.p2p.INetwork;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;

    private static final int REQUEST_APP_SETTINGS = 168;

    private static final String[] APP_PERMISSIONS = {
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.hasPermissions(APP_PERMISSIONS)) {
            this.askForPermissions();
        } else {
            startApplication();
        }
    }

    private void startApplication() {
        Context context = this.getApplicationContext();
        GeoListLogic.makeInstance(context);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        INetwork network = GeoListLogic.getNetInterface().getNetwork();
        network.startDiscovery();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setCheckedItem(R.id.listItem);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.listItem) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
                ft.commit();
            } else if (item.getItemId() == R.id.mapItem) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, MapFragment.newInstance());
                ft.commit();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
        ft.commit();

        // Services starten
        Intent notIntent = new Intent("com.htwberlin.geolist.LONGRUNSERVICE_NOTIFY");
        Intent locIntent = new Intent("com.htwberlin.geolist.LONGRUNSERVICE_LOCATION");
        notIntent.setPackage(this.getPackageName());
        locIntent.setPackage(this.getPackageName());
        startService(notIntent);
        startService(locIntent);
    }

    public void stopApplication() {
        INetwork network = GeoListLogic.getNetInterface().getNetwork();
        network.stopDiscovery();
    }

    private void askForPermissions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fehlende Berechtigungen");
        builder.setMessage("GeoList benötigt erweiterte Berechtigungen, die manuell freigegeben werden müssen. Möchten Sie die Berechtigungen jetzt erteilen?");

        builder.setPositiveButton("Erteilen", (dialog, which) -> {
            Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            appSettings.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(appSettings, REQUEST_APP_SETTINGS);
        });
        builder.setNegativeButton("Später", (dialog, which) -> this.finish());

        builder.show();
    }

    @Override
    protected void onDestroy() {
        if (GeoListLogic.hasInstance()) {
            this.stopApplication();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_APP_SETTINGS) {
            if (!this.hasPermissions(APP_PERMISSIONS)) {
                Toast.makeText(this, "Keine Berechtigungen erhalten", Toast.LENGTH_LONG).show();
                this.finish();
            } else {
                recreate();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission)) {
                return false;
            }
        }
        return true;
    }
}

