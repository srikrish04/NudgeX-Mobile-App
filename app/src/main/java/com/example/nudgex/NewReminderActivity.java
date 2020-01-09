package com.example.nudgex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.AsyncTask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.nudgex.recievers.AlarmReceiver;
import com.example.nudgex.recievers.AppNotification;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewReminderActivity extends AppCompatActivity {
    EditText newTask, taskDate, taskTime, taskDescription;
    Button btnCreateTask;
    ImageButton imgTaskDate;

    String taskId;
    String androidId ;
    DatabaseReference databaseTasks;
    private static final String TAG = "NewActivityActivity";
    private PlacesClient mPlacesClient;
    Double mlatitude;
    Double mlongitude;

    String locationName;

    String aTime;
    int date,monthOfDate,yearOfDate, hourOfDate, minuteOfDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setHint("Search Location");
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                System.out.println("Place selected:"+place.getName()+","+place.getLatLng()+","+place.getId());
                mlatitude = place.getLatLng().latitude;
                mlongitude = place.getLatLng().longitude;
                locationName  = place.getName();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        Intent intent = getIntent();
        Bundle extras = intent.getExtras()!=null?intent.getExtras():new Bundle();

        String invokedFrom = extras.getString("INVOKE_FROM")!=null?extras.getString("INVOKE_FROM"):"";
        String taskTitle = extras.getString("TASK_TITLE")!=null?extras.getString("TASK_TITLE"):"";

        String dtTask = extras.getString("TASK_DATE")!=null?extras.getString("TASK_DATE"):"";
        String tmTask = extras.getString("TASK_TIME")!=null?extras.getString("TASK_TIME"):"";
        String descTask = extras.getString("TASK_DESC")!=null?extras.getString("TASK_DESC"):"";
        String locationName = extras.getString("TASK_LOCATION") != null?extras.getString("TASK_LOCATION") : "";
        Double latitude = extras.getDouble("TASK_LATITUDE") ;
        Double longitude = extras.getDouble("TASK_LONGITUDE") ;

        taskId = extras.getString("TASK_ID")!=null?extras.getString("TASK_ID"):"";


        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        newTask = findViewById(R.id.newTask);
        taskDate = findViewById(R.id.taskDate);
        taskTime = findViewById(R.id.taskTime);
        taskDescription = findViewById(R.id.taskDescription);
        btnCreateTask = findViewById(R.id.createTask);
        imgTaskDate =  findViewById(R.id.imgTaskDate);
        databaseTasks = FirebaseDatabase.getInstance().getReference("tasks");

        if(invokedFrom != null && !invokedFrom.isEmpty() ){
            if(invokedFrom.equals("CardViewAdapter")){
                newTask.setText(taskTitle);
                taskDate.setText(dtTask);
                taskTime.setText(tmTask);
                taskDescription.setText(descTask);
                btnCreateTask.setText("UPDATE");
                autocompleteFragment.setText(locationName);
                setTitle("EDIT TASK");
                String[] dt = dtTask.split("-");
                String[] tm = tmTask.split(":");
                String[] tmAp = tm[1].split(" ");
                date = Integer.parseInt(dt[2]);
                monthOfDate = Integer.parseInt(dt[1]);
                yearOfDate = Integer.parseInt(dt[0]);
                hourOfDate = Integer.parseInt(tm[0]);
                minuteOfDate = Integer.parseInt(tmAp[0]);


            }
        }else {
            newTask.setText("");
            taskDate.setText("");
            taskTime.setText("");
            taskDescription.setText("");
            btnCreateTask.setText("CREATE");
            setTitle("CREATE");
        }

        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateView();
            }
        });

        imgTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateView();
            }
        });

        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        int hour = hourOfDay;

                        int minute = minutes;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minute < 10)
                            min = "0" + minute ;
                        else
                            min = String.valueOf(minute);

                        // Append in a StringBuilder
                        hourOfDate = hourOfDay;
                        minuteOfDate = minutes;
                        aTime = new StringBuilder().append(hour).append(':')
                                .append(min ).append(" ").append(timeSet).toString();
                        taskTime.setText(aTime);


                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();

            }
        });
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskTitle = newTask.getText()!=null ? newTask.getText().toString().trim():"";
                scheduleSingleAlarm(NewReminderActivity.this,date,monthOfDate,yearOfDate,hourOfDate,minuteOfDate,taskTitle);
                addTask();

            }
        });



    }
    private void addTask(){
        String taskTitle = newTask.getText().toString().trim();
        String date = taskDate.getText().toString().trim();
        String time = taskTime.getText().toString().trim();
        String desc = taskDescription.getText().toString().trim();
        if(!TextUtils.isEmpty(taskTitle) && !TextUtils.isEmpty(date)  && !TextUtils.isEmpty(time) ){
            String taskID;
            if(taskId!=null && !taskId.isEmpty()){
                taskID =  taskId;
            }else{
                taskID =databaseTasks.push().getKey();
            }

           Task task = new Task(taskID,taskTitle,date,time,desc,mlatitude,mlongitude,locationName);
           databaseTasks.child(androidId).child(taskID).setValue(task);
            Toast.makeText(this,"Task added",Toast.LENGTH_LONG).show();

            Intent i = new Intent(NewReminderActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this,"Please enter a valid task",Toast.LENGTH_LONG).show();
        }
    }

    public void DateView(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth)
            {
                taskDate.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                date = dayOfMonth;
                monthOfDate = monthOfYear;
                yearOfDate = year;
            }};
         Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));


        dpDialog.show();
    }




    public void scheduleSingleAlarm(Context context,int day,int monthOfYear,int year, int hour, int min, String title) {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(year,monthOfYear,day,hour,min,0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("TIME_TO_SET_ALARM", futureDate.getTimeInMillis());
        intent.putExtra("TASK_TITLE", title);

        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context,
                1111, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, futureDate.getTimeInMillis(), pendingUpdateIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, futureDate.getTimeInMillis(), pendingUpdateIntent);
        }
    }




}
