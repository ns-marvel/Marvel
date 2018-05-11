package com.springwoodcomputers.marvel.dagger;

import com.springwoodcomputers.marvel.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@SuppressWarnings("unused")
@Module
abstract class FragmentInjectors {

    @ContributesAndroidInjector
    abstract MainFragment mainFragment();
}