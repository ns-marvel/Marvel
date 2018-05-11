package com.springwoodcomputers.marvel.dagger;

import com.springwoodcomputers.marvel.MarvelApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityInjectors.class,
        ApiServiceModule.class
})
public interface AppComponent extends AndroidInjector<MarvelApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<MarvelApplication> {}
}