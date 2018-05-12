package com.springwoodcomputers.marvel.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Character {

    private int id;
    private String name;
    private String description;
    private Thumbnail thumbnail;
    private ComicList comicList;
    private StoryList storyList;
    private EventList eventList;
    private SeriesList seriesList;
}