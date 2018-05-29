package com.faruksahin.twitterclone;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class searchPostAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> tweet;
    private Activity getContext;
    public searchPostAdapter(ArrayList<String> tweetFromDatabase,Activity context)
    {
        super(context,R.layout.search_post,tweetFromDatabase);
        this.getContext=context;
        this.tweet = tweetFromDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = getContext.getLayoutInflater().inflate(R.layout.search_post,null,false);
        TextView post = view.findViewById(R.id.search_post);
        post.setText(tweet.get(position));
        return view;
    }
}
