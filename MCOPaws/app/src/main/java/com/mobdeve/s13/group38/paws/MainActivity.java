package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "100";
    //    private static final String TAG = ;
    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 444;
    private ImageButton ibGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.initFirebase();
        this.initComponents();
        this.googleSignIn();

//        ibGoogle.setOnClickListener(new View.onClickListener());
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initComponents(){
        this.ibGoogle = findViewById(R.id.btn_google);
        this.tvRegister = findViewById(R.id.tv_register_login);
        this.etEmail = findViewById(R.id.et_email_login);
        this.etPassword = findViewById(R.id.et_password_login);
        this.btnLogin = findViewById(R.id.btn_login);
        this.pbLogin = findViewById(R.id.pb_login);

        this.tvRegister.setOnClickListener(view->{
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        this.btnLogin.setOnClickListener(view->{
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            signIn(email,password);
        });


        this.ibGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });
    }

    private void googleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        this.pbLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            mAuth = FirebaseAuth.getInstance();
                            database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");

                            database.getReference().child(Collections.users.name()).addValueEventListener(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Boolean firstTime = true;
                                    for(DataSnapshot ds: snapshot.getChildren()){
                                        if(ds.child("email").getValue().toString().equals(user.getEmail())) {
                                            firstTime = false;
                                            break;
                                        }
                                    }

                                    if(firstTime) {
                                        Date datenow = new Date();

                                        String date = datenow.getMonth()+1 + "/" + datenow.getDate() + "/" + 2018;

                                        User user_input = new User(user.getEmail(), "", "Male", "", "", date, "", "none");

                                        database.getReference(Collections.users.name())
                                                .child(mAuth.getCurrentUser().getUid())
                                                .setValue(user_input).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    MainActivity.this.pbLogin.setVisibility(View.GONE);
//                                        Toast.makeText(MainActivity.this, "User Registration Registered", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    pbLogin.setVisibility(View.VISIBLE);
                                                } else {
                                                    MainActivity.this.pbLogin.setVisibility(View.GONE);
                                                    Toast.makeText(MainActivity.this, "User Registration Failed", Toast.LENGTH_SHORT).show();
                                                    pbLogin.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        pbLogin.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void signIn(String email, String password){
        this.pbLogin.setVisibility(View.VISIBLE);

        if(!email.isEmpty() && !password.isEmpty()) {
            this.mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                MainActivity.this.pbLogin.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            MainActivity.this.pbLogin.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
        }
    }
}