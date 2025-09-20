package com.example.androidproject;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ItineraryAdapter extends ArrayAdapter<ItineraryItem> {
    private Context context;
    private int resource;
    private UserDatabaseHelper db;

    public ItineraryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ItineraryItem> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.db = UserDatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ItineraryItem item = getItem(position);
        if (item == null) {
            return new View(context);
        }

        final String title = item.getTitle();
        final String date = item.getDate();
        final long id = item.getId();
        final long userId = item.getUserId();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView titleTextView = convertView.findViewById(R.id.itineraryTitleTextView);
        TextView dateTextView = convertView.findViewById(R.id.itineraryDateTextView);
        ImageButton moreOptionsIcon = convertView.findViewById(R.id.menuButton);

        if (title != null) {
            titleTextView.setText(title);
        }
        if (date != null) {
            dateTextView.setText(date);
        }

        moreOptionsIcon.setTag(id);
        moreOptionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, id);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItineraryScrollView.class);
                intent.putExtra("itineraryId", id);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void showPopupMenu(final View view, final long id) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.action_delete) {
                    deleteItem(id);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteItem(long id) {
        db.deleteItinerary(id);
        remove(getItemById(id));
        notifyDataSetChanged();
        Toast.makeText(context, R.string.deleteItineraryItem, Toast.LENGTH_SHORT).show();
    }

    private ItineraryItem getItemById(long id) {
        for (int i = 0; i < getCount(); i++) {
            ItineraryItem item = getItem(i);
            if (item != null && item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}
