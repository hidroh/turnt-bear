package com.github.hidroh.turntbear.map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.github.hidroh.turntbear.map.model.IMapMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class MarkerManagerTest {
    private MarkerManager<IMapMarker> manager;
    private BitmapDescriptor normalIcon;
    private BitmapDescriptor selectedIcon;
    private BitmapDescriptor currentIcon;
    private ActivityController<Activity> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class);
        Activity context = controller.create().get();
        normalIcon = mock(BitmapDescriptor.class);
        selectedIcon = mock(BitmapDescriptor.class);
        currentIcon = mock(BitmapDescriptor.class);
        manager = new MarkerManager<IMapMarker>(context) {
            @Override
            protected void initialize(Context context) {
                // do nothing
            }

            @Override
            protected BitmapDescriptor getNormalIcon() {
                return normalIcon;
            }

            @Override
            protected BitmapDescriptor getSelectedIcon() {
                return selectedIcon;
            }

            @Override
            protected BitmapDescriptor getCurrentIcon() {
                return currentIcon;
            }
        };
    }

    @Test
    public void testEmpty() {
        manager.init(new IMapMarker[]{}, new GoogleMapWrapperStub());
        assertEquals(0, manager.getCount());
        assertNull(manager.getItem(0));
        manager.init(null, new GoogleMapWrapperStub());
        assertEquals(0, manager.getCount());
        assertNull(manager.getItem(0));
    }

    @Test
    public void testClear() {
        manager.init(new IMapMarker[]{new MapMarkerStub("1")}, new GoogleMapWrapperStub());
        assertEquals(1, manager.getCount());
        manager.clear();
        assertEquals(0, manager.getCount());
    }

    @Test
    public void testGetMarker() {
        manager.init(new MapMarkerStub[]{new MapMarkerStub("1"), new MapMarkerStub("2")},
                new GoogleMapWrapperStub());
        MapMarkerStub item = (MapMarkerStub) manager.getItem(1);
        assertEquals("2", item.getId());
        Marker marker = manager.getMarker(1);
        assertEquals("2", marker.getSnippet());
        assertEquals(marker, manager.getMarker(1));
        assertNull(manager.getItem(100));
        assertNull(manager.getMarker(100));
        assertEquals(-1, manager.getPosition(mock(Marker.class)));
        assertEquals(1, manager.getPosition(marker));
        item.id = "aloha"; // this should not happen
        assertNull(manager.getMarker(1));
    }

    @Test
    public void testSetCurrentMarker() {
        manager.init(new MapMarkerStub[]{new MapMarkerStub("1"), new MapMarkerStub("2")},
                new GoogleMapWrapperStub());
        manager.setCurrentMarker(manager.getMarker(0));
        verify(manager.getMarker(0)).setIcon(currentIcon);
        manager.setCurrentMarker(manager.getMarker(1));
        verify(manager.getMarker(0)).setIcon(selectedIcon);
        verify(manager.getMarker(1)).setIcon(currentIcon);
        manager.setCurrentMarker(null);
        manager.setCurrentMarker(null);
        verify(manager.getMarker(1), times(1)).setIcon(selectedIcon);
    }

    @Test
    public void testMarkerOrder() {
        manager.init(new MapMarkerStub[]{
                        new MapMarkerStub("second") {
                            @Override
                            public double getLatitude() {
                                return -1;
                            }

                            @Override
                            public double getLongitude() {
                                return -1;
                            }
                        },
                        new MapMarkerStub("first") {
                            @Override
                            public double getLatitude() {
                                return 1;
                            }

                            @Override
                            public double getLongitude() {
                                return -1;
                            }
                        },
                        new MapMarkerStub("second to last") {
                            @Override
                            public double getLatitude() {
                                return 1;
                            }

                            @Override
                            public double getLongitude() {
                                return 1;
                            }
                        },
                        new MapMarkerStub("last") {
                            @Override
                            public double getLatitude() {
                                return -1;
                            }

                            @Override
                            public double getLongitude() {
                                return 1;
                            }
                        },
                        new MapMarkerStub("second as well") {
                            @Override
                            public double getLatitude() {
                                return -1;
                            }

                            @Override
                            public double getLongitude() {
                                return -1;
                            }
                        },
                },
                new GoogleMapWrapperStub());
        assertEquals("first", manager.getMarker(0).getSnippet());
        assertEquals("last", manager.getMarker(4).getSnippet());
    }

    @After
    public void tearDown() {
        controller.destroy();
    }

    private class GoogleMapWrapperStub implements IGoogleMapWrapper {
        @Override
        public VisibleRegion getVisibleRegion() {
            return null;
        }

        @Override
        public void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener onCameraChangeListener) {

        }

        @Override
        public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener) {

        }

        @Override
        public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {

        }

        @Override
        public void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener) {

        }

        @Override
        public void clear() {

        }

        @Override
        public void goToLocation(double latitude, double longitude, int zoomLevel, boolean zoomAnimated) {

        }

        @Override
        public void goToLocation(double latitude, double longitude) {

        }

        @Override
        public Marker addMarker(MarkerOptions markerOptions) {
            final Marker marker = mock(Marker.class);
            when(marker.getSnippet()).thenReturn(markerOptions.getSnippet());
            return marker;
        }

        @Override
        public LatLng getCenter() {
            return null;
        }

        @Override
        public void setMyLocationEnabled(boolean enabled) {

        }

        @Override
        public UiSettings getUiSettings() {
            return null;
        }

        @Override
        public void goToMyLocation() {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onLowMemory() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void bringToFront(Marker marker) {

        }
    }

    private class MapMarkerStub implements IMapMarker {
        public String id;

        public MapMarkerStub(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public double getLatitude() {
            return 0;
        }

        @Override
        public double getLongitude() {
            return 0;
        }
    }
}
