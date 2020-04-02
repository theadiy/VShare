package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private String TAG = "VideoPlayerActivity";

    ProgressDialog pd;

    Toolbar toolbar;
    VideoView videoView;
    ImageView playButton;
    ImageView fullscreenButton;
    TextView currentTextView, durationTextView;
    ProgressBar bufferProgressbar;
    private SeekBar videosPlayerProgressbar;
    Uri videouri;
    boolean isplaying;


    int currentTime = 0;
    int durationTime = 0;

    MediaController mediaController;

    LinearLayout videoDetailsLinearLayout;
    TextView videoPlayerTitleTextView;
    TextView videoPlayerDescriptionTextView;
    TextView videoPlayerDateTextView;
    TextView videoPlayerUploadByTextView;

    Boolean isFullScreen;

    ImageView likeImageView;
    TextView likeTextView;
    TextView commentTextView;
    LinearLayout likeLinearLayout;
    LinearLayout commentLinearLayout;
    View line1, line2, line3;
    LinearLayout likeCommentLinearlayout;
    RelativeLayout commentMainRelativeLayout;
    Boolean isLiked;

    //comment views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cImageview;

    boolean nProcessComment = false;

    String videoId , myDp , myName , myEmail , myUid;

    RecyclerView recyclerView;

    List<CommentModel> commentList;
    AdapterComments adapterComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        //init views

        videoView = findViewById(R.id.VideoPlayerVideoView);
        //playButton = findViewById(R.id.VideoPlayerPlayButton);
       //currentTextView = findViewById(R.id.VideoPlayerCurrentTimer);
        //durationTextView = findViewById(R.id.VideoPlayerDurationTimer);
        //videosPlayerProgressbar = findViewById(R.id.VideoPlayerProgressbar);
        bufferProgressbar = findViewById(R.id.bufferProgressbar);
        fullscreenButton = findViewById(R.id.VideoPlayerFullScreenImagebutton);

        videoDetailsLinearLayout = findViewById(R.id.VideoDetailsLinearLayout);
        videoPlayerTitleTextView = findViewById(R.id.videoPlayerTitleTextView);
        videoPlayerDescriptionTextView = findViewById(R.id.videoPlayerDescriptionTextView);
        videoPlayerDateTextView = findViewById(R.id.videoPlayerDateTextView);
        videoPlayerUploadByTextView = findViewById(R.id.videoPlayerUploadByTextView);

        likeImageView = findViewById(R.id.VideoPlayerLikeImageView);
        likeTextView = findViewById(R.id.VideoPlayerLikeTextView);
        commentTextView = findViewById(R.id.VideoPlayerCommentTextView);
        likeLinearLayout = findViewById(R.id.likeLinearLayout);
        commentLinearLayout = findViewById(R.id.commentLinearLayout);
        line1 = findViewById(R.id.VideoPlayerLine1);
        line2 = findViewById(R.id.VideoPlayerLine2);
        line3 = findViewById(R.id.VideoPlayerLine3);
        likeCommentLinearlayout = findViewById(R.id.VideoPlayerLikeCommentLayout);
        commentMainRelativeLayout = findViewById(R.id.VideoPlayerCommentLayout);


        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cImageview = findViewById(R.id.cImageView);
        recyclerView = findViewById(R.id.recyclerView);


        checkUserStatus();

        loadUserInfo();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        Log.e("MSg.....1","video id: "+videoId);




        toolbar = findViewById(R.id.videoPlayerToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("VSHARE");
        

        //videosPlayerProgressbar.setMax(100);
        mediaController = new MediaController(this);
        mediaController.setBackgroundColor(Color.parseColor("#00FCAFAF"));

        isplaying = false;
        isFullScreen = false;
        isLiked = false;

        final Sprite sprite = SpriteFactory.create(Style.MULTIPLE_PULSE_RING);
        //sprite.setAnimationDelay(50000);
        sprite.setColor(android.graphics.Color.parseColor("#fa0842"));
        bufferProgressbar.setIndeterminateDrawable(sprite);

        Intent intent = this.getIntent();

        Bundle bundle = intent.getExtras();




        //Type object = (Type) bundle.getSerializable("KEY");
        final HomeViewModel feed = (HomeViewModel) bundle.getSerializable("feed");


        videoId = intent.getStringExtra("videoId");



        loadComment(feed.videoId);




        //videoView.setBackground(new BitmapDrawable(getResources(), feed.videoThumbnailImageView));

        Log.e(TAG,"feed ------"+feed.videoId);
        Log.e(TAG,"feed ------"+feed.videoName);
        Log.e(TAG,"feed ------"+feed.videoDescription);
        Log.e(TAG,"feed ------"+feed.videoUploadDate);
        Log.e(TAG,"feed ------"+feed.videoTimeDuration);


        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                durationTime = mp.getDuration()/1000;
                String durationTimeString = String.format("%02d:%02d", durationTime/60,durationTime%60);
                durationTextView.setText(durationTimeString);
            }
        });*/

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                styleMediaController(mediaController);


            }
        });

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("videos/"+feed.videoId+".mp4");

        Log.e(TAG,"We got videos for u --------------"+storageRef.getStream());

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e(TAG,"We got videos for u --------------"+uri);

                videouri = uri;

                if(videouri != null){

                    //videoView.setBackground(null);

                    videoView.setVideoURI(videouri);
                    videoView.requestFocus();
                    videoView.setMediaController(mediaController);

                    mediaController.setAnchorView(videoView);

                    videoView.start();
                    isplaying = true;
                    //bufferProgressbar.setVisibility(View.INVISIBLE);
                    //playButton.setImageResource(R.mipmap.pause);
                    videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            Log.e(TAG,"tararararara...."+what+" "+extra);
                            switch (what) {
                                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                                    bufferProgressbar.setVisibility(View.GONE);
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                                    bufferProgressbar.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                    bufferProgressbar.setVisibility(View.GONE);
                                    return true;
                                }
                            }

                            return false;
                        }
                    });
                }
            }
        });

        /*playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplaying){
                    videoView.pause();
                    isplaying = false;
                    //playButton.setImageResource(R.mipmap.play_button);
                }else{
                    videoView.start();
                    isplaying = true;
                    //playButton.setImageResource(R.mipmap.pause);
                }
            }
        });*/


        likeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLiked){
                    likeImageView.setImageResource(R.mipmap.dislike_1);
                    isLiked = false;
                    final DatabaseReference databaseReferenceLike = FirebaseDatabase.getInstance().getReference().child("Videos/"+feed.videoId);
                    databaseReferenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG,"DISLIKE-------- "+dataSnapshot.child("likeCount").getValue());
                            int like = Integer.parseInt(dataSnapshot.child("likeCount").getValue().toString()) - 1;
                            Log.e(TAG,"DISLIKE-------- "+like);

                            databaseReferenceLike.child("likeCount").setValue(Integer.toString(like));
                            likeTextView.setText("Likes "+like);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    likeImageView.setImageResource(R.mipmap.like_1);
                    isLiked = true;
                    final DatabaseReference databaseReferenceLike = FirebaseDatabase.getInstance().getReference().child("Videos/"+feed.videoId);
                    databaseReferenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG,"LIKE-------- "+dataSnapshot.child("likeCount").getValue());
                            int like = Integer.parseInt(dataSnapshot.child("likeCount").getValue().toString()) + 1;
                            Log.e(TAG,"LIKE-------- "+like);

                            databaseReferenceLike.child("likeCount").setValue(Integer.toString(like));
                            likeTextView.setText("Likes "+like);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isFullScreen){
                    Log.e(TAG,"++++++++++++++++++++++++ portrait");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getSupportActionBar().show();
                    isFullScreen = false;
                    videoDetailsLinearLayout.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    likeCommentLinearlayout.setVisibility(View.VISIBLE);
                    commentMainRelativeLayout.setVisibility(View.VISIBLE);
                }else{
                    Log.e(TAG,"++++++++++++++++++++++++ landscape");
                    getSupportActionBar().hide();
                    videoDetailsLinearLayout.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    line3.setVisibility(View.GONE);
                    likeCommentLinearlayout.setVisibility(View.GONE);
                    commentMainRelativeLayout.setVisibility(View.GONE);
                    isFullScreen = true;

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


                }


            }
        });

        //video details
        videoId = feed.videoId;
        videoPlayerTitleTextView.setText(feed.videoName);
        videoPlayerDescriptionTextView.setText(videoPlayerDescriptionTextView.getText().toString()+feed.videoDescription);
        videoPlayerDateTextView.setText(videoPlayerDateTextView.getText().toString()+feed.videoUploadDate);
        commentTextView.setText(commentTextView.getText().toString()+feed.commentCount);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("uid").equalTo(feed.owner);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Owner is -----------"+dataSnapshot.child(feed.owner).child("email"));

                if(dataSnapshot.child(feed.owner).child("name") != null){
                    videoPlayerUploadByTextView.setText(videoPlayerUploadByTextView.getText().toString()+dataSnapshot.child(feed.owner).child("name").getValue().toString());
                }else {
                    videoPlayerUploadByTextView.setText(videoPlayerUploadByTextView.getText().toString()+dataSnapshot.child(feed.owner).child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference databaseReferenceLike = FirebaseDatabase.getInstance().getReference().child("Videos/"+feed.videoId);
        databaseReferenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"LIKE-------- "+dataSnapshot.child("likeCount").getValue());
                int like = Integer.parseInt(dataSnapshot.child("likeCount").getValue().toString());
                Log.e(TAG,"LIKE-------- "+like);

                likeTextView.setText("Likes "+like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadComment(String videoId) {
        //Linear Layout for recycler


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //init comment list

        commentList = new ArrayList<>();

        //path of post

        Log.e("MSg.....","video id: "+ videoId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos").child(videoId).child("comments");



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    CommentModel commentModel = ds.getValue(CommentModel.class);

                    commentList.add(commentModel);
                    //setup adapter
                    adapterComments = new AdapterComments(getApplicationContext(),commentList);

                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadUserInfo() {
        //get current user info
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    myEmail = ""+ds.child("email").getValue();
                    myUid = ""+ds.child("uid").getValue();
                    myName = ""+ds.child("name").getValue();
                    myDp = ""+ds.child("image").getValue();

                    //set data

                    try {
                        Picasso.get().load(myDp).into(cImageview);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_profile_photo).into(cImageview);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            //user signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        }
        else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment");

        //get  data from comment
        String comment = commentEt.getText().toString().trim();
        //validating
        if(TextUtils.isEmpty(comment)){
            //if no value is entered
            Toast.makeText(this, "Comment is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        Log.e("MSg.....2","video id: "+videoId);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos").child(videoId).child("comments");
        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("com_id", timeStamp);
        hashMap.put("com_Des", comment);
        hashMap.put("createdOn", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);

        //put this data in db

        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(VideoPlayerActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VideoPlayerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });


    }


    private void updateCommentCount() {
        //comment count
        nProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos").child(videoId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (nProcessComment){
                    String comments = ""+ dataSnapshot.child("commentCount").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("commentCount").setValue(""+newCommentVal);
                    nProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    private void styleMediaController(View view) {
        if (view instanceof MediaController) {
            MediaController v = (MediaController) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                styleMediaController(v.getChildAt(i));
            }
        } else if (view instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) view;
            for (int i = 0; i < ll.getChildCount(); i++) {
                styleMediaController(ll.getChildAt(i));
            }
        } else if (view instanceof SeekBar) {
            ((SeekBar) view)
                    .getProgressDrawable()
                    .mutate()
                    .setColorFilter(
                            getResources().getColor(
                                    R.color.colorPrimary),
                            PorterDuff.Mode.SRC_IN);
            Drawable thumb = ((SeekBar) view).getThumb().mutate();
            thumb.setColorFilter(
                    getResources().getColor(R.color.colorPrimaryDark),
                        PorterDuff.Mode.SRC_IN);
        }
    }



}
