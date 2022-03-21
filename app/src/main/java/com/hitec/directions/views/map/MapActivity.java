package com.hitec.directions.views.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitec.directions.R;
import com.hitec.directions.utils.Constants;
import com.hitec.directions.utils.MapListeners;
import com.hitec.directions.viewmodel.MapActivityViewModel;
import com.hitec.directions.views.directions.DirectionsActivity;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.StyleInterface;
import com.mapbox.maps.plugin.LocationPuck;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.delegates.MapDelegateProvider;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationProvider;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.PuckLocatedAtPointListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MapActivity extends AppCompatActivity {
    MapView mapView;
    PermissionsManager permissionsManager;
    PermissionsListener permissionsListener;
    LocationEngine locationEngine;

    private LocationListeningCallback callback = new LocationListeningCallback(this);

    static double destinationLatitude;
    static double destinationLongitude;
    static double currentLatitude;
    static double currentLongitude;

    EditText searchInput;
    String input;
    FloatingActionButton directionsBtn;

    MapListeners mapListeners;

    MapActivityViewModel mapActivityViewModel;
    private final static String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        searchInput = findViewById(R.id.location_input_search);
        directionsBtn = findViewById(R.id.fabDirections);

        mapActivityViewModel = new ViewModelProvider(this).get(MapActivityViewModel.class);

        // obtain the best location engine that is available
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        mapListeners = new MapListeners(mapView, this);

        //
        setUpPermissionListener();
        setUpPermissionManager();

        mapActivityViewModel.setUpNetworkRequest();

        searchLocation();


        directionsBtn.setOnClickListener(v-> {
            if(searchInput.getText().length() > 1) {
                startDirectionsActivity(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude);
            }else{
                showDialogue("Error","Destination is empty, please enter a place to go:)", "OK");
            }
        });
    }

    private void setUpPermissionManager() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            displayCurrentLocation();
        } else {
            permissionsManager = new PermissionsManager(permissionsListener);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * returns information about the state of permissions.
     */
    private void setUpPermissionListener() {
        permissionsListener = new PermissionsListener() {
            @Override
            public void onExplanationNeeded(List<String> permissionsToExplain) {
                //Toast
                Toast.makeText(MapActivity.this, "You need to accept location permissions.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionResult(boolean granted) {
                if (granted) {
                    // activate the Maps SDK's LocationComponent to show the device's location
                    displayCurrentLocation();
                } else {
                    showDialogue(Constants.INSTANCE.getDIALOGUE_TITLE(), Constants.INSTANCE.getDIALOGUE_MESSAGE(), Constants.INSTANCE.getDIALOGUE_NEGATIVE_BTN_TEXT());
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialogue(String title, String message, String buttonText) {
        new AlertDialog.Builder(MapActivity.this).setTitle(title).setMessage(message)
                .setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

    }

    private void displayCurrentLocation() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(14.0).build());
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        mapListeners.initLocationComponent();
        mapListeners.setupGesturesListener();

        //request location updates
        getLatLng();
    }

    //update location
    private void getLatLng() {
        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
        long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

        //
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            currentLongitude = location.getLongitude();
            currentLatitude = location.getLatitude();

            Log.i(TAG, currentLatitude + "lat::lng" + currentLongitude);
        }

    }

    //Destination
    private void searchLocation(){
        MarkerHelperClass markerHelperClass = new MarkerHelperClass(this);
        markerHelperClass.setMapView(mapView);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    input = s.toString().trim();
                    mapActivityViewModel.fetchDestinationCoordinates(input).observe(MapActivity.this, new Observer<List<String>>() {
                        @Override
                        public void onChanged(List<String> center) {

                            Log.i(TAG, "dest \n"+center.get(0)+"::"+center.get(1));

                            destinationLongitude = Double.parseDouble(center.get(0));
                            destinationLatitude = Double.parseDouble(center.get(1));


                            markerHelperClass.setLat(destinationLatitude);
                            markerHelperClass.setLong(destinationLongitude);
                            markerHelperClass.setDestinationMarker();
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void startDirectionsActivity(double currentLatitude, double currentLongitude, double destinationLatitude, double destinationLongitude){
        Intent intent = new Intent(this, DirectionsActivity.class);
        intent.putExtra(Constants.INSTANCE.getCURRENT_LATITUDE(), currentLatitude);
        intent.putExtra(Constants.INSTANCE.getCURRENT_LONGITUDE(), currentLongitude);
        intent.putExtra(Constants.INSTANCE.getDESTINATION_LATITUDE(), destinationLatitude);
        intent.putExtra(Constants.INSTANCE.getDESTINATION_LONGITUDE(), destinationLongitude);
        intent.putExtra(Constants.INSTANCE.getDESTINATION(), input);

        startActivity(intent);
    }


    static class LocationListeningCallback implements LocationEngineCallback<LocationEngineResult> {
        //prevent memory leak
        private final WeakReference<MapActivity> activityWeakReference;

        public double lat;

        LocationListeningCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        //called whenever the Mapbox Core Libraries identifies a change in the device's location.
        @Override
        public void onSuccess(LocationEngineResult locationEngineResult) {
            Location lastLocation = locationEngineResult.getLastLocation();

            //Log.i("MapsStaticActivity", currentLatitude+" ::lat<--->long:: "+currentLongitude);

        }

        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e("Error", e.getMessage());
        }

        public double getLat() {
            return lat;
        }
    }
}