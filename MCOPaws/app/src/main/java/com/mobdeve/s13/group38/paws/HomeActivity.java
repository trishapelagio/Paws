package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ImageButton ibHome;
    private ImageButton ibAdd;
    private ImageButton ibProfile;
    private ImageButton ibSearch;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private FirebaseUser user;
    private String userId;
    
    private ArrayList<Post> posts = new ArrayList<Post>();
    private PostHomeAdapter postHomeAdapter;
    private RecyclerView rvPostHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.initComponents();

        rvPostHome = findViewById(R.id.rv_home_posts);
        rvPostHome.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));

        postHomeAdapter = new PostHomeAdapter(posts);
        rvPostHome.setAdapter(postHomeAdapter);

        this.initFirebase();
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        posts = new ArrayList<>();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        posts = new ArrayList<>();
//    }

    private void initFirebase(){

        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference(Collections.posts.name());
        this.user = this.mAuth.getCurrentUser();
        this.userId = this.user.getUid();

//        String name = snapshot.child("name").getValue().toString();
//        String birthday = snapshot.child("birthday").getValue().toString();
//        String gender = snapshot.child("gender").getValue().toString();
//        String breed = snapshot.child("breed").getValue().toString();
//        String description = snapshot.child("description").getValue().toString();
        
        DatabaseReference reference = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(Collections.posts.name());

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (posts.size() > 0) {
                    posts.clear();
                }
                for(DataSnapshot ds: snapshot.getChildren()){
//                    posts.add(ds.child("user").getValue(Post.class));

                    String user = ds.child("user").getValue().toString();
                    String photo = ds.child("photo").getValue().toString();

                    ArrayList<String> likes = new ArrayList<String>();
                    for (DataSnapshot dsLikes: ds.child("likes").getChildren())
                        likes.add(dsLikes.getValue().toString());
                    ArrayList<String> comments = new ArrayList<String>();
                    for (DataSnapshot dsComments: ds.child("comments").getChildren())
                        comments.add(dsComments.getValue().toString());

                    String datePosted = ds.child("datePosted").getValue().toString();

                    String description = ds.child("description").getValue().toString();
//                    Post post = ;
                    posts.add(new Post(user, photo, likes, comments, datePosted, description));
                }
                Comparator<Post> compareById = (Post o1, Post o2) -> new Date(o1.getDatePosted()).compareTo( new Date(o2.getDatePosted()) );
                posts.sort(compareById);
                java.util.Collections.reverse(posts);

                postHomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void initComponents(){
        this.ibHome = findViewById(R.id.btn_home_home);
        this.ibAdd = findViewById(R.id.btn_add_home);
        this.ibProfile = findViewById(R.id.btn_profile_home);
        this.ibSearch = findViewById(R.id.btn_home_search);

        this.ibHome.setOnClickListener(view->{
            Intent i = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });

        this.ibAdd.setOnClickListener(view->{
            Intent i = new Intent(HomeActivity.this, EditImageActivity.class);
            startActivity(i);
        });

        this.ibProfile.setOnClickListener(view->{
            Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(i);
        });

        this.ibSearch.setOnClickListener(view->{
            Intent i = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(i);
        });
    }
}