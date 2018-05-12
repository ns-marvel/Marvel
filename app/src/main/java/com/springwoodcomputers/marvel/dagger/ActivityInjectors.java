package com.springwoodcomputers.marvel.dagger;

import com.springwoodcomputers.marvel.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityInjectors {

    @ContributesAndroidInjector(modules = {
            ActivityModules.class,
            FragmentInjectors.class,
            ViewModelModule.class
    })
    abstract MainActivity mainActivity();
}