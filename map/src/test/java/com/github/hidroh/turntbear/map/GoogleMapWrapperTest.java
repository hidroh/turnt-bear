package com.github.hidroh.turntbear.map;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GoogleMapWrapperTest {
    private ActivityController<Activity> controller;
    private GoogleMapWrapper wrapper;
    private MapView mapView;
    private GoogleMap map;
    private CameraUpdate cameraUpdate;
    private Activity context;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class);
        context = controller.create().get();
        mapView = mock(MapView.class);
        map = mock(GoogleMap.class);
        when(mapView.getMap()).thenReturn(map);
        cameraUpdate = mock(CameraUpdate.class);
        wrapper = new GoogleMapWrapperStub(context, mapView) {
            @Override
            protected CameraUpdate createCameraUpdate(double latitude, double longitude, int zoomLevel) {
                return cameraUpdate;
            }
        };
    }

    @Test
    public void testMapWrapper() {
        wrapper.setMyLocationEnabled(true);
        verify(map).setMyLocationEnabled(true);

        GoogleMap.OnCameraChangeListener cameraListener = mock(GoogleMap.OnCameraChangeListener.class);
        wrapper.setOnCameraChangeListener(cameraListener);
        verify(map).setOnCameraChangeListener(cameraListener);

        GoogleMap.OnMapClickListener mapClickListener = mock(GoogleMap.OnMapClickListener.class);
        wrapper.setOnMapClickListener(mapClickListener);
        verify(map).setOnMapClickListener(mapClickListener);

        GoogleMap.OnMarkerClickListener markerClickListener = mock(GoogleMap.OnMarkerClickListener.class);
        wrapper.setOnMarkerClickListener(markerClickListener);
        verify(map).setOnMarkerClickListener(markerClickListener);

        wrapper.getUiSettings();
        verify(map).getUiSettings();

        Marker marker = mock(Marker.class);
        wrapper.bringToFront(marker);
        verify(marker).showInfoWindow();

        MarkerOptions markerOptions = mock(MarkerOptions.class);
        wrapper.addMarker(markerOptions);
        verify(map).addMarker(markerOptions);

        wrapper.clear();
        verify(map).clear();

        wrapper.onResume();
        verify(mapView).onResume();

        Bundle bundle = new Bundle();
        wrapper.onSaveInstanceState(bundle);
        verify(mapView).onSaveInstanceState(bundle);

        wrapper.onPause();
        verify(mapView).onPause();

        wrapper.onLowMemory();
        verify(mapView).onLowMemory();
        wrapper.onDestroy();
        verify(mapView).onDestroy();

        final LocationClient locationClient = mock(LocationClient.class);
        wrapper = new GoogleMapWrapperStub(context, mapView) {
            @Override
            protected LocationClient getLocationClient(Context context) {
                return locationClient;
            }
        };
        wrapper.onStart();
        verify(locationClient).connect();

        wrapper.onStop();
        verify(locationClient).disconnect();
    }

    @Test
    public void testGetCenter() {
        LatLng expected = new LatLng(0, 0);
        when(map.getCameraPosition()).thenReturn(new CameraPosition(expected, 10, 0, 0));
        LatLng actual = wrapper.getCenter();
        assertEquals(expected, actual);
    }

    @Test
    public void testGoToLocation() {
        wrapper.goToLocation(5, 9);
        verify(map).animateCamera(cameraUpdate);
    }

    @Test
    public void testGoToLocationWithZoom() {
        wrapper.goToLocation(5, 9, 10, true);
        verify(map).animateCamera(cameraUpdate);

        wrapper.goToLocation(5, 9, 10, false);
        verify(map).moveCamera(cameraUpdate);
    }

    @Test
    public void testGoToMyLocation() {
        final LocationClient locationClient = mock(LocationClient.class);
        wrapper = new GoogleMapWrapperStub(context, mapView) {
            @Override
            protected LocationClient getLocationClient(Context context) {
                return locationClient;
            }

            @Override
            protected CameraUpdate createCameraUpdate(double latitude, double longitude, int zoomLevel) {
                return cameraUpdate;
            }
        };
        when(locationClient.getLastLocation()).thenReturn(null);
        wrapper.goToMyLocation();
        verify(map, never()).animateCamera(any(CameraUpdate.class));

        Location location = mock(Location.class);
        when(locationClient.getLastLocation()).thenReturn(location);
        wrapper.goToMyLocation();
        verify(map).animateCamera(cameraUpdate);
    }

    @After
    public void tearDown() {
        controller.destroy();
    }

    private class GoogleMapWrapperStub extends GoogleMapWrapper {
        public GoogleMapWrapperStub(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        protected void initialize(Context context) {
            // do nothing
        }
    }
}
