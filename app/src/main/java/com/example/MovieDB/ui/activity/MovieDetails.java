package com.example.MovieDB.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.MovieDB.contract.MovieKeywordContract;
import com.example.MovieDB.contract.RecommendationsMoviesContract;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.contract.SimilarMoviesContract;
import com.example.MovieDB.contract.TrailerContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;
import com.example.MovieDB.model.keywords.Keyword;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.movie_trailer.MovieTrailer;
import com.example.MovieDB.model.person.PersonCast;
import com.example.MovieDB.model.reviews.Reviews;
import com.example.MovieDB.persistance.database.entity.MovieEntity;
import com.example.MovieDB.persistance.database.roomdb.DatabaseRepository;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.presenter.KeywordPresenter;
import com.example.MovieDB.presenter.MovieCreditsPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
import com.example.MovieDB.presenter.ReviewPresenter;
import com.example.MovieDB.presenter.SimilarPresenter;
import com.example.MovieDB.presenter.TrailerPresenter;
import com.example.MovieDB.ui.adapter.CastAdapter;
import com.example.MovieDB.ui.adapter.InnerDetailsAdapter;
import com.example.MovieDB.ui.adapter.KeywordAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDetails extends AppCompatActivity implements MovieKeywordContract, TrailerContract, CreditContract, SimilarMoviesContract, RecommendationsMoviesContract, ReviewContract, KeywordAdapter.OnMovieKeywordClickListener<Keyword> {
    private Movies movie;
    private static final String TAG = "TAG";
    private Bundle bundle;
    ProgressBar rateProgressbar;
    private ImageView movieSmallIcon, seenlistIcon, wishlistIcon;
    private LinearLayout directorContainer, recommendationContainer, similarContainer, keywordContainer, crewContainer, castContainer, seenlistContianer, wishlistContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView keywordRecyclerView, castRecyclerView, crewRecyclerView, similarRecyclerView, recommendationRecyclerView;
    private TextView rateNumber, rateCount, reviewCount, overViewContent, directorName, wishlistText, seenlistText;
    private Context context = this;
    private KeywordAdapter<Keyword> keywordAdapter;
    private CastAdapter<Cast> castAdapter;
    private CastAdapter<Crew> crewAdapter;
    private InnerDetailsAdapter<Movies> similarAdapter;
    private ReviewPresenter movieReviewPresenter;
    private TrailerPresenter trailerPresenter;
    private InnerDetailsAdapter<Movies> recommendation;
    private MovieCreditsPresenter movieCreditsPresenter;
    private SimilarPresenter similarPresenter;
    private RecommendationsPresenter recoPresenter;
    private KeywordPresenter keywordPresenter;
    private boolean isLoadingSim = false;
    private boolean isLoadingReco = false;
    private Intent intent;
    private PersonCast cast;
    private String type;
    private RelativeLayout reviewContainer;
    private YouTubePlayerView view;
    private YouTubePlayer player;
    private ProgressDialog dialog;
    private DatabaseRepository repository;
    private MovieSharedPreference.UserPreferences userPreferences;

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @SuppressLint("CheckResult")
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
        keywordPresenter = new KeywordPresenter(this);
        movieCreditsPresenter = new MovieCreditsPresenter(this);
        trailerPresenter = new TrailerPresenter(this);
        recoPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        movieReviewPresenter = new ReviewPresenter(this);
        intent = getIntent();
        type = intent.getStringExtra("type");
        bundle = intent.getExtras();
        if (bundle != null) {
            if (type.equals("one")) {
                movie = (Movies) bundle.getSerializable("movie_object");
                movieReviewPresenter.getMovieReviews(movie.getId());
                keywordPresenter.getMovieKeyword(movie.getId());
                movieCreditsPresenter.getMovieCredits(movie.getId());
                trailerPresenter.getMovieTrailers(movie.getId());
                similarPresenter.getMovieSimilars(movie.getId(), 1);
                recoPresenter.getMovieRecommendations(movie.getId(), 1);
                Picasso.get().load(EndPoints.Image200W + movie.getPosterPath()).error(R.drawable.cinema).into(movieSmallIcon);
                rateNumber.setText(String.valueOf(movie.getVoteAverage()));
                setRate(movie.getVoteAverage());
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
                    movieReviewPresenter.getMovieReviews(cast.getId());
                    keywordPresenter.getMovieKeyword(cast.getId());
                    movieCreditsPresenter.getMovieCredits(cast.getId());
                    trailerPresenter.getMovieTrailers(cast.getId());
                    similarPresenter.getMovieSimilars(cast.getId(), 1);
                    recoPresenter.getMovieRecommendations(cast.getId(), 1);
                    Picasso.get().load(EndPoints.Image200W + cast.getPosterPath()).error(R.drawable.cinema).into(movieSmallIcon);
                    rateNumber.setText(String.valueOf(cast.getVoteAverage()));
                    setRate(cast.getVoteAverage());
                    rateCount.setText(String.valueOf(cast.getVoteCount()));
                    overViewContent.setText(cast.getOverview());
                });
            }
        }
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        repository = DatabaseRepository.getRepo(context);
        wishlistContainer.setOnClickListener(click -> {
            MovieEntity entity = MovieEntity.getMovieEntity(movie, userPreferences.getID());
            if (wishlistContainer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.wishlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background_fill);
                wishlistIcon.setImageResource(R.drawable.favourite_white);
                wishlistText.setTextColor(getResources().getColor(R.color.white));
                repository.getMovieById(entity.getMovie_id())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<MovieEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(MovieEntity entity) {
                                repository.setMovieToWish(entity.getMovie_id());
                            }

                            @Override
                            public void onError(Throwable e) {
                                repository.addMovie(entity);
                                repository.setMovieToWish(entity.getMovie_id());
                            }
                        });
            } else {
                Toast.makeText(context, "item removed from Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background);
                wishlistIcon.setImageResource(R.drawable.whishlist_heart);
                wishlistText.setTextColor(getResources().getColor(R.color.wishlist_color));
                repository.deleteWishMovie(entity.getMovie_id());
            }
        });

        seenlistContianer.setOnClickListener(click -> {
            MovieEntity entity = MovieEntity.getMovieEntity(movie, userPreferences.getID());
            if (seenlistContianer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.seenlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background_fill);
                seenlistIcon.setImageResource(R.drawable.eye_white);
                seenlistText.setTextColor(getResources().getColor(R.color.white));
                repository.getMovieById(entity.getMovie_id())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<MovieEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(MovieEntity entity) {
                        repository.setMovieToSeen(entity.getMovie_id());
                    }

                    @Override
                    public void onError(Throwable e) {
                        repository.addMovie(entity);
                        repository.setMovieToSeen(entity.getMovie_id());
                    }
                });
            } else {
                Toast.makeText(context, "item removed from Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background);
                seenlistIcon.setImageResource(R.drawable.seenlist_eye);
                seenlistText.setTextColor(getResources().getColor(R.color.seenlist_color));
                repository.deleteSeenMovie(entity.getMovie_id());
            }
        });
        repository.getWishMovie(movie.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<MovieEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(MovieEntity entity) {
                if (entity != null) {
                    wishlistContainer.setBackgroundResource(R.drawable.wishlist_background_fill);
                    wishlistIcon.setImageResource(R.drawable.favourite_white);
                    wishlistText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wishlistContainer.setBackgroundResource(R.drawable.wishlist_background);
                    wishlistIcon.setImageResource(R.drawable.whishlist_heart);
                    wishlistText.setTextColor(getResources().getColor(R.color.wishlist_color));
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
        repository.getSeenMovie(movie.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<MovieEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(MovieEntity entity) {
                if (entity != null) {
                    seenlistContianer.setBackgroundResource(R.drawable.seenlist_background_fill);
                    seenlistIcon.setImageResource(R.drawable.eye_white);
                    seenlistText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    seenlistContianer.setBackgroundResource(R.drawable.seenlist_background);
                    seenlistIcon.setImageResource(R.drawable.seenlist_eye);
                    seenlistText.setTextColor(getResources().getColor(R.color.seenlist_color));
                }
            }

            @Override
            public void onError(Throwable e) {

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
                if ((totalItems - 4) <= (totalVisibleItems + currentItem)) {
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
                if ((totalItems - 4) <= (totalVisibleItems + currentItem)) {
                    if (!isLoadingSim) {
                        isLoadingSim = true;
                        similarPresenter.increasePages();
                    }
                } else {
                    isLoadingSim = false;
                }
            }
        });
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        keywordAdapter = new KeywordAdapter<>(context, this);
        keywordRecyclerView.setAdapter(keywordAdapter);
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new InnerDetailsAdapter<>(context);
        similarRecyclerView.setAdapter(similarAdapter);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendation = new InnerDetailsAdapter<>(context);
        recommendationRecyclerView.setAdapter(recommendation);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        castAdapter = new CastAdapter<>(context);
        castRecyclerView.setAdapter(castAdapter);
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        crewAdapter = new CastAdapter<>(context);
        crewRecyclerView.setAdapter(crewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.home_icon) {
            Intent i = new Intent(context, NowPlaying_OnTheAir.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        dialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading Data");
        dialog.show();
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        toolbar = findViewById(R.id.movie_details_toolbar);
        appBarLayout = findViewById(R.id.appbarlayout_container);
        movieSmallIcon = findViewById(R.id.movie_small_icon);
        rateProgressbar = findViewById(R.id.rate_progress_bar);
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
        crewContainer = findViewById(R.id.crew_container);
        castContainer = findViewById(R.id.cast_container);
        recommendationRecyclerView = findViewById(R.id.recommendation_recycler);
        reviewContainer = findViewById(R.id.review_container);
        seenlistContianer = findViewById(R.id.seenlist_container);
        wishlistContainer = findViewById(R.id.wishlist_container);
        seenlistIcon = findViewById(R.id.seenlist_icon);
        wishlistIcon = findViewById(R.id.wishlist_icon);
        seenlistText = findViewById(R.id.seenlist_text);
        wishlistText = findViewById(R.id.wishlist_text);
        view = findViewById(R.id.youtube_player_view);
    }

    @Override
    public void crewListener(List<Crew> crews) {
        if (crews.size() == 0) {
            crewContainer.setVisibility(View.GONE);
        }
        for (Crew c : crews) {
            if (c.getJob().equals("Director")) {
                directorContainer.setVisibility(View.VISIBLE);
                directorName.setText(c.getName());
            }
        }
        crewAdapter.setCastList(crews);
    }

    @Override
    public void castListener(List<Cast> casts) {
        if (casts.size() == 0) {
            castContainer.setVisibility(View.GONE);
        }
        castAdapter.setCastList(casts);
    }

    @Override
    public void keywordListener(List<Keyword> keywords) {
        if (keywords.size() == 0) {
            keywordContainer.setVisibility(View.GONE);
        }
        keywordAdapter.setKeywordList(keywords);
        keywordAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    public void similarListener(List<Movies> similarList) {
        if (similarList.size() == 0) {
            similarContainer.setVisibility(View.GONE);
        }
        similarAdapter.setList(similarList);
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
        recommendation.setList(recommendationsList);
        recommendation.notifyDataSetChanged();
    }

    @Override
    public void reviewListener(List<Reviews> reviews) {

    }

    @Override
    public void reviewResultListener(Integer totalReviews) {
        if (totalReviews > 0) {
            reviewContainer.setVisibility(View.VISIBLE);
            reviewCount.setText(getResources().getString(R.string.review_count, totalReviews));
            reviewContainer.setOnClickListener(click -> {
                if (totalReviews > 0) {
                    Intent i = new Intent(context, ReviewActivity.class);
                    i.putExtra("movie_id", movie.getId());
                    i.putExtra("movie_title", movie.getTitle());
                    startActivity(i);
                }
            });

        }
    }


    @Override
    public void OnMovieKeywordClick(Keyword keyword) {
        if (context.getClass().equals(MovieDetails.class)) {
            Intent i = new Intent(context, Search.class);
            i.putExtra("keyword_id", keyword.getId());
            i.putExtra("keyword_name", keyword.getName());
            i.putExtra("movie_keyword", "movie_details");
            startActivity(i);
        }
    }

    private void setRate(double rateNumber) {
        rateProgressbar.setMax(10);
        switch ((int) Math.round(rateNumber)) {
            case 0:
            case 1:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.yellow_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.yellow_A200));
                break;
            case 2:
            case 3:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.light_green_900));
                break;
            case 4:
            case 5:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.light_blue_900));
                break;
            case 6:
            case 7:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.purple_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.purple_900));
                break;
            case 8:
            case 9:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.red_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.red_900));
                break;
            default:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.gray_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.blue_gray_700));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.release();
    }

    @Override
    public void trailers(List<MovieTrailer> trailer) {
        if (trailer.size() > 0) {
            String videoId = trailer.get(0).getKey();
            Log.e("life", "first " + videoId);
            initializeYouTube(videoId);
        }
    }

    private void initializeYouTube(String videoId) {
        getLifecycle().addObserver(view);
        Log.e("life", "second " + videoId);
        view.getYouTubePlayerWhenReady(youTubePlayer -> {
            player = youTubePlayer;
            YouTubePlayerUtils.loadOrCueVideo(youTubePlayer,
                    getLifecycle(),
                    videoId,
                    0f);
            Log.e("life", "third " + videoId);
            appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
                if (i == 0) {
                    youTubePlayer.play();
                } else {
                    youTubePlayer.pause();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_icons, menu);
        return true;
    }
}