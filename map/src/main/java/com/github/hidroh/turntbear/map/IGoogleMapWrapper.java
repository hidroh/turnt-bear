package com.github.hidroh.turntbear.map;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Interface for Google Map wrapper
 */
public interface IGoogleMapWrapper {
    static final int ZOOM_LEVEL_STREET = 16;

    /**
     * Get visible map region
     * @return  visible map region
     */
    VisibleRegion getVisibleRegion();

    /**
     * Set listener to map's camera change event
     * @param onCameraChangeListener    listener
     */
    void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener onCameraChangeListener);

    /**
     * Set listener to marker click event
     * @param onMarkerClickListener     listener
     */
    void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener);

    /**
     * Set listener to map click event
     * @param onMapClickListener    listener
     */
    void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener);

    /**
     * Set listener to my location button click event
     * @param onMyLocationButtonClickListener  listener
     */
    void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener);

    /**
     * Clear map markers
     */
    void clear();

    /**
     * Centralize and zoom map to specified location with given zoom level
     * @param latitude  centre latitude
     * @param longitude centre longitude
     * @param zoomLevel zoom level
     * @param zoomAnimated  true if zoom is animated
     */
    void goToLocation(double latitude, double longitude, int zoomLevel, boolean zoomAnimated);

    /**
     * Centralize and animate map to specified location
     * @param latitude  centre latitude
     * @param longitude centre longitude
     */
    void goToLocation(double latitude, double longitude);

    /**
     * Add marker to map with given options
     * @param markerOptions marker options
     * @return  added marker
     */
    Marker addMarker(MarkerOptions markerOptions);

    /**
     * Get map centre
     * @return  centre latitude and longitude
     */
    LatLng getCenter();

    /**
     * Enable or disable my location layer in map
     * @param enabled   true to enable, false to disable
     */
    void setMyLocationEnabled(boolean enabled);

    /**
     * Get map's UI settings object
     * @return  UI settings object
     */
    UiSettings getUiSettings();

    /**
     * Centralize map to current user's location based on GPS or network location
     */
    void goToMyLocation();

    /**
     * Fired when parent activity or fragment started
     */
    void onStart();

    /**
     * Fired when parent activity or fragment resumed
     */
    void onResume();

    /**
     * Fired when parent activity or fragment preparing to pause
     * @param outState
     */
    void onSaveInstanceState(Bundle outState);

    /**
     * Fired when parent activity or fragment paused
     */
    void onPause();

    /**
     * Fired when parent activity or fragment on low memory
     */
    void onLowMemory();

    /**
     * Fired when parent activity or fragment stopped
     */
    void onStop();

    /**
     * Fired when parent activity or fragment destroyed
     */
    void onDestroy();

    /**
     * Make given marker appears on top of other overlapping markers
     * @param marker    marker to be on top
     */
    void bringToFront(Marker marker);
}
