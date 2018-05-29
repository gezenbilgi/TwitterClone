package com.faruksahin.twitterclone;

import android.app.ProgressDialog;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class post_share extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView imageView;
    private TextInputEditText tweet;
    private Button share;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_share);
        toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.share_imageView);
        Picasso.with(getApplicationContext()).load(feedActivity.user.getPhoto()).into(imageView);
        tweet = findViewById(R.id.tweet_txt);
        share = findViewById(R.id.share_button);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Posts");
        progress = new ProgressDialog(post_share.this);
        share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if(tweet.getText().toString().equals("")==true)
                    {
                        Message("Lütfen Tweetinizi Yazınız!");
                    }else
                    {
                        progress.setMessage("Yükleniyor...");
                        progress.show();
                        String key = UUID.randomUUID().toString();
                        myRef.child(key).child("Username").setValue(feedActivity.user.getUsername());
                        myRef.child(key).child("Photo").setValue(feedActivity.user.getPhoto());
                        myRef.child(key).child("Tweet").setValue(tweet.getText().toString());
                        post_share.this.finish();
                    }
                    progress.dismiss();
                }catch (Exception ex)
                {
                    Message(ex.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        post_share.this.finish();
        return super.onOptionsItemSelected(item);
    }

    public void Message(String message)
    {
        ConstraintLayout lay = findViewById(R.id.post_Lay);
        Snackbar snackbar = Snackbar.make(lay,message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
