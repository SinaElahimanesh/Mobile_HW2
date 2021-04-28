package edu.sharif.mobile_hw2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.util.ArrayList;

import edu.sharif.mobile_hw2.db.DataBaseContract;
import edu.sharif.mobile_hw2.db.DataBaseHelper;
import edu.sharif.mobile_hw2.speed_calculator.GPSCallback;
import edu.sharif.mobile_hw2.speed_calculator.GPSManager;

public class MainActivity extends AppCompatActivity implements GPSCallback {

    private DataBaseHelper dbHelper;
    protected SQLiteDatabase db;

    private ConstraintLayout mapContainer;
//    private EditText searchText;

    private static double searchLatitude;
    private static double searchLongitude;

    public static void setSearchLatitude(double searchLatitude) {
        MainActivity.searchLatitude = searchLatitude;
    }

    public static void setSearchLongitude(double searchLongitude) {
        MainActivity.searchLongitude = searchLongitude;
    }

    @Override
    protected void onResume() {
        super.onResume();

        voiceSearch();

        txtview=(TextView) findViewById(R.id.speedt);
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        LatLng latLng = new LatLng(searchLatitude, searchLongitude);
//        addMarker(latLng);
//        System.out.println("hiiiiii");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeAppThem(this, "dark_mode");
        SettingsFragment.setContext(this);
        MapFragment.setContext(this);

        mapContainer = findViewById(R.id.mapContainer);
        searchText = findViewById(R.id.searchText);

        // Permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.maps);   // set default page as Map
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bookmarks:
                        removeAllFragments();
                        mapContainer.setVisibility(View.INVISIBLE);
                        loadFragment(new BookmarkFragment());
                        break;
                    case R.id.maps:
                        removeAllFragments();
                        mapContainer.setVisibility(View.VISIBLE);
//                        loadFragment(new MapFragment());
                        break;
                    case R.id.settings:
                        removeAllFragments();
                        mapContainer.setVisibility(View.INVISIBLE);
                        loadFragment(new SettingsFragment());
                        break;
                }
                return true;
            }
        });

//        insertDB();

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(MainActivity.this, SearchPlacesActivity.class);
                    startActivity(intent);
                } else {
//                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void insertDB(String placeName, double latitude, double longitude) {
        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.DataBaseEntry.PLACE_NAME, placeName);
        values.put(DataBaseContract.DataBaseEntry.PLACE_LATITUDE, latitude);
        values.put(DataBaseContract.DataBaseEntry.PLACE_LONGITUDE, longitude);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DataBaseContract.DataBaseEntry.TABLE_NAME, null, values);
    }


    private void removeAllFragments() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public static void changeAppThem(Context context, String key) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sharedPreferences.getBoolean(key, false);
        if(darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    private static EditText searchText;

    public static void setSearchText(EditText searchText) {
        MainActivity.searchText = searchText;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchText.setText((String) result.get(0));

                }
                break;
            }
        }
    }





    private final int REQ_CODE = 100;

    private void voiceSearch() {
        searchText = findViewById(R.id.searchText);
        ImageView speak = findViewById(R.id.speakButton);
        System.out.println(speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

//        MainActivity.setSearchText(searchText);
    }




    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isGPSEnabled=false;
    LocationManager locationManager;
    double currentSpeed,kmphSpeed;
    TextView txtview;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrentSpeed(View view){
        txtview.setText("R.string.info)");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled) {
            gpsManager.startListening(this);
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }

    @Override
    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        currentSpeed = round(speed,3, BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed*3.6),3,BigDecimal.ROUND_HALF_UP);
        txtview.setText(kmphSpeed+"km/h");
    }

    @Override
    public void onDestroy() {
//        gpsManager.stopListening();
//        gpsManager.setGPSCallback(null);
//        gpsManager = null;
        super.onDestroy();
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
}