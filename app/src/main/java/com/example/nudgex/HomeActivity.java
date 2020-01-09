package com.example.nudgex;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nudgex.board.viewmodel.BoardsViewModel;
import com.example.nudgex.database.AppDatabase;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView ;
    SharedPreferences pref;
    String androidId;
    private ActionBar action_bar;
    private BoardsViewModel boardModel;
    private static final String TAG = "HomeActivity";

    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grantPermissions();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        editor.putString("USER_ID", androidId);
        editor.commit();
        bottomNavigationView = findViewById(R.id.nudge_bottom_nav);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentChange, new Reminder());
        ft.commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {

                    case R.id.action_notes:
                        ft.replace(R.id.fragmentChange,new Notes());
                        ft.commit();
                        break;
                    case R.id.action_reminders:
                        ft.replace(R.id.fragmentChange, new Reminder());
                        ft.commit();
                        break;
                    case R.id.action_boards:
                        ft.replace(R.id.fragmentChange,new Boards());
                        ft.commit();
                        break;
                }
                return true;
            }
        });

        this.action_bar = getSupportActionBar();
        boardModel = ViewModelProviders.of(this).get(BoardsViewModel.class);
        boardModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                getSupportActionBar().setTitle(s);
            }
        });




    }

    public ActionBar getAction_bar() {
        return action_bar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.closeInstance();
    }

    public void grantPermissions(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
}
