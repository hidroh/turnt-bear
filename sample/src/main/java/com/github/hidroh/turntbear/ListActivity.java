package com.github.hidroh.turntbear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.hidroh.turntbear.list.ListViewAdapter;
import com.github.hidroh.turntbear.list.model.ImageProvider;
import com.github.hidroh.turntbear.view.ImageLoader;
import com.squareup.picasso.Picasso;

public class ListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        ListViewAdapter adapter = new ListAdapter(this, new ImageLoader() {
            @Override
            public void load(Context context, ImageView imageView, String url) {
                Picasso.with(context).load(url).into(imageView);
            }
        }, new ListViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(ListActivity.this, "Clicked on item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.add(new ImageProvider() {
            @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm4.staticflickr.com/3666/9281251321_1e62b0e056_z_d.jpg",
                            "https://farm4.staticflickr.com/3699/9281250109_eb04059e8d_z_d.jpg",
                            "https://farm4.staticflickr.com/3673/9284033194_5418ccee39_z_d.jpg",
                    };
                }
            });
        adapter.add(new ImageProvider() {
                @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm8.staticflickr.com/7145/6633462713_59368ec6f3_z_d.jpg",
                            "https://farm5.staticflickr.com/4154/5032697697_35bf0778e9_z_d.jpg",
                            "https://farm5.staticflickr.com/4123/4938108058_5a97d5d59a_z_d.jpg"
                    };
                }
            });
        adapter.add(new ImageProvider() {
                @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm4.staticflickr.com/3666/9281251321_1e62b0e056_z_d.jpg",
                            "https://farm4.staticflickr.com/3673/9284033194_5418ccee39_z_d.jpg",
                            "https://farm4.staticflickr.com/3680/9284033866_7b9cca5471_z_d.jpg"
                    };
                }
            });
        adapter.add(new ImageProvider() {
                @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm8.staticflickr.com/7303/9281243941_1e54eaf082_z_d.jpg",
                            "https://farm8.staticflickr.com/7145/6633462713_59368ec6f3_z_d.jpg",
                            "https://farm5.staticflickr.com/4123/4938108058_5a97d5d59a_z_d.jpg"
                    };
                }
            });
        adapter.add(new ImageProvider() {
                @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm4.staticflickr.com/3666/9281251321_1e62b0e056_z_d.jpg",
                            "https://farm4.staticflickr.com/3680/9284033866_7b9cca5471_z_d.jpg"
                    };
                }
            });
        adapter.add(new ImageProvider() {
                @Override
                public String[] getImageUrls() {
                    return new String[]{
                            "https://farm5.staticflickr.com/4123/4938108058_5a97d5d59a_z_d.jpg"
                    };
                }
            });
        listView.setAdapter(adapter);
        setContentView(listView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
