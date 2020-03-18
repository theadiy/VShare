package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class UploadVideoActivity extends AppCompatActivity {

    private static String TAG = "UploadVideoActivity";
    Button uploadVideoButton;
    Intent dataFromPreviousActivity;
    FirebaseStorage storage = getInstance();
    StorageReference storageRef = storage.getReference();
    private static final int UPLOAD_VIDEO = 500;
    static Uri fileUri = null;

    ProgressBar uploadProgress;
    TextView uploadPercentTextView;

    String videoId, videoName, videoDescription, owner, videoTimeDuration, videoUploadDate;
    int likeCount ;
    EditText videoNameEditText, videoDescriptionEditText;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        storageRef = getInstance().getReference();
        uploadVideoButton = findViewById(R.id.uploadButton);
        uploadProgress = findViewById(R.id.uploadProgressBar);
        uploadPercentTextView = findViewById(R.id.uploadPrcntTextView);
        uploadProgress.setVisibility(View.INVISIBLE);
        uploadPercentTextView.setVisibility(View.INVISIBLE);

        videoNameEditText = findViewById(R.id.uploadVideoNameEditText);
        videoDescriptionEditText = findViewById(R.id.uploadVideoDescription);





        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Log.e(TAG,"SHARED PREFS -- "+sharedPreferences.getString("uid",""));
        owner = sharedPreferences.getString("uid","");

       /* dataFromPreviousActivity = getIntent();
        final Uri fileUri = Uri.parse(dataFromPreviousActivity.getExtras().getString("videofileUri"));*/


        Intent fileIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        fileIntent.setType("video/*");
        startActivityForResult(fileIntent,UPLOAD_VIDEO);
        Toast.makeText(getBaseContext(),"Uploading video...",Toast.LENGTH_SHORT).show();



        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadProgress.setVisibility(View.VISIBLE);
                uploadPercentTextView.setVisibility(View.VISIBLE);

                //get video name and description
                if(!videoNameEditText.getText().toString().equals("") && !videoDescriptionEditText.getText().toString().equals("")){

                    videoName = videoNameEditText.getText().toString();
                    videoDescription = videoDescriptionEditText.getText().toString();

                    //Uploading video file
                    if(fileUri != null){
                        videoNameEditText.setEnabled(false);
                        videoDescriptionEditText.setEnabled(false);

                        uploadVideoButton.setVisibility(View.INVISIBLE);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String format = simpleDateFormat.format(new Date());
                        videoId = format+owner;
                        StorageReference videoRef = storageRef.child("videos/"+videoId+".mp4");
                        Log.e(TAG,"storare ref---"+videoRef.getPath());

                        UploadTask uploadTask = videoRef.putFile(fileUri);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(getBaseContext(),"video uploaded succesfully.",Toast.LENGTH_LONG).show();


                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int)(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                uploadProgress.setProgress(progress);
                                uploadPercentTextView.setText(""+progress+"% ");
                                Log.e(TAG,"Upload is " + progress + "% done");
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.e(TAG,"Upload is paused");
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                uploadVideoButton.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,"Demn something went wrong ------");
                            }
                        });


                        //videoId =
                        //videoName =
                        //videoDescription =

                        //video Duration
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getApplicationContext(), fileUri);
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInMillisec = Long.parseLong(time );
                        retriever.release();
                        videoTimeDuration = MilliSecToMinAndSecs(timeInMillisec);


                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                        videoUploadDate = simpleDateFormat1.format(new Date());

                        //Thumbnail

                        Log.e(TAG,"fileUri.getPath() ------------------------"+fileUri.getPath());
                        Log.e(TAG,"fileUri.getLastPathSegment() ------------------------"+fileUri.getLastPathSegment());
                        Log.e(TAG,"fileUri.toString() ------------------------"+fileUri.toString());
                        Log.e(TAG,"getPath() ------------------------"+getPath(getApplicationContext(),fileUri));

                        File VideoFile = new File(getPath(getApplicationContext(),fileUri));


                        Size size = new Size(512,384);

                        Bitmap bitmap = null;
                        bitmap = ThumbnailUtils.createVideoThumbnail(VideoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


                        //pushing thumbnail to firebase

                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReference("VideosThumbnail");

                        UploadTask uploadTask1 =storageReference.child(videoId+".jpg").putFile(getImageUri(getApplicationContext(),bitmap));

                        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Log.e(TAG,"Thumbnail uploaded Succesfully");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed to create video Thumbnail!",Toast.LENGTH_LONG).show();

                            }
                        });



                        likeCount = 0;

                        HashMap<Object, String> hashMap = new HashMap<>();
                        // put info in hashmap
                        hashMap.put("videoId",videoId);
                        hashMap.put("videoName",videoName);
                        hashMap.put("videoDescription",videoDescription);
                        hashMap.put("owner",owner);
                        hashMap.put("videoTimeDuration",videoTimeDuration);
                        hashMap.put("videoUploadDate",videoUploadDate);
                        hashMap.put("likeCount",String.valueOf(likeCount));



                        //Fb Db instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        //path to store data named "Videos"
                        DatabaseReference reference = database.getReference("Videos");
                        //put data within hashmap in database
                        reference.child(videoId).setValue(hashMap);

                    }
                    else {
                        Toast.makeText(getBaseContext(),"Please select a video file !",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(),"Please Enter video Details!",Toast.LENGTH_LONG).show();

                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case UPLOAD_VIDEO:
                if(data != null){
                    fileUri = data.getData();
                }else{
                    Toast.makeText(getBaseContext(), "Not selected any file !", Toast.LENGTH_SHORT).show();
                    }

                //Log.e(TAG,"Date ------------------------------"+format);
            // break;




        }


    }
    // Convert video Duration from Millisec to Min and secs
    String MilliSecToMinAndSecs(long millisec){

        String minSec = null;
        int min,sec;
        String sec1 = null;
        String min1 = null;

        sec = (int) millisec/1000;
        Log.e(TAG,"SECS --------------------------"+sec);
        min = sec / 60;
        sec = sec % 60;

        if(sec < 10){
            sec1 = "0"+sec;
        }else{
            sec1 = String.valueOf(sec);
        }
        minSec = min +":"+sec1;
        return minSec;
    }


    // bitmap to uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                Log.e(TAG,"getDoc ID"+id);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads/"), Long.valueOf(id));

                Log.e(TAG,"contentUri ;;;;;;;;;;;;;;-----------------"+contentUri.toString());
                Log.e(TAG,"contentUri -----------------"+contentUri.getLastPathSegment());
                Log.e(TAG,"conentUri -----------------"+contentUri.getPath());
                Log.e(TAG,"contentUri ;;;;;;;;;;;;;;-----------------"+contentUri.getAuthority());
                return getDataColumn(context, uri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                Log.e(TAG,"contentUri //////-----------------"+contentUri.toString());
                Log.e(TAG,"contentUri -----------------"+contentUri.getLastPathSegment());
                Log.e(TAG,"conetntUri -----------------"+contentUri.getPath());
                Log.e(TAG,"contentUri //////-----------------"+contentUri.getAuthority());
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {


            Log.e(TAG,"contentUri $$$$$$ scheme----------------"+uri.getScheme());
            Log.e(TAG,"contentUri $$$$$$----------------"+uri.toString());
            Log.e(TAG,"contentUri -----------------"+uri.getLastPathSegment());
            Log.e(TAG,"conentUri -----------------"+uri.getPath());
            Log.e(TAG,"contentUri $$$$$$-----------------"+uri.getAuthority());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };


        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                Log.e(TAG,"CURSOR ------------------------------------"+column_index);
                Log.e(TAG,"CURSOR ------------------------------------"+cursor.getString(column_index));
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



}
