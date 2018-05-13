package com.springwoodcomputers.marvel.dagger;

import android.arch.lifecycle.ViewModel;

import com.springwoodcomputers.marvel.activity.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel mainViewModel(MainViewModel mainViewModel);
}