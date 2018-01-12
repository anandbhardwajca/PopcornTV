package com.androidsalad.popcorntvapp.Activity.NewPost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.androidsalad.popcorntvapp.Activity.Welcome.WelcomeActivity;
import com.androidsalad.popcorntvapp.Model.Celeb;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //views
    private Button saveButton;
    private ImageButton addImageButton;
    private EditText postDescEditText, postViewsEditText;
    private Spinner celebNameSpinner;
    private ImageView celebImageView;

    //database storage
    private StorageReference mPhotoStorage;
    private DatabaseReference mPostDatabase, mBaseDatabase, mCelebDatabase, mPhotoDatabase;

    //select celeb spinner items:
    List<String> celebNames;
    ArrayAdapter<String> spinnerAdapter;
    private String celebName, celebThumbUrl, celebId;

    //bitmaps
    private Bitmap fullSizeBitmap, thumbnailBitmap;

    //dialog
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //initialize Variables:
        initializeVariables();

        //initialize list of celebs in spinner:
        addCelebNamesToSpinner();

        //add images from gallery:
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
            }
        });

        //finally save to firebase
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                saveToFirebase();
            }
        });

    }

    private void initializeVariables() {

        //initialize views
        celebImageView = (ImageView) findViewById(R.id.newPostCelebImageView);
        postDescEditText = (EditText) findViewById(R.id.newPostDescEditText);
        postViewsEditText = (EditText) findViewById(R.id.newPostViewsEditText);
        addImageButton = (ImageButton) findViewById(R.id.newPostImageButton);
        saveButton = (Button) findViewById(R.id.newPostSaveButton);

        //initialize firebase
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_DATABASE);
        mPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);
        mBaseDatabase = FirebaseDatabase.getInstance().getReference();
        mCelebDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_DATABASE);
        mPhotoStorage = FirebaseStorage.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);

        //spinner items
        celebNameSpinner = (Spinner) findViewById(R.id.newPostCelebNameSpinner);
        celebNameSpinner.setOnItemSelectedListener(this);
        celebNames = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, celebNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebNameSpinner.setAdapter(spinnerAdapter);

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);

    }

    private void addCelebNamesToSpinner() {

        mCelebDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get celeb list from database:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                //add to celebNames List:
                celebNames.add(celeb.getCelebName());

                //notify the adapter to update:
                spinnerAdapter.notifyDataSetChanged();
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

    //celeb Name Spinner implemented methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //get selected celeb name for saving in post:
        celebName = parent.getItemAtPosition(position).toString();

        //get celeb profile pic from database:
        mCelebDatabase.orderByChild("celebName").equalTo(celebName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get selected celeb:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                celebName = celeb.getCelebName();
                celebId = celeb.getCelebId();
                celebThumbUrl = celeb.getCelebThumbUrl();

                //display celeb image to confirm selection of celeb:
                Glide.with(getApplicationContext()).load(celebThumbUrl).into(celebImageView);
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

    //implemented method from spinner:
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //select images from gallery:
    private void selectImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.INT_ACTION_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.INT_ACTION_PICK && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            //create full size and thumbnail bitmaps
            try {
                fullSizeBitmap = decodeSampledBitmapFromUri(imageUri, 1280, 1280);
                thumbnailBitmap = decodeSampledBitmapFromUri(imageUri, 128, 128);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //display image
            Glide.with(this).load(imageUri).into(addImageButton);
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

    //finally save to firebase:
    private void saveToFirebase() {

        //disable save button to avoid repetition in saving
        saveButton.setEnabled(false);

        //generate unique post id for saving post:
        final String postId = mPostDatabase.push().getKey();

        //create unique photo Id:
        final String photoId = mPhotoDatabase.push().getKey();

        //compress bitmap for full upload
        ByteArrayOutputStream fullSizeStream = new ByteArrayOutputStream();
        fullSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream);

        //upload full size first to firebase storage:
        mPhotoStorage.child(postId).child(photoId).child("full").putBytes(fullSizeStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get full size download url from firebase:
                final String fullSizeUrl = taskSnapshot.getDownloadUrl().toString();

                //compress bitmap for full upload
                ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 50, thumbnailStream);

                mPhotoStorage.child(postId).child(photoId).child("thumb").putBytes(thumbnailStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //get thumbnail download url from firebase:
                        String thumbnailUrl = taskSnapshot.getDownloadUrl().toString();

                        //create new photo:
                        Photo photo = new Photo(photoId, fullSizeUrl);

                        //create new Post:
                        Post post = new Post(postId, celebId, celebName, celebThumbUrl, postDescEditText.getText().toString(), thumbnailUrl, Constants.getPostViews(postViewsEditText.getText().toString()), 1);

                        //child updates to save post and photo:
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> postValues = post.toMap();
                        Map<String, Object> photoValues = photo.toMap();

                        childUpdates.put("/posts/" + postId, postValues);
                        childUpdates.put("/photos/" + photoId, photoValues);

                        //save photo again in post_photos to retrieve post wise photos:
                        childUpdates.put("/post_photos/" + postId + "/" + photoId, photoValues);

                        //save post in celeb_posts to retrieve celeb wise posts:
                        childUpdates.put("/celeb_posts/" + celebId + "/" + postId, postValues);

                        //update all the children of the firebase database:
                        mBaseDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //dismiss dialog if running
                                if (dialog.isShowing()) dialog.dismiss();

                                //start welcome activity and finish:
                                startActivity(new Intent(NewPostActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        }); // end of update children on complete listener
                    }
                }); // end of thumbnail success listener

            }
        }); // end of full size success listener


    }

}
