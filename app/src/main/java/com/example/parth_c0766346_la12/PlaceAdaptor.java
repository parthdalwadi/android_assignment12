package com.example.parth_c0766346_la12;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlaceAdaptor extends ArrayAdapter {

    Context context;
    List<Place> places;
    int layoutRes;
    //SQLiteDatabase mDatabase;
    DatabaseHelper mDatabase;

    public PlaceAdaptor(@NonNull Context context, int resource, List<Place> places, DatabaseHelper mDatabase) {
        super(context, resource, places);
        this.context = context;
        this.places = places;
        this.layoutRes = resource;
        this.mDatabase = mDatabase;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layoutRes, null);

        TextView tvName = v.findViewById(R.id.name_ID);
        TextView tvLat = v.findViewById(R.id.lat_ID);
        TextView tvLng = v.findViewById(R.id.lng_ID);
        TextView tvVisited = v.findViewById(R.id.isVisited_ID);



        final Place p = places.get(position);
        tvName.setText(p.getName());
        tvLat.setText(String.valueOf(p.getLat()));
        tvLng.setText(String.valueOf(p.getLng()));
        tvVisited.setText(String.valueOf(p.getVisited()));

        if (p.getVisited())
            v.setBackgroundColor(0xFF03fc39);

        return v;
    }




}