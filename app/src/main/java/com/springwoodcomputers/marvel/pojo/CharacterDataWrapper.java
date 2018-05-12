package com.springwoodcomputers.marvel.pojo;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CharacterDataWrapper {

    private String attributionText;

    @SerializedName("data")
    private CharacterDataContainer characterDataContainer;
}