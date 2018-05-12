package com.springwoodcomputers.marvel.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;

import java.util.ArrayList;

public class SavedSearchesAdapter extends ArrayAdapter<CharacterSearch> implements Filterable {

    private final Filter filter;

    SavedSearchesAdapter(@NonNull Context context, Filter filter) {
        super(context, R.layout.item_saved_search, new ArrayList<>());
        this.filter = filter;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}