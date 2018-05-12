package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.design.widget.Snackbar;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.api.MarvelServiceManager;
import com.springwoodcomputers.marvel.database.dao.SearchDao;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.pojo.Character;
import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import lombok.Getter;

public class MainViewModel extends ViewModel implements MarvelServiceManager.SearchForCharactersListener {

    @Inject
    ExecutorService executor;

    @Inject
    MarvelServiceManager manager;

    @Inject
    SearchDao searchDao;

//    private LiveData<List<CharacterSearch>> savedSearches;

    @Getter
    private MutableLiveData<List<Character>> searchResults = new MutableLiveData<>();

    @Getter
    private MutableLiveData<String> attributionText = new MutableLiveData<>();

    @Getter
    private MutableLiveData<MainViewState> mainViewState = new MutableLiveData<>();

    @Inject
    MainViewModel() {
    }


    public void searchForCharacter(CharacterSearch characterSearch) {
        searchResults.setValue(new ArrayList<>());
        manager.searchForCharacters(characterSearch.getSearchString(), this);
        saveSearchInDatabase(characterSearch);
    }

    private void saveSearchInDatabase(CharacterSearch characterSearch) {
        executor.execute(() -> searchDao.insertCharacterSearch(characterSearch));
    }


    @Override
    public void onSearchSucceeded(CharacterDataWrapper characterDataWrapper) {
        List<Character> characterList = characterDataWrapper.getCharacterDataContainer().getCharacterList();
        if (characterList != null && characterList.size() > 0) {
            searchResults.setValue(characterList);
        } else {
            mainViewState.setValue(new MainViewState(true));
        }

        String newAttributionText = characterDataWrapper.getAttributionText();
        if (newAttributionText != null && !newAttributionText.equals(attributionText.getValue())) {
            attributionText.setValue(newAttributionText);
        }
    }

    @Override
    public void onSearchFailed() {
        mainViewState.setValue(new MainViewState(Snackbar.LENGTH_INDEFINITE, R.string.network_error, R.string.retry));
    }

    public void getMatchingSavedSearches(CharacterSearch characterSearch) {

        searchDao.getMatchingPreviousSearches(characterSearch.getSearchString());
    }
}