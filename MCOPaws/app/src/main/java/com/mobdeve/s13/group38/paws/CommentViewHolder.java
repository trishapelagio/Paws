package com.mobdeve.s13.group38.paws;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private TextView tvUsername;
    private TextView tvComment;
    private LinearLayout llComment;


    public CommentViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.tvUsername = itemView.findViewById(R.id.tv_comment_user);
        this.tvComment = itemView.findViewById(R.id.tv_comment_text);
        this.llComment = itemView.findViewById(R.id.ll_comment);
    }

    public TextView getTvUsername() {
        return tvUsername;
    }

    public TextView getTvComment() {
        return tvComment;
    }

    public LinearLayout getLlComment() {
        return llComment;
    }

    public void setTvUsername(String username){
        this.tvUsername.setText(username);
    }

    public void setTvComment(String comment){
        this.tvComment.setText(comment);
    }
}
