package com.github.hidroh.turntbear.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Parallax view transformer for view pager. Set via {@link ViewPager#setPageTransformer(boolean, android.support.v4.view.ViewPager.PageTransformer)}
 */
public class ViewPagerParallaxTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // add parallax effect
            transformX(page, page.getWidth() * -0.7f * position);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void transformX(View view, float translationX) {
        if (!(view instanceof ViewGroup)) {
            view.setTranslationX(translationX);
            return;
        }

        ViewGroup viewGroup = (ViewGroup) view;
        final int childCount = viewGroup.getChildCount();
        if (childCount == 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            transformX(viewGroup.getChildAt(i), translationX);
        }
    }
}
