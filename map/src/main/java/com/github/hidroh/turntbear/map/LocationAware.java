package com.github.hidroh.turntbear.map;

import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Callback interface for location changed events
 */
public interface LocationAware {
    /**
     * Fired when location has been changed
     * @param latitude  center latitude
     * @param longitude center longitude
     * @param radius    radius from center
     */
    void onLocationChanged(VisibleRegion visibleRegion);
}
