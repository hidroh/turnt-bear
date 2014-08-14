package com.github.hidroh.turntbear.model;

import com.github.hidroh.turntbear.map.model.IMapMarker;

public class MapMarker implements IMapMarker {
    private final String id;
    private final double latitude;
    private final double longitude;
    private final String title;
    private final String imageUrl;

    public MapMarker(String id, String title, double latitude, double longitude, String imageUrl) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
