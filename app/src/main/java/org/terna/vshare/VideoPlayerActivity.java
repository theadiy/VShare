package org.terna.vshare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;

public class VideoPlayerActivity extends AppCompatActivity {

    private String TAG = "VideoPlayerActivity";

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.VideoPlayerVideoView);
        //playButton = findViewById(R.id.VideoPlayerPlayButton);
       //currentTextView = findViewById(R.id.VideoPlayerCurrentTimer);
        //durationTextView = findViewById(R.id.VideoPlayerDurationTimer);
        //videosPlayerProgressbar = findViewById(R.id.VideoPlayerProgressbar);
        bufferProgressbar = findViewById(R.id.bufferProgressbar);
        fullscreenButton = findViewById(R.id.VideoPlayerFullScreenImagebutton);


        //videosPlayerProgressbar.setMax(100);
        mediaController = new MediaController(this);
        mediaController.setBackgroundColor(Color.parseColor("#00FCAFAF"));

        isplaying = false;

        final Sprite sprite = SpriteFactory.create(Style.MULTIPLE_PULSE_RING);
        //sprite.setAnimationDelay(50000);
        sprite.setColor(android.graphics.Color.parseColor("#fa0842"));
        bufferProgressbar.setIndeterminateDrawable(sprite);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();




        //Type object = (Type) bundle.getSerializable("KEY");
        HomeViewModel feed = (HomeViewModel) bundle.getSerializable("feed");


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
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);




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

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


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
