package com.springwoodcomputers.marvel.dagger;

import com.springwoodcomputers.marvel.child.ChildFragment;
import com.springwoodcomputers.marvel.main.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@SuppressWarnings("unused")
@Module
abstract class FragmentInjectors {

    @ContributesAndroidInjector
    abstract MainFragment mainFragment();

    @ContributesAndroidInjector
    abstract ChildFragment childFragment();
}