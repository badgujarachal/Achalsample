package com.achal.test.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.achal.test.network.retrofit.ApiRequest;
import com.achal.test.network.retrofit.RetrofitRequest;
import com.achal.test.response.CanadaResponse;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CanadaArticleRepository {
    private static final String TAG = CanadaArticleRepository.class.getSimpleName();
    private ApiRequest apiRequest;

    public CanadaArticleRepository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<CanadaResponse> getMovieArticles() {
        final MutableLiveData<CanadaResponse> data = new MutableLiveData<>();
        apiRequest.getMovieArticles()
                .enqueue(new Callback<CanadaResponse>() {


                    @Override
                    public void onResponse(Call<CanadaResponse> call, Response<CanadaResponse> response) {
                        Log.d(TAG, "onResponse response:: " + response);


                        if (response.body() != null) {
                            data.setValue(response.body());

                            Log.d(TAG, "articles size:: " + response.body().getTitle());
                            Log.d(TAG, "articles title pos 0:: " + response.body().getArticles().get(0).getTitle());
                        }
                    }

                    @Override
                    public void onFailure(Call<CanadaResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }
}
