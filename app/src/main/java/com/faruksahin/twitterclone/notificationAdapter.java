package com.faruksahin.twitterclone;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class notificationAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> username;
    private ArrayList<String> tweet;
    private Activity getContext;
    public notificationAdapter(ArrayList<String> usernameDatabase,ArrayList<String>tweetDatabase,Activity context)
    {
        super(context,R.layout.notification_listview,tweetDatabase);
        this.username = usernameDatabase;
        this.tweet = tweetDatabase;
        this.getContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = getContext.getLayoutInflater().inflate(R.layout.notification_listview,null,false);
        TextView usernametxt = view.findViewById(R.id.notification_username);
        TextView tweettxt = view.findViewById(R.id.notification_tweet);
        usernametxt.setText(username.get(position).toString()+" Tweetini Beğendi!");
        tweettxt.setText(tweet.get(position));
        return view;
    }
}
