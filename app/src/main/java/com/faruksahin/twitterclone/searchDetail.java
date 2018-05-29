package com.faruksahin.twitterclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchDetail extends AppCompatActivity
{

    private Toolbar toolbar;
    private ListView listView;
    private TextView name,username;
    private CircleImageView photo;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference tweetRef;

    private searchPostAdapter adapter;
    private ArrayList<String> data;

    private String strUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        toolbar = findViewById(R.id.search_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = new ArrayList<>();
        adapter = new searchPostAdapter(data,searchDetail.this);
        listView = findViewById(R.id.Detail_ListView);
        listView.setAdapter(adapter);
        name = findViewById(R.id.detail_nameSurname);
        username = findViewById(R.id.detail_username);
        photo = findViewById(R.id.detail_Photo);
        Picasso.with(getApplicationContext()).load(searchFragment.searchModel.getPhoto()).into(photo);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        HashMap<String, String> hashMap = (HashMap<String,String>)ds.getValue();
                        if(hashMap.get("Mail").equals(searchFragment.searchModel.getMail())==true)
                        {
                            strUsername = hashMap.get("Username");
                            name.setText(hashMap.get("Name"));
                            username.setText(hashMap.get("Username"));
                        }
                    }
                }catch (Exception ex)
                {
                    Toast.makeText(searchDetail.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tweetRef = FirebaseDatabase.getInstance().getReference("Posts");
        tweetRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                data.clear();
                try
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        HashMap<String,String> hashMap = (HashMap<String, String>)ds.getValue();
                        if(hashMap.get("Username").equals(strUsername)==true)
                        {
                            data.add(hashMap.get("Tweet"));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception ex)
                {
                    Toast.makeText(searchDetail.this,ex.getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        searchDetail.this.finish();
        return super.onOptionsItemSelected(item);
    }
}
