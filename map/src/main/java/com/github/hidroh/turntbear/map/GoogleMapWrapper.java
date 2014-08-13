package com.github.hidroh.turntbear.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.github.hidroh.turntbear.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Wrapper class for {@link com.google.android.gms.maps.MapView},
 * {@link com.google.android.gms.maps.GoogleMap} and
 * {@link com.google.android.gms.location.LocationClient}.
 * This class support lifecycle callbacks similar to {@link android.app.Activity} or
 * {@link android.support.v4.app.Fragment}, and they should be called accordingly.
 */
public class GoogleMapWrapper implements IGoogleMapWrapper {
    private final View mEmptyInfoWindow;
    private LocationClient mLocationClient;
    private MapView mMapView;
    private GoogleMap mMap;

    public GoogleMapWrapper(Context context, MapView mapView) {
        initialize(context);
        mMapView = mapView;
        mMapView.onCreate(null);
        mMap = mapView.getMap();
        mEmptyInfoWindow =
                ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.empty_info_window, null);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return mEmptyInfoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        setMapTransparent(mMapView);
        mLocationClient = getLocationClient(context);
    }

    @Override
    public VisibleRegion getVisibleRegion() {
        return mMap.getProjection().getVisibleRegion();
    }

    @Override
    public void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener onCameraChangeListener) {
        mMap.setOnCameraChangeListener(onCameraChangeListener);
    }

    @Override
    public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener) {
        mMap.setOnMarkerClickListener(onMarkerClickListener);
    }

    @Override
    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        mMap.setOnMapClickListener(onMapClickListener);
    }

    @Override
    public void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
    }

    @Override
    public void clear() {
        mMap.clear();
    }

    @Override
    public void goToLocation(double latitude, double longitude, int zoomLevel, boolean zoomAnimated) {
        final CameraUpdate cameraUpdate = createCameraUpdate(latitude, longitude, zoomLevel);
        if (zoomAnimated) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    @Override
    public void goToLocation(double latitude, double longitude) {
        final CameraUpdate cameraUpdate = createCameraUpdate(latitude, longitude, -1);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public Marker addMarker(MarkerOptions markerOptions) {
        return mMap.addMarker(markerOptions);
    }

    @Override
    public LatLng getCenter() {
        return mMap.getCameraPosition().target;
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {
        mMap.setMyLocationEnabled(enabled);
    }

    @Override
    public UiSettings getUiSettings() {
        return mMap.getUiSettings();
    }

    @Override
    public void goToMyLocation() {
        Location location = mLocationClient.getLastLocation();

        if (location != null) {
            goToLocation(location.getLatitude(), location.getLongitude(), ZOOM_LEVEL_STREET, true);
        }
    }

    @Override
    public void bringToFront(Marker marker) {
        marker.showInfoWindow(); // trick to show info window, thus bring marker to front
    }

    @Override
    public void onStart() {
        mLocationClient.connect();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mMap = null;
        mMapView = null;
        mLocationClient = null;
    }

    /**
     * Prevent UI glitch when map view frame is moved
     * @param group view group that contains {@link com.google.android.gms.maps.GoogleMap}
     */
    private void setMapTransparent(ViewGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);

            if (child instanceof ViewGroup) {
                setMapTransparent((ViewGroup) child);
            } else if (child instanceof SurfaceView) {
                child.setBackgroundColor(0x00000000);
            }
        }
    }

    protected LocationClient getLocationClient(Context context) {
        return new LocationClient(context,
                new GooglePlayServicesClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // do nothing
                    }

                    @Override
                    public void onDisconnected() {
                        // do nothing
                    }
                },
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // do nothing
                    }
                });
    }

    protected CameraUpdate createCameraUpdate(double latitude, double longitude, int zoomLevel) {
        if (zoomLevel > 0) {
            return CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLevel);
        } else {
            return CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        }
    }

    protected void initialize(Context context) {
        MapsInitializer.initialize(context);
    }
}
