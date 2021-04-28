package edu.sharif.mobile_hw2;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import edu.sharif.mobile_hw2.db.DataBaseContract;
import edu.sharif.mobile_hw2.db.DataBaseHelper;

public class MainActivity extends AppCompatActivity {

    private DataBaseHelper dbHelper;
    protected SQLiteDatabase db;

    private ConstraintLayout mapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeAppThem(this, "dark_mode");
        SettingsFragment.setContext(this);
        MapFragment.setContext(this);

        mapContainer = findViewById(R.id.mapContainer);

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
}