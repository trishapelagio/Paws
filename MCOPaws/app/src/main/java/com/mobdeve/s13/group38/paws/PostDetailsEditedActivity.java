package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class PostDetailsEditedActivity extends AppCompatActivity{
    private ImageButton ibHome;
    private ImageButton ibAdd;
    private ImageButton ibProfile;
    private ImageView ivPhoto;
    private Button btnPost;
    private EditText etDescription;

    private Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Post post;

    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details_edited);
        this.initComponents();
        this.initFirebase();

        Intent i = getIntent();
        this.imageUri = Uri.parse(i.getStringExtra("URI"));
        String path = i.getStringExtra("FILEPATH");

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            this.ivPhoto.setImageBitmap(bitmap);
        }
        catch(Exception e) {

        }

        this.initFirebase();
    }

    private void initComponents(){
        this.ibHome = findViewById(R.id.btn_home_post);
        this.ibAdd = findViewById(R.id.btn_add_post);
        this.ibProfile = findViewById(R.id.btn_profile_post);
        this.ivPhoto = findViewById(R.id.iv_photo_details);
        this.etDescription = findViewById(R.id.et_caption_details);

        this.btnPost = findViewById(R.id.btn_post_edit);
        // set iv photo from previous activity here

        this.btnPost.setOnClickListener(view->{
            uploadPicture();
//            Intent i = new Intent(PostDetailsEditedActivity.this, HomeActivity.class);
//            startActivity(i);
        });

        this.ibHome.setOnClickListener(view->{
            Intent i = new Intent(PostDetailsEditedActivity.this, HomeActivity.class);
            startActivity(i);
        });

        this.ibAdd.setOnClickListener(view->{
            Intent i = new Intent(PostDetailsEditedActivity.this, EditImageActivity.class);
            startActivity(i);
        });

        this.ibProfile.setOnClickListener(view->{
            Intent i = new Intent(PostDetailsEditedActivity.this, ProfileActivity.class);
            startActivity(i);
        });
    }

    private void initFirebase(){
        this.storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
        this.storageReference = storage.getReference();
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference(Collections.posts.name());
    }

    private void uploadPicture(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        System.out.println("Filename = " + randomKey);
        StorageReference riversRef = storageReference.child("images/"+randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        description = etDescription.getText().toString().trim();

                        ArrayList<String> filler = new ArrayList<>();
                        post = new Post(mAuth.getCurrentUser().getUid(), randomKey, filler, filler, new Date().toString(), description);

                        databaseReference
                                .child(randomKey)
                                .setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PostDetailsEditedActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PostDetailsEditedActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(PostDetailsEditedActivity.this, "Failed to Post", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
}

