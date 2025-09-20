package com.example.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public static final String tag = "UserDatabaseHelper";
    public static final String TRAVELERS = "travelers";
    public static final String ID = "_id";
    public static final String FULL_NAME = "fullName";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String THIRD_PARTY = "thirdParty";
    public static final String RECEIVE_EMAILS = "receiveEmails";
    public static final String REMINDERS = "reminders";
    public static final String TRENDING_PLACES = "trendingPlaces";
    public static final String FEEDBACK = "feedback";
    public static final String SUPPORT = "support";
    private static final String DATABASE_NAME = "travelers.db";
    private static final int VERSION_NUM = 7;
    private static UserDatabaseHelper instance;

    // Itinerary-related fields
    private static final String TABLE_ITINERARY = "itinerary";
    private static final String TABLE_TIMELINE = "timeline";
    private static final String COLUMN_USER_ID = "user_id"; // New column for user ID
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ITINERARY_ID = "itinerary_id";
    private static final String COLUMN_TIMELINE_PERIOD = "timeline_period";
    private static final String COLUMN_NOTES = "notes";
    public static final String TABLE_WISHLIST = "wishlist";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_IMAGE_URL = "image_url";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TRAVELERS + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FULL_NAME + " TEXT NOT NULL, "
            + USERNAME + " TEXT NOT NULL, "
            + EMAIL + " TEXT NOT NULL, "
            + PASSWORD + " TEXT NOT NULL, "
            + THIRD_PARTY + " INTEGER NOT NULL DEFAULT 1, "
            + RECEIVE_EMAILS + " INTEGER NOT NULL DEFAULT 1, "
            + REMINDERS + " INTEGER NOT NULL DEFAULT 1, "
            + TRENDING_PLACES + " INTEGER NOT NULL DEFAULT 1, "
            + FEEDBACK + " INTEGER NOT NULL DEFAULT 1, "
            + SUPPORT + " INTEGER NOT NULL DEFAULT 1);";
    private static final String TABLE_CREATE_ITINERARY =
            "CREATE TABLE " + TABLE_ITINERARY + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " + // New column for user ID
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TRAVELERS + "(" + ID + "));";

    private static final String TABLE_CREATE_TIMELINE =
            "CREATE TABLE " + TABLE_TIMELINE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ITINERARY_ID + " INTEGER, " +
                    COLUMN_TIMELINE_PERIOD + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_ITINERARY_ID + ") REFERENCES " + TABLE_ITINERARY + "(" + ID + "));";
    private static final String TABLE_CREATE_WISHLIST =
            "CREATE TABLE " + TABLE_WISHLIST + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_CITY + " TEXT, " +
                    COLUMN_COUNTRY + " TEXT, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TRAVELERS + "(" + ID + "));";

    public static synchronized UserDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public UserDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(TABLE_CREATE_ITINERARY);
        db.execSQL(TABLE_CREATE_TIMELINE);
        db.execSQL(TABLE_CREATE_WISHLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + THIRD_PARTY + " INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + RECEIVE_EMAILS + " INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + REMINDERS + " INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + TRENDING_PLACES + " INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + FEEDBACK + " INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE " + TRAVELERS + " ADD COLUMN " + SUPPORT + " INTEGER NOT NULL DEFAULT 1");
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMELINE);
            db.execSQL(TABLE_CREATE_ITINERARY);
            db.execSQL(TABLE_CREATE_TIMELINE);
        }
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
            db.execSQL(TABLE_CREATE_WISHLIST);
        }
    }
    public long addUser(String fullName, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FULL_NAME, fullName);
        values.put(USERNAME, username);
        values.put(EMAIL, email);
        values.put(PASSWORD, password);
        long newRowId = db.insert(TRAVELERS, null, values);
        db.close();
        return newRowId;
    }

    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {FULL_NAME, EMAIL};
        String selection = USERNAME + " = ?";
        String[] selectionArgs = {username};
        return db.query(TRAVELERS, columns, selection, selectionArgs, null, null, null);
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TRAVELERS, EMAIL + " = ?", new String[]{email});
        if (rowsDeleted > 0) {
            Log.i(tag, "User deleted successfully.");
        } else {
            Log.i(tag, "User not found or unable to delete.");
        }
        db.close();
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(PASSWORD, newPassword);
        int rows = db.update(TRAVELERS, cValues, USERNAME + " = ?", new String[]{username});
        return rows > 0;
    }

    public void saveAccountPrivacySettings(String username, boolean thirdParty, boolean receiveEmails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(THIRD_PARTY, thirdParty ? 1 : 0);
        cValues.put(RECEIVE_EMAILS, receiveEmails ? 1 : 0);
        db.update(TRAVELERS, cValues, USERNAME + "=?", new String[]{username});
    }

    public boolean[] loadAccountPrivacySettings(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {THIRD_PARTY, RECEIVE_EMAILS};
        String selection = USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TRAVELERS, columns, selection, selectionArgs, null, null, null);
        boolean[] settings = new boolean[2];
        if (cursor != null && cursor.moveToFirst()) {
            int thirdPartyIndex = cursor.getColumnIndex(THIRD_PARTY);
            int receiveEmailsIndex = cursor.getColumnIndex(RECEIVE_EMAILS);
            settings[0] = cursor.getInt(thirdPartyIndex) == 1;
            settings[1] = cursor.getInt(receiveEmailsIndex) == 1;
            cursor.close();
        }
        return settings;
    }

    public void saveNotificationSettings(String username, boolean reminders, boolean trendingPlaces, boolean feedback, boolean support) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(REMINDERS, reminders ? 1 : 0);
        cValues.put(TRENDING_PLACES, trendingPlaces ? 1 : 0);
        cValues.put(FEEDBACK, feedback ? 1 : 0);
        cValues.put(SUPPORT, support ? 1 : 0);
        db.update(TRAVELERS, cValues, USERNAME + " = ?", new String[]{username});
    }

    public boolean[] loadNotificationSettings(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {REMINDERS, TRENDING_PLACES, FEEDBACK, SUPPORT};
        String selection = USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TRAVELERS, columns, selection, selectionArgs, null, null, null);
        boolean[] settings = new boolean[4];
        if (cursor != null && cursor.moveToFirst()) {
            int remindersIndex = cursor.getColumnIndex(REMINDERS);
            int trendingPlacesIndex = cursor.getColumnIndex(TRENDING_PLACES);
            int feedbackIndex = cursor.getColumnIndex(FEEDBACK);
            int supportIndex = cursor.getColumnIndex(SUPPORT);
            settings[0] = cursor.getInt(remindersIndex) == 1;
            settings[1] = cursor.getInt(trendingPlacesIndex) == 1;
            settings[2] = cursor.getInt(feedbackIndex) == 1;
            settings[3] = cursor.getInt(supportIndex) == 1;
            cursor.close();
        }
        return settings;
    }
    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ID};
        String selection = USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TRAVELERS, columns, selection, selectionArgs, null, null, null);

        long userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
        }
        db.close();
        return userId;
    }
    public long addItinerary(long userId, String title, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId); // Add user ID
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        long id = db.insert(TABLE_ITINERARY, null, values);
        db.close();
        return id;
    }
    public ArrayList<ItineraryItem> getAllItineraries(long userId) {
        ArrayList<ItineraryItem> itineraries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITINERARY + " WHERE " + COLUMN_USER_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                itineraries.add(new ItineraryItem(id, userId, title, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itineraries;
    }

    public String[] getItineraryDetails(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_TITLE, COLUMN_DATE};
        String selection = ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_ITINERARY, columns, selection, selectionArgs, null, null, null);

        String[] details = null;
        if (cursor != null && cursor.moveToFirst()) {
            details = new String[2];
            details[0] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            details[1] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            cursor.close();
        }
        db.close();
        return details;
    }

    public long addTimeline(long itineraryId, String timelinePeriod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITINERARY_ID, itineraryId);
        values.put(COLUMN_TIMELINE_PERIOD, timelinePeriod);
        values.put(COLUMN_NOTES, notes);
        long id = db.insert(TABLE_TIMELINE, null, values);
        db.close();
        return id;
    }

    public void updateTimeline(long id, String timelinePeriod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMELINE_PERIOD, timelinePeriod);
        values.put(COLUMN_NOTES, notes);
        db.update(TABLE_TIMELINE, values, ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<TimelineItem> getAllTimelines(long itineraryId) {
        ArrayList<TimelineItem> timelines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TIMELINE + " WHERE " + COLUMN_ITINERARY_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(itineraryId)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String timelinePeriod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMELINE_PERIOD));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES));
                timelines.add(new TimelineItem(id, itineraryId, timelinePeriod, notes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return timelines;
    }

    public TimelineItem getTimeline(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMELINE, null, ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long itineraryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITINERARY_ID));
            String timelinePeriod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMELINE_PERIOD));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES));
            cursor.close();
            return new TimelineItem(id, itineraryId, timelinePeriod, notes);
        } else {
            return null;
        }
    }

    public void deleteItinerary(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITINERARY, ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_TIMELINE, COLUMN_ITINERARY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public long addWishlistItem(long userId, String city, String country, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        long id = db.insert(TABLE_WISHLIST, null, values);
        db.close();
        return id;
    }

    public List<Map<String, Object>> getWishlistItems(long userId) {
        List<Map<String, Object>> wishlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WISHLIST, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> item = new HashMap<>();
                item.put("City", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY)));
                item.put("Country", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY)));
                item.put("ImageURL", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                wishlist.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return wishlist;
    }
    public void deleteWishlistItem(long userId, String city, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USER_ID + "=? AND " + COLUMN_CITY + "=? AND " + COLUMN_COUNTRY + "=?";
        String[] whereArgs = {String.valueOf(userId), city, country};
        db.delete(TABLE_WISHLIST, whereClause, whereArgs);
        db.close();
    }
}
