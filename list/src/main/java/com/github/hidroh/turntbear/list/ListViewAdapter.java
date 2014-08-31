package com.github.hidroh.turntbear.list;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.github.hidroh.turntbear.list.model.ImageProvider;
import com.github.hidroh.turntbear.view.ImageLoader;
import com.github.hidroh.turntbear.view.ViewPagerParallaxTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * An adapter for a list of image gallery that supports horizontal swipe
 */
public abstract class ListViewAdapter<T extends ImageProvider, ViewHolderType> extends ArrayAdapter<T> {
    /**
     * Callback interface for item on click event.
     * We need this since inner {@link android.support.v4.view.ViewPager} prevents bubbling
     * of click event to parent {@link android.widget.ListView}
     */
    public interface OnClickListener {
        /**
         * Fired when an item is clicked
         * @param position  item's position
         */
        void onClick(int position);
    }

    private final OnClickListener mListener;
    private final ImageLoader mImageLoader;

    protected LayoutInflater mInflater;
    private final Map<Integer, Integer> mSelectedImages = new HashMap<Integer, Integer>();

    /**
     * Construct an instance of this adapter
     * @param context           an instance of {@link android.content.Context}
     * @param animUtils         animation utility that handles image loading
     * @param listener          callback for item click event
     */
    public ListViewAdapter(Context context, ImageLoader animUtils, OnClickListener listener) {
        super(context, 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = animUtils;
        mListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_layout, parent, false);
            holder = new ViewHolder();
            holder.content = createContentViewHolder((ViewGroup) convertView.findViewById(R.id.list_item_container));
            holder.ivThumb = (ViewPager) convertView.findViewById(R.id.image_pager);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // populate view with data
        final T item = getItem(position);
        setUpContent(holder.content, item);
        holder.ivThumb.setAdapter(new PagerAdapter() {
            private String[] imageUrls = item.getImageUrls();
            @Override
            public Object instantiateItem(ViewGroup container, int pagePosition) {
                final View view = mInflater.inflate(R.layout.list_item_image_view, container, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(position);
                    }
                });
                mImageLoader.load(getContext(), (ImageView) view.findViewById(R.id.image_view),
                        imageUrls[pagePosition]);
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return imageUrls.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        if (item.getImageUrls().length > 1) {
            holder.ivThumb.setPageTransformer(false, new ViewPagerParallaxTransformer());
        }
        holder.ivThumb.setCurrentItem(mSelectedImages.containsKey(position) ? mSelectedImages.get(position) : 0);
        holder.ivThumb.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int pagePosition) {
                // remember selected image to scroll to when item is visible again
                mSelectedImages.put(position, pagePosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
        return convertView;
    }

    protected abstract void setUpContent(ViewHolderType contentViewHolder, T item);

    protected abstract ViewHolderType createContentViewHolder(ViewGroup view);

    private class ViewHolder {
        ViewPager ivThumb;
        ViewHolderType content;
    }
}
