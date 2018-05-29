package com.faruksahin.twitterclone;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class postAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> dataPhoto;
    private ArrayList<String> dataUsername;
    private ArrayList<String> dataTweet;
    private ArrayList<String> key;
    private ArrayList<String> notKey;
    private Activity getContext;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private DatabaseReference notification;

    public  postAdapter(ArrayList<String> photoDatabase, ArrayList<String>usernameDatabase, ArrayList<String>tweetDatabase, ArrayList<String> keyDatabase, ArrayList<String> notKeyDatabase, Activity activity)
    {
        super(activity,R.layout.post_list,tweetDatabase);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Like");
        notification = database.getInstance().getReference("Notification");
        mAuth = FirebaseAuth.getInstance();
        this.key = keyDatabase;
        this.dataPhoto=photoDatabase;
        this.dataUsername=usernameDatabase;
        this.dataTweet=tweetDatabase;
        this.notKey = notKeyDatabase;
        this.getContext=activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final View view =getContext.getLayoutInflater().inflate(R.layout.post_list,null,false);
        CircleImageView photo = view.findViewById(R.id.post_photo);
        TextView username = view.findViewById(R.id.post_username);
        final TextView tweet = view.findViewById(R.id.post_tweet);
        Picasso.with(getContext).load(dataPhoto.get(position)).into(photo);
        username.setText("@"+dataUsername.get(position));
        tweet.setText(dataTweet.get(position));
        final LikeButton likeButton = view.findViewById(R.id.post_like_button);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds:dataSnapshot.child(key.get(position)).getChildren()) 
                {
                    if(ds.getKey().equals(mAuth.getUid())==true)
                    {
                        likeButton.setLiked(true);
                    }else
                    {
                        likeButton.setLiked(false);
                    }
                } 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) 
            {

            }
        });
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(final LikeButton likeButton)
            {
                Snackbar snackbar = Snackbar.make(view,"Tweet Başarıyla Beğenildi!",Snackbar.LENGTH_LONG).setAction("Geri", new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        myRef.child(key.get(position)).child(mAuth.getUid()).removeValue();
                        notification.child(notKey.get(position)).removeValue();
                        likeButton.setLiked(false);
                    }
                });
                snackbar.show();
                myRef.child(key.get(position)).child(mAuth.getUid()).setValue(1);
                String random = UUID.randomUUID().toString();
                notification.child(random).child("User").setValue(mAuth.getCurrentUser().getEmail());
                notification.child(random).child("Tweet").setValue(dataTweet.get(position));
            }

            @Override
            public void unLiked(LikeButton likeButton)
            {
                myRef.child(key.get(position)).child(mAuth.getUid()).removeValue();
                notification.child(notKey.get(position)).removeValue();
                likeButton.setLiked(false);
            }
        });
        return view;
    }
}
