package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.androidsalad.popcorntvapp.Adapter.ImageAdapter;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";

    //views
    private ViewPager viewPager;

    //adapter for displaying images:
    private ImageAdapter imageAdapter;

    //database reference
    private DatabaseReference mPostDatabase, mPostPhotoDatabase, mBaseReference;

    //post Id from Intent:
    private String postId;

    //list
    List<String> photoList;

    //dialog
    private ProgressDialog dialog;

    //post views:
    int postViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //initializeVariables:
        intializeVariables();

        //get and update post Views:
        updatePostViews();

        //download list of photos from firebase:
        downloadPhotoListFromFirebase();

    }

    private void updatePostViews() {

        //get Post Views:
        mPostDatabase.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                postViews = post.getPostViews();

                Log.d(TAG, "Post Views: " + postViews);

                postViews = postViews + 1;

                //child updates to save post and photo:
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/celeb_posts/" + post.getCelebId() + "/" + post.getPostId() + "/" + "postViews", postViews);
                childUpdates.put("/posts/" + post.getPostId() + "/" + "postViews", postViews);

                mBaseReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void intializeVariables() {

        //get postId from Intent:
        postId = getIntent().getStringExtra("postId");

        //image adapter:
        imageAdapter = new ImageAdapter(this);

        //initialize photoList:
        photoList = new ArrayList<>();

        //views:
        viewPager = (ViewPager) findViewById(R.id.postViewPager);
        viewPager.setAdapter(imageAdapter);

        //database:
        mPostPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_DATABASE);
        mBaseReference = FirebaseDatabase.getInstance().getReference();

        //progress dialog:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //method for downloading photos from Firebase:
    private void downloadPhotoListFromFirebase() {

        mPostPhotoDatabase.child(postId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get Photo from Database:
                Photo photo = dataSnapshot.getValue(Photo.class);

                //extract image url from Photo:
                photoList.add(photo.getPhotoFullUrl());

                //dismiss dialog if running:
                if (dialog.isShowing()) dialog.dismiss();

                //update the image adapter:
                imageAdapter.updatePhotoList(photoList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
