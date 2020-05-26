package com.example.MovieDB.persistance.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MovieSharedPreference {
    private SharedPreferences sharedPreferences;
    private static MovieSharedPreference movieSharedPreference;
    private final String preferenceName = "Movie_preferences";

    private MovieSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public static MovieSharedPreference getMoviePreferences(Context context) {
        if (movieSharedPreference == null) {
            movieSharedPreference = new MovieSharedPreference(context);
        }
        return movieSharedPreference;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static class UserPreferences {
        private final static String EMAIL = "email";
        private final static String USERNAME = "name";
        private final static String IMAGE = "image";
        private final static String ID = "id";
        private final static String TYPE = "type";
        private static UserPreferences userPreferences;
        private MovieSharedPreference sInstance;

        private UserPreferences(Context context) {
            sInstance = MovieSharedPreference.getMoviePreferences(context);
        }

        public static UserPreferences getUserPreference(Context context) {
            if (userPreferences == null) {
                userPreferences = new UserPreferences(context);
            }
            return userPreferences;
        }

        public void putEmail(String value) {
            sInstance.putString(EMAIL, value);
        }

        public void putUsername(String value) {
            sInstance.putString(USERNAME, value);
        }

        public void putID(String value) {
            sInstance.putString(ID, value);
        }

        public void putImageUri(String value) {
            sInstance.putString(IMAGE, value);
        }

        public String getEmail() {
            return sInstance.getString(EMAIL);
        }

        public String getUsername() {
            return sInstance.getString(USERNAME);
        }

        public String getID() {
            return sInstance.getString(ID);
        }

        public String getImage() {
            return sInstance.getString(IMAGE);
        }

        public void putType(String value) {
            sInstance.putString(TYPE, value);
        }

        public String getType() {
            return sInstance.getString(TYPE);
        }

        public boolean isFirstTime() {
            if (userPreferences.getID().isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        public void logOut() {
            sInstance.clearAll();
        }
    }
}
