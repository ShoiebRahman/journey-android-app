package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Wishlist extends AppCompatActivity implements WishlistAdapter.OnItemClickListener {
    private RecyclerView wishlistRecycler;
    private WishlistAdapter wishlistAdapter;
    private List<Map<String, Object>> destinationList;
    private BottomNavigationView navBar;

    private String tag = "Wishlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        wishlistRecycler = findViewById(R.id.wishlist_recycler);
        wishlistRecycler.setVerticalScrollBarEnabled(true);
        wishlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        navBar = findViewById(R.id.navBar);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        long userId = sharedPreferences.getLong("userId", -1);

        if (userId != -1) {
            UserDatabaseHelper dbHelper = UserDatabaseHelper.getInstance(this);
            destinationList = dbHelper.getWishlistItems(userId);
            Log.d(tag, "Wishlist loaded with " + destinationList.size() + " items");
        } else {
            destinationList = new ArrayList<>();
            Log.d(tag, "User not logged in");
        }

        wishlistAdapter = new WishlistAdapter(this, destinationList, this);
        wishlistRecycler.setAdapter(wishlistAdapter);

        navBar.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Wishlist.this, home_page.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(Wishlist.this, ProfileSection.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(int position) {
        Map<String, Object> item = destinationList.get(position);
        Intent intent = new Intent(this, LocationPage.class);
        intent.putExtra("City", (String) item.get("City"));
        intent.putExtra("Country", (String) item.get("Country"));
        intent.putExtra("ImageURL", (String) item.get("ImageURL"));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Map<String, Object> item = destinationList.get(position);
            String city = (String) item.get("City");
            String country = (String) item.get("Country");

            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            long userId = sharedPreferences.getLong("userId", -1);

            if (userId != -1) {
                UserDatabaseHelper dbHelper = UserDatabaseHelper.getInstance(this);
                dbHelper.deleteWishlistItem(userId, city, country);
                destinationList.remove(position);
                wishlistAdapter.notifyItemRemoved(position);
                Toast.makeText(this, R.string.itemRemoved, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.userLog, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
