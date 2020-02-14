package com.example.parth_c0766346_la12;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class mapActvt extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final int RADIUS = 1500;
    private static final String TAG = "MAP";
    private static final long WAIT_TIME = 5L;
    private GoogleMap mMap;
    Location currUserLocation;
    Marker fvt_dest, startL, User;

    Boolean isEditing = false;

    AlertDialog dropdownMenu;

    String s = null;
    Place p = null;

    DatabaseHelper mDatabase;

    // location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;

    //Context context;

    Spinner typeMap, nearby, markerAction;

    private String place_name;
    private Object[] dataTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_actvt);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mDatabase = new DatabaseHelper(this);

        findViewById(R.id.userLocationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FocusHomeMarker(currUserLocation);
            }
        });

        typeMap = findViewById(R.id.mapType);
        typeMap.setSelection(1);

        nearby = findViewById(R.id.nearByPlaces);
        nearby.setSelection(0);

        typeMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.setMapType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nearby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if(position > 0){


                    Boolean isFvt = (p != null);

                    mMap.clear();
                     if(User != null){
                        User.remove();
                    }
                    User = null;

                    if (isFvt){

                        FocusLocation(new LatLng(p.getLat(), p.getLng()));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLng()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .title(p.getName())).showInfoWindow();

                        addUSerMarker(currUserLocation);


                    }else{

                        FocusHomeMarker(currUserLocation);

                    }
                    // show nearby places
                    String searchPlace = nearby.getSelectedItem().toString();

                    String url = isFvt ? getUrl(p.getLat(), p.getLng(), searchPlace) :
                            getUrl(currUserLocation.getLatitude(), currUserLocation.getLongitude(), searchPlace);


                    Log.i(TAG, "onItemSelected: "+url);
                    dataTransfer = new Object[2];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    GetNearByPlaceData getNearByPlaceData = new GetNearByPlaceData();
                    getNearByPlaceData.execute(dataTransfer);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i(TAG, "onLocationChanged: called : " + location);

                if (location != null) {
                    currUserLocation = location;
                    addUSerMarker(currUserLocation);
                }
                


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



    } // eof


    @Override
    protected void onStart() {
        super.onStart();


//        if (!checkPermission())
//            requestPermission();
//        else
//
//            reqLocationUpdate();
//            currUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            addUSerMarker(currUserLocation);





    }


    private String getUrl(double lat, double lng, String nearByPlace){

        
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location="+lat+","+lng);
        placeUrl.append("&radius="+RADIUS);
        placeUrl.append("&type="+nearByPlace);
        placeUrl.append("&key="+getString(R.string.google_maps_key));
        return placeUrl.toString();
    }

    private String getDirectionUrl(){

        if(startL == null){
            System.out.println("null value detected");
        }

        Log.i(TAG, "getDirectionUrl: ");
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin=" + startL.getPosition().latitude + "," + startL.getPosition().longitude);
        directionUrl.append("&destination=" + fvt_dest.getPosition().latitude + "," + fvt_dest.getPosition().longitude);
        directionUrl.append("&key=" + getString(R.string.google_maps_key));
        return directionUrl.toString();

    }


    @SuppressLint("MissingPermission")
    public void startupACtivity(){

        reqLocationUpdate();
        currUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        addUSerMarker(currUserLocation);


        Intent i = getIntent();
        isEditing = i.getBooleanExtra("EDIT", false);
        Log.i(TAG, "isEditing: " + isEditing);
        p = (Place) i.getSerializableExtra("selectedPlace");


        if (p != null) {

            Log.i(TAG, "onMapReady: Place is not null good job");

            LatLng pos = new LatLng(p.getLat(), p.getLng());

            fvt_dest = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(isEditing ? "Drag to change location" : p.getName()).draggable(isEditing));

            Log.i(TAG, "onMapReady: marker added successfully");

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(pos)
                    .zoom(15)
                    .bearing(0)
                    .tilt(45)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {

            FocusHomeMarker(currUserLocation);
        }

        if (!isEditing) {
            if (fvt_dest != null) {
                fvt_dest.showInfoWindow();
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {


                    System.out.println("you just clicked on the marker");
                    fvt_dest = marker;


                    dataTransfer = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = getDirectionUrl();
                    Log.i(TAG, "directionURL: " + getDirectionUrl());
                    dataTransfer[2] = fvt_dest.getPosition();

                    GetDirectionData getDirectionData = new GetDirectionData();
                    // execute asynchronously
                    getDirectionData.execute(dataTransfer);


                    try {
                        s = getDirectionData.get(WAIT_TIME, TimeUnit.SECONDS);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }


                    HashMap<String, String> distanceHashMap = null;
                    DataParser distanceParser = new DataParser();
                    distanceHashMap = distanceParser.parseDistance(s);

                    showMarkerClickedAlert(marker.getTitle(), distanceHashMap.get("distance"), distanceHashMap.get("duration"));

                    return true;

                }
            });


            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {


//                    Location place = new Location("your destination");
//                    place.setLongitude(latLng.latitude);
//                    place.setLongitude(latLng.longitude);
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                    fvt_dest = mMap.addMarker(options);
                    fvt_dest.setTitle(fetchAddressLine(fvt_dest));
                    fvt_dest.showInfoWindow();
                }
            });


        } // this part will only execute if editing mode is off
        else {
            // when editing mode is ON
            Log.i(TAG, "old data: " + fvt_dest.getPosition());
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    fvt_dest = marker;
                    Log.i(TAG, "new data: " + fvt_dest.getPosition());

                }
            });


            findViewById(R.id.editModeLayout).setVisibility(View.VISIBLE);

            final CheckBox visited = findViewById(R.id.visitedCheckBox);
            Log.i(TAG, "onMapReady: " + p.getVisited());
            visited.setChecked(p.getVisited());
            // update button
            findViewById(R.id.updateBTN).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newPlaceName = fetchAddressLine(fvt_dest);
                    Boolean success = mDatabase.updatePlace(p.getId(), newPlaceName, visited.isChecked(),
                            fvt_dest.getPosition().latitude, fvt_dest.getPosition().longitude);
                    fvt_dest.setTitle(newPlaceName);
                    fvt_dest.showInfoWindow();
                    Log.i(TAG, "new data: " + fvt_dest.getPosition());


                    Toast.makeText(mapActvt.this, success ? "updated successfully" : "update failed", Toast.LENGTH_SHORT).show();


                }
            });

        }



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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {




        mMap = googleMap;

//        currUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        addUSerMarker(currUserLocation);


        if (!checkPermission()){
            requestPermission();}
        else {

           startupACtivity();
        }
    }


    private void showMarkerClickedAlert(String address, String distance, String duration) {

        AlertDialog.Builder alert = new AlertDialog.Builder(mapActvt.this);
        final View v = LayoutInflater.from(mapActvt.this).inflate(R.layout.dropdown, null);
        alert.setView(v);

        TextView tvPlace = v.findViewById(R.id.place_name);
        TextView tvDist = v.findViewById(R.id.distance);
        TextView tvDur = v.findViewById(R.id.duration);

        tvPlace.setText(address);
        tvDist.setText(distance);
        tvDur.setText(duration);

        if (mDatabase.numberOfResults(fvt_dest.getPosition().latitude, fvt_dest.getPosition().longitude)>0){

            Button b = v.findViewById(R.id.addToFvtBtn);
            b.setEnabled(false);
            b.setText("SAVED");

        }

        dropdownMenu = alert.create();
        dropdownMenu.show();

    }



    private void displayDirections(String[] directionsList) {


        int count = directionsList.length;

        for(int i=0; i<count; i++){
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }

    }


    public String fetchAddressLine(Marker m){

        try {
            List<Address> addresses = geocoder.getFromLocation(m.getPosition().latitude, m.getPosition().longitude,1);



            return addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + format);

        return format;
    }

    public void addToFvt() {


            place_name = fvt_dest.getTitle();


        if (mDatabase.addPlace(place_name, false,fvt_dest.getPosition().latitude, fvt_dest.getPosition().longitude)){

            Toast.makeText(this, place_name + " added" , Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Place NOT added", Toast.LENGTH_SHORT).show();
        }

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, 80, 80);
        Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    
    
    /*
    Location related functions
     */


    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void reqLocationUpdate() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);


    }


    private void FocusHomeMarker(Location location){


        p = null;
        addUSerMarker(location);

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    private void FocusLocation(LatLng latLng){
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void addUSerMarker(Location l){




            if(User != null){
                User.remove();
                User = null;
            }


            LatLng home = new LatLng(l.getLatitude(), l.getLongitude());
            Log.i(TAG, "run without error this time: ");
            startL = mMap.addMarker(new MarkerOptions()
                    .position(home)
                    .title("Current User Location")
                    .icon(bitmapDescriptorFromVector(this, R.drawable.user_current_location))

            );
            User = startL;





    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                startupACtivity();

            }else{

                Toast.makeText(this, "Permission is required to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void markerClickAction(View view) {

        switch (view.getId()) {

            case R.id.addToFvtBtn:
                addToFvt();
                break;
            case R.id.getDirBtn:

                mMap.clear();
                User = null;


                String[] directionsList;
                DataParser directionParser = new DataParser();
                directionsList = directionParser.parseDirections(s);
                displayDirections(directionsList);

                startL = mMap.addMarker(new MarkerOptions().position(startL.getPosition())
                        .title(startL.getTitle())
                        .icon(bitmapDescriptorFromVector(this,R.drawable.start2 )));

                fvt_dest = mMap.addMarker(new MarkerOptions().position(fvt_dest.getPosition())
                        .title(fvt_dest.getTitle())
                        .icon(bitmapDescriptorFromVector(this,R.drawable.destination )));
                fvt_dest.showInfoWindow();
                break;
            case R.id.setStartBtn:
                startL = fvt_dest;
                break;

            default:
                break;

        }

        dropdownMenu.dismiss();


    }
}
