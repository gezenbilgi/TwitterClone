package com.faruksahin.twitterclone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class register_activity extends AppCompatActivity
{

    private Toolbar toolbar;
    private LinearLayout registerLay;
    private AppCompatEditText nameSurname,username,mail,password;
    private CircleImageView imageView;
    private Button signUp;
    private Uri uri = null;
    private ArrayList<String> users;
    private ProgressDialog progress;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
            }
        };
        toolbar = findViewById(R.id.registerToolbar);
        registerLay = findViewById(R.id.registerLay);
        registerLay.setOnClickListener(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameSurname = findViewById(R.id.register_name_edittext);
        username = findViewById(R.id.register_name_edittext);
        mail = findViewById(R.id.register_mail_edittext);
        password = findViewById(R.id.register_password_edittext);
        imageView = findViewById(R.id.register_profile_photo);
        signUp = findViewById(R.id.register_signUp);
        users = new ArrayList<>();
        progress = new ProgressDialog(register_activity.this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    HashMap<String,String> databaseUsers = (HashMap<String,String>) ds.getValue();
                    users.add(databaseUsers.get("Username").toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ActivityCompat.checkSelfPermission(register_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),1);
                }else
                {
                    ActivityCompat.requestPermissions(register_activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if(uri==null||nameSurname.getText().toString().equals("")==true||username.getText().toString().equals("")==true||mail.getText().toString().equals("")==true||password.getText().toString().equals("")==true)
                    {
                        Message("Lütfen Verileri Eksiksiz Giriniz!");
                    }else
                    {
                        boolean kontrol = false;
                        for (String data: users)
                        {
                            Toast.makeText(register_activity.this, data.toString(), Toast.LENGTH_SHORT).show();
                            if(username.getText().toString().equals(data) ==true)
                            {
                                kontrol = true;
                                break;
                            }
                        }
                        if(kontrol == false)
                        {
                            progress.setMessage("Kayıt Olunuyor...");
                            progress.show();
                            mAuth.createUserWithEmailAndPassword(mail.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    final String key  = UUID.randomUUID().toString();
                                    StorageReference photoRef = mStorageRef.child("Images/"+key+".jpg");
                                    photoRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            String url = taskSnapshot.getDownloadUrl().toString();
                                            myRef.child(key).child("Name").setValue(nameSurname.getText().toString());
                                            myRef.child(key).child("Username").setValue(username.getText().toString());
                                            myRef.child(key).child("Mail").setValue(mail.getText().toString());
                                            myRef.child(key).child("Password").setValue(password.getText().toString());
                                            myRef.child(key).child("Profile_Photo").setValue(url);
                                            myRef.onDisconnect();

                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Message(e.getLocalizedMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Message(e.getLocalizedMessage());
                                }
                            });
                            progress.dismiss();
                            new CountDownTimer(1 * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish()
                                {
                                    mAuth.signOut();
                                    register_activity.this.finish();
                                }
                            }.start();

                        }else
                        {
                            Message("Böyle Bir Kullanıcı Zaten Mevcut!");
                        }
                    }
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
        register_activity.this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 2)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),1);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            if(data!=null)
            {
                uri = data.getData();
                imageView.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void Message(String message)
    {
        Snackbar snack = Snackbar.make(registerLay,message,Snackbar.LENGTH_LONG);
        snack.show();
    }

    @Override
    protected void onStart()
    {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }
}
