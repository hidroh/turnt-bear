package com.github.hidroh.turntbear.view;

import android.content.Context;
import android.widget.ImageView;

/**
 * Interface for image loader
 */
public interface ImageLoader {
    /**
     * Load an image from URL into given image view
     * @param context   an instance of {@link android.content.Context}
     * @param imageView {@link android.widget.ImageView} that will display image
     * @param url       URL from where to load image
     */
    void load(Context context, ImageView imageView, String url);
}
