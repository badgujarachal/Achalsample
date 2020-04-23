package com.achal.test.mvvm.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.achal.test.R;
import com.achal.test.mvvm.models.Canada;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CanadaArticleAdapter extends RecyclerView.Adapter<CanadaArticleAdapter.ViewHolder> {

    private Context context;
    ArrayList<Canada> articleArrayList;

    public CanadaArticleAdapter(Context context, ArrayList<Canada> articleArrayList) {
        this.context = context;
        this.articleArrayList = articleArrayList;
    }

    @NonNull
    @Override
    public CanadaArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_each_row_article, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CanadaArticleAdapter.ViewHolder viewHolder, int i) {
        Canada article = articleArrayList.get(i);
        viewHolder.tvTitle.setText(article.getTitle());
        // viewHolder.tvAuthorAndPublishedAt.setText("-"+article.getAuthor() +" | "+"Piblishetd At: "+article.getPublishedAt());
        viewHolder.tvDescription.setText(article.getDescription());
        Glide.with(context)
                .load(article.getImageHref())
                .into(viewHolder.imgViewCover);
    }

    @Override
    public int getItemCount() {
        return articleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgViewCover;
        private final TextView tvTitle;
        //  private final TextView tvAuthorAndPublishedAt;
        private final TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgViewCover = (ImageView) itemView.findViewById(R.id.imgViewCover);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //  tvAuthorAndPublishedAt=(TextView) itemView.findViewById(R.id.tvAuthorAndPublishedAt);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }
}
