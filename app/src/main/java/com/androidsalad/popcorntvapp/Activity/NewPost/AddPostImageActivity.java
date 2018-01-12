package com.androidsalad.popcorntvapp.Activity.NewPost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidsalad.popcorntvapp.Activity.Welcome.WelcomeActivity;
import com.androidsalad.popcorntvapp.Adapter.UploadListAdapter;
import com.androidsalad.popcorntvapp.Model.Celeb;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.Model.UploadItem;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class AddPostImageActivity extends AppCompatActivity {

    private static final String TAG = "AddPostImageActivity";

    //views
    private Button saveButton;
    private ImageButton addImageButton;
    private Spinner celebSpinner, postSpinner;
    private TextView postDescTextView, postCountTextView;

    //database storage
    private StorageReference mPhotoStorage;
    private DatabaseReference mBaseDatabase, mCelebDatabase, mPhotoDatabase, mCelebPostsDatabase, mPostPhotoDatabase;

    //select celeb spinner items:
    private List<String> celebNamesList, postDescList;
    private String celebId, postId;
    ArrayAdapter<String> celebSpinnerAdapter, postSpinnerAdapter;

    // bitmap
    private Bitmap fullSizeBitmap;

    //progress dialog
    private ProgressDialog dialog;

    //image picker:
    private List<Uri> imageUriList;

    //recycler View:
    private RecyclerView mRecyclerView;
    private List<UploadItem> uploadItemList;
    private UploadListAdapter mAdapter;

    //post count:
    int postCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_image);

        //initializeVariables:
        initializeVariables();

        //set up spinner on Item selected listeners:
        setUpSpinnerItemSelectedListeners();

        //initialize list of celebs in spinner:
        addCelebNamesToSpinner();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addImageButton.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                selectImageFromGallery();
            }
        });

        //save To Firebase:
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                saveToFirebase();
            }
        });
    }

    private void initializeVariables() {

        //views
        saveButton = (Button) findViewById(R.id.addPostImageSaveButton);
        addImageButton = (ImageButton) findViewById(R.id.addPostImageButton);
        celebSpinner = (Spinner) findViewById(R.id.addPostImageCelebSpinner);
        postSpinner = (Spinner) findViewById(R.id.addPostImagePostSpinner);
        postDescTextView = (TextView) findViewById(R.id.addPostImagePostDescriptionTextView);
        postCountTextView = (TextView) findViewById(R.id.addPostImageCountTextView);

        //spinner adapters:
        celebNamesList = new ArrayList<>();
        celebSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, celebNamesList);
        celebSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebSpinner.setAdapter(celebSpinnerAdapter);

        postDescList = new ArrayList<>();
        postSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, postDescList);
        postSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSpinner.setAdapter(postSpinnerAdapter);

        //initialize firebase:
        mBaseDatabase = FirebaseDatabase.getInstance().getReference();
        mPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);
        mCelebDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_DATABASE);
        mCelebPostsDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_POSTS_DATABASE);
        mPostPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);
        mPhotoStorage = FirebaseStorage.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);

        //progress dialog:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);

        //image picker uri:
        imageUriList = new ArrayList<>();

        //recycler view:
        mRecyclerView = (RecyclerView) findViewById(R.id.addPostImageRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new UploadListAdapter(this);
        uploadItemList = new ArrayList<>();
        mRecyclerView.setAdapter(mAdapter);

    }


    private void setUpSpinnerItemSelectedListeners() {

        celebSpinner.post(new Runnable() {
            @Override
            public void run() {

                celebSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //get selected celeb name for saving in post:
                        String celebName = parent.getItemAtPosition(position).toString();

                        postDescList.clear();
                        retrievePostDescriptionList(celebName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        postSpinner.post(new Runnable() {
            @Override
            public void run() {
                postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String postDesc = postDescList.get(position);

                        //set post description
                        postDescTextView.setText(postDesc);
                        //retrieve post id from description:
                        retrievePostId(postDesc);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });


    }

    private void retrievePostId(String postDesc) {

        mCelebPostsDatabase.child(celebId).orderByChild("postDesc").equalTo(postDesc).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Post post = dataSnapshot.getValue(Post.class);
                postId = post.getPostId();

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

    private void retrievePostDescriptionList(String celebName) {

        //get celeb profile pic from database:
        mCelebDatabase.orderByChild("celebName").equalTo(celebName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get selected celeb:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                celebId = celeb.getCelebId();

                mCelebPostsDatabase.child(celebId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);

                        postDescList.add(post.getPostDesc());
                        postSpinnerAdapter.notifyDataSetChanged();
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

    private void selectImageFromGallery() {
        //start intent for selecting image from gallery
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, Constants.MAX_IMAGE_PICK); // set limit for image selection
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(new File(images.get(i).path));

                //saveImageToFirebase:
                saveImagesToFirebase(uri, i);


            }
        }
    }

    //methods to extract bitmap from uris:
    private Bitmap decodeSampledBitmapFromUri(Uri fileUri, int reqWidth, int reqHeight) throws IOException {
        InputStream stream = new BufferedInputStream(
                getApplicationContext().getContentResolver().openInputStream(fileUri));
        stream.mark(stream.available());
        BitmapFactory.Options options = new BitmapFactory.Options();
        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        stream.reset();

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(stream, null, options);
    }

    //method to calculate in sample size for extracting bitmaps from uri:
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void addCelebNamesToSpinner() {

        mCelebDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get celeb list from database:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                //add to celebNames List:
                celebNamesList.add(celeb.getCelebName());

                //notify the adapter to update:
                celebSpinnerAdapter.notifyDataSetChanged();
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


    private void saveImagesToFirebase(final Uri imageUri, int position) {

        //create full size and thumbnail bitmaps
        try {
            fullSizeBitmap = decodeSampledBitmapFromUri(imageUri, 1280, 1280);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get unique id for new photo:
        final String photoId = mPhotoDatabase.push().getKey();

        //compress bitmap for full upload
        ByteArrayOutputStream fullSizeStream = new ByteArrayOutputStream();
        fullSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream);

        mPhotoStorage.child(postId).child(photoId).putBytes(fullSizeStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get full size download url from firebase:
                final String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                //create new photo:
                Photo photo = new Photo(photoId, downloadUrl);

                //child updates to save post and photo:
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> photoValues = photo.toMap();

                childUpdates.put("/photos/" + photoId, photoValues);

                //save photo again in post_photos to retrieve post wise photos:
                childUpdates.put("/post_photos/" + postId + "/" + photoId, photoValues);

                //update all the children of the firebase database:
                mBaseDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //dismiss dialog if running
                        if (dialog.isShowing()) dialog.dismiss();

                        //create upload item:
                        UploadItem item = new UploadItem(downloadUrl, imageUri.getPath(), true);
                        uploadItemList.add(item);
                        mAdapter.updateList(uploadItemList);

                        //update post image count:
                        updatePostImageCount();

                    }
                }); // end of update children on complete listener
            }
        }); // end of full size success listener

    }

    private void updatePostImageCount() {

        mPostPhotoDatabase.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postCount = (int) dataSnapshot.getChildrenCount();

                postCountTextView.setText("Image Count: " + postCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //finally save to firebase:
    private void saveToFirebase() {

        //disable save button to avoid repetition in saving
        saveButton.setEnabled(false);


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/celeb_posts/" + celebId + "/" + postId + "/" + "postImages", postCount);
        childUpdates.put("/posts/" + postId + "/" + "postImages", postCount);

        mBaseDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //start welcome activity and finish:
                startActivity(new Intent(AddPostImageActivity.this, WelcomeActivity.class));
                finish();


            }
        });


    }
}
