package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.CreditContract;
import com.example.MovieDB.contract.KeywordContract;
import com.example.MovieDB.contract.RecommendationsContract;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.contract.SimilarContract;
import com.example.MovieDB.data.movie_credits.Cast;
import com.example.MovieDB.data.movie_credits.Crew;
import com.example.MovieDB.data.keywords.Keyword;
import com.example.MovieDB.data.movie.Movies;
import com.example.MovieDB.data.reviews.Reviews;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.presenter.MovieCreditsPresenter;
import com.example.MovieDB.presenter.MovieKeywordPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
import com.example.MovieDB.presenter.MovieReviewPresenter;
import com.example.MovieDB.presenter.SimilarPresenter;
import com.example.MovieDB.ui.adapter.CastAdapter;
import com.example.MovieDB.ui.adapter.CrewAdapter;
import com.example.MovieDB.ui.adapter.KeywordAdapter;
import com.example.MovieDB.ui.adapter.SimilarAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetails extends AppCompatActivity implements KeywordContract, CreditContract, SimilarContract, RecommendationsContract, ReviewContract {

    private Movies movie;
    private Bundle bundle;
    private ImageView movieLargeIcon, movieSmallIcon;
    private LinearLayout directorContainer, recommendationContainer, similarContainer, keywordContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView keywordRecyclerView, castRecyclerView, crewRecyclerView, similarRecyclerView, recommendationRecyclerView;
    private TextView rateNumber, rateCount, reviewCount, overViewContent, directorName;
    private Context context = this;
    private KeywordAdapter keywordAdapter;
    private CrewAdapter crewAdapter;
    private CastAdapter castAdapter;
    private SimilarAdapter similarAdapter;
    private MovieReviewPresenter movieReviewPresenter;
    private SimilarAdapter recommendation;
    private MovieCreditsPresenter movieCreditsPresenter;
    private SimilarPresenter similarPresenter;
    private RecommendationsPresenter recoPresenter;
    private MovieKeywordPresenter movieKeywordPresenter;
    private boolean isLoadingSim = false;
    private boolean isLoadingReco = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        initView();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            movie = (Movies) bundle.getSerializable("movie_object");
        }
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.blue_gray_100));
                    actionBar.setDisplayHomeAsUpEnabled(false);
                } else {
                    collapsingToolbarLayout.setTitle(movie.getTitle());
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.blue_gray_100));
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        });
        movieKeywordPresenter = new MovieKeywordPresenter(this);
        movieCreditsPresenter = new MovieCreditsPresenter(this);
        recoPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        movieReviewPresenter = new MovieReviewPresenter(this);
        movieReviewPresenter.getReviews(movie.getId());
        movieKeywordPresenter.getKeyword(movie.getId());
        movieCreditsPresenter.getCredits(movie.getId());
        similarPresenter.getSimilars(movie.getId(), 1);
        recoPresenter.getRecommendations(movie.getId(), 1);
        Picasso.get().load(EndPoints.Image500W + movie.getPosterPath()).error(R.drawable.cinema).into(movieLargeIcon);
        Picasso.get().load(EndPoints.Image200W + movie.getPosterPath()).error(R.drawable.cinema).into(movieSmallIcon);
        rateNumber.setText(String.valueOf(movie.getVoteAverage()));
        rateCount.setText(String.valueOf(movie.getVoteCount()));
        overViewContent.setText(movie.getOverview());
        recommendationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItems = manager.getItemCount();
                int totalVisibleItems = manager.getChildCount();
                int currentItem = manager.findFirstVisibleItemPosition();
                if (totalItems <= (totalVisibleItems + currentItem)) {
                    if (!isLoadingReco) {
                        isLoadingReco = true;
                        recoPresenter.increasePages();
                    }
                } else {
                    isLoadingReco = false;
                }
            }
        });
        similarRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItems = manager.getItemCount();
                int totalVisibleItems = manager.getChildCount();
                int currentItem = manager.findFirstVisibleItemPosition();
                Log.e("123", "visibleItems:- " + String.valueOf(totalVisibleItems) + " currentItemPosition:- " + String.valueOf(currentItem) + " totalItems:- " + String.valueOf(totalItems));
                if ((totalVisibleItems + currentItem) >= totalItems) {
                    if (!isLoadingSim) {
                        isLoadingSim = true;
                        similarPresenter.increasePages();
                    }
                } else {
                    isLoadingSim = false;
                }
            }
        });
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new SimilarAdapter(context);
        similarRecyclerView.setAdapter(similarAdapter);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendation = new SimilarAdapter(context);
        recommendationRecyclerView.setAdapter(recommendation);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        toolbar = findViewById(R.id.movie_details_toolbar);
        appBarLayout = findViewById(R.id.appbarlayout_container);
        movieLargeIcon = findViewById(R.id.movie_icon);
        movieSmallIcon = findViewById(R.id.movie_small_icon);
        rateNumber = findViewById(R.id.rate_number_text);
        rateCount = findViewById(R.id.rating_people_count_text);
        overViewContent = findViewById(R.id.over_view_content);
        castRecyclerView = findViewById(R.id.cast_recycler);
        crewRecyclerView = findViewById(R.id.crew_recycler);
        keywordRecyclerView = findViewById(R.id.keyword_recycler);
        reviewCount = findViewById(R.id.review_count);
        similarRecyclerView = findViewById(R.id.similar_recycler);
        directorName = findViewById(R.id.director_name);
        directorContainer = findViewById(R.id.director_container);
        recommendationContainer = findViewById(R.id.recommendation_container);
        similarContainer = findViewById(R.id.similar_container);
        keywordContainer = findViewById(R.id.keyword_container);
        recommendationRecyclerView = findViewById(R.id.recommendation_recycler);
    }

    @Override
    public void crewListener(List<Crew> crews) {
        for (Crew c : crews) {
            if (c.getJob().equals("Director")) {
                directorContainer.setVisibility(View.VISIBLE);
                directorName.setText(c.getName());
            }
        }
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        crewAdapter = new CrewAdapter(context, crews);
        crewRecyclerView.setAdapter(crewAdapter);
    }

    @Override
    public void castListener(List<Cast> casts) {
        castRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        castAdapter = new CastAdapter(context, casts);
        castRecyclerView.setAdapter(castAdapter);
    }

    @Override
    public void keywordListener(List<Keyword> keywords) {
        if(keywords.size() == 0){
            keywordContainer.setVisibility(View.GONE);
        }
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        keywordAdapter = new KeywordAdapter(context, keywords);
        keywordRecyclerView.setAdapter(keywordAdapter);
    }

    @Override
    public void similarListener(List<Movies> similarList) {
        if(similarList.size() == 0){
            similarContainer.setVisibility(View.GONE);
        }
        similarAdapter.setMovies(similarList);
        similarAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void removeLoading() {

    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {

    }

    @Override
    public void recommendationsListener(List<Movies> recommendationsList) {
        if(recommendationsList.size() == 0){
            recommendationContainer.setVisibility(View.GONE);
        }
        recommendation.setMovies(recommendationsList);
        recommendation.notifyDataSetChanged();
    }

    @Override
    public void reviewListener(List<Reviews> reviews) {

    }

    @Override
    public void reviewResultListener(Integer totalReviews) {
        reviewCount.setText(getResources().getString(R.string.review_count, totalReviews));
    }
}
