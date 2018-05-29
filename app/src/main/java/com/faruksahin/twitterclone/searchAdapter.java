package com.faruksahin.twitterclone;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> username;
    private ArrayList<String> mail;
    private ArrayList<String> photo;
    private Activity getContext;

    public searchAdapter(ArrayList<String> usernameFromDatabase, ArrayList<String> mailFromDatabase, ArrayList<String> photoFromDatabase,Activity activity)
    {
        super(activity,R.layout.search_list,usernameFromDatabase);

        this.username = usernameFromDatabase;
        this.mail = mailFromDatabase;
        this.photo = photoFromDatabase;
        this.getContext = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View custom = getContext.getLayoutInflater().inflate(R.layout.search_list,null,false);
        CircleImageView imageView = custom.findViewById(R.id.search_list_photo);
        TextView name = custom.findViewById(R.id.search_username_textView);
        TextView Mail = custom.findViewById(R.id.search_mail_textView);
        Picasso.with(getContext).load(photo.get(position)).into(imageView);
        name.setText(username.get(position));
        Mail.setText(mail.get(position));
        return custom;
    }
}
