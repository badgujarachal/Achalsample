package com.achal.test.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
