package com.github.hidroh.turntbear.map;

import android.content.Context;
import android.util.Pair;

import com.github.hidroh.turntbear.map.model.IMapMarker;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to manage markers in a map
 * @param <T>   type of objects representing details information associated with markers
 */
public class MarkerManager<T extends IMapMarker> {
    private BitmapDescriptor mNormalIcon;
    private BitmapDescriptor mSelectedIcon;
    private BitmapDescriptor mCurrentIcon;
    private Set<String> mSelectedMarkers = new HashSet<String>();
    private Marker mCurrentMarker;
    private T[] mItems;
    private Map<String, Pair<Integer, Marker>> mMarkerMap = new HashMap<String, Pair<Integer, Marker>>();
    private MarkerComparator mComparator;

    public MarkerManager(Context context) {
        initialize(context);
    }

    /**
     * Clean, populate manager with list of markers and insert them into map
     * @param items array of items used to populate
     * @param map   map to add markers into
     */
    public void init(T[] items, IGoogleMapWrapper map) {
        clear();
        map.clear();
        mItems = null;
        if (items == null) {
            return;
        }

        mItems = items.clone();
        Arrays.sort(mItems, getMarkerComparator());
        for (int i = 0; i < mItems.length; i++) {
            final IMapMarker item = mItems[i];
            final Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                    .icon(mSelectedMarkers.contains(item.getId()) ?
                            getSelectedIcon() :
                            getNormalIcon())
                    .snippet(item.getId()) // use snippet to store ID
            );
            mMarkerMap.put(item.getId(), new Pair<Integer, Marker>(i, marker));
        }
    }

    /**
     * Get total number of items managed
     * @return  total number of items
     */
    public int getCount() {
        if (mItems == null) {
            return 0;
        }

        return mItems.length;
    }

    /**
     * Get position of a marker in the list
     * @param marker    marker to get position
     * @return  position of given marker
     */
    public int getPosition(Marker marker) {
        final String id = marker.getSnippet();
        if (!mMarkerMap.containsKey(id)) {
            return -1;
        }

        return mMarkerMap.get(id).first;
    }

    /**
     * Get item by position
     * @param position  position of item to get
     * @return  item at given position or null
     */
    public T getItem(int position) {
        if (isEmpty()) {
            return null;
        }

        if (position >= mItems.length) {
            return null;
        }

        return mItems[position];
    }

    /**
     * Get marker by position
     * @param position  position of marker to get
     * @return  marker at given position or null
     */
    public Marker getMarker(int position) {
        final T item = getItem(position);
        if (item == null) {
            return null;
        }

        return getMarker(item.getId());
    }

    /**
     * Set given marker as currently selected one
     * @param marker    marker to be set as current
     */
    public void setCurrentMarker(Marker marker) {
        if (marker != null) { // set action
            if (mCurrentMarker != null) {
                mCurrentMarker.setIcon(getSelectedIcon());
            }

            mCurrentMarker = marker;
            setSelected(marker);
        } else { // clear action
            final Marker currentMarker = getCurrentMarker();
            if (currentMarker == null) {
                return;
            }

            currentMarker.setIcon(getSelectedIcon());
            mCurrentMarker = null;
        }
    }

    /**
     * Clear internal data. List of previously selected markers remains.
     */
    public void clear() {
        mMarkerMap.clear();
        mItems = null;
        mCurrentMarker = null;
    }

    private boolean isEmpty() {
        return mItems == null || mItems.length == 0;
    }

    private Marker getMarker(String id) {
        if (!mMarkerMap.containsKey(id)) {
            return null;
        }

        return mMarkerMap.get(id).second;
    }

    private Marker getCurrentMarker() {
        return mCurrentMarker;
    }

    private void setSelected(Marker marker) {
        marker.setIcon(getCurrentIcon());
        mSelectedMarkers.add(marker.getSnippet());
    }

    protected BitmapDescriptor getNormalIcon() {
        if (mNormalIcon == null) {
            mNormalIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
        }

        return mNormalIcon;
    }

    protected BitmapDescriptor getSelectedIcon() {
        if (mSelectedIcon == null) {
            mSelectedIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_grey);
        }

        return mSelectedIcon;
    }

    protected BitmapDescriptor getCurrentIcon() {
        if (mCurrentIcon == null) {
            mCurrentIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_orange);
        }

        return mCurrentIcon;
    }

    protected void initialize(Context context) {
        MapsInitializer.initialize(context);
    }

    private Comparator<IMapMarker> getMarkerComparator() {
        if (mComparator == null) {
            mComparator = new MarkerComparator();
        }

        return mComparator;
    }

    /**
     * Comparator that sort markers from west to east, north to south
     */
    private class MarkerComparator implements Comparator<IMapMarker> {
        @Override
        public int compare(IMapMarker lhs, IMapMarker rhs) {
            // west before east
            if (lhs.getLongitude() < rhs.getLongitude()) {
                return -1;
            }

            if (lhs.getLongitude() > rhs.getLongitude()) {
                return 1;
            }

            // north before south
            if (lhs.getLatitude() > rhs.getLatitude()) {
                return -1;
            }

            if (lhs.getLatitude() < rhs.getLatitude()) {
                return 1;
            }

            return 0;
        }
    }
}
