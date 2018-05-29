package com.faruksahin.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity
{

    private ConstraintLayout loginLayout;
    private TextInputLayout usernameLay,passwordLay;
    private AppCompatEditText txtUsername,txtPassword;
    private TextView forgotPassword;
    private TextView register;
    private AppCompatButton signIn;
    private ProgressDialog progress;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String userData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userData = preferences.getString("Username","");
        if(userData.equals("")==false)
        {
            startActivity(new Intent(loginActivity.this,feedActivity.class));
            loginActivity.this.finish();
        }
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
            }
        };
        progress = new ProgressDialog(loginActivity.this);
        loginLayout = findViewById(R.id.loginLayout);
        loginLayout.setOnClickListener(null);
        forgotPassword = findViewById(R.id.forgotPassword);
        register = findViewById(R.id.register);
        signIn = findViewById(R.id.signInButton);
        usernameLay = findViewById(R.id.usernameLayout);
        passwordLay = findViewById(R.id.passwordLayout);
        txtUsername = findViewById(R.id.usernameEditText);

        txtUsername.setText("deneme@gmail.com");

        txtPassword = findViewById(R.id.passwordEditText);

        txtPassword.setText("03102593");

        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(txtUsername.getText().toString().equals("")==true)
                {
                    usernameLay.setHint("Lütfen E-Posta Adresinizi Giriniz!");
                }else
                {
                    usernameLay.setHint("E-Mail Adresi");
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(txtPassword.getText().toString().equals("")==true)
                {
                    passwordLay.setHint("Lütfen Şifrenizi Giriniz!");
                }else
                {
                    passwordLay.setHint("Şifreniz");
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progress.setMessage("Giriş Yapılıyor...");
                progress.show();
                //Giriş İşlemi
                try
                {
                    if(txtUsername.getText().toString().equals("")==true||txtPassword.getText().toString().equals("")==true)
                    {
                        setMessage("E-Posta ve Şifrenizi Eksiksiz Giriniz!");
                        progress.dismiss();
                    }else
                    {
                        mAuth.signInWithEmailAndPassword(txtUsername.getText().toString(),txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    preferences.edit().putString("Username",txtUsername.getText().toString()).apply();
                                    startActivity(new Intent(loginActivity.this,feedActivity.class));
                                    loginActivity.this.finish();
                                    progress.dismiss();
                                }else
                                {
                                    progress.dismiss();
                                    setMessage("Giriş Başarısız !");
                                }
                            }
                        });
                    }
                }catch (Exception ex)
                {
                    setMessage(ex.getLocalizedMessage());
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(loginActivity.this,register_activity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(txtUsername.getText().toString().equals("")==true)
                {
                    setMessage("Lütfen E-Posta Adresinizi Giriniz!");
                }else
                {
                    mAuth.sendPasswordResetEmail(txtUsername.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            setMessage("Şifre Yenileme Bağlantısı E-Postanıza Gönderildi!");
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            setMessage(e.getLocalizedMessage());
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void setMessage(String message)
    {
        Snackbar snackbar = Snackbar.make(loginLayout,message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
