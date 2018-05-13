package com.springwoodcomputers.marvel.pojo;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Thumbnail {

    private String path;
    private String extension;

    public String getImageUrl() {
        return path + "." + extension;
    }

    public String getBigImageUrl() {
        return path + "/landscape_xlarge." + extension;
    }
}