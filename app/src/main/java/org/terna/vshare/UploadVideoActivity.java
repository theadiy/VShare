package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
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


        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
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
                        StorageReference videoRef = storageRef.child("videos/"+videoId);
                        UploadTask uploadTask = videoRef.putFile(fileUri);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(getBaseContext(),"Video uploaded succesfully.",Toast.LENGTH_LONG).show();


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
                        });


                        //videoId =
                        //videoName =
                        //videoDescription =


                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getApplicationContext(), fileUri);
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInMillisec = Long.parseLong(time );
                        retriever.release();
                        videoTimeDuration = MilliSecToMinAndSecs(timeInMillisec);


                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                        videoUploadDate = simpleDateFormat1.format(new Date());


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
                    Toast.makeText(getBaseContext(),"Please Enter Video Details!",Toast.LENGTH_LONG).show();

                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case UPLOAD_VIDEO:
                fileUri = data.getData();
                //Log.e(TAG,"Date ------------------------------"+format);


                break;




        }


    }
    // Convert Video Duration from Millisec to Min and secs
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
}
