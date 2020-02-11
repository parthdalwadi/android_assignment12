package com.example.parth_c0766346_la12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LV_places = findViewById(R.id.locationList);
        LV_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, position + "--" + id , Toast.LENGTH_SHORT).show();



                Intent mapI = new Intent(MainActivity.this, mapActvt.class);


                mapI.putExtra("selectedPlace", Place.MySavedPlaces.get(position));
                startActivity(mapI);



            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        String[] tempList = new String[Place.MySavedPlaces.size()];
        int i = 0;
        for (Place p:
             Place.MySavedPlaces) {

            tempList[i] = p.getName();

            i++;
        }


        final ArrayAdapter<String> placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tempList);
        LV_places.setAdapter(placeAdapter);

        placeAdapter.notifyDataSetChanged();

    }

    public void showMap(View view) {

        // go to another activity


        Intent mapI = new Intent(this, mapActvt.class);


        startActivity(mapI);

    }
}
