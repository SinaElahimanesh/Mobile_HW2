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

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

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

    private static double searchLatitude = 0;
    private static double searchLongitude = 0;
    private static String searchTitle;

    public static void setSearchLatitude(double searchLatitude) {
        MainActivity.searchLatitude = searchLatitude;
    }

    public static void setSearchLongitude(double searchLongitude) {
        MainActivity.searchLongitude = searchLongitude;
    }

    public static void setSearchTitle(String searchTitle) {
        MainActivity.searchTitle = searchTitle;
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        voiceSearch();

        txtview = (TextView) findViewById(R.id.speedt);
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        LatLng latLng = new LatLng(searchLatitude, searchLongitude);
//        addMarker(latLng);
//        System.out.println("hiiiiii");
        if(searchLatitude != 0 && searchLongitude != 0) {
//            addMarkerToMap(new GeoPoint(searchLatitude, searchLongitude), searchTitle);
        }
    }

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapContainer = findViewById(R.id.mapContainer);
        searchText = findViewById(R.id.searchText);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        changeAppThem(this, "dark_mode", false);
        SettingsFragment.setContext(this);
        MapFragment.setContext(this);

        // Permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bottomNavigationView.setSelectedItemId(R.id.maps);   // set default page as Map
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("mido","shigi");
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


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, mLocationListener);


        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        loadingGif = findViewById(R.id.mapLoad);
        Glide.with(this).load(R.drawable.loading).into(loadingGif);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setVisibility(View.INVISIBLE);
//        requestPermissionsIfNecessary(new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        });
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(18);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                LayoutInflater layoutinflater = LayoutInflater.from(MainActivity.this);
                View promptUserView = layoutinflater.inflate(R.layout.user_input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(promptUserView);
                alertDialogBuilder.setTitle("What Do you want to call this location?");
                final EditText locationName = promptUserView.findViewById(R.id.locationName);
                alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (locationName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "please enter a name for your location", Toast.LENGTH_LONG).show();
                        } else {
                            addMarkerToMap(p, locationName.getText().toString());
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);
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

    public void changeAppThem(Context context, String key, boolean goToSettings) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sharedPreferences.getBoolean(key, false);
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(goToSettings) {
            removeAllFragments();
            mapContainer.setVisibility(View.VISIBLE);
            bottomNavigationView.setSelectedItemId(R.id.maps);
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
    Boolean isGPSEnabled = false;
    LocationManager locationManager;
    double currentSpeed, kmphSpeed;
    TextView txtview;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrentSpeed(View view) {
        txtview.setText("R.string.info)");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            gpsManager.startListening(this);
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }

    @Override
    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
        txtview.setText(kmphSpeed + "km/h");
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


    /////map


    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    IMapController mapController;
    static Location location;
    ImageView loadingGif;


    @Override
    public void onPause() {
        super.onPause();
        //map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }


    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.setCenter(startPoint);
            setIcon(startPoint);
            MainActivity.location = location;
            map.setVisibility(View.VISIBLE);
            loadingGif.setVisibility(View.INVISIBLE);
        }

        Marker startMarker;

        public void setIcon(GeoPoint startPoint) {
            map.getOverlays().remove(startMarker);
            startMarker = new Marker(map);
            startMarker.setIcon(getDrawable(R.drawable.marker_default));
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle("this is your current location");
            map.getOverlays().add(startMarker);
        }


    };

    public void currentLocation(View view) {
        if (location == null) return;
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    public void addMarkerToMap(GeoPoint point, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.showInfoWindow();
        map.getOverlays().add(marker);
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select your answer.");
                builder.setMessage("Are you you want to delete this marker?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.setVisible(false);
                        mapView.getOverlays().remove(marker);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    public void clickTest(View view) {
        System.out.println("kpwksws w s w s w s w s w s  w ");
        Log.d("gigggiggg","shigi");
    }
}