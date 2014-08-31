package com.github.hidroh.turntbear.list.model;

/**
 * Interface for objects with multiple cover images
 */
public interface ImageProvider {
    /**
     * Get array of image URLs
     * @return  array of image URLs
     */
    String[] getImageUrls();
}
