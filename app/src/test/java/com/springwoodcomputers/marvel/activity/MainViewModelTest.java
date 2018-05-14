package com.springwoodcomputers.marvel.activity;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.support.design.widget.Snackbar;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.api.MarvelServiceManager;
import com.springwoodcomputers.marvel.database.dao.SearchDao;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.main.MainViewState;
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

import static com.springwoodcomputers.marvel.utility.Constants.MAX_LIMIT_PERMITTED;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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

    @Mock
    private Observer<Boolean> mockInfiniteScrollingActiveObserver;

    @Mock
    private Observer<Character> mockCharacterObserver;

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

    private CharacterDataContainer subsequentCharacterDataContainer = new CharacterDataContainer(10, 10, 20, 10, characterList);
    private CharacterDataWrapper subsequentCharacterDataWrapper = new CharacterDataWrapper(attributionText, subsequentCharacterDataContainer);


    private List<Character> emptyCharacterList = Collections.emptyList();
    private CharacterDataContainer emptyCharacterDataContainer = new CharacterDataContainer(0, 10, 0, 10, emptyCharacterList);
    private CharacterDataWrapper emptyCharacterDataWrapper = new CharacterDataWrapper(attributionText, emptyCharacterDataContainer);

    private Character character;

    private int limit = 10;
    private int biggerLimit = 20;
    private int zeroOffset = 0;
    private int subsequentOffset = 10;
    private int maxLimit = MAX_LIMIT_PERMITTED;
    private int tooBigLimit = MAX_LIMIT_PERMITTED + 1;

    @Test
    public void newSearch_clearsExistingSearchResults() {
        viewModel.getSearchResults().observeForever(mockSearchResultsObserver);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockSearchResultsObserver).onChanged(characterListArgumentCaptor.capture());
        assertEquals(0, characterListArgumentCaptor.getValue().size());
    }

    @Test
    public void searchForCharacter_maxLimitIs100() {
        viewModel.searchForCharacter(characterSearch, tooBigLimit);

        verify(mockManager).searchForCharacters(characterSearch.getSearchString(), maxLimit, zeroOffset, viewModel);
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
    public void searchingForMoreCharacters_setsLoadingInProgress() {
        viewModel.getLoadingInProgress().observeForever(mockLoadingInProgressObserver);
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();

        verify(mockLoadingInProgressObserver, times(2)).onChanged(true);
    }

    @Test
    public void SearchingForMoreCharacters_usesValuesFromPreviousSuccessfulSearch() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();

        verify(mockManager).searchForCharacters(characterSearch.getSearchString(), limit, subsequentOffset, viewModel);
    }

    @Test
    public void searchingForCharacters_enablesInfiniteScrolling() {
        viewModel.getIsInfiniteScrollingActive().observeForever(mockInfiniteScrollingActiveObserver);

        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockInfiniteScrollingActiveObserver).onChanged(true);
    }

    @Test
    public void searchingForMoreCharacters_enablesInfiniteScrolling() {
        viewModel.getIsInfiniteScrollingActive().observeForever(mockInfiniteScrollingActiveObserver);

        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();

        verify(mockInfiniteScrollingActiveObserver, times(2)).onChanged(true);
    }

    @Test
    public void searchingForCharacters_searchFailureDisablesInfiniteScrolling() {
        viewModel.getIsInfiniteScrollingActive().observeForever(mockInfiniteScrollingActiveObserver);

        viewModel.onSearchFailed();

        verify(mockInfiniteScrollingActiveObserver).onChanged(false);
    }

    @Test
    public void searchingForCharacters_infiniteScrollingIsDisabledWhenNoMoreResults() {
        viewModel.getIsInfiniteScrollingActive().observeForever(mockInfiniteScrollingActiveObserver);

        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(emptyCharacterDataWrapper);

        verify(mockInfiniteScrollingActiveObserver, times(1)).onChanged(true);
        verify(mockInfiniteScrollingActiveObserver, times(1)).onChanged(false);
    }

    @Test
    public void searchingForCharactersWithPreviousSearchValue_doesNothing() {
        viewModel.searchForCharacter(characterSearch, limit);
        viewModel.searchForCharacter(characterSearch, limit);

        verify(mockManager, times(1)).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());
    }

    @Test
    public void SearchingForMoreCharacters_shouldOnlyBeAttemptedIfMoreAvailable() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());
        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(subsequentOffset), searchForCharactersListenerCaptor.capture());
        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(subsequentCharacterDataWrapper);

        viewModel.getMoreSearchResults();

        verify(mockManager, times(2)).searchForCharacters(anyString(), anyInt(), anyInt(), any(MarvelServiceManager.SearchForCharactersListener.class));
    }

    @Test
    public void retry_shouldClearTheErrorState() {
        viewModel.getMainViewState().observeForever(mockMainViewStateObserver);
        viewModel.searchForCharacter(characterSearch, limit);

        viewModel.retryFailedCharacterSearch();

        verify(mockMainViewStateObserver).onChanged(new MainViewState());

    }

    @Test
    public void retryShouldRepeatTheLastAttempt_whenSearchingForNewCharacter() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchFailed();

        viewModel.retryFailedCharacterSearch();

        verify(mockManager, times(2)).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());
    }

    @Test
    public void retryShouldRepeatTheLastAttempt_whenSearchingGettingMoreSearchResults() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.getMoreSearchResults();
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(subsequentOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchFailed();

        viewModel.retryFailedCharacterSearch();

        verify(mockManager, times(2)).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(subsequentOffset), searchForCharactersListenerCaptor.capture());
    }

    @Test
    public void whenCharacterClicked_viewModelEmitsCharacterObject() {
        viewModel.getSelectedCharacter().observeForever(mockCharacterObserver);

        viewModel.onCharacterClicked(character);

        verify(mockCharacterObserver).onChanged(character);
    }

    @Test
    public void settingNewLimitGreaterThanPrevious_shouldLoadMoreData() {
        viewModel.searchForCharacter(characterSearch, limit);
        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(limit), eq(zeroOffset), searchForCharactersListenerCaptor.capture());

        searchForCharactersListenerCaptor.getValue().onSearchSucceeded(characterDataWrapper);

        viewModel.setNewLimit(biggerLimit);

        verify(mockManager).searchForCharacters(eq(characterSearch.getSearchString()), eq(biggerLimit), eq(subsequentOffset), searchForCharactersListenerCaptor.capture());
    }
}