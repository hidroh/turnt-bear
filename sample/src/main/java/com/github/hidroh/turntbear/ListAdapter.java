package com.github.hidroh.turntbear;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.hidroh.turntbear.list.ListViewAdapter;
import com.github.hidroh.turntbear.list.model.ImageProvider;
import com.github.hidroh.turntbear.view.ImageLoader;

public final class ListAdapter extends ListViewAdapter<ImageProvider, ListAdapter.ViewHolder> {
    public ListAdapter(Context context, ImageLoader animUtils, OnClickListener listener) {
        super(context, animUtils, listener);
    }

    @Override
    protected void setUpContent(ViewHolder contentViewHolder, ImageProvider item) {
        contentViewHolder.textView.setText(item.toString());
    }

    @Override
    protected ViewHolder createContentViewHolder(ViewGroup view) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(20f);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        textView.setLayoutParams(params);
        textView.setBackgroundColor(Color.parseColor("#CC000000"));
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textView = textView;
        view.addView(textView);
        return viewHolder;
    }

    protected class ViewHolder {
        TextView textView;
    }
}
