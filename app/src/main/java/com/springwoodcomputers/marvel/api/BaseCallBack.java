package com.springwoodcomputers.marvel.api;

import android.support.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public abstract class BaseCallBack<T> implements Callback<T> {

    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            switch (response.code()) {
                case HTTP_BAD_REQUEST:
                    onBadRequest(response.errorBody());
                    break;
                case HTTP_UNAUTHORIZED:
                    onUnauthorized(response.errorBody());
                    break;
                case HTTP_NOT_FOUND:
                    onNotFound(response.errorBody());
                    break;
                default:
                    onError();
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onError();
    }

    abstract void onSuccess(T body);

    void onBadRequest(ResponseBody errorBody) {
        onError();
    }

    void onUnauthorized(ResponseBody errorBody) {
        onError();
    }

    void onNotFound(ResponseBody errorBody) {
        onError();
    }

    void onError() {
    }
}
