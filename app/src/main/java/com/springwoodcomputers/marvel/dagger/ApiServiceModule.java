package com.springwoodcomputers.marvel.dagger;

import android.app.Application;

import com.springwoodcomputers.marvel.BuildConfig;
import com.springwoodcomputers.marvel.MarvelService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.springwoodcomputers.marvel.Constants.API_KEY;
import static com.springwoodcomputers.marvel.Constants.URL_MARVEL;

@Module
class ApiServiceModule {

    @Provides
    @Singleton
    MarvelService provideMarvelService(Application context) {
        return new Retrofit
                .Builder()
                .baseUrl(URL_MARVEL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(context))
                .build()
                .create(MarvelService.class);
    }

    private OkHttpClient getOkHttpClient(Application context) {
        return new OkHttpClient
                .Builder()
                .cache(getCache(context))
                .addInterceptor(getApiKeyInterceptor())
                .addInterceptor(getLoggingInterceptor())
                .build();
    }

    private Cache getCache(Application context) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(context.getCacheDir(), cacheSize);
    }

    private Interceptor getApiKeyInterceptor() {
        return chain -> {
            Request originalRequest = chain.request();
            HttpUrl httpUrl = originalRequest.url().newBuilder().addEncodedQueryParameter("apikey", API_KEY).build();
            Request updatedRequest = originalRequest.newBuilder().url(httpUrl).build();
            return chain.proceed(updatedRequest);
        };
    }

    private Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }
}