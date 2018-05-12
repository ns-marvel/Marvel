package com.springwoodcomputers.marvel.dagger;

import android.app.Application;

import com.springwoodcomputers.marvel.BuildConfig;
import com.springwoodcomputers.marvel.api.MarvelService;
import com.springwoodcomputers.marvel.utility.Storage;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Locale;

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

import static com.springwoodcomputers.marvel.utility.Constants.API_KEY;
import static com.springwoodcomputers.marvel.utility.Constants.PRIVATE_KEY;
import static com.springwoodcomputers.marvel.utility.Constants.URL_MARVEL;
import static com.springwoodcomputers.marvel.utility.Storage.REQUEST_TIMESTAMP;

@Module
class ApiServiceModule {

    @Provides
    @Singleton
    MarvelService provideMarvelService(Application context, Storage storage) {
        return new Retrofit
                .Builder()
                .baseUrl(URL_MARVEL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(context, storage))
                .build()
                .create(MarvelService.class);
    }

    private OkHttpClient getOkHttpClient(Application context, Storage storage) {
        return new OkHttpClient
                .Builder()
                .cache(getCache(context))
                .addInterceptor(getApiKeyInterceptor())
                .addInterceptor(getTsAndHashCodeInterceptor(storage))
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

    private Interceptor getTsAndHashCodeInterceptor(Storage storage) {
        final String timeStamp = getTimeStamp(storage);
        return chain -> {
            Request originalRequest = chain.request();

            String stringToConvert = String.format(Locale.getDefault(), "%s%s%s", timeStamp, PRIVATE_KEY, API_KEY);
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                return chain.proceed(chain.request());
            }
            StringBuilder sb = new StringBuilder();
            byte[] bytesToConvert = md.digest(stringToConvert.getBytes());
            for (byte byteToConvert : bytesToConvert) {
                sb.append(Integer.toHexString((byteToConvert & 0xFF) | 0x100), 1, 3);
            }

            HttpUrl.Builder builder = originalRequest.url().newBuilder();
            builder.addEncodedQueryParameter("ts", timeStamp);
            builder.addEncodedQueryParameter("hash", sb.toString());
            HttpUrl httpUrl = builder.build();
            Request updatedRequest = originalRequest.newBuilder().url(httpUrl).build();
            return chain.proceed(updatedRequest);
        };
    }

    private String getTimeStamp(Storage storage) {
        String storedTimeStamp = storage.getString(REQUEST_TIMESTAMP);
        if (storedTimeStamp == null) {
            storedTimeStamp = Long.toString(Calendar.getInstance().getTimeInMillis());
            storage.putString(REQUEST_TIMESTAMP, storedTimeStamp);
        }
        return storedTimeStamp;
    }

    private Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }
}