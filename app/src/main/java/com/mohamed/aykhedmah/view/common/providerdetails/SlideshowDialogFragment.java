package com.mohamed.aykhedmah.view.common.providerdetails;



import static com.mohamed.aykhedmah.data.Constants.STORAGE;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.mohamed.aykhedmah.R;
import com.mohamed.aykhedmah.data.network.response.providerdetails.UserImage;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SlideshowDialogFragment extends DialogFragment {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    public PhotoView imageViewPreview;
    private static List<UserImage> images ;
    private static int currentPosition = 0;
    PhotoViewAttacher mAttacher;

    public static SlideshowDialogFragment newInstance(List<UserImage> _images, int position) {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        currentPosition = position;
        images = _images;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_slideshow_dialog, container, false);
        viewPager = v.findViewById(R.id.viewpager);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(currentPosition);
        return v;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        @Override
        public void onPageSelected(int position) {
            Log.e("onPageSelected", position + "");
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.e("onPageScrolled", arg2 + "");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.e("onScrollStateChanged", arg0 + "");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            imageViewPreview = view.findViewById(R.id.image_preview);
            Picasso.get().load(STORAGE + images.get(position).getImage()).placeholder(R.drawable.ic_picture).into(imageViewPreview);
            container.addView(view);
            mAttacher = new PhotoViewAttacher(imageViewPreview);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}