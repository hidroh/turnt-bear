package com.github.hidroh.turntbear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.hidroh.turntbear.map.CompositeMapFragment;
import com.github.hidroh.turntbear.model.MapMarker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends CompositeMapFragment<MapMarker> {
    private final MapMarker[] items = new MapMarker[]{
            new MapMarker("0", "Marker 1", 36, 9, "https://farm4.staticflickr.com/3666/9281251321_1e62b0e056_z_d.jpg"),
            new MapMarker("1", "Marker 2", 38, 8, "https://farm4.staticflickr.com/3699/9281250109_eb04059e8d_z_d.jpg"),
            new MapMarker("2", "Marker 3", 37, 11, "https://farm4.staticflickr.com/3673/9284033194_5418ccee39_z_d.jpg"),
            new MapMarker("3", "Marker 4", 39, 12, "https://farm4.staticflickr.com/3680/9284033866_7b9cca5471_z_d.jpg"),
            new MapMarker("4", "Marker 5", 59, 20, "https://farm8.staticflickr.com/7303/9281243941_1e54eaf082_z_d.jpg"),
            new MapMarker("5", "Marker 6", 60, 21, "https://farm8.staticflickr.com/7145/6633462713_59368ec6f3_z_d.jpg"),
            new MapMarker("6", "Marker 7", 55, 19, "https://farm5.staticflickr.com/4154/5032697697_35bf0778e9_z_d.jpg"),
            new MapMarker("7", "Marker 8", 57, 18, "https://farm5.staticflickr.com/4123/4938108058_5a97d5d59a_z_d.jpg")
    };

    @Override
    protected View inflateDetailsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, MapMarker item) {
        final View view = inflater.inflate(R.layout.details_layout, container, false);
        ((TextView) view.findViewById(R.id.details_text)).setText(item.getTitle());
        Picasso.with(getActivity())
                .load(item.getImageUrl())
                .into((ImageView) view.findViewById(R.id.details_background));
        return view;
    }

    @Override
    protected void searchByVisibleRegion(VisibleRegion visibleRegion, CompositeMapFragment.NetworkCallbacks<MapMarker> networkCallbacks) {
        final List<MapMarker> visibleItems = new ArrayList<MapMarker>();
        for (int i = 0; i < items.length; i++) {
            if (visibleRegion.latLngBounds.contains(
                    new LatLng(items[i].getLatitude(), items[i].getLongitude()))) {
                visibleItems.add(items[i]);
            }
        }

        networkCallbacks.onResponse(visibleItems.toArray(new MapMarker[visibleItems.size()]));
    }

    @Override
    protected double getDefaultCenterLat() {
        return 35;
    }

    @Override
    protected double getDefaultCenterLng() {
        return 10;
    }

    @Override
    protected int getDefaultZoomLevel() {
        return 4;
    }
}
