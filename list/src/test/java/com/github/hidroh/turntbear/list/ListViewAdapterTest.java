package com.github.hidroh.turntbear.list;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.github.hidroh.turntbear.list.model.ImageProvider;
import com.github.hidroh.turntbear.view.ImageLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ListViewAdapterTest {
    private ActivityController<Activity> controller;
    private Context context;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class);
        context = controller.create().get();
    }

    @Test
    public void testGetView() {
        final StringBuilder expectedClickedPosition = new StringBuilder();
        ListViewAdapter adapter = new ListViewAdapter(context,
                mock(ImageLoader.class),
                new ListViewAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position) {
                        expectedClickedPosition.append(position);
                    }
                }) {

            @Override
            protected void setUpContent(Object contentViewHolder, ImageProvider item) {
                // do nothing
            }

            @Override
            protected Object createContentViewHolder(ViewGroup view) {
                return null;
            }
        };

        adapter.addAll(new ImageProvider() {
            @Override
            public String[] getImageUrls() {
                return new String[]{"url1", "url2"};
            }
        }, new ImageProvider() {
            @Override
            public String[] getImageUrls() {
                return new String[0];
            }
        });


        View view = adapter.getView(0, null, null);
        // test view pager click
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.image_pager);
        viewPager.getAdapter().instantiateItem(viewPager, 0);
        viewPager.getChildAt(0).performClick();
        assertEquals("0", expectedClickedPosition.toString()); // 1st list view item

        // test view pager selection state
        viewPager.setCurrentItem(1);
        adapter.getView(0, view, null);
        assertEquals(1, viewPager.getCurrentItem());
    }

    @After
    public void tearDown() {
        controller.destroy();
    }
}
