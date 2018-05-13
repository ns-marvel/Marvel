package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.design.widget.Snackbar;
import android.widget.Filter;

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

    @Getter
    private MutableLiveData<List<CharacterSearch>> savedSearches = new MutableLiveData<>();

    @Getter
    private MutableLiveData<List<Character>> searchResults = new MutableLiveData<>();

    @Getter
    private MutableLiveData<String> attributionText = new MutableLiveData<>();

    @Getter
    private MutableLiveData<MainViewState> mainViewState = new MutableLiveData<>();

    @Getter
    private MutableLiveData<Boolean> loadingInProgress = new MutableLiveData<>();

    @Getter
    private MutableLiveData<Boolean> isInfiniteScrollingActive = new MutableLiveData<>();

    private int previousOffset;
    private int previousLimit;
    private CharacterSearch previousCharacterSearch;

    @Inject
    MainViewModel() {
    }

    void searchForCharacter(CharacterSearch characterSearch, int limit) {
        if (previousCharacterSearch == null || !previousCharacterSearch.equals(characterSearch)) {
            searchResults.setValue(new ArrayList<>());
            loadingInProgress.setValue(true);
            isInfiniteScrollingActive.setValue(true);
            manager.searchForCharacters(characterSearch.getSearchString(), limit, 0, this);
            saveSearchInDatabase(characterSearch);
            previousCharacterSearch = characterSearch;
            previousOffset = 0;
            previousLimit = 0;
        }
    }

    private void saveSearchInDatabase(CharacterSearch characterSearch) {
        executor.execute(() -> searchDao.insertCharacterSearch(characterSearch));
    }

    void getMoreSearchResults() {
        loadingInProgress.setValue(true);
        isInfiniteScrollingActive.setValue(true);
        manager.searchForCharacters(previousCharacterSearch.getSearchString(), previousLimit, previousOffset + previousLimit, this);
    }

    private static final String TAG = "MainViewModel";
    @Override
    public void onSearchSucceeded(CharacterDataWrapper characterDataWrapper) {
        List<Character> characterList = characterDataWrapper.getCharacterDataContainer().getCharacterList();
        if (characterList != null && characterList.size() > 0) {
            List<Character> newCharacterList = new ArrayList<>();
            if (searchResults.getValue() != null) newCharacterList.addAll(searchResults.getValue());
            newCharacterList.addAll(characterList);
            searchResults.setValue(newCharacterList);
        } else {
            mainViewState.setValue(new MainViewState(true));
        }

        String newAttributionText = characterDataWrapper.getAttributionText();
        if (newAttributionText != null && !newAttributionText.equals(attributionText.getValue())) {
            attributionText.setValue(newAttributionText);
        }
        loadingInProgress.setValue(false);
        previousLimit = characterDataWrapper.getCharacterDataContainer().getLimit();
        previousOffset = characterDataWrapper.getCharacterDataContainer().getOffset();
        if (characterDataWrapper.getCharacterDataContainer().getTotal() <= previousLimit + previousOffset) {
            isInfiniteScrollingActive.setValue(false);
        }
    }

    @Override
    public void onSearchFailed() {
        mainViewState.setValue(new MainViewState(Snackbar.LENGTH_INDEFINITE, R.string.network_error, R.string.retry));
        loadingInProgress.setValue(false);
        isInfiniteScrollingActive.setValue(false);
    }

    @Getter
    private Filter characterSearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                filterResults.values = new ArrayList<CharacterSearch>();
                filterResults.count = 0;
            } else {
                filterResults.values = searchDao.getMatchingPreviousSearches(charSequence.toString());
                filterResults.count = ((List) filterResults.values).size();
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                savedSearches.setValue((List<CharacterSearch>) results.values);
            }
        }
    };
}