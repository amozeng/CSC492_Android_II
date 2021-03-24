package com.amozeng.a3_walkingtours;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class GeoFenceData implements Serializable {

    private final String id;
    private final String address;
    private final double lat;
    private final double lon;
    private final float radius;
    private final String description;
    private final String fenceColor;
    private final String imageURL;

    GeoFenceData(String id, String address, double lat, double lon, float radius, String description, String fenceColor, String image) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.radius = radius;
        this.description = description;
        this.fenceColor = fenceColor;
        this.imageURL = image;
    }

    String getId() {
        return id;
    }
    String getAddress() {
        return address;
    }
    double getLat() {
        return lat;
    }
    double getLon() {
        return lon;
    }
    float getRadius() {
        return radius;
    }
    String getDescription() { return description; }
    String getFenceColor() {
        return fenceColor;
    }
    String getImageURL() { return imageURL; }


    @NonNull
    @Override
    public String toString() {
        return "FenceData{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", radius=" + radius +
                ", description=" + description +
                ", fenceColor='" + fenceColor + '\'' +
                '}';
    }
}
