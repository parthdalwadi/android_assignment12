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
import android.content.DialogInterface;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class mapActvt extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final int RADIUS = 1500;
    private static final String TAG = "MAP";
    private GoogleMap mMap;
    Location currLocation;
    Marker fvt_dest, startL;

    String s = null;


    // location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;

    //Context context;

    Spinner typeMap, nearby, markerAction;

    private String place_name;
    Place placeToAdd;
    private Object[] dataTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_actvt);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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
                    // show nearby places
                    String searchPlace = nearby.getSelectedItem().toString();
                    String url = getUrl(currLocation.getLatitude(), currLocation.getLongitude(), searchPlace);
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

                currLocation = location;
                
                setHomeMarker(currLocation);

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


        if (!checkPermission())
            requestPermission();
        else
            reqLocationUpdate();


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

        Log.i(TAG, "getDirectionUrl: ");
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin=" + startL.getPosition().latitude + "," + startL.getPosition().longitude);
        directionUrl.append("&destination=" + fvt_dest.getPosition().latitude + "," + fvt_dest.getPosition().longitude);
        directionUrl.append("&key=" + getString(R.string.google_maps_key));
        return directionUrl.toString();

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



        Intent i = getIntent();

        Place p = (Place) i.getSerializableExtra("selectedPlace");

        if (p != null){

            fvt_dest = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLat(),p.getLng()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(p.getName())
            );
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
                Log.i(TAG, "directionURL: "+getDirectionUrl());
                dataTransfer[2] = fvt_dest.getPosition();

                GetDirectionData getDirectionData = new GetDirectionData();
                // execute asynchronously
                getDirectionData.execute(dataTransfer);


                try {
                    s = getDirectionData.get(5L, TimeUnit.SECONDS);

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

                showMarkerClickedAlert(distanceHashMap.get("distance"),distanceHashMap.get("duration") );







                return true;

        }});


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {



                Location place = new Location("your destination");
                place.setLongitude(latLng.latitude);
                place.setLongitude(latLng.longitude);
                MarkerOptions options = new MarkerOptions().position(latLng).title("your place ")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                if (fvt_dest != null){

                    fvt_dest.remove();


                }

                fvt_dest = mMap.addMarker(options);
                fvt_dest = fvt_dest;






            }
        });

    }//eof


    private void showMarkerClickedAlert(String distance, String duration) {


        String place_name = "Place Name";



        AlertDialog.Builder alert = new AlertDialog.Builder(mapActvt.this);
        final View v = LayoutInflater.from(mapActvt.this).inflate(R.layout.dropdown, null);
        alert.setView(v);

        TextView tvPlace = v.findViewById(R.id.place_name);
        TextView tvDist = v.findViewById(R.id.distance);
        TextView tvDur = v.findViewById(R.id.duration);

        tvPlace.setText(place_name);
        tvDist.setText(distance);
        tvDur.setText(duration);



        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){


                markerAction = v.findViewById(R.id.selectAction);

               switch (markerAction.getSelectedItem().toString()){

                   case "Add to Favourite":
                       addToFvt();
                       break;
                   case "Get Directions":

                       String[] directionsList;
                       DataParser directionParser = new DataParser();
                       directionsList = directionParser.parseDirections(s);
                       displayDirections(directionsList);

                       break;
                   case "Mark Starting Point":
                       startL = fvt_dest;
                       break;

                   default:
                       break;

               }


            }

        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

                // Do nothing

            }

        });

        alert.create().show();
    }

    private void displayDirections(String[] directionsList) {


        int count = directionsList.length;

        for(int i=0; i<count; i++){
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
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
        return "No Address found";
    }

    public void addToFvt() {

            place_name = fetchAddressLine(fvt_dest);
            placeToAdd = new Place(place_name, false, fvt_dest.getPosition().latitude, fvt_dest.getPosition().longitude);


            Place.MySavedPlaces.add(placeToAdd);

            Toast.makeText(this, "Place added to favourites", Toast.LENGTH_SHORT).show();

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
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


    private void setHomeMarker(Location location){

        // Add a marker in Sydney and move the camera
        LatLng home = new LatLng(location.getLatitude(), location.getLongitude());
        startL = mMap.addMarker(new MarkerOptions()
                .position(home)
                .title("Current User Location")
                .icon(bitmapDescriptorFromVector(this, R.drawable.blueicon))
                );



        CameraPosition cameraPosition = CameraPosition.builder()
                .target(home)
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                reqLocationUpdate();

            }else{

                Toast.makeText(this, "Permission is required to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
}
