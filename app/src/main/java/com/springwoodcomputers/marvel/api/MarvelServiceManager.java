package com.springwoodcomputers.marvel.api;

import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import javax.inject.Inject;

public class MarvelServiceManager {

    @Inject
    MarvelService service;

    @Inject
    public MarvelServiceManager() {
    }

    public void searchForCharacters(String nameBeginsWith, SearchForCharactersListener listener) {
        service.getCharactersStartingWith(nameBeginsWith).enqueue(new BaseCallBack<CharacterDataWrapper>() {
            @Override
            void onSuccess(CharacterDataWrapper body) {
                listener.onSearchSucceeded(body);
            }

            @Override
            void onError() {
                listener.onSearchFailed();
            }
        });
    }


    public interface SearchForCharactersListener {

        void onSearchSucceeded(CharacterDataWrapper characterDataWrapper);

        void onSearchFailed();
    }
}