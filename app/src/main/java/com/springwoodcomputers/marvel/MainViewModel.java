package com.springwoodcomputers.marvel;

import android.arch.lifecycle.ViewModel;

import com.springwoodcomputers.marvel.database.dao.SearchDao;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    @Inject
    ExecutorService executor;

    @Inject
    MarvelService marvelService;

    @Inject
    SearchDao searchDao;

    @Inject
    MainViewModel() {
    }
}