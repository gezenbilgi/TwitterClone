package com.faruksahin.twitterclone;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.HashMap;

public class postFragment extends Fragment
{


    public postFragment()
    {
    }
    private postAdapter adapter;
    private ArrayList<String> databaseFromUser;
    private ArrayList<String> databaseFromPhoto;
    private ArrayList<String> databaseFromTweet;
    private ArrayList<String> keyDatabase;
    private ArrayList<String> notificationKey;

    private ListView listView;
    private ProgressDialog progress;
    private LikeButton likeButton;
    private ConstraintLayout layout;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference notRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_post,container,false);
        databaseFromUser = new ArrayList<>();
        databaseFromPhoto = new ArrayList<>();
        databaseFromTweet = new ArrayList<>();
        keyDatabase = new ArrayList<>();
        notificationKey = new ArrayList<>();
        adapter = new postAdapter(databaseFromPhoto,databaseFromUser,databaseFromTweet,keyDatabase,notificationKey,getActivity());
        listView = view.findViewById(R.id.listview_tweet);
        listView.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Posts");
        layout=view.findViewById(R.id.postFragLay);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if(feedActivity.user.getUsername().equals(databaseFromUser.get(position))==true)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext(),android.R.style.Theme_Material_Dialog_Alert);
                    alert.setTitle("Emin Misiniz?");
                    alert.setMessage("Bu Tweet'i Silmek İstiyor Musunuz?");
                    alert.setPositiveButton("Evet", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            myRef.child(keyDatabase.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() 
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) 
                                {
                                    Toast.makeText(getActivity(), "Tweetiniz Başarıyla Silindi!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).setNegativeButton("Hayır",null);
                    alert.show();
                }else
                {
                    Toast.makeText(getActivity(), "Sadece Kendi Tweetlerinizi Silebilirsiniz!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    databaseFromTweet.clear();
                    databaseFromPhoto.clear();
                    databaseFromUser.clear();
                    keyDatabase.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        HashMap<String,String> hashMap = (HashMap<String,String>)ds.getValue();
                        keyDatabase.add(ds.getKey());
                        databaseFromUser.add(hashMap.get("Username"));
                        databaseFromPhoto.add(hashMap.get("Photo"));
                        databaseFromTweet.add(hashMap.get("Tweet"));
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
        notRef = database.getReference("Notification");
        notRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                notificationKey.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    notificationKey.add(ds.getKey());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

}
