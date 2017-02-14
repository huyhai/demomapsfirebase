package g8.com.android.mapsfirebasedemo;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.attr.name;
import static android.R.string.no;
import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sys();
        mLocationListener.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                createUser(location.getLatitude(),location.getLongitude());
            }

        };
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
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    name = String.valueOf(user.child("name").getValue());
                    Log.v("HAI", name);
                }

//                String appTitle = dataSnapshot.getValue(String.class);
                Toast.makeText(MapsActivity.this, name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MapsActivity.this, "Failed to read app title value." + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });
        createUser(0,0);
    }

    private String userId;

    private void createUser(double lat, double lng) {
        User user = new User("hai", lat, lng);
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener(userId);
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
                Toast.makeText(MapsActivity.this, user.latt + "", Toast.LENGTH_SHORT).show();

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
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
        });

    }
}
