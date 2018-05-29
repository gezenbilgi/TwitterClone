package com.faruksahin.twitterclone;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class searchFragment extends Fragment
{


    public searchFragment()
    {
    }
    private LinearLayout layout;
    private ListView searchList;
    private AppCompatEditText username;
    private AppCompatButton searchButton;

    //Adapter

    private ArrayList<String> usernameList;
    private ArrayList<String> mailList;
    private ArrayList<String> photoList;
    private searchAdapter adapter;
    public static searchModel searchModel;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        layout = view.findViewById(R.id.search_layout);
        layout.setOnClickListener(null);
        searchList = view.findViewById(R.id.search_listView);
        username = view.findViewById(R.id.search_username);
        searchButton = view.findViewById(R.id.search_frag_button);
        usernameList = new ArrayList<>();
        mailList = new ArrayList<>();
        photoList = new ArrayList<>();
        adapter = new searchAdapter(usernameList,mailList,photoList,getActivity());
        searchList.setAdapter(adapter);
        searchModel = new searchModel();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) 
            {
                try
                {
                    if(username.getText().toString().equals("")==true)
                    {
                        Toast.makeText(getActivity(), "Lütfen Aranacak Kullanıcının Adını Giriniz!", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        myRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                usernameList.clear();
                                mailList.clear();
                                photoList.clear();
                                for (DataSnapshot ds:dataSnapshot.getChildren())
                                {
                                    HashMap<String,String> hashMap = (HashMap<String,String>)ds.getValue();
                                    if(hashMap.get("Name").equals(username.getText().toString())==true)
                                    {
                                        usernameList.add(hashMap.get("Username"));
                                        mailList.add(hashMap.get("Mail"));
                                        photoList.add(hashMap.get("Profile_Photo"));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (Exception ex)
                {
                    Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
             searchModel.setMail(mailList.get(position));
             searchModel.setPhoto(photoList.get(position));
             startActivity(new Intent(getContext().getApplicationContext(),searchDetail.class));
            }
        });

        return view;
    }

}
