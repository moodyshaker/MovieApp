package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.MovieDB.model.data.keywords.Keyword;
import com.example.MovieDB.model.data.movie.Movies;
import com.example.MovieDB.model.data.movie_credits.Cast;
import com.example.MovieDB.model.data.movie_credits.Crew;
import com.example.MovieDB.model.data.person.PersonCast;
import com.example.MovieDB.model.data.reviews.Reviews;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.presenter.MovieCreditsPresenter;
import com.example.MovieDB.presenter.MovieKeywordPresenter;
import com.example.MovieDB.presenter.MovieReviewPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
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
    private ImageView movieLargeIcon, movieSmallIcon, seenlistIcon, wishlistIcon;
    private LinearLayout directorContainer, recommendationContainer, similarContainer, keywordContainer, seenlistContianer, wishlistContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView keywordRecyclerView, castRecyclerView, crewRecyclerView, similarRecyclerView, recommendationRecyclerView;
    private TextView rateNumber, rateCount, reviewCount, overViewContent, directorName, wishlistText, seenlistText;
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
    private Intent intent;
    private PersonCast cast;
    private String type;
    private RelativeLayout reviewContainer;

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
        movieKeywordPresenter = new MovieKeywordPresenter(this);
        movieCreditsPresenter = new MovieCreditsPresenter(this);
        recoPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        movieReviewPresenter = new MovieReviewPresenter(this);
        intent = getIntent();
        type = intent.getStringExtra("type");
        bundle = intent.getExtras();
        if (bundle != null) {
            if (type.equals("one")) {
                movie = (Movies) bundle.getSerializable("movie_object");
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
                appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
                    if (i == 0) {
                        collapsingToolbarLayout.setTitle("");
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.blue_gray_100));
                    } else {
                        collapsingToolbarLayout.setTitle(movie.getTitle());
                        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.blue_gray_100));
                    }
                });
            } else if (type.equals("two")) {
                cast = (PersonCast) bundle.getSerializable("movie_object");
                appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
                    if (i == 0) {
                        collapsingToolbarLayout.setTitle("");
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.blue_gray_100));
                    } else {
                        collapsingToolbarLayout.setTitle(cast.getTitle());
                        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.blue_gray_100));
                    }
                    movieReviewPresenter.getReviews(cast.getId());
                    movieKeywordPresenter.getKeyword(cast.getId());
                    movieCreditsPresenter.getCredits(cast.getId());
                    similarPresenter.getSimilars(cast.getId(), 1);
                    recoPresenter.getRecommendations(cast.getId(), 1);
                    Picasso.get().load(EndPoints.Image500W + cast.getPosterPath()).error(R.drawable.cinema).into(movieLargeIcon);
                    Picasso.get().load(EndPoints.Image200W + cast.getPosterPath()).error(R.drawable.cinema).into(movieSmallIcon);
                    rateNumber.setText(String.valueOf(cast.getVoteAverage()));
                    rateCount.setText(String.valueOf(cast.getVoteCount()));
                    overViewContent.setText(cast.getOverview());
                });
            }
        }
        wishlistContainer.setOnClickListener(click -> {
            if (wishlistContainer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.wishlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background_fill);
                wishlistIcon.setImageResource(R.drawable.favourite_white);
                wishlistText.setTextColor(getResources().getColor(R.color.white));
            } else {
                Toast.makeText(context, "item removed from Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background);
                wishlistIcon.setImageResource(R.drawable.whishlist_heart);
                wishlistText.setTextColor(getResources().getColor(R.color.wishlist_color));
            }
        });

        seenlistContianer.setOnClickListener(click -> {
            if (seenlistContianer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.seenlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background_fill);
                seenlistIcon.setImageResource(R.drawable.eye_white);
                seenlistText.setTextColor(getResources().getColor(R.color.white));
            } else {
                Toast.makeText(context, "item removed from Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background);
                seenlistIcon.setImageResource(R.drawable.seenlist_eye);
                seenlistText.setTextColor(getResources().getColor(R.color.seenlist_color));
            }
        });
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
        similarRecyclerView.setLayoutManager(new

                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new

                SimilarAdapter(context);
        similarRecyclerView.setAdapter(similarAdapter);
        recommendationRecyclerView.setLayoutManager(new

                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendation = new

                SimilarAdapter(context);
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
        reviewContainer = findViewById(R.id.review_container);
        seenlistContianer = findViewById(R.id.seenlist_container);
        wishlistContainer = findViewById(R.id.wishlist_container);
        seenlistIcon = findViewById(R.id.seenlist_icon);
        wishlistIcon = findViewById(R.id.wishlist_icon);
        seenlistText = findViewById(R.id.seenlist_text);
        wishlistText = findViewById(R.id.wishlist_text);
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
        if (keywords.size() == 0) {
            keywordContainer.setVisibility(View.GONE);
        }
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        keywordAdapter = new KeywordAdapter(context, keywords);
        keywordRecyclerView.setAdapter(keywordAdapter);
    }

    @Override
    public void similarListener(List<Movies> similarList) {
        if (similarList.size() == 0) {
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
        if (recommendationsList.size() == 0) {
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
        reviewContainer.setOnClickListener(click -> {
            if (totalReviews > 0) {
                Intent i = new Intent(context, ReviewActivity.class);
                i.putExtra("movie_id", movie.getId());
                i.putExtra("movie_title", movie.getTitle());
                startActivity(i);
            }
        });
        if (totalReviews == 0) {
            reviewContainer.setVisibility(View.GONE);
        }
    }
}
