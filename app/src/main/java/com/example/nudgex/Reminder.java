package com.example.nudgex;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nudgex.adapters.CardViewRecyclerViewAdapter;
import com.example.nudgex.adapters.CardViewRecyclerViewLocationAdapter;
import com.example.nudgex.board.viewmodel.BoardsViewModel;
import com.example.nudgex.recievers.AlarmReceiver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wefika.calendar.CollapseCalendarView;
import com.wefika.calendar.manager.CalendarManager;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class Reminder extends Fragment {



    private int idCount = 0 ;
    private double currentLat,currentLong;

    String androidId;
    ImageButton addReminder ;
    RecyclerView listView;
    RecyclerView.LayoutManager layoutManager;
    CollapseCalendarView calendarView;
    Calendar myCalendar = Calendar.getInstance();
    String todayDate = myCalendar.get(Calendar.YEAR)+"-"+(myCalendar.get(Calendar.MONTH)+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH);
    private BoardsViewModel boardModel;

    TextView switchTxt;
    SwitchMaterial taskSwitch;

    LocalDate localDate = LocalDate.now();
    ArrayAdapter<Task> adapter;
    ArrayList<Task> tasks ;

    SharedPreferences pref;

    boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final String TAG = "Reminder";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    //locationBasedView
    LinearLayout locationLinear,todayView ;
    TextView locationTextTitle;
    Button showTodayList,showLocationList;
    RecyclerView taskListForLocation;
    RecyclerView.LayoutManager layoutManagerForLocation;

    FloatingActionButton btnAddReminder;

    public Reminder() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boardModel = ViewModelProviders.of(getActivity()).get(BoardsViewModel.class);

        boardModel.updateActionBarTitle("Reminders");


        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        btnAddReminder = view.findViewById(R.id.addReminder);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocationPermission();
        CalendarManager manager = new CalendarManager(LocalDate.now(), CalendarManager.State.MONTH, LocalDate.now(), LocalDate.now().plusYears(1));

        calendarView = view.findViewById(R.id.calendar);
        calendarView.init(LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1));

      //  addReminder = view.findViewById(R.id.addReminder);
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewReminderActivity.class);
                startActivity(i);
            }
        });



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        androidId = pref.getString("USER_ID",null);


        listView = view.findViewById(R.id.taskList);

        listView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());

        listView.setLayoutManager(layoutManager);

        taskSwitch = view.findViewById(R.id.viewSwitch);

        todayView = view.findViewById(R.id.todayView);
        locationLinear = view.findViewById(R.id.locationView);
        locationTextTitle = view.findViewById(R.id.locationTextTitle);
        taskListForLocation = view.findViewById(R.id.taskListForLocation);
        layoutManagerForLocation = new LinearLayoutManager(getContext());
        taskListForLocation.setLayoutManager(layoutManagerForLocation);

        todayView.setVisibility(View.GONE);
        locationLinear.setVisibility(View.GONE);
        tasks = new ArrayList<>();

        final DatabaseReference myRef = database.getReference("tasks").child(androidId);
        calendarView.setClickable(true);

        calendarView.setListener(new CollapseCalendarView.OnDateSelect() {
            @Override
            public void onDateSelected(LocalDate localDate) {
                System.out.println("Date:"+localDate.toDate().toString());
                todayDate = localDate.get(DateTimeFieldType.year())+"-"+(localDate.toDate().getMonth()+1)+"-"+localDate.toDate().getDate();




                retrieveDataForEachDate(myRef,todayDate);

            }
        });



        taskSwitch.setChecked(true);
        taskSwitch.setText("DATE BASED TASKS    ");
        todayView.setVisibility(View.VISIBLE);
        locationLinear.setVisibility(View.GONE);
        retrieveDataForEachDate(myRef,todayDate);

        taskSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    todayView.setVisibility(View.VISIBLE);
                    taskSwitch.setText("DATE BASED TASKS    ");

                    locationLinear.setVisibility(View.GONE);
                    retrieveDataForEachDate(myRef,todayDate);
                }else{
                    todayView.setVisibility(View.GONE);
                    taskSwitch.setText("LOCATION BASED TASKS ");

                    locationLinear.setVisibility(View.VISIBLE);
                    retrieveData(myRef,todayDate);
                }
            }
        });
    }

    private void  retrieveDataForEachDate(final DatabaseReference myRef  ,final String today){
        final ArrayList<Object> arrayList = new ArrayList<>();


        Calendar c1 = Calendar.getInstance();
        String currDAte = c1.get(Calendar.YEAR)+"-"+(c1.get(Calendar.MONTH)+1)+"-"+c1.get(Calendar.DAY_OF_MONTH);
        final int currentHour ;
        if(currDAte.equals(today)) {
            currentHour = c1.get(Calendar.HOUR_OF_DAY);
        }else{
            currentHour = 0;

        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){


                    CardViewRecyclerViewAdapter arrayAdapter = new CardViewRecyclerViewAdapter(getContext(), arrayList);
                    arrayAdapter.clear();

                    for (DataSnapshot childSnap : dataSnapshot.getChildren()) {


                       if( childSnap.child("taskDate").getValue().toString().equals(today)){

                            String[] check = childSnap.child("taskTime").getValue().toString().split(":");
                            String[] checkAmPm = check[1].split(" ");
                            int timeCheck = Integer.parseInt(check[0]);

                            if( checkAmPm[1].equals("PM")){
                                timeCheck = timeCheck + 12;
                            }
                            if (currentHour<=timeCheck) {

                                String[] timeToSetReminder = childSnap.child("taskTime").getValue().toString().split(":");
                                String[] splitAmPm = timeToSetReminder[1].split(" ");
                                int hourToSetAlarm = Integer.parseInt(timeToSetReminder[0]);
                                int minute = Integer.parseInt(splitAmPm[0]);
                                Task task = new Task(checkIfStringIsNull(childSnap.child("taskId").getValue()), checkIfStringIsNull(childSnap.child("newTask").getValue()),
                                        checkIfStringIsNull(childSnap.child("taskDate").getValue()), hourToSetAlarm+":"+minute+" "+splitAmPm[1],
                                        checkIfStringIsNull(childSnap.child("taskDescription").getValue()),
                                        checkIfDoubleIsNull(childSnap.child("latitude").getValue()),
                                        checkIfDoubleIsNull(childSnap.child("longitude").getValue()),
                                        checkIfStringIsNull(childSnap.child("locationName").getValue()));
                                arrayList.add(task);



                            }

                        }
                        Collections.sort(arrayList, new CustomComparator());
                        arrayAdapter = new CardViewRecyclerViewAdapter(getContext(), arrayList);

                        listView.setAdapter(arrayAdapter);
                        listView.smoothScrollToPosition(arrayList.size());
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity().getApplicationContext(), "Failed to read value.",Toast.LENGTH_LONG).show();
            }
        });




    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                com.google.android.gms.tasks.Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation!=null){
                                currentLat = mLastKnownLocation.getLatitude();
                                currentLong = mLastKnownLocation.getLongitude();
                                Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                                Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                                pref = getContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("LAT", String.valueOf(currentLat));
                                editor.putString("LONG", String.valueOf(currentLong));

                                editor.commit();


                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }

                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    private void retrieveData(final DatabaseReference myRef  ,final String today){
        final ArrayList<Object> arrayList = new ArrayList<>();

        getDeviceLocation();

        Calendar c1 = Calendar.getInstance();
        String currDAte = c1.get(Calendar.YEAR)+"-"+(c1.get(Calendar.MONTH)+1)+"-"+c1.get(Calendar.DAY_OF_MONTH);
        final int currentHour ;
        if(currDAte.equals(today)) {
            currentHour = c1.get(Calendar.HOUR_OF_DAY);

        }else{
            currentHour = 0;
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Location current = new Location("");

                    current.setLatitude(currentLat);
                    current.setLongitude(currentLong);

                    CardViewRecyclerViewLocationAdapter arrayForLocationAdapter = new CardViewRecyclerViewLocationAdapter(getContext(), arrayList);
                    arrayForLocationAdapter.clear();

                    for (DataSnapshot childSnap : dataSnapshot.getChildren()) {


                        Location chilSnaplocation = new Location("");
                        chilSnaplocation.setLatitude(checkIfDoubleIsNull(childSnap.child("latitude").getValue()));
                        chilSnaplocation.setLongitude(checkIfDoubleIsNull(childSnap.child("longitude").getValue()));
                        float distanceInMeters = current.distanceTo(chilSnaplocation);

                        if (distanceInMeters < 500 ) {

                            String[] timeToSetReminder = childSnap.child("taskTime").getValue().toString().split(":");
                            String[] splitAmPm = timeToSetReminder[1].split(" ");
                            int hourToSetAlarm = Integer.parseInt(timeToSetReminder[0]);
                            int minute = Integer.parseInt(splitAmPm[0]);

                            locationTextTitle.setText("Hey. Here are the things you need to do in " + checkIfStringIsNull(childSnap.child("locationName").getValue()));
                            Task task = new Task(checkIfStringIsNull(childSnap.child("taskId").getValue()), checkIfStringIsNull(childSnap.child("newTask").getValue()),
                                    checkIfStringIsNull(childSnap.child("taskDate").getValue()), hourToSetAlarm + ":" + minute + " " + splitAmPm[1],
                                    checkIfStringIsNull(childSnap.child("taskDescription").getValue()),
                                    checkIfDoubleIsNull(childSnap.child("latitude").getValue()),
                                    checkIfDoubleIsNull(childSnap.child("longitude").getValue()),
                                    checkIfStringIsNull(childSnap.child("locationName").getValue()));
                            arrayList.add(task);

                        }

                        Collections.sort(arrayList, new CustomComparator());
                        arrayForLocationAdapter = new CardViewRecyclerViewLocationAdapter(getContext(), arrayList);

                        taskListForLocation.setAdapter(arrayForLocationAdapter);
                        taskListForLocation.smoothScrollToPosition(arrayList.size());

                    }
                    if(arrayList.size()==0){
                        retrieveDataForEachDate(myRef,today);
                    }else{

                        todayView.setVisibility(View.GONE);
                        locationLinear.setVisibility(View.VISIBLE);
                    }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity().getApplicationContext(), "Failed to read value.",Toast.LENGTH_LONG).show();
            }
        });




    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }
    }


    private String checkIfStringIsNull(Object str){
        return  str!=null ? str.toString() :"";
    }

    private  Double checkIfDoubleIsNull(Object str){
        return  str!=null?  Double.parseDouble(str.toString()): 0.0;
    }

}
