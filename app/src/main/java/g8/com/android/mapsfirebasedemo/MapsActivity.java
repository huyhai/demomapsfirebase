package g8.com.android.mapsfirebasedemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private LocationListener mLocationListener;
    int permission;
    int permission2;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    GoogleApiClient mGoogleApiClient;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sm = new SessionManager(this);
        sys();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        if (shouldAskPermission()) {
            if (verifyStoragePermissions(MapsActivity.this)) {
                GG();
            }
        } else {
            GG();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showLog(String log) {
        Log.v("HAI", log);
    }

    private void GG() {

        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = null;
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
        }

        if (mLastLocation != null) {
            Toast.makeText(MapsActivity.this, String.valueOf(mLastLocation.getLatitude()), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        showLog("onLocationChanged " + location.getLatitude());
//        Toast.makeText(MapsActivity.this, "onLocationChanged " + location.getLatitude(), Toast.LENGTH_SHORT).show();
        createUser(location.getLatitude(), location.getLongitude());
    }

    LocationRequest mLocationRequest;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates s = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        showLog("success");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        // try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        //status.startResolutionForResult(
                        // OuterClass.this,
                        // 1);
                        // } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                        //  }
                        showLog("RESOLUTION_REQUIRED");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        showLog("SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

//    protected void onStart() {
//        mGoogleApiClient.connect();
//        super.onStart();
//    }
//
//    protected void onStop() {
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
        super.onDestroy();
    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }
//
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    public  boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeAccepted) {
                    GG();
//                    GPSTracker gps = new GPSTracker(MapsActivity.this, new NotifyDataAbstract() {
//                        @Override
//                        public void onReturnInt(int value) {
//                            super.onReturnInt(value);
//                        }
//                    });
                }
                break;

        }
    }

    public boolean verifyStoragePermissions(Activity activity) {
        permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permission2 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
            );
            return false;
        } else {

            return true;
        }
    }

    private void update() {
/*        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        Query pendingTasks = mFirebaseDatabase.orderByChild("name").equalTo("hai");
        pendingTasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot tasksSnapshot) {
                for (DataSnapshot snapshot : tasksSnapshot.getChildren()) {
                    int downC = Integer.parseInt(snapshot.child("down").getValue().toString()) - 1;
                    snapshot.getRef().child("down").setValue(downC);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void sys() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        // store app title to 'app_title' node
//        mFirebaseInstance.getReference("app_title").setValue("Realtime aaaa");
        // app_title change listener
        mFirebaseInstance.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = "";
                ArrayList<User> listUser = new ArrayList<User>();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    name = String.valueOf(user.child("name").getValue());
                    User user1 = new User(name, Double.valueOf(user.child("latt").getValue() + ""), Double.valueOf(user.child("lng").getValue() + ""));
                    Log.v("HAI", name);
                    listUser.add(user1);
                }
                setMarker(listUser);
//                String appTitle = dataSnapshot.getValue(String.class);
//                Toast.makeText(MapsActivity.this, name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MapsActivity.this, "Failed to read app title value." + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });
//        createUser(0, 0);
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void createUser(double lat, double lng) {
        User user = new User(getDeviceName(), lat, lng);
        if (TextUtils.isEmpty(sm.getLocationName())) {
            sm.setLocationName(mFirebaseDatabase.push().getKey());
        }
        mFirebaseDatabase.child(sm.getLocationName()).setValue(user);
        addUserChangeListener(sm.getLocationName());
    }

    private void addUserChangeListener(final String userId) {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = null;
                try {
                    user = dataSnapshot.getValue(User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Toast.makeText(MapsActivity.this, user.latt + "", Toast.LENGTH_SHORT).show();

                // Check for null
                if (user == null) {
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void setMarker(ArrayList<User> list) {
        mMap.clear();
        if (list.size() == 0) return;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (User u : list) {
            LatLng sydney = new LatLng(u.latt, u.lng);
            builder.include(sydney);
            mMap.addMarker(new MarkerOptions().position(sydney).title(u.name));
        }

        final LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                } catch (Exception e) {
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
/*        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng sydney = new LatLng(10.8003934, 106.6916823);
        LatLng sydne1y = new LatLng(10.7750993, 106.6586053);
        builder.include(sydney);
        builder.include(sydne1y);
        final LatLngBounds bounds = builder.build();
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydne1y).title("Marker in dfdf"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                } catch (Exception e) {
                }

            }
        });*/

    }
}
