package com.springwoodcomputers.marvel;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class Storage {

    public static final String REQUEST_TIMESTAMP = "request_timestamp";

    private SharedPreferences preferences;

    @Inject
    public Storage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }


    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }
}