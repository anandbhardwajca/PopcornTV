package com.androidsalad.popcorntvapp.Util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;

public class Constants {


    //duration of wait of splash screen:
    public final static int SPLASH_DISPLAY_LENGTH = 2000;


    //return trimmed string:
    public static String trim(String name) {

        return name.replace(" ", "");
    }

    //return trimmed string:
    public static int getPostViews(String postViews) {

        if (TextUtils.isEmpty(postViews)) {
            return 0;
        } else {
            return Integer.parseInt(postViews);
        }
    }


    //firebase database & storage constants:
    public final static String FIREBASE_CELEB_DATABASE = "celebs";
    public final static String FIREBASE_POST_DATABASE = "posts";
    public final static String FIREBASE_PHOTO_DATABASE = "photos";
    public final static String FIREBASE_CELEB_POSTS_DATABASE = "celeb_posts";
    public final static String FIREBASE_POST_PHOTOS_DATABASE = "post_photos";
    public final static String FIREBASE_IMAGE_STORAGE = "images";

    //int for picking image from gallery:
    public final static int INT_ACTION_PICK = 1;

    //maximum image that can be picked:
    public final static int MAX_IMAGE_PICK = 10;




}
