package com.springwoodcomputers.marvel.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.springwoodcomputers.marvel.pojo.CharacterDataWrapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Call;

import static com.springwoodcomputers.marvel.api.MarvelServiceManager.SearchForCharactersListener;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MarvelServiceManagerTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private MarvelService mockService;

    @Mock
    private Call<CharacterDataWrapper> mockCall;

    @Mock
    private SearchForCharactersListener mockSearchForCharactersListener;

    @Captor
    private ArgumentCaptor<BaseCallBack<CharacterDataWrapper>> searchForCharactersCaptor;

    @InjectMocks
    private MarvelServiceManager manager;

    private String characterStartsWith = "Super";
    private int limit = 20;
    private int offset = 0;
    private CharacterDataWrapper characterDataWrapper = new CharacterDataWrapper();

    @Test
    public void searchSuccess_callsBackToOnSearchSucceeded() {
        doReturn(mockCall).when(mockService).getCharactersStartingWith(characterStartsWith, limit, offset);

        manager.searchForCharacters(characterStartsWith, limit, offset, mockSearchForCharactersListener);

        verify(mockService).getCharactersStartingWith(characterStartsWith, limit, offset);
        verify(mockCall).enqueue(searchForCharactersCaptor.capture());

        searchForCharactersCaptor.getValue().onSuccess(characterDataWrapper);

        verify(mockSearchForCharactersListener).onSearchSucceeded(characterDataWrapper);
    }

    @Test
    public void searchFailure_callsBackToOnSearchFailed() {
        doReturn(mockCall).when(mockService).getCharactersStartingWith(characterStartsWith, limit, offset);

        manager.searchForCharacters(characterStartsWith, limit, offset, mockSearchForCharactersListener);

        verify(mockService).getCharactersStartingWith(characterStartsWith, limit, offset);
        verify(mockCall).enqueue(searchForCharactersCaptor.capture());

        searchForCharactersCaptor.getValue().onError();

        verify(mockSearchForCharactersListener).onSearchFailed();
    }
}