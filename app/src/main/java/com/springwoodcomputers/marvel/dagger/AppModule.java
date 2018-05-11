package com.springwoodcomputers.marvel.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.springwoodcomputers.marvel.MarvelApplication;
import com.springwoodcomputers.marvel.database.MarvelDatabase;
import com.springwoodcomputers.marvel.database.dao.SearchDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class AppModule {

    @Binds
    @Singleton
    public abstract Application application(MarvelApplication application);

    @Singleton
    @Provides
    static ExecutorService getExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Singleton
    @Provides
    static MarvelDatabase provideMarvelDatabase(Application context) {
        return Room.databaseBuilder(context, MarvelDatabase.class, "marvel.db").build();
    }

    @Singleton
    @Provides
    static SearchDao provideSearchDao(MarvelDatabase database) {
        return database.getSearchDao();
    }
}