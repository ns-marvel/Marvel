package com.springwoodcomputers.marvel.main;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.support.design.widget.Snackbar;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.api.MarvelServiceManager;
import com.springwoodcomputers.marvel.database.dao.SearchDao;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.pojo.Character;
import com.springwoodcomputers.marvel.pojo.CharacterDataContainer;
import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private MarvelServiceManager mockManager;

    @Mock
    private SearchDao mockSearchDao;

    @Mock
    private ExecutorService mockExecutor;

    @Mock
    private Observer<List<Character>> mockSearchResultsObserver;

    @Mock
    private Observer<MainViewState> mockMainViewStateObserver;

    @Mock
    private Observer<Boolean> mockLoadingInProgressObserver;

    @Mock
    private Observer<String> mockAttributionTextObserver;

    @Captor
    private ArgumentCaptor<MarvelServiceManager.SearchForCharactersListener> searchForCharactersListenerCaptor;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Character>> characterListArgumentCaptor;

    @InjectMocks
    private MainViewModel viewModel;

    private String attributionText = "Copyright Marvel";
    private CharacterSearch characterSearch = new CharacterSearch("Super");

    private List<Character> characterList = Collections.singletonList(new Character());
    private CharacterDataContainer characterDataContainer = new CharacterDataContainer(0, 10, 20, 10, characterList);
    private CharacterDataWrapper characterDataWrapper = new CharacterDataWrapper(attributionText, characterDataContainer);

    private List<Character> emptyCharacterList = Collections.emptyList();
    private CharacterDataContainer emptyCharacterDataContainer = new CharacterDataContainer(0, 10, 20, 10, emptyCharacterList);
    private CharacterDataWrapper emptyCharacterDataWrapper = new CharacterDataWrapper(attributionText, emptyCharacterDataContainer);

    private int limit = 10;
    private int zeroOffset = 0;
    private int subsequentOffset = 10;

    @Test
    public void newSearch_clearsExistingSearchResults() {
        viewModel.getSearchResults().observeForever(mockSearchResultsObserver);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockSearchResultsObserver).onChanged(characterListArgumentCaptor.capture());
        assertEquals(0, characterListArgumentCaptor.getValue().size());
    }

    @Test
    public void searchForCharacter_invokesServiceManagerToSearchForCharacters() {
        doNothing().when(mockManager).searchForCharacters(characterSearch.getSearchString(), limit, zeroOffset, viewModel);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockManager).searchForCharacters(characterSearch.getSearchString(), limit, zeroOffset, viewModel);
    }

    @Test
    public void whenSearchingForCharacter_searchShouldBeSavedInDatabase() {
        doNothing().when(mockManager).searchForCharacters(characterSearch.getSearchString(), limit, zeroOffset, viewModel);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockExecutor).execute(runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue().run();

        verify(mockSearchDao).insertCharacterSearch(characterSearch);
    }

    @Test
    public void successfulSearch_addsRetrievedCharactersToSearchResults() {
        viewModel.getSearchResults().observeForever(mockSearchResultsObserver);

        viewModel.onSearchSucceeded(characterDataWrapper);

        verify(mockSearchResultsObserver).onChanged(characterListArgumentCaptor.capture());
        assertEquals(characterList, characterListArgumentCaptor.getValue());
    }

    @Test
    public void successfulSearch_setsTheAttributionText() {
        viewModel.getAttributionText().observeForever(mockAttributionTextObserver);

        viewModel.onSearchSucceeded(characterDataWrapper);

        verify(mockAttributionTextObserver).onChanged(attributionText);
    }

    @Test
    public void emptySearchResults_setsNoResultsErrorState() {
        viewModel.getMainViewState().observeForever(mockMainViewStateObserver);

        viewModel.onSearchSucceeded(emptyCharacterDataWrapper);

        verify(mockMainViewStateObserver).onChanged(new MainViewState(true));
    }

    @Test
    public void searchFailure_setsSearchNetworkErrorState() {
        viewModel.getMainViewState().observeForever(mockMainViewStateObserver);

        viewModel.onSearchFailed();

        verify(mockMainViewStateObserver).onChanged(new MainViewState(Snackbar.LENGTH_INDEFINITE, R.string.network_error, R.string.retry));
    }

    @Test
    public void searchingForCharacters_setsLoadingInProgress() {
        viewModel.getLoadingInProgress().observeForever(mockLoadingInProgressObserver);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockLoadingInProgressObserver).onChanged(true);
    }

    @Test
    public void searchingForCharacters_searchSuccessResetsLoadingInProgress() {
        viewModel.getLoadingInProgress().observeForever(mockLoadingInProgressObserver);

        viewModel.onSearchSucceeded(characterDataWrapper);

        verify(mockLoadingInProgressObserver).onChanged(false);
    }

    @Test
    public void searchingForCharacters_searchFailureResetsLoadingInProgress() {
        viewModel.getLoadingInProgress().observeForever(mockLoadingInProgressObserver);

        viewModel.onSearchFailed();

        verify(mockLoadingInProgressObserver).onChanged(false);
    }

    @Test
    public void SearchingForMoreCharacters_usesValuesFromPreviousSuccessfulSearch() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();

        verify(mockManager).searchForCharacters(characterSearch.getSearchString(), limit, subsequentOffset, viewModel);
    }
}