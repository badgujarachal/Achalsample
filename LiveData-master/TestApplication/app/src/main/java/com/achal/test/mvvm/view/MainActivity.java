package com.achal.test.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.achal.test.R;
import com.achal.test.mvvm.adapter.CanadaArticleAdapter;
import com.achal.test.mvvm.models.Canada;
import com.achal.test.mvvm.view_model.CanadaArticleViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView my_recycler_view;
    private ProgressBar progress_circular_movie_article;
    private LinearLayoutManager layoutManager;
    private CanadaArticleAdapter adapter;
    private ArrayList<Canada> articleArrayList = new ArrayList<>();
    CanadaArticleViewModel articleViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        getMovieArticles();
    }

    /**
     * initialization of views and others
     *
     * @param @null
     */
    private void initialization() {
        progress_circular_movie_article = (ProgressBar) findViewById(R.id.progress_circular_movie_article);
        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(MainActivity.this);
        my_recycler_view.setLayoutManager(layoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        my_recycler_view.setHasFixedSize(true);

        // adapter
        adapter = new CanadaArticleAdapter(MainActivity.this, articleArrayList);
        my_recycler_view.setAdapter(adapter);

        // View Model
        articleViewModel = ViewModelProviders.of(this).get(CanadaArticleViewModel.class);
    }

    /**
     * get movies articles from news api
     *
     * @param @null
     */
    private void getMovieArticles() {
        articleViewModel.getArticleResponseLiveData().observe(this, articleResponse -> {
            if (articleResponse != null) {
                setTitle(articleResponse.getTitle());
                progress_circular_movie_article.setVisibility(View.GONE);
                List<Canada> articles = articleResponse.getArticles();
                articleArrayList.addAll(articles);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
