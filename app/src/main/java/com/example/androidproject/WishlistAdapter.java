package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    private Context context;
    private List<Map<String, Object>> destinationList;
    private OnItemClickListener listener;

    public WishlistAdapter(Context context, List<Map<String, Object>> destinationList, OnItemClickListener listener) {
        this.context = context;
        this.destinationList = destinationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = destinationList.get(position);
        holder.destinationTitle.setText((String) item.get("City"));
        holder.destinationLocation.setText((String) item.get("Country"));
        String imageUrl = (String) item.get("ImageURL");
        Picasso.get().load(imageUrl).into(holder.destinationImage);
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView destinationImage;
        public TextView destinationTitle;
        public TextView destinationLocation;
        public ImageView deleteButton;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            destinationImage = itemView.findViewById(R.id.destination_image);
            destinationTitle = itemView.findViewById(R.id.destination_title);
            destinationLocation = itemView.findViewById(R.id.destination_location);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
