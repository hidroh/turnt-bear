package com.github.hidroh.turntbear.map.model;

/**
 * Represents a map marker
 */
public interface IMapMarker {
    /**
     * Get unique map ID
     * @return  unique ID
     */
    String getId();

    /**
     * Get latitude
     * @return  latitude
     */
    double getLatitude();

    /**
     * Get longitude
     * @return longitude
     */
    double getLongitude();
}
