package com.springwoodcomputers.marvel.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;

@Getter
@Entity(tableName = "search_history")
public class Search {

    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name= "SEARCH_STRING")
    private String searchString;

    public Search(long id, String searchString) {
        this.id = id;
        this.searchString = searchString;
    }
}