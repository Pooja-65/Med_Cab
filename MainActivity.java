package com.rockstar.medcab.Activities;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.common.Method;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rockstar.medcab.Adapters.RequestAdapter;
import com.rockstar.medcab.DataModels.RequestDataModel;
import com.rockstar.medcab.R;
import com.rockstar.medcab.Utils.Endpoints;
import com.rockstar.medcab.Utils.VolleySingleton;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  private RecyclerView recyclerView;
  private List<RequestDataModel> requestDataModels;
  private RequestAdapter requestAdapter;
  private int appointmentYear, appointmentMonth, appointmentDay, appointmentHour, appointmentMinute;
  // Request code for SMS permission
  private static final int SMS_PERMISSION_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolBar = findViewById(R.id.toolbar);
    toolBar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.search_button) {
          startActivity(new Intent(MainActivity.this, SearchActivity.class));
          return true;
        } else if (item.getItemId() == R.id.menu_item_instructions) {
          startActivity(new Intent(MainActivity.this, InstructionsActivity.class));
          return true;
        }
        return false;
      }
    });

    // Create an instance of MySessionManager
    MySessionManager sessionManager = new MySessionManager(this);


    // Find the DrawerLayout in your XML layout
    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

// Find the NavigationView in your XML layout
    NavigationView navigationView = findViewById(R.id.nav_view);

// Set the click listener for the navigation drawer icon
    Toolbar toolbar = findViewById(R.id.toolbar);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
    );
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

// Set the item selected listener for the navigation menu
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_sign_out) {
          // Handle sign out action here
          // Example:
          signOut();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
      }

      private void signOut() {

        // Clear user sessions or reset authentication state
        // Replace the following code with your implementation to clear user sessions
        // Call the clearSession() method on the sessionManager instance
        sessionManager.clearSession();
        // Navigate to sign-in screen or any desired screen after sign out
        // Example:
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
      }
    });





    TextView make_request_button = findViewById(R.id.make_request_button);
    make_request_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, MakeRequestActivity.class));
      }
    });

    TextView scheduleAppointmentButton = findViewById(R.id.schedule_appointment_button);
    scheduleAppointmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showDateTimePicker();
      }
    });

    requestDataModels = new ArrayList<>();
    Toolbar toolbar1 = findViewById(R.id.toolbar);
    toolbar1.setOnMenuItemClickListener(new OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.search_button) {
          startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }
        return false;
      }
    });

    recyclerView = findViewById(R.id.recyclerView);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    requestAdapter = new RequestAdapter(requestDataModels, this);
    recyclerView.setAdapter(requestAdapter);
    populateHomePage();

    TextView pick_Location = findViewById(R.id.pick_location);
    String location = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("city", "no_city_found");
    if (!location.equals("no_city_found")) {
      pick_Location.setText(location);
    }
    // Check for SMS permission
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
      // Permission is not granted, request it
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
              SMS_PERMISSION_REQUEST_CODE);

    }
  }

  private void populateHomePage() {
    final String city = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getString("city", "no_city");
    StringRequest stringRequest = new StringRequest(
            Method.POST, Endpoints.get_requests, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<RequestDataModel>>() {
        }.getType();
        List<RequestDataModel> dataModels = gson.fromJson(response, type);
        requestDataModels.clear(); // Clear the existing data
        requestDataModels.addAll(dataModels);
        requestAdapter.notifyDataSetChanged();


      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(MainActivity.this, "Something went wrong:(", Toast.LENGTH_SHORT).show();
        Log.d("VOLLEY", Objects.requireNonNull(error.getMessage()));
      }
    }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("city", city);
        return params;
      }
    };

    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
  }

  private void showDateTimePicker() {
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                appointmentYear = year;
                appointmentMonth = month;
                appointmentDay = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                          @Override
                          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            appointmentHour = hourOfDay;
                            appointmentMinute = minute;

                            Calendar selectedDateTime = Calendar.getInstance();
                            selectedDateTime.set(appointmentYear, appointmentMonth, appointmentDay,
                                    appointmentHour, appointmentMinute);

                            // TODO: Handle the selected appointment date and time
                            // You can save the appointment in your database or perform any other required action

                            String dateTimeString = DateFormat.getDateTimeInstance().format(selectedDateTime.getTime());
                            Toast.makeText(MainActivity.this, "Appointment scheduled: " + dateTimeString, Toast.LENGTH_LONG).show();
                            // Send the appointment confirmation to all registered phone numbers
                            sendAppointmentConfirmationToRegisteredNumbers(dateTimeString);
                          }
                        }, hour, minute, false);

                timePickerDialog.show();
              }
            }, year, month, day);

    datePickerDialog.show();
  }

  private void sendAppointmentConfirmationToRegisteredNumbers(String dateTime) {
    // Retrieve the registered phone numbers from your database or data source
    List<String> phoneNumbers = getRegisteredPhoneNumbers();

    for (String phoneNumber : phoneNumbers) {
      String message = "Your appointment is scheduled for: " + dateTime;

      // Check if SMS permission is granted
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
              == PackageManager.PERMISSION_GRANTED) {
        // Permission is granted, send the SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(MainActivity.this, "Appointment confirmation sent to " + phoneNumber,
                Toast.LENGTH_SHORT).show();
      } else {
        // Permission is not granted, show a message or handle the situation as desired
        Toast.makeText(MainActivity.this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private List<String> getRegisteredPhoneNumbers() {
    // Replace this with your implementation to fetch the registered phone numbers from your database or data source
    List<String> phoneNumbers = new ArrayList<>();
    phoneNumbers.add("9373019639");
    phoneNumbers.add("7760943388");
    phoneNumbers.add("8546954246");
    return phoneNumbers;
  }

}
