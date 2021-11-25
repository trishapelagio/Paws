package com.mobdeve.s13.group38.paws;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class PostSearchViewHolder extends RecyclerView.ViewHolder{
    private ImageView ivPostImage;
    private ConstraintLayout clPost;

    public PostSearchViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.ivPostImage = itemView.findViewById(R.id.iv_grid_photo);
        this.clPost = itemView.findViewById(R.id.iv_profile_grid_photo);
    }

    public ImageView getIvPostImage() {
        return ivPostImage;
    }

    public void setIvPostImage(int userImage){
        this.ivPostImage.setImageResource(userImage);
    }

    public ConstraintLayout getClPost() {
        return this.clPost;
    }
}
