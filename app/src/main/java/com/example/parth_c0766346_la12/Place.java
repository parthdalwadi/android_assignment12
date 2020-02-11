package com.example.parth_c0766346_la12;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Place implements Serializable {


    private String name;
    private Boolean isVisited;
    private Double lat, lng;

    public Place(String name, Boolean isVisited, Double lat, Double lng) {
        this.name = name;
        this.isVisited = isVisited;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public static List<Place> MySavedPlaces = new ArrayList<>();
}
