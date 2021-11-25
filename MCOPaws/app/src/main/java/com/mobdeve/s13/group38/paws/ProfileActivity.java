package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ProfileActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvAgeGender;
    private TextView tvBreed;
    private TextView tvDescription;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;

    private ImageButton ibHome;
    private ImageButton ibAdd;
    private ImageButton ibProfile;
    private ImageButton ibLogout;
    private ImageButton ibEdit;
    private ImageButton ibProfileChange;
    private ImageView ivProfile;
    private RecyclerView rvPostProfile;
    private PostProfileAdapter postProfileAdapter;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private ArrayList<Post> posts = new ArrayList<Post>();
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initComponents();
        rvPostProfile = findViewById(R.id.rv_posts_profile);
        rvPostProfile.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));

        postProfileAdapter = new PostProfileAdapter(posts);

        rvPostProfile.setAdapter(postProfileAdapter);
        initFirebase();
    }

    private void initComponents() {
        this.tvName = findViewById(R.id.tv_name_profile);
        this.tvAgeGender = findViewById(R.id.tv_age_gender_profile);
        this.tvBreed = findViewById(R.id.tv_breed_profile);
        this.tvDescription = findViewById(R.id.tv_description_profile);
        this.ivProfile = findViewById(R.id.iv_profilepic_profile);

        this.ibHome = findViewById(R.id.btn_home_profile);
        this.ibAdd = findViewById(R.id.btn_add_profile);
        this.ibProfile = findViewById(R.id.btn_profile_profile);
        this.ibLogout = findViewById(R.id.ib_logout);
        this.ibEdit = findViewById(R.id.ib_edit);

        this.ibProfileChange = findViewById(R.id.ib_profilechange_profile);
        this.ibHome.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(i);
        });

        this.ibAdd.setOnClickListener(view -> {
//            HERE
            Intent i = new Intent(ProfileActivity.this, EditImageActivity.class);
            startActivity(i);
        });

        this.ibProfile.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(i);
            finish();
        });

        this.ibEdit.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, EditActivity.class);
            startActivity(i);
        });

        this.ibLogout.setOnClickListener(view -> {
            this.mAuth.signOut();
            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        this.ibProfileChange.setOnClickListener(view -> {
            choosePicture();
        });
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference(Collections.posts.name());

        this.mAuth = FirebaseAuth.getInstance();
        this.user = this.mAuth.getCurrentUser();
        this.userId = this.user.getUid();

        if (getIntent().getExtras() != null && !getIntent().getStringExtra("USER").equals(this.userId)) {
            this.userId = getIntent().getStringExtra("USER");
            this.ibEdit.setVisibility(View.GONE);
            this.ibLogout.setVisibility(View.GONE);
            this.ibProfileChange.setVisibility(View.GONE);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        reference.child(Collections.users.name()).child(this.userId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String birthday = snapshot.child("birthday").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String breed = snapshot.child("breed").getValue().toString();
                String description = snapshot.child("description").getValue().toString();
                String profilepic = snapshot.child("profilepic").getValue().toString();

                try {

                    Date birthdayConv = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(birthday);
                    Calendar c = Calendar.getInstance();
                    c.setTime(birthdayConv);
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int date = c.get(Calendar.DATE);
                    LocalDate l1 = LocalDate.of(year, month, date);
                    LocalDate now1 = LocalDate.now();

                    Period diff = Period.between(l1, now1);
//                    System.out.println(l1 + ", " + now1 + ", " + diff.getYears());
                    String ageGender = diff.getYears() + " years, " + gender;
                    tvAgeGender.setText(ageGender);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tvName.setText(name);
                tvBreed.setText(breed);
                tvDescription.setText(description);

                if (!profilepic.equals("none")) {
                    storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
                    storageReference = storage.getReference().child("images");
                    Glide.with(ProfileActivity.this).load(storageReference.child(profilepic)).into(ivProfile);
                }

                reference.child(Collections.posts.name()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (posts.size() > 0) {
                            posts.clear();
                        }
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String user = ds.child("user").getValue().toString();
                            if (user.equals(userId)) {
                                String photo = ds.child("photo").getValue().toString();

                                ArrayList<String> likes = new ArrayList<String>();
                                for (DataSnapshot dsLikes : ds.child("likes").getChildren())
                                    likes.add(dsLikes.getValue().toString());
                                ArrayList<String> comments = new ArrayList<String>();
                                for (DataSnapshot dsComments : ds.child("comments").getChildren())
                                    comments.add(dsComments.getValue().toString());

                                String datePosted = ds.child("datePosted").getValue().toString();

                                String description = ds.child("description").getValue().toString();
                                posts.add(0, new Post(user, photo, likes, comments, datePosted, description));
                            }
                        }
//                        System.out.println(posts);

                        Comparator<Post> compareById = (Post o1, Post o2) -> new Date(o1.getDatePosted()).compareTo(new Date(o2.getDatePosted()));

                        posts.sort(compareById);

                        java.util.Collections.reverse(posts);
                        postProfileAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                postProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivProfile.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
        storageReference = storage.getReference();

        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        database.getReference(Collections.users.name())
                                .child(mAuth.getCurrentUser().getUid())
                                .child("profilepic")
                                .setValue(randomKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                                    startActivity(i);
                                    pd.dismiss();
                                }
                                else{
                                    Toast.makeText(ProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });
    }
}