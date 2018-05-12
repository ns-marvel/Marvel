package com.springwoodcomputers.marvel.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CharacterDataContainer {

    private int offset;
    private int limit;
    private int total;
    private int count;
    @SerializedName("results")
    private List<Character> characterList;
}