package com.androidsalad.popcorntvapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.androidsalad.popcorntvapp.Activity.Welcome.WelcomeActivity;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter {

    //context, photo list and inflater:
    private Context context;
    private List<String> photoList;
    private LayoutInflater inflater;

    //constructor with context:
    public ImageAdapter(Context context) {
        this.context = context;
        this.photoList = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //update photo list and notify adapter:
    public void updatePhotoList(List<String> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photoList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //views:
        TouchImageView imageView;
        Button closeButton;
        final ProgressBar progressBar;

        //convertView:
        View convertView = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        imageView = (TouchImageView) convertView.findViewById(R.id.imgDisplay);
        closeButton = (Button) convertView.findViewById(R.id.btnClose);
        progressBar = (ProgressBar) convertView.findViewById(R.id.fullScreenImageProgressBar);

        Glide.with(context)
                .load(photoList.get(position))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, WelcomeActivity.class));
                ((Activity) context).finish();
            }
        });

        container.addView(convertView);
        return convertView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
