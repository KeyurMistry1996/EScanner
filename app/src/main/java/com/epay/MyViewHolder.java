package com.epay;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView imageView;
    CardView cardView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.titleData);
        imageView= itemView.findViewById(R.id.deletebtn);
        cardView = itemView.findViewById(R.id.card);
    }
}
