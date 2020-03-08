package appmoviles.com.googlemapsapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //Constants
    public final static int PETITION_PERMISSION_LOCATION = 101;


    private GoogleMap mMap;
    private Marker marker;
    double lat = 0.0;
    double ing = 0.0;
    String message;
    String direccion = "";


    private Geocoder geocoder;
    private LocationManager manager;
    private Marker personalMarker;
    private Marker customPositionMarker;
    private LatLng personalPosition, customPosition;

    private ArrayList<LatLng> customPositions;
    private ArrayList<Marker> addMarkers;
    private List<Address> personalAddress, customAddress;
    private Location userLocation, customLocation;


    //Buttons
    private Button btnAddLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        addMarkers = new ArrayList<>();
        customPositions = new ArrayList<>();

        btnAddLocation = findViewById(R.id.btnAddMarket);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customPosition!= null){
                    customPositions.add(customPosition);
                    //getNearLocation();
                }
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        } else {
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }
        */
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateLocation(location);
            setLocation(location);

            /*

            if (personalMarker == null) {
                personalPosition = new LatLng(location.getLatitude(), location.getLongitude());

                try {
                    personalAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String addres = personalAddress.get(0).getAddressLine(0);
                    //personalMarker = mMap.addMarker(new MarkerOptions().position(personalPosition).title(getString(R.string.userPosition) + addres));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(personalPosition));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                userLocation = location;
                personalPosition = null;
                personalPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(personalPosition, 15));
                personalMarker.setPosition(personalPosition);

                try {
                    personalAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String addres = personalAddress.get(0).getAddressLine(0).split(",")[0];
                    //personalMarker.setTitle(getString(R.string.userPosition) + addres);
                } catch (IOException e) {

                    Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }*/
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            message = ("GPS on");
            showMessage();

        }

        @Override
        public void onProviderDisabled(String provider) {

            message = ("GPS off");
            locationStart();
            showMessage();

        }

    };

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
        myLocation();
        //mMap.setOnMapLongClickListener(this);
    }

    //Activar servicios del GPS
    private void locationStart() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            return;
        }*/
    }

    private void setLocation(Location location) {

        //Obtener dirección de la calle

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> personalAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!personalAddress.isEmpty()) {
                    Address address = personalAddress.get(0);
                    direccion = address.getAddressLine(0);
                }

            } catch (IOException e) {
                e.printStackTrace();

            }


        }


    }


    private void addMarker(double lat, double ing) {

        LatLng coordinates = new LatLng(lat, ing);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, 16);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Address: " + direccion));
        mMap.animateCamera(myLocation);

        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)
    }

    private void myLocation() {


        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PETITION_PERMISSION_LOCATION);
            return;
        }else{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1200,0,locationListener);
        }


    }

    private void updateLocation(Location location){
        if(location!=null){
            lat = location.getLatitude();
            ing = location.getLongitude();
            addMarker(lat,ing);
        }
    }

    public void showMessage(){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }


}
