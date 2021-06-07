package com.example.pmpproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private Context context;
    private List<ClassData> listdata;
    private FirebaseUser firebaseUser;

    // RecyclerView recyclerView;
    public MyListAdapter(FirebaseUser firebaseUser, Context context, List<ClassData> listdata) {
        this.firebaseUser = firebaseUser;
        this.context = context;
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ClassData classData = listdata.get(position);
        holder.imageView.setImageResource(classData.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        holder.textView.setText(classData.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("id", classData.getId());
                intent.putExtra("name", classData.getName());
                intent.putExtra("description", classData.getDescription());
                intent.putExtra("favId", classData.getFavId());
                intent.putExtra("favorite", classData.isFavorite());
                context.startActivity(intent);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference pushRef;
                if (classData.isFavorite()) {
                    pushRef = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("fav_classes").child(classData.getFavId());
                    pushRef.removeValue();
                } else {
                    pushRef = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("fav_classes").push();
                    pushRef.setValue(classData.getId());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
