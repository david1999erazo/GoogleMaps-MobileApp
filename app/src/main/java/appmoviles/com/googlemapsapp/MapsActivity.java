package appmoviles.com.googlemapsapp;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener  {
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
    private Button btnAddMarker;

    //TextView
    private TextView textDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Initilization
        geocoder = new Geocoder(this, Locale.getDefault());
        addMarkers = new ArrayList<>();
        customPositions = new ArrayList<>();


        textDescription = findViewById(R.id.textDescription);
        btnAddMarker = findViewById(R.id.btnAddMarker);
        btnAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customPosition!=null){
                    customPositions.add(customPosition);


                    List<Address> nearAddress;
                    Location vLocation = new Location("variable location");
                    try {
                        for (int i = 0; i<customPositions.size();i++){
                            nearAddress = geocoder.getFromLocation(customPositions.get(i).latitude,customPositions.get(i).longitude,1);
                            vLocation.setLatitude(customPositions.get(i).latitude);
                            vLocation.setLongitude(customPositions.get(i).longitude);
                            double distance = Math.round((userLocation.distanceTo(vLocation)*100)/100d);
                            String addres = nearAddress.get(0).getAddressLine(0).split(",")[0];
                            Marker newMarker = mMap.addMarker(new MarkerOptions().position(customPositions.get(i)).icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))).title("Address: "+addres).snippet("Distance: "+distance+" m"));
                            addMarkers.add(newMarker);
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    getNearLocation();
                }
            }
        });

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateLocation(location);
            setLocation(location);
            userLocation=location;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation();
        mMap.setOnMapLongClickListener(this);
    }

    //Activar servicios del GPS
    private void locationStart() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }

    private void setLocation(Location location) {

        //Obtener dirección de la calle
        if (personalMarker==null){
            personalPosition = new LatLng(location.getLatitude(), location.getLongitude());

            try {
                personalAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                String addres = personalAddress.get(0).getAddressLine(0);
                personalMarker = mMap.addMarker(new MarkerOptions().position(personalPosition).title("Address: "+ addres));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(personalPosition));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            userLocation=location;
            personalPosition = null;
            personalPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(personalPosition,15));
            personalMarker.setPosition(personalPosition);
            try {
                personalAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                String addres = personalAddress.get(0).getAddressLine(0).split(",")[0];
                personalMarker.setTitle("Address: "+ addres);

            } catch (IOException e) {
                Toast.makeText(MapsActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }


    private void addMarker(double lat, double ing) {

        LatLng coordinates = new LatLng(lat, ing);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, 16);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(coordinates).title("Address: " + direccion).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE )));
        mMap.animateCamera(myLocation);

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



    @Override
    public void onMapLongClick(LatLng latLng) {

        customPosition = latLng;
        customLocation = new Location("custom Location");
        customLocation.setLongitude(latLng.longitude);
        customLocation.setLatitude(latLng.latitude);

        double distance = Math.round(userLocation.distanceTo(customLocation)*100/100d);

        try{

            customAddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String direccionX = customAddress.get(0).getAddressLine(0).split(",")[0];

            if(customPositionMarker==null){
                customPositionMarker = mMap.addMarker(new MarkerOptions().position(customPosition).title("Address: "+ direccionX).snippet("Distance: "+distance + " m").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }else{
                customPositionMarker.setPosition(latLng);
                customPositionMarker.setTitle(direccionX);
            }
        }catch (IOException e){
            e.printStackTrace();
        }




    }


    public String getNearLocation(){

        String nearLocation = "";
        double distance = Double.MAX_VALUE;
        List<Address> nearAddress=null;
        Location xLocation = new Location("variable location");
        try{

            for (int i = 0; i<customPositions.size();i++){
                xLocation.setLatitude(customPositions.get(i).latitude);
                xLocation.setLongitude(customPositions.get(i).longitude);
                double xDistance = Math.round((userLocation.distanceTo(xLocation)*100)/100d);
                if (xDistance<distance){
                    nearAddress = geocoder.getFromLocation(xLocation.getLatitude(),xLocation.getLongitude(),1);
                    distance=xDistance;
                }

            }
            if (nearAddress!=null){
                if (distance<100){
                    String address = nearAddress.get(0).getAddressLine(0).split(",")[0];
                    textDescription.setText("You are in the place: " +address);
                }
                else{
                    String address = nearAddress.get(0).getAddressLine(0).split(",")[0];
                    textDescription.setText("Near location: "+ address);
                }
            }



        }catch (IOException e){
            e.printStackTrace();
        }
        return nearLocation;
    }




    public void showMessage(){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

}
