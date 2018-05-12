package com.springwoodcomputers.marvel.api;

import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MarvelService {

    @GET("characters")
    Call<CharacterDataWrapper> getCharactersStartingWith(@Query("nameStartsWith") String startsWith,
                                                         @Query("limit") int limit,
                                                         @Query("offset") int offset);

    @GET("characters/{character_id}")
    Call<CharacterDataWrapper> getCharacterById(@Path("character_id") int characterId);
}