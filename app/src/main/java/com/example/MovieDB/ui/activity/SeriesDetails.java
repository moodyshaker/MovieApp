package com.example.MovieDB.ui.activity;

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
import com.example.MovieDB.contract.RecommendationsSeriesContract;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.contract.SeriesDetailsContract;
import com.example.MovieDB.contract.SeriesKeywordContract;
import com.example.MovieDB.contract.SimilarSeriesContract;
import com.example.MovieDB.contract.TrailerContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;
import com.example.MovieDB.model.keywords.SeriesKeywords;
import com.example.MovieDB.model.movie_trailer.MovieTrailer;
import com.example.MovieDB.model.reviews.Reviews;
import com.example.MovieDB.model.series.SeriesDetailsModel;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.model.series.SeriesSeasons;
import com.example.MovieDB.persistance.database.entity.SeriesEntity;
import com.example.MovieDB.persistance.database.roomdb.DatabaseRepository;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.presenter.KeywordPresenter;
import com.example.MovieDB.presenter.MovieCreditsPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
import com.example.MovieDB.presenter.ReviewPresenter;
import com.example.MovieDB.presenter.SeriesPresenter;
import com.example.MovieDB.presenter.SimilarPresenter;
import com.example.MovieDB.presenter.TrailerPresenter;
import com.example.MovieDB.ui.adapter.CastAdapter;
import com.example.MovieDB.ui.adapter.InnerDetailsAdapter;
import com.example.MovieDB.ui.adapter.KeywordAdapter;
import com.example.MovieDB.ui.adapter.SeasonAdapter;
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

public class SeriesDetails extends AppCompatActivity implements SeriesDetailsContract, SeriesKeywordContract, SeasonAdapter.SeasonClickListener, TrailerContract, CreditContract, SimilarSeriesContract, RecommendationsSeriesContract, ReviewContract, KeywordAdapter.OnMovieKeywordClickListener<SeriesKeywords> {
    private SeriesResult series;
    private static final String TAG = "TAG";
    private Bundle bundle;
    private SeriesDetailsModel details;
    private ProgressBar rateProgressbar;
    private ImageView seriesSmallIcon, seenlistIcon, wishlistIcon;
    private LinearLayout directorContainer, recommendationContainer, similarContainer, keywordContainer, seenlistContianer, crewContainer, castContainer, wishlistContainer;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView keywordRecyclerView, castRecyclerView, crewRecyclerView, similarRecyclerView, recommendationRecyclerView, seasonRecyclerView;
    private TextView rateNumber, rateCount, reviewCount, overViewContent, directorName, wishlistText, seasonNumber, episodeNumber, seenlistText;
    private Context context = this;
    private KeywordAdapter<SeriesKeywords> keywordAdapter;
    private SeriesPresenter seriesPresenter;
    private CastAdapter<Cast> castAdapter;
    private CastAdapter<Crew> crewAdapter;
    private InnerDetailsAdapter<SeriesResult> similarAdapter;
    private ReviewPresenter reviewPresenter;
    private TrailerPresenter trailerPresenter;
    private InnerDetailsAdapter<SeriesResult> recommendation;
    private MovieCreditsPresenter movieCreditsPresenter;
    private SimilarPresenter similarPresenter;
    private RecommendationsPresenter recoPresenter;
    private KeywordPresenter keywordPresenter;
    private boolean isLoadingSim = false;
    private boolean isLoadingReco = false;
    private Intent intent;
    private RelativeLayout reviewContainer;
    private YouTubePlayerView view;
    private YouTubePlayer player;
    private SeasonAdapter seasonAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_details);
        initView();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            series = (SeriesResult) bundle.getSerializable("series_object");
        }
        seriesPresenter = new SeriesPresenter(this);
        seriesPresenter.getSeriesById(series.getId());
        keywordPresenter = new KeywordPresenter(this);
        movieCreditsPresenter = new MovieCreditsPresenter(this);
        trailerPresenter = new TrailerPresenter(this);
        recoPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        reviewPresenter = new ReviewPresenter(this);
        repository = DatabaseRepository.getRepo(context);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        wishlistContainer.setOnClickListener(click -> {
            SeriesEntity entity = SeriesEntity.getSeriesEntity(series, userPreferences.getID());
            if (wishlistContainer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.wishlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background_fill);
                wishlistIcon.setImageResource(R.drawable.favourite_white);
                wishlistText.setTextColor(getResources().getColor(R.color.white));
                repository.getSeriesById(entity.getSeries_id())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<SeriesEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(SeriesEntity seriesEntity) {
                                repository.setSeriesToWish(seriesEntity.getSeries_id());
                            }

                            @Override
                            public void onError(Throwable e) {
                                repository.addSeries(entity);
                                repository.setSeriesToWish(entity.getSeries_id());
                            }
                        });
            } else {
                Toast.makeText(context, "item removed from Wishlist", Toast.LENGTH_SHORT).show();
                wishlistContainer.setBackgroundResource(R.drawable.wishlist_background);
                wishlistIcon.setImageResource(R.drawable.whishlist_heart);
                wishlistText.setTextColor(getResources().getColor(R.color.wishlist_color));
                repository.deleteWishSeries(entity.getSeries_id());
            }
        });
        seenlistContianer.setOnClickListener(click -> {
            SeriesEntity entity = SeriesEntity.getSeriesEntity(series, userPreferences.getID());
            if (seenlistContianer.getBackground().getConstantState() == getResources().getDrawable(R.drawable.seenlist_background).getConstantState()) {
                Toast.makeText(context, "item added to Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background_fill);
                seenlistIcon.setImageResource(R.drawable.eye_white);
                seenlistText.setTextColor(getResources().getColor(R.color.white));
                repository.getSeriesById(entity.getSeries_id())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<SeriesEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(SeriesEntity seriesEntity) {
                                repository.setSeriesToSeen(seriesEntity.getSeries_id());
                            }

                            @Override
                            public void onError(Throwable e) {
                                repository.addSeries(entity);
                                repository.setSeriesToSeen(entity.getSeries_id());
                            }
                        });
            } else {
                Toast.makeText(context, "item removed from Seenlist", Toast.LENGTH_SHORT).show();
                seenlistContianer.setBackgroundResource(R.drawable.seenlist_background);
                seenlistIcon.setImageResource(R.drawable.seenlist_eye);
                seenlistText.setTextColor(getResources().getColor(R.color.seenlist_color));
                repository.deleteSeenSeries(entity.getSeries_id());
            }
        });
        repository.getSeenSeries(series.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<SeriesEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(SeriesEntity seriesEntity) {
                        if (seriesEntity != null) {
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
        repository.getWishSeries(series.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<SeriesEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(SeriesEntity seriesEntity) {
                        if (seriesEntity != null) {
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
                        recoPresenter.increaseSeriesPages();
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
                        similarPresenter.increaseSeriesPages();
                    }
                } else {
                    isLoadingSim = false;
                }
            }
        });
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        keywordAdapter = new KeywordAdapter<>(context, this);
        keywordRecyclerView.setAdapter(keywordAdapter);
        seasonRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        seasonAdapter = new SeasonAdapter(context, this);
        seasonRecyclerView.setAdapter(seasonAdapter);
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new InnerDetailsAdapter<>(context);
        similarRecyclerView.setAdapter(similarAdapter);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendation = new InnerDetailsAdapter<>(context);
        recommendationRecyclerView.setAdapter(recommendation);
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        crewAdapter = new CastAdapter<>(context);
        crewRecyclerView.setAdapter(crewAdapter);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        castAdapter = new CastAdapter<>(context);
        castRecyclerView.setAdapter(castAdapter);
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
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        dialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading Data");
        dialog.show();
        toolbar = findViewById(R.id.movie_details_toolbar);
        appBarLayout = findViewById(R.id.appbarlayout_container);
        seriesSmallIcon = findViewById(R.id.movie_small_icon);
        rateProgressbar = findViewById(R.id.rate_progress_bar);
        rateNumber = findViewById(R.id.rate_number_text);
        rateCount = findViewById(R.id.rating_people_count_text);
        overViewContent = findViewById(R.id.over_view_content);
        castRecyclerView = findViewById(R.id.cast_recycler);
        seasonRecyclerView = findViewById(R.id.season_recycler);
        crewRecyclerView = findViewById(R.id.crew_recycler);
        keywordRecyclerView = findViewById(R.id.keyword_recycler);
        reviewCount = findViewById(R.id.review_count);
        similarRecyclerView = findViewById(R.id.similar_recycler);
        directorName = findViewById(R.id.director_name);
        directorContainer = findViewById(R.id.director_container);
        episodeNumber = findViewById(R.id.episode_number);
        seasonNumber = findViewById(R.id.seasons_number);
        recommendationContainer = findViewById(R.id.recommendation_container);
        similarContainer = findViewById(R.id.similar_container);
        keywordContainer = findViewById(R.id.keyword_container);
        castContainer = findViewById(R.id.cast_container);
        crewContainer = findViewById(R.id.crew_container);
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
    public void keywordListener(List<SeriesKeywords> keywords) {
        if (keywords.size() == 0) {
            keywordContainer.setVisibility(View.GONE);
        }
        keywordAdapter.setKeywordList(keywords);
        keywordAdapter.notifyDataSetChanged();
        dialog.dismiss();
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
                    i.putExtra("series_id", series.getId());
                    i.putExtra("series_title", series.getName());
                    startActivity(i);
                }
            });
        }
    }

    private void setRate(double rateNumber) {
        rateProgressbar.setMax(10);
        switch ((int) Math.round(rateNumber)) {
            case 0:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.cyan_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.cyan_900));
                break;
            case 1:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.yellow_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.yellow_A200));
                break;
            case 2:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.brown_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.brown_900));
                break;
            case 3:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.light_green_900));
                break;
            case 4:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.amber_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.amber_900));
                break;
            case 5:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.light_blue_900));
                break;
            case 6:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.white_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.white));
                break;
            case 7:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.purple_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.purple_900));
                break;
            case 8:
                rateProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.pink_progress_bar));
                rateProgressbar.setProgress((int) rateNumber);
                rateCount.setTextColor(getResources().getColor(R.color.pink_A200));
                break;
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
            String videoId = trailer.get(trailer.size() - 1).getKey();
            initializeYouTube(videoId);
        }
    }

    private void initializeYouTube(String videoId) {
        getLifecycle().addObserver(view);
        view.getYouTubePlayerWhenReady(youTubePlayer -> {
            player = youTubePlayer;
            YouTubePlayerUtils.loadOrCueVideo(youTubePlayer,
                    getLifecycle(),
                    videoId,
                    0f);
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
    public void SeriesListener(SeriesDetailsModel details) {
        this.details = details;
        reviewPresenter.getSeriesReviews(details.getId());
        keywordPresenter.getSeriesKeyword(details.getId());
        movieCreditsPresenter.getSeriesCredits(details.getId());
        trailerPresenter.getSeriesTrailers(details.getId());
        similarPresenter.getSeriesSimilars(details.getId(), 1);
        recoPresenter.getSeriesRecommendations(details.getId(), 1);
        episodeNumber.setText(String.valueOf(details.getNumberOfEpisodes()));
        seasonNumber.setText(String.valueOf(details.getNumberOfSeasons()));
        seasonAdapter.setMovies(details.getSeasons());
        seasonAdapter.notifyDataSetChanged();
        Picasso.get().load(EndPoints.Image200W + details.getPosterPath()).error(R.drawable.cinema).into(seriesSmallIcon);
        rateNumber.setText(String.valueOf(details.getVoteAverage()));
        setRate(details.getVoteAverage());
        rateCount.setText(String.valueOf(details.getVoteCount()));
        overViewContent.setText(details.getOverview());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i == 0) {
                collapsingToolbarLayout.setTitle("");
                collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.blue_gray_100));
            } else {
                collapsingToolbarLayout.setTitle(details.getName());
                collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.blue_gray_100));
            }
        });
    }

    @Override
    public void RecommendationsSeriesListener(List<SeriesResult> seriesDetailsModels) {
        if (seriesDetailsModels.size() == 0) {
            recommendationContainer.setVisibility(View.GONE);
        }
        recommendation.setList(seriesDetailsModels);
        recommendation.notifyDataSetChanged();
    }

    @Override
    public void similarSeriesListener(List<SeriesResult> seriesDetailsModels) {
        if (seriesDetailsModels.size() == 0) {
            similarContainer.setVisibility(View.GONE);
        }
        similarAdapter.setList(seriesDetailsModels);
        similarAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnMovieKeywordClick(SeriesKeywords object) {

    }

    @Override
    public void onSeasonClickListenr(SeriesSeasons season) {
        Intent i = new Intent(context, SeasonDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("season_object", season);
        bundle.putSerializable("series_object", details);
        i.putExtras(bundle);
        context.startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_icons, menu);
        return true;
    }
}

