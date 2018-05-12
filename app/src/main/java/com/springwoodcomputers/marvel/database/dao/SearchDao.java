package com.springwoodcomputers.marvel.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.springwoodcomputers.marvel.database.entity.CharacterSearch;

import java.util.List;

@Dao
public interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCharacterSearch(CharacterSearch characterSearch);

    @Query("select * from search_history where lower(SEARCH_STRING) like '%'||:searchText||'%' order by SEARCH_STRING ")
    List<CharacterSearch> getMatchingPreviousSearches(String searchText);

}