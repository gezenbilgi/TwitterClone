package com.faruksahin.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class feedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,NotificationObserver, NotificationSubject
{
    private ArrayList<NotificationObserver> observers = new ArrayList<>();
    public static feedActivity instance;
    private SharedPreferences preferences;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    public static userModel user;
    private CircleImageView drawerPhoto;
    private TextView drawerMail,drawerName;
    private ProgressDialog progress;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    postFragment post = new postFragment();
                    fragTransaction.replace(R.id.feedLayout,post,"Home").commit();
                    return true;
                case R.id.navigation_search:
                    searchFragment search = new searchFragment();
                    fragTransaction.replace(R.id.feedLayout,search,"Search").commit();
                    return true;
                case R.id.navigation_notifications:
                    notificationFragement notification = new notificationFragement();
                    fragTransaction.replace(R.id.feedLayout,notification,"Notification").commit();
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
     
        super.onCreate(savedInstanceState);
        instance = this;
        notificationFragement notificationFragmentInstance = new notificationFragement();
        registerObserver(notificationFragmentInstance);
        setContentView(R.layout.activity_feed);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        View navigation_header = navigationView.getHeaderView(0);
        drawerPhoto = navigation_header.findViewById(R.id.drawer_ImageView);
        drawerMail = navigation_header.findViewById(R.id.drawer_Name);
        drawerName = navigation_header.findViewById(R.id.drawer_Mail);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        progress = new ProgressDialog(feedActivity.this);
        user = new userModel();
        progress.setMessage("Sunucuya Bağlanılıyor...");
        progress.show();
        userRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                try
                {
                    user.setMail(preferences.getString("Username",""));
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        HashMap<String,String> hashMap = (HashMap<String,String>)ds.getValue();
                        if(hashMap.get("Mail").toString().equals(user.getMail())==true)
                        {
                            user.setName(hashMap.get("Name").toString());
                            user.setPhoto(hashMap.get("Profile_Photo").toString());
                            user.setUsername(hashMap.get("Username").toString());
                        }
                    }
                    progress.dismiss();
                    drawerMail.setText(user.getMail());
                    drawerName.setText(user.getName());
                    Picasso.with(getApplicationContext()).load(user.getPhoto()).into(drawerPhoto);
                    changeHomeFrag();
                }catch (Exception ex)
                {
                    Toast.makeText(feedActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }
    private void changeHomeFrag()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        postFragment post = new postFragment();
        transaction.replace(R.id.feedLayout,post,"Home").commit();
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==R.id.action_share)
        {
            startActivity(new Intent(feedActivity.this,post_share.class));
        }
        if (id == R.id.action_logOut)
        {
            preferences.edit().remove("Username").apply();
            startActivity(new Intent(feedActivity.this,loginActivity.class));
            feedActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onNotificationReceived(String username, String tweet) {
        // Aquí puedes actualizar el feed con el nuevo tweet.
    }
    @Override
    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String username, String tweet) {
        for (NotificationObserver observer : observers) {
            observer.onNotificationReceived(username, tweet);
        }
    }
}
