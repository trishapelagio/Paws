package com.mobdeve.s13.group38.paws;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;

public class PostHomeViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivUserImage;
    private TextView tvUsername;
    private TextView tvTime;
    private TextView tvLikes;
    private TextView tvComments;
    private TextView tvDescription;
    private TextView tvCaptionUsername;
    private ImageView ivPostPhoto;

    private LinearLayout llPost;
    private LinearLayout llCaption;
    private ImageButton ibLike;
    private ImageButton ibComment;

    public PostHomeViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.ivUserImage = itemView.findViewById(R.id.iv_home_user_image);
        this.tvUsername = itemView.findViewById(R.id.tv_post_username);
        this.tvTime = itemView.findViewById(R.id.tv_post_time);
        this.tvLikes = itemView.findViewById(R.id.tv_post_like);
        this.tvComments = itemView.findViewById(R.id.tv_post_comments);
        this.tvDescription = itemView.findViewById(R.id.tv_post_caption);
        this.tvCaptionUsername = itemView.findViewById(R.id.tv_post_caption_username);
        this.llPost = itemView.findViewById(R.id.ll_post);
        this.llCaption = itemView.findViewById(R.id.ll_caption);
        this.ivPostPhoto = itemView.findViewById(R.id.iv_post_photo);
        this.ibLike = itemView.findViewById(R.id.btn_post_like);
        this.ibComment = itemView.findViewById(R.id.btn_post_comment);
    }

    public void setIvUserImage(int userImage) {
        this.ivUserImage.setImageResource(userImage);
    }

    public ImageView getIvPostPhoto(){
//        this.ivPostPhoto.setImageResource(userPhoto);
        return ivPostPhoto;
    }

    public TextView getTvUsername(){
        return tvUsername;
    }

    public void setTvUsername(String username) {
        this.tvUsername.setText(username);
    }

    public void setTvTime(String tvTime) {
        this.tvTime.setText(tvTime);
    }

    public void setTvLikes(String likes) {
        this.tvLikes.setText(likes);
    }

    public void setTvComments(String comments) {
        this.tvComments.setText(comments);
    }

    public void setTvDescription(String description) {
        this.tvDescription.setText(description);
    }

    public void setTvCaptionUsername(String captionUsername) {
        this.tvCaptionUsername.setText(captionUsername);
    }

    public LinearLayout getLlPost(){
        return this.llPost;
    }

    public LinearLayout getLlCaption(){
        return this.llCaption;
    }

    public ImageButton getIbComment() {
        return this.ibComment;
    }

    public ImageView getIvUserImage() {
        return this.ivUserImage;
    }

    public ImageButton getIbLike() {
        return this.ibLike;
    }

    public void setLikeBtnOnClickListener(View.OnClickListener onClickListener) {
        this.ibLike.setOnClickListener(onClickListener);
    }

}



