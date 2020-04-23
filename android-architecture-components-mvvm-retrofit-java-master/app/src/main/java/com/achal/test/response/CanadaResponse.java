package com.achal.test.response;

import com.achal.test.model.Canada;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;

public class CanadaResponse {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("rows")
    @Expose
    private List<Canada> articles = null;


    public List<Canada> getArticles() {
        return articles;
    }

    public void setArticles(List<Canada> articles) {
        this.articles = articles;
    }
}
