package com.springwoodcomputers.marvel.api;

import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import javax.inject.Inject;

public class MarvelServiceManager {

    @Inject
    MarvelService service;

    @Inject
    MarvelServiceManager() {
    }

    public void searchForCharacters(String nameBeginsWith, int limit, int offset, SearchForCharactersListener listener) {
        service.getCharactersStartingWith(nameBeginsWith, limit, offset).enqueue(new BaseCallBack<CharacterDataWrapper>() {
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