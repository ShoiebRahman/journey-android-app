package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RecommendedPlacesAdapter extends RecyclerView.Adapter<RecommendedPlacesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RecommendedPlace place);
    }
    private List<RecommendedPlace> places;
    private Context context;
    private OnItemClickListener listener;

    public RecommendedPlacesAdapter(Context context, List<RecommendedPlace> places,OnItemClickListener listener) {
        this.context = context;
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedPlace place = places.get(position);
        holder.cityText.setText(place.getCity());
        holder.countryText.setText(place.getCountry());
        String imageUrl = place.getImageUrl();
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.itemView.setOnClickListener(v->listener.onItemClick(place));
        holder.wishlistIcon.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            long userId = sharedPreferences.getLong("userId", -1);

            if (userId != -1) {
                UserDatabaseHelper dbHelper = UserDatabaseHelper.getInstance(context);
                dbHelper.addWishlistItem(userId, place.getCity(), place.getCountry(), place.getImageUrl());
                Toast.makeText(context,R.string.addedWishlist, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.userLog, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cityText;
        public TextView countryText;
        public ShapeableImageView imageView;
        public ImageView wishlistIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            cityText = itemView.findViewById(R.id.destination_title);
            countryText = itemView.findViewById(R.id.destination_location);
            imageView = itemView.findViewById(R.id.destination_image);
            wishlistIcon = itemView.findViewById(R.id.wishlist_add);
        }
    }
}
