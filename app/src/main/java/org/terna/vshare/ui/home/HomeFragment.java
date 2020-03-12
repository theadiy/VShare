package org.terna.vshare.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.terna.vshare.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    RecyclerView feedRecyclerView;
    FeedAdapter myAdapter;
    ArrayList<HomeViewModel> homeViewModels = new ArrayList<>();
    private DatabaseReference feedsdatabaseReference;
    private StorageReference feedsstorageReference;
    private DatabaseReference postDatabase;
    private FirebaseStorage postStorage;
    Bitmap thumbnail;
    ProgressBar spinKitView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //Loading screen
        spinKitView = root.findViewById(R.id.spin_kit);
        final Sprite sprite = SpriteFactory.create(Style.MULTIPLE_PULSE_RING);
        //sprite.setAnimationDelay(50000);
        sprite.setColor(android.graphics.Color.parseColor("#fa0842"));
        spinKitView.setIndeterminateDrawable(sprite);

        //recycler view
        feedRecyclerView = root.findViewById(R.id.feedRecyclerView);


        feedRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        myAdapter = new FeedAdapter();
        feedRecyclerView.setAdapter(myAdapter);

        //retrieving feeds
        postStorage = FirebaseStorage.getInstance();
        postDatabase = FirebaseDatabase.getInstance().getReference();

        feedsdatabaseReference = FirebaseDatabase.getInstance().getReference("Videos");

        feedsstorageReference = FirebaseStorage.getInstance().getReference("VideosThumbnail");
        feedsdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot feedsSnapshot : dataSnapshot.getChildren()){

                    final HashMap feedHashmasp = ((HashMap) feedsSnapshot.getValue());
                    final HomeViewModel newFeed = new HomeViewModel();
                    thumbnail = null;


                    newFeed.videoName = feedHashmasp.get("videoName").toString();
                    newFeed.videoDescription = feedHashmasp.get("videoDescription").toString();
                    newFeed.videoUploadDate = feedHashmasp.get("videoUploadDate").toString();
                    newFeed.videoTimeDuration = feedHashmasp.get("videoTimeDuration").toString();
                    newFeed.owner = feedHashmasp.get("owner").toString();
                    newFeed.likeCount = feedHashmasp.get("likeCount").toString();
                    newFeed.videoName = feedHashmasp.get("videoName").toString();
                    newFeed.videoId = feedHashmasp.get("videoId").toString();
                    Log.e(TAG,"VIDEO ID -----------"+newFeed.videoId);


                    feedsstorageReference.child(feedHashmasp.get("videoId").toString()+".jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            thumbnail= BitmapFactory.decodeByteArray(bytes,0,bytes.length);


                            //Log.e(TAG,"Bitmap is not null -----"+newFeed.videoThumbnailImageView.getByteCount());
                            newFeed.videoThumbnailImageView = thumbnail;

                            if(newFeed != null && newFeed.videoThumbnailImageView != null){
                                homeViewModels.add(newFeed);

                                //sorting
                                Collections.reverse(homeViewModels);

                                myAdapter.notifyDataSetChanged();
                                if(
                                        myAdapter.getItemCount() > 0) {
                                    sprite.stop();
                                    spinKitView.setVisibility(View.INVISIBLE);
                                }
                            }else
                            {
                                Log.e(TAG,"newfeed prolem occured");
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"-----------------------------thumbnail problem"+e.getMessage());
                        }
                    });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return root;
    }

    class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        class FeedItem extends RecyclerView.ViewHolder{

            ImageView postCellThumbnailImageView;
            TextView postCellDurationTextView, postCellVideoTitleTextView, postCellVideouploadDateTextView, postCellLikeTextView, postCellCommentTextView;


            public FeedItem(@NonNull View itemView) {
                super(itemView);

                postCellThumbnailImageView = itemView.findViewById(R.id.postCellThumbnailImageView);
                postCellDurationTextView = itemView.findViewById(R.id.postCellDurationTextView);
                postCellVideoTitleTextView = itemView.findViewById(R.id.postCellVideoTitleTextView);
                postCellVideouploadDateTextView = itemView.findViewById(R.id.postCellVideouploadDateTextView);
                postCellLikeTextView = itemView.findViewById(R.id.postCellLikeTextView);
                postCellCommentTextView = itemView.findViewById(R.id.postCellCommentTextView);


            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_cell,parent,false);


            return new FeedItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

            final HomeViewModel feed = homeViewModels.get(position);
            ((FeedItem)holder).postCellVideoTitleTextView.setText(feed.videoName);
            ((FeedItem)holder).postCellVideouploadDateTextView.setText("Uploaded on  "+feed.videoUploadDate);
            ((FeedItem)holder).postCellDurationTextView.setText(feed.videoTimeDuration);
            ((FeedItem)holder).postCellLikeTextView.setText("Likes "+feed.likeCount);
            ((FeedItem)holder).postCellCommentTextView.setText("Comments "+feed.likeCount);
            ((FeedItem)holder).postCellThumbnailImageView.setImageBitmap(feed.videoThumbnailImageView);

            ((FeedItem)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(getContext()," you clicked "+((FeedItem)holder).postCellVideoTitleTextView.getText(),Toast.LENGTH_LONG).show();


                }
            });

        }

        @Override
        public int getItemCount() {
            return homeViewModels.size();
        }
    }



}