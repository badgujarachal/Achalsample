package com.snipex.shantu.test.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.achal.test.repository.CanadaArticleRepository;
import com.achal.test.response.CanadaResponse;


public class CanadaArticleViewModel extends AndroidViewModel {

    private CanadaArticleRepository articleRepository;
    private LiveData<CanadaResponse> articleResponseLiveData;

    public CanadaArticleViewModel(@NonNull Application application) {
        super(application);

        articleRepository = new CanadaArticleRepository();
        this.articleResponseLiveData = articleRepository.getMovieArticles();
    }

    public LiveData<CanadaResponse> getArticleResponseLiveData() {
        return articleResponseLiveData;
    }
}
