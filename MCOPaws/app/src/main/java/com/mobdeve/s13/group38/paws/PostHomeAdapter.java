package com.mobdeve.s13.group38.paws;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostHomeAdapter extends RecyclerView.Adapter<PostHomeViewHolder>{
    private ArrayList<Post> dataPosts;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private boolean liked = false;

    public PostHomeAdapter(ArrayList<Post> dataPosts){
        this.dataPosts = dataPosts;
    }

    @NonNull
    @NotNull
    @Override
    public PostHomeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        initFirebase();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_user_post, parent, false);


        PostHomeViewHolder postHomeViewHolder = new PostHomeViewHolder(itemView);
//
//        postHomeViewHolder.setLikeBtnOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //LIKE
//
//            }
//        });
//
//        postHomeViewHolder.getIbComment().setOnClickListener(view-> {
//
//        });

        return postHomeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostHomeViewHolder holder, int position) {
        initFirebase();
        Post currentPost = this.dataPosts.get(position);

        this.mAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        reference.child(Collections.users.name()).child(currentPost.getUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilepic = snapshot.child("profilepic").getValue().toString();
                if(!profilepic.equals("none")) {
                    storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
                    storageReference = storage.getReference().child("images");
                    Glide.with(holder.getIvPostPhoto().getContext()).load(storageReference.child(profilepic)).into(holder.getIvUserImage());
                }
                else{
                    holder.getIvUserImage().setImageResource(R.drawable.paw);
                }

                if(currentPost.getLikes().contains(mAuth.getCurrentUser().getUid())) {
                    holder.getIbLike().setColorFilter(Color.parseColor("#FF9800"));
                    liked = true;
                }
                else{
                    holder.getIbLike().setColorFilter(Color.parseColor("#262626"));
                    liked = false;
                }

                String username = snapshot.child("name").getValue().toString();
                if(!currentPost.getDescription().equals("")) {
                    holder.setTvCaptionUsername(username);
                    holder.setTvDescription(currentPost.getDescription());
                }
                else{
                    holder.getLlCaption().setVisibility(View.GONE);
                }
                holder.setTvComments(currentPost.getComments().size()+"");
                holder.setTvLikes(currentPost.getLikes().size()+"");
                holder.setTvUsername(username);

                String[] splitString = currentPost.getDatePosted().split("\\s+");
                String date = splitString[0] + " " + splitString[1] + ", " + splitString[2] + " " + splitString[5];
                holder.setTvTime(date);

                storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
                storageReference = storage.getReference().child("images");

                Glide.with(holder.getIvPostPhoto().getContext()).load(storageReference.child(currentPost.getPhoto())).into(holder.getIvPostPhoto());

                holder.getIbLike().setOnClickListener(view->{
                    ArrayList<String> updatedLikes;
                    liked = currentPost.getLikes().contains(mAuth.getCurrentUser().getUid());
                    if(liked) {
                        currentPost.getLikes().remove(mAuth.getCurrentUser().getUid());
                        holder.getIbLike().setColorFilter(Color.parseColor("#262626"));
                    }
                    else {
                        currentPost.getLikes().add(mAuth.getCurrentUser().getUid());
                        holder.getIbLike().setColorFilter(Color.parseColor("#FF9800"));
                    }
//                    liked = !liked;

                    holder.setTvLikes(currentPost.getLikes().size()+"");
                    updatedLikes = currentPost.getLikes();

                    database.getReference(Collections.posts.name())
                            .child(currentPost.getPhoto())
                            .child("likes")
                            .setValue(updatedLikes).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(holder.getIvPostPhoto().getContext(), "Description changed", Toast.LENGTH_SHORT).show();
//                                tvCaptionDescription.setText();
                            }
                            else{
//                                Toast.makeText(holder.getIvPostPhoto().getContext(), "Failed to change description", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });

                holder.getIvPostPhoto().setOnClickListener(new View.OnClickListener() {
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

                holder.getIvUserImage().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        Intent i = new Intent(view.getContext(), ProfileActivity.class);

                        i.putExtra("USER", currentPost.getUser());

                        view.getContext().startActivity(i);
                    }
                });

                holder.getTvUsername().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        Intent i = new Intent(view.getContext(), ProfileActivity.class);

                        i.putExtra("USER", currentPost.getUser());

                        view.getContext().startActivity(i);
                    }
                });

                holder.getIbComment().setOnClickListener(new View.OnClickListener() {
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
        this.storage = FirebaseStorage.getInstance("gs://mobdeve-paws.appspot.com");
        this.storageReference = storage.getReference();
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference(Collections.posts.name());
    }

}
