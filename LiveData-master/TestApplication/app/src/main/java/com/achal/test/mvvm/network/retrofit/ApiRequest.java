package com.achal.test.mvvm.network.retrofit;

import com.achal.test.mvvm.network.response.CanadaResponse;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRequest {

    @GET("facts.json")
    Call<CanadaResponse> getMovieArticles(

    );
}
