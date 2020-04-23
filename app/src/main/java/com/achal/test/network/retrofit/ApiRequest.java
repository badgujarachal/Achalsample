package com.achal.test.network.retrofit;

import com.achal.test.response.CanadaResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRequest {

    @GET("facts.json")
    Call<CanadaResponse> getMovieArticles(

    );
}
