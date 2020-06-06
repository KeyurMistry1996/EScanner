package com.epay;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class DisplayData extends RecyclerView.Adapter<DisplayData.ViewHolder> {
    private ArrayList<Data> dataLsit;
    private Context mContext;

    public DisplayData(Context context,ArrayList<Data> dataLsit ){
        this.mContext=context;
        this.dataLsit = dataLsit;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_recycler,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Data data = dataLsit.get(position);
        Log.i("Get Name",data.getName());
        holder.textView.setText(dataLsit.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String id = dataLsit.get(position).getId();
                Intent intent = new Intent(v.getContext(),DataShow.class);
              //  intent.putExtra("id",id);
                mContext.startActivity(intent);

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              FirebaseAuth  mAuth = FirebaseAuth.getInstance();
//              DatabaseReference billReference = FirebaseDatabase.getInstance().getReference().child("Users")
//                        .child(mAuth.getCurrentUser().getUid().toString()).child("Bills").child(data.getId());
//              billReference.removeValue();
//              dataLsit.remove(dataLsit.get(position));
//              notifyItemRemoved(position);
//              notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataLsit.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.titleData);
            imageView = itemView.findViewById(R.id.deletebtn);
        }
    }
}
