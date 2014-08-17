package com.github.hidroh.turntbear.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.hidroh.turntbear.map.model.IMapMarker;
import com.github.hidroh.turntbear.map.view.ViewPagerParallaxTransformer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Base class for composite map screen, including a map view and details view
 * @param <T>   implementation of {@link com.github.hidroh.turntbear.map.model.IMapMarker}
 *           representing items on map
 */
public abstract class CompositeMapFragment<T extends IMapMarker> extends Fragment {
    /**
     * Callback interface for async network events
     * @param <T>   parameterized response type
     */
    public interface NetworkCallbacks<T> {
        /**
         * Fired when network call successfully returns response
         * @param items list of items in response
         */
        void onResponse(T[] items);
    }

    /**
     * Return true if fragment is in resumed state
     */
    protected boolean mIsResumed;
    private boolean mToggleLock;
    protected ViewPager mViewPager;
    protected IGoogleMapWrapper mMap;
    protected FragmentStatePagerAdapter mAdapter;
    private MarkerManager<T> mMarkerManager;
    private ViewPagerParallaxTransformer mParallaxTransformer;
    private NetworkCallbacks<T> mNetworkCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.composite_map_layout, container, false);
        mMarkerManager = getMarkerManager();
        mParallaxTransformer = new ViewPagerParallaxTransformer();
        mMap = getMapWrapper((MapView) view.findViewById(R.id.map_view));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (view.findViewById(R.id.map_viewpager_container).getVisibility() == View.VISIBLE) {
                    return;
                }

                handleCameraChange();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mViewPager.setCurrentItem(mMarkerManager.getPosition(marker), false);
                toggleDetailsView(true);
                handleMarkerSelected(marker);
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerManager.setCurrentMarker(null);
                toggleDetailsView(false);
            }
        });
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(false);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.goToMyLocation();
                return true;
            }
        });

        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(final int position) {
                final T item = mMarkerManager.getItem(position);
                if (item == null) {
                    return null;
                }

                return new Fragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        final View view = inflateDetailsView(inflater, container, savedInstanceState, item);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            private float startY;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    if (startY < event.getY()) { // swipe down
                                        mMarkerManager.setCurrentMarker(null);
                                        toggleDetailsView(false);
                                    }
                                }

                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    startY = event.getY();
                                }

                                return true;
                            }
                        });
                        return view;
                    }
                };
            }

            @Override
            public int getCount() {
                return mMarkerManager.getCount();
            }
        };
        mViewPager = (ViewPager) view.findViewById(R.id.map_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(false, new ViewPagerParallaxTransformer());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                handleMarkerSelected(mMarkerManager.getMarker(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
        return view;
    }

    protected void handleCameraChange() {
        searchByVisibleRegion(mMap.getVisibleRegion(), getNetworkCallbacks());
        if (getActivity() instanceof LocationAware) {
            ((LocationAware) getActivity()).onLocationChanged(
                    mMap.getVisibleRegion());
        }
    }

    private NetworkCallbacks<T> getNetworkCallbacks() {
        if (mNetworkCallbacks == null) {
            mNetworkCallbacks = new NetworkCallbacks<T>() {
                @Override
                public void onResponse(T[] items) {
                    if (!mIsResumed) {
                        return;
                    }

                    mViewPager.setPageTransformer(false, items != null && items.length > 0 ? mParallaxTransformer : null);
                    if (items == null) {
                        return;
                    }

                    mMarkerManager.init(items, mMap);
                    mAdapter.notifyDataSetChanged();

                }
            };
        }

        return mNetworkCallbacks;
    }

    /**
     * Inflate details view that will be used to display details information for a map marker
     * @param inflater              an instance of {@link android.view.LayoutInflater}
     * @param container             parent view for details view
     * @param savedInstanceState    saved instance state
     * @param item                  object contains data used to populate view
     * @return  view with populated data from item
     */
    protected abstract View inflateDetailsView(LayoutInflater inflater, ViewGroup container,
                                               Bundle savedInstanceState, T item);

    private void handleMarkerSelected(Marker marker) {
        if (marker == null) {
            return;
        }

        mMarkerManager.setCurrentMarker(marker);
        mMap.bringToFront(marker);
        mMap.goToLocation(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResumed = true;
        mMap.onResume();
    }

    @Override
    public void onStop() {
        mMap.onStop();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mIsResumed = false;
        mMap.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMap.onDestroy();
        mMarkerManager.clear();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMap.onLowMemory();
        super.onLowMemory();
    }

    protected MarkerManager<T> getMarkerManager() {
        return new MarkerManager<T>(getActivity());
    }

    protected IGoogleMapWrapper getMapWrapper(MapView mapView) {
        return new GoogleMapWrapper(getActivity(), mapView);
    }

    protected void resetLocation() {
        mMap.goToLocation(getDefaultCenterLat(),
                getDefaultCenterLng(),
                getDefaultZoomLevel(), false);
    }

    /**
     * This method should response to visible region change by retrieving
     * {@link com.github.hidroh.turntbear.map.model.IMapMarker} that correspond to
     * updated visible region
     * @param visibleRegion     current visible region
     * @param networkCallbacks  async callback that will be triggered when this method returns
     */
    protected abstract void searchByVisibleRegion(VisibleRegion visibleRegion, NetworkCallbacks<T> networkCallbacks);

    /**
     * Get initial map center latitude
     * @return  initial latitude
     */
    protected abstract double getDefaultCenterLat();

    /**
     * Get initial map center longitude
     * @return  initial longitude
     */
    protected abstract double getDefaultCenterLng();

    /**
     * Get initial map zoom level
     * @return  initial zoom level
     */
    protected abstract int getDefaultZoomLevel();

    protected void toggleDetailsView(final boolean isVisible) {
        if (mToggleLock) {
            return;
        }

        final View view = getView().findViewById(R.id.map_viewpager_container);
        final boolean isViewVisible = view.getVisibility() == View.VISIBLE;
        if (isViewVisible == isVisible) {
            return;
        }

        mMap.getUiSettings().setZoomGesturesEnabled(!isVisible);
        final Animation animation = AnimationUtils.loadAnimation(getActivity(),
                isVisible ? R.anim.slide_up : R.anim.slide_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mToggleLock = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mToggleLock = false;
                view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // do nothing
            }
        });
        view.startAnimation(animation);
    }
}
