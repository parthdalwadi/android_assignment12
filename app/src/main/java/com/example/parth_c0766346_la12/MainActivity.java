package com.example.parth_c0766346_la12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ListView LV_places;
    List<Place> placeList;
    DatabaseHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = new DatabaseHelper(this);
        LV_places = findViewById(R.id.locationList);
        LV_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, position + "--" + id , Toast.LENGTH_SHORT).show();



                Intent mapI = new Intent(MainActivity.this, mapActvt.class);


                //mapI.putExtra("selectedPlace", Place.MySavedPlaces.get(position));
                startActivity(mapI);



            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        loadPlaces();




    }

    public void showMap(View view) {

        // go to another activity


        Intent mapI = new Intent(this, mapActvt.class);


        startActivity(mapI);

    }


    private void loadPlaces() {
//        String sql = "SELECT * FROM employees";
//        Cursor cursor = mDatabase.rawQuery(sql, null);

        placeList = new ArrayList<>();

        Cursor cursor = mDatabase.getAllPlaces();

        if (cursor.moveToFirst()) {
            do {
                placeList.add(new Place(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2).equals("true"),
                        cursor.getDouble(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();

            // show items in a listView
            // we use a custom adapter to show employees


            PlaceAdaptor adaptor = new PlaceAdaptor(this, R.layout.place_cell, placeList, mDatabase);

            LV_places.setAdapter(adaptor);

        }
    }
}
