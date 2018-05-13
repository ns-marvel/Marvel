package com.springwoodcomputers.marvel.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

import lombok.Getter;

@Entity(tableName = "search_history",
        indices = {@Index(value = "SEARCH_STRING", unique = true)})
public class CharacterSearch {

    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    @Getter
    private long id;

    @ColumnInfo(name = "SEARCH_STRING")
    private String searchString;

    public CharacterSearch(long id, String searchString) {
        this.id = id;
        this.searchString = searchString;
    }

    @Ignore
    public CharacterSearch(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString.toLowerCase();
    }

    @Override
    public String toString() {
        return searchString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterSearch that = (CharacterSearch) o;
        return Objects.equals(searchString, that.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchString);
    }
}