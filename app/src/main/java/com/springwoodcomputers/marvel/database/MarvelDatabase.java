package com.springwoodcomputers.marvel.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.springwoodcomputers.marvel.database.dao.SearchDao;
import com.springwoodcomputers.marvel.database.entity.Search;

@Database(
        entities = {Search.class},
        version = 1)
public abstract class MarvelDatabase extends RoomDatabase {

    public abstract SearchDao getSearchDao();
}