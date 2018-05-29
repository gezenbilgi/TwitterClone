package com.faruksahin.twitterclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class notificationFragement extends Fragment
{


    public notificationFragement()
    {
    }
    private ListView lstView;
    private FirebaseDatabase databse;
    private DatabaseReference myRef;
    private TextView username;
    private TextView tweet;

    private ArrayList<String> usernameDatabase;
    private ArrayList<String> tweetDatabase;

    private notificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification_fragement, container, false);
        usernameDatabase = new ArrayList<>();
        tweetDatabase = new ArrayList<>();
        adapter = new notificationAdapter(usernameDatabase,tweetDatabase,getActivity());
        lstView = view.findViewById(R.id.notification_ListView);
        lstView.setAdapter(adapter);
        username = view.findViewById(R.id.notification_username);
        tweet = view.findViewById(R.id.notification_tweet);
        databse = FirebaseDatabase.getInstance();
        myRef = databse.getReference("Notification");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                usernameDatabase.clear();
                tweetDatabase.clear();
                try
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        HashMap<String,String> hashMap = (HashMap<String,String>)ds.getValue();
                        usernameDatabase.add(hashMap.get("User"));
                        tweetDatabase.add(hashMap.get("Tweet"));
                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception ex)
                {
                    Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

}
