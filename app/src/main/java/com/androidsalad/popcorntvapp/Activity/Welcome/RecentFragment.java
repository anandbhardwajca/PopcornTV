package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsalad.popcorntvapp.Adapter.PostListAdapter;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.androidsalad.popcorntvapp.Util.GridSpacingItemDecoration;
import com.androidsalad.popcorntvapp.Util.InternetUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentFragment extends Fragment {

    //views:
    private RecyclerView mRecyclerView;
    private PostListAdapter mAdapter;
    private List<Post> postList;

    //firebase database:
    private DatabaseReference mPostDatabase;

    //progress dialog:
    private ProgressDialog dialog;

    //items per page:
    private int mPostPerPage = 20;

    public RecentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_recent, container, false);
        initializeVariables(convertView);

        //check Internet Connection:
        if (InternetUtil.isNetworkAvailable(getActivity())) {

            //download post list from firebase database:
            loadDataFromFirebase(null);
        } else {

            showAlertDialog();
        }


        return convertView;
    }

    private void initializeVariables(View convertView) {

        //firebase:
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_DATABASE);

        //views:
        mRecyclerView = (RecyclerView) convertView.findViewById(R.id.recentFragmentPostRecyclerView);
        mAdapter = new PostListAdapter(getActivity());
        postList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        //progress dialog: show:
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void loadDataFromFirebase(String nodeId) {

        //create firebase query:
        Query query;

        //check if node passed:
        if (nodeId == null) {
            query = mPostDatabase.orderByKey()
                    .limitToLast(mPostPerPage);
        } else {

            query = mPostDatabase.orderByKey()
                    .endAt(nodeId)
                    .limitToLast(mPostPerPage);
        }

        //run query:
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //initiate new post List:
                List<Post> newPosts = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.getValue(Post.class);
                    //check if post is live:
                    if (post != null) {
                        //add to new post List:
                        newPosts.add(post);
                    }


                }
                if (dialog.isShowing()) dialog.dismiss();

                Collections.reverse(newPosts);
                mAdapter.addAll(newPosts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setMessage("Internet Connection Required")
                .setCancelable(false)
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {

                                // Restart the activity
                                Intent intent = new Intent(
                                        getActivity(),
                                        WelcomeActivity.class);
                                getActivity().finish();
                                startActivity(intent);

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
