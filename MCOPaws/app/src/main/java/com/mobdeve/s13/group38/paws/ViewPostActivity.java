package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class ViewPostActivity extends AppCompatActivity {
    private ImageButton ibHome;
    private ImageButton ibAdd;
    private ImageButton ibProfile;
    private ImageButton ibLike;
    private String user;

    private ImageView ivUserImage;
    private TextView tvUsername;
    private TextView tvDatePosted;
    private ImageView ivPostPhoto;
    private TextView tvLikes;
    private TextView tvCaptionUsername;
    private TextView tvCaptionDescription;
    private ImageButton ibEdit;
    private LinearLayout llCaption;
    private ImageButton ibComment;
    private EditText etCommentText;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String description;
    private String photo;
    private ArrayList<String> comments = new ArrayList<>();
    private ArrayList<Comment> commentsRv = new ArrayList<>();
    private boolean liked = false;

    private CommentAdapter commentAdapter;
    private RecyclerView rvComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Intent i = getIntent();

        String username = i.getStringExtra("USERNAME");
        this.description = i.getStringExtra("DESCRIPTION");
        String likes = i.getStringExtra("LIKES");
        String time = i.getStringExtra("TIME");
        this.photo = i.getStringExtra("PHOTO");
        user = i.getStringExtra("USER");
        String profilepic = i.getStringExtra("PROFILEPIC");

        rvComments = findViewById(R.id.rv_comments_view);
        rvComments.setLayoutManager(new LinearLayoutManager(ViewPostActivity.this, LinearLayoutManager.VERTICAL, false));

        commentAdapter = new CommentAdapter(commentsRv);
        rvComments.setAdapter(commentAdapter);
        rvComments.setNestedScrollingEnabled(false);

        this.initFirebase();
        this.initComponents();

        storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
        storageReference = storage.getReference().child("images");

        Glide.with(this).load(storageReference.child(this.photo)).into(ivPostPhoto);

        if (user.equals(mAuth.getCurrentUser().getUid()))
            ibEdit.setVisibility(View.VISIBLE);

        if(!profilepic.equals("none"))
            Glide.with(this).load(storageReference.child(profilepic)).into(ivUserImage);
        else
            ivUserImage.setImageResource(R.drawable.paw);

        tvUsername.setText(username);
        tvDatePosted.setText(time);
        tvLikes.setText(likes);
         if(!description.equals("")){
            tvCaptionDescription.setText(this.description);
            tvCaptionUsername.setText(username);
         }
         else{
            llCaption.setVisibility(View.GONE);
         }
    }

    @SuppressLint("ShowToast")
    private void initComponents(){
        this.ibHome = findViewById(R.id.btn_home_view);
        this.ibAdd = findViewById(R.id.btn_add_view);
        this.ibProfile = findViewById(R.id.btn_profile_view);
        this.ibEdit = findViewById(R.id.ib_edit_view);
        this.ibLike = findViewById(R.id.ib_post_like_view);
        this.ibComment = findViewById(R.id.ib_comment);
        this.etCommentText = findViewById(R.id.et_caption_view);

        this.ivUserImage = findViewById(R.id.iv_home_user_image_view);
        this.tvUsername = findViewById(R.id.tv_post_username_view);
        this.tvDatePosted = findViewById(R.id.tv_post_time_view);
        this.ivPostPhoto = findViewById(R.id.iv_post_photo_view);
        this.tvLikes = findViewById(R.id.tv_post_like_view);
        this.tvCaptionUsername = findViewById(R.id.tv_post_caption_username_view);
        this.tvCaptionDescription = findViewById(R.id.tv_post_caption_view);
        this.llCaption = findViewById(R.id.ll_caption_view);

        this.ibHome.setOnClickListener(view->{
            Intent i = new Intent(ViewPostActivity.this, HomeActivity.class);
            startActivity(i);
        });

        this.ibAdd.setOnClickListener(view->{
            Intent i = new Intent(ViewPostActivity.this, EditImageActivity.class);
            startActivity(i);
        });

        this.ibProfile.setOnClickListener(view->{
            Intent i = new Intent(ViewPostActivity.this, ProfileActivity.class);
            startActivity(i);
        });

        this.ibEdit.setOnClickListener(view->{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(ViewPostActivity.this);
            edittext.setText(description);
            alert.setTitle("Edit your description");

            alert.setView(edittext);

            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    String YouEditTextValue = edittext.getText().toString().trim();
                    database.getReference(Collections.posts.name())
                            .child(photo)
                            .child("description")
                            .setValue(YouEditTextValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ViewPostActivity.this, "Description changed", Toast.LENGTH_SHORT).show();
                                description = YouEditTextValue;
                                tvCaptionDescription.setText(YouEditTextValue);
                            }
                            else{
                                Toast.makeText(ViewPostActivity.this, "Failed to change description", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();
        });

        ivUserImage.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), ProfileActivity.class);

            i.putExtra("USER", user);

            view.getContext().startActivity(i);
        });

        tvUsername.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), ProfileActivity.class);

            i.putExtra("USER", user);

            view.getContext().startActivity(i);
        });

        ibComment.setOnClickListener(view-> {
            String comment = etCommentText.getText().toString().trim();
            if(comment.equals(""))
                Toast.makeText(this, "You must input at least 1 character", Toast.LENGTH_SHORT);
            else{

                final String randomKey = UUID.randomUUID().toString();
                Comment comment_post = new Comment(comment, mAuth.getCurrentUser().getUid(), new Date().toString());
                database.getReference(Collections.comments.name())
                        .child(randomKey)
                        .setValue(comment_post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            comments.add(randomKey);
                            database.getReference(Collections.posts.name())
                                    .child(photo)
                                    .child("comments")
                                    .setValue(comments).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ViewPostActivity.this, "Comment Posted", Toast.LENGTH_SHORT);
                                    }
                                    else{
                                        Toast.makeText(ViewPostActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(ViewPostActivity.this, "Comment Posted", Toast.LENGTH_SHORT);
                        }
                        else{
                            Toast.makeText(ViewPostActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            etCommentText.setText("");

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        });
        // INITIALIZE RECYCLERVIEW HERE
    }
    private void initFirebase(){
        this.storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
        this.storageReference = storage.getReference();
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference();

        databaseReference.child(Collections.users.name()).child(photo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotUser) {
                databaseReference.child(Collections.posts.name()).child(photo).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotPost) {
                        ArrayList<String> likes = new ArrayList<String>();
                        for (DataSnapshot dsLikes: snapshotPost.child("likes").getChildren())
                            likes.add(dsLikes.getValue().toString());

                        if (comments.size() > 0)
                            comments.clear();

                        for (DataSnapshot dsComments: snapshotPost.child("comments").getChildren())
                            comments.add(dsComments.getValue().toString());

                        if (commentsRv.size() > 0)
                            commentsRv.clear();

                        tvLikes.setText(likes.size() + "");
                        for(String comment: comments) {
                            databaseReference.child(Collections.comments.name()).child(comment).addValueEventListener(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshotComments) {

                                    String user = snapshotComments.child("user").getValue().toString();
                                    String comment = snapshotComments.child("comment").getValue().toString();
                                    String datePosted = snapshotComments.child("datePosted").getValue().toString();
                                    commentsRv.add(new Comment(comment, user, datePosted));

                                    Comparator<Comment> compareById = (Comment o1, Comment o2) -> new Date(o1.getDatePosted()).compareTo(new Date(o2.getDatePosted()));
                                    commentsRv.sort(compareById);

                                    commentAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }

                        if(likes.contains(mAuth.getCurrentUser().getUid())) {
                            ibLike.setColorFilter(Color.parseColor("#FF9800"));
                            liked = true;
                        }
                        else{
                            ibLike.setColorFilter(Color.parseColor("#262626"));
                            liked = false;
                        }

                        ibLike.setOnClickListener(view->{
                            ArrayList<String> updatedLikes;
                            liked = likes.contains(mAuth.getCurrentUser().getUid());
                            if(liked) {
                                likes.remove(mAuth.getCurrentUser().getUid());
                                ibLike.setColorFilter(Color.parseColor("#262626"));
                            }
                            else {
                                likes.add(mAuth.getCurrentUser().getUid());
                                ibLike.setColorFilter(Color.parseColor("#FF9800"));
                            }
//                    liked = !liked;
                            tvLikes.setText(likes.size()+"");
                            updatedLikes = likes;

                            database.getReference(Collections.posts.name())
                                    .child(photo)
                                    .child("likes")
                                    .setValue(updatedLikes).addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                        else{
                                        }
                                    });
                        });
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                commentAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
