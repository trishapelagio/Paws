package com.mobdeve.s13.group38.paws;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileViewHolder> {
    private ArrayList<Post> dataPosts;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public PostProfileAdapter(ArrayList<Post> dataPosts){
        this.dataPosts = dataPosts;
    }

    @NonNull
    @NotNull
    @Override
    public PostProfileViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        initFirebase();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_profile_grid, parent, false);

        PostProfileViewHolder postProfileViewHolder = new PostProfileViewHolder(itemView);

        return postProfileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostProfileViewHolder holder, int position) {
        initFirebase();
        Post currentPost = this.dataPosts.get(position);
        this.mAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        reference.child(Collections.users.name()).child(currentPost.getUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("name").getValue().toString();
                String profilepic = snapshot.child("profilepic").getValue().toString();
                String[] splitString = currentPost.getDatePosted().split("\\s+");
                String date = splitString[0] + " " + splitString[1] + ", " + splitString[2] + " " + splitString[5];

                storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
                storageReference = storage.getReference().child("images");

                Glide.with(holder.getIvPostImage().getContext()).load(storageReference.child(currentPost.getPhoto())).into(holder.getIvPostImage());

                holder.getClPost().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        Intent i = new Intent(view.getContext(), ViewPostActivity.class);

                        i.putExtra("USERNAME", username);
                        i.putExtra("USER", currentPost.getUser());
                        i.putExtra("DESCRIPTION", currentPost.getDescription());
                        i.putExtra("COMMENTS", currentPost.getComments());
                        i.putExtra("LIKES", currentPost.getLikes().size()+"");
                        i.putExtra("TIME", date);
                        i.putExtra("PHOTO", currentPost.getPhoto());
                        i.putExtra("PROFILEPIC", profilepic);

                        view.getContext().startActivity(i);
                    }
                });

            }
            //                holder.setIvPostPhoto(R.drawable.seal);
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataPosts.size();
    }

    private void initFirebase(){

//        this.storageReference = storage.getReference();
        this.mAuth = FirebaseAuth.getInstance();
    }
}
