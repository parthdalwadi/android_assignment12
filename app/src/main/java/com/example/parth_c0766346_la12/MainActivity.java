package com.example.parth_c0766346_la12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    SwipeMenuListView LV_places;
    List<Place> placeList;
    DatabaseHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = new DatabaseHelper(this);
        LV_places = findViewById(R.id.locationList);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                
                SwipeMenuItem del_item = new SwipeMenuItem(getApplicationContext());
                del_item.setWidth(150);
                del_item.setBackground(new ColorDrawable(Color.RED));
                del_item.setTitle("DELETE");
                del_item.setTitleSize(15);
                del_item.setTitleColor(Color.WHITE);
                menu.addMenuItem(del_item);


                SwipeMenuItem update_item = new SwipeMenuItem(getApplicationContext());
                update_item.setWidth(150);
                update_item.setBackground(new ColorDrawable(Color.rgb(0x00, 0x00,
                        0xff)));
                update_item.setTitle("EDIT");
                update_item.setTitleSize(15);
                update_item.setTitleColor(Color.WHITE);
                menu.addMenuItem(update_item);


            }
        };
        LV_places.setMenuCreator(creator);
        LV_places.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        LV_places.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch(index){

                    case 0:
                        Log.i("DEBUG", "DELETE ITEM SELECTED: " + position);
                        mDatabase.removePlace(placeList.get(position).getId());
                        loadPlaces();

                        break;
                    case 1:
                        Log.i("DEBUG", "UPDATE ITEM SELECTED: " + position);
                        Intent editI = new Intent(MainActivity.this, mapActvt.class);
                        editI.putExtra("selectedPlace", placeList.get(position));
                        editI.putExtra("EDIT", true);

                        startActivity(editI);
                        break;
                    default:
                        break;
                }


                return false;
            }
        });

        LV_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, position + "--" + id , Toast.LENGTH_SHORT).show();



                Intent mapI = new Intent(MainActivity.this, mapActvt.class);
                mapI.putExtra("selectedPlace", placeList.get(position));
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

                Log.i("DATACHECK", "loadPlaces: "+cursor.getString(2));
                placeList.add(new Place(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2).equals("1"),
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
