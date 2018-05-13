package com.springwoodcomputers.marvel.pojo;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Character {

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private Thumbnail thumbnail;

    @SerializedName("comics")
    private ComicList comicList;

    @SerializedName("stories")
    private StoryList storyList;

    @SerializedName("events")
    private EventList eventList;

    @SerializedName("series")
    private SeriesList seriesList;

    public int getComicListCount() {
        return comicList == null ? 0 : comicList.getAvailable();
    }

    public int getStoryListCount() {
        return storyList == null ? 0 : storyList.getAvailable();
    }

    public int getEventListCount() {
        return eventList == null ? 0 : eventList.getAvailable();
    }

    public int getSeriesListCount() {
        return seriesList == null ? 0 : seriesList.getAvailable();
    }
}