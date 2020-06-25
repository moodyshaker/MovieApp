package com.example.MovieDB.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.example.MovieDB.contract.MovieSeriesImageContract;
import com.example.MovieDB.contract.RecommendationsSeriesContract;
import com.example.MovieDB.contract.SeasonContract;
import com.example.MovieDB.contract.SimilarSeriesContract;
import com.example.MovieDB.design.CirclePagerIndicatorDecoration;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.images_models.MovieSeriesImageDetails;
import com.example.MovieDB.model.series.SeriesDetailsModel;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.model.series.SeriesSeasons;
import com.example.MovieDB.model.series_seasons.SeasonDetailsModel;
import com.example.MovieDB.model.series_seasons.SeasonEpisodes;
import com.example.MovieDB.presenter.ImagesPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
import com.example.MovieDB.presenter.SeasonPresenter;
import com.example.MovieDB.presenter.SimilarPresenter;
import com.example.MovieDB.ui.adapter.ImageAdapter;
import com.example.MovieDB.ui.adapter.InnerDetailsAdapter;
import com.example.MovieDB.ui.adapter.SeasonAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeasonDetails extends AppCompatActivity implements RecommendationsSeriesContract, SimilarSeriesContract, SeasonContract, MovieSeriesImageContract, SeasonAdapter.SeasonClickListener, InnerDetailsAdapter.EpisodeCLickListener {

    private Context context = this;
    private Bundle bundle;
    private Toolbar toolbar;
    private SeriesSeasons seasons;
    private SeriesDetailsModel series;
    private LinearLayout recommendationContainer, similarContainer, episodeContainer, seasonContainer;
    private RecyclerView recommendationRecyclerView, similarRecyclerView, episodesRecyclerView, seasonsRecyclerView, seasonImagesRecyclerView;
    private TextView seriesName, seasonNumber, seasonOverview;
    private SeasonAdapter seasonAdapter;
    private InnerDetailsAdapter<SeriesResult> similarAdapter;
    private InnerDetailsAdapter<SeriesResult> recommendationAdapter;
    private InnerDetailsAdapter<SeasonEpisodes> episodesAdapter;
    private ImageView seasonImage;
    private Handler handler;
    private boolean isLoadingSim = false;
    private boolean isLoadingReco = false;
    private RecommendationsPresenter recommendationsPresenter;
    private SeasonPresenter seasonPresenter;
    private SimilarPresenter similarPresenter;
    private ImagesPresenter imagesPresenter;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageAdapter<MovieSeriesImageDetails> imageAdapter;
    private ProgressDialog dialog;
    private ActionBar actionBar;

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_details);
        initView();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            seasons = (SeriesSeasons) bundle.getSerializable("season_object");
            series = (SeriesDetailsModel) bundle.getSerializable("series_object");
        }
        initPresenterAndRecyclerView();
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
                        recommendationsPresenter.increaseSeriesPages();
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
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i == 0) {
                collapsingToolbarLayout.setTitle("");
                collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                scrollRecycler();
            } else {
                handler.removeCallbacksAndMessages(null);
                collapsingToolbarLayout.setTitle(series.getName());
            }
        });
        scrollRecycler();
    }

    private void initView() {
        dialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading Data");
        dialog.show();
        toolbar = findViewById(R.id.toolbar);
        recommendationRecyclerView = findViewById(R.id.recommendation_recyclerView);
        similarRecyclerView = findViewById(R.id.similar_recyclerView);
        episodesRecyclerView = findViewById(R.id.episodes_recyclerView);
        seasonsRecyclerView = findViewById(R.id.seasons_recyclerView);
        seasonImagesRecyclerView = findViewById(R.id.season_images_recyclerView);
        seasonNumber = findViewById(R.id.season_number);
        seriesName = findViewById(R.id.series_name);
        seasonOverview = findViewById(R.id.season_over_view);
        seasonImage = findViewById(R.id.season_image);
        collapsingToolbarLayout = findViewById(R.id.actor_collapsing_layout);
        appBarLayout = findViewById(R.id.actor_appbar_layout);
        seasonImagesRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
    public void RecommendationsSeriesListener(List<SeriesResult> seriesDetailsModels) {
        if (seriesDetailsModels.size() == 0) {
            recommendationContainer.setVisibility(View.GONE);
        }
        recommendationAdapter.setList(seriesDetailsModels);
        recommendationAdapter.notifyDataSetChanged();
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
    public void movieSeriesImageListener(List<MovieSeriesImageDetails> movieSeriesImageDetails) {
        imageAdapter = new ImageAdapter<>(this, movieSeriesImageDetails);
        seasonImagesRecyclerView.setAdapter(imageAdapter);
        seasonImagesRecyclerView.addItemDecoration(new CirclePagerIndicatorDecoration());
    }

    private void scrollRecycler() {
        seasonImagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int currentPosition = manager.findFirstVisibleItemPosition();
                handler = new Handler();
                handler.postDelayed(() -> {
                    int x = currentPosition;
                    if (currentPosition >= (manager.getItemCount() - 1)) {
                        x = 0;
                    } else {
                        x += 1;
                    }
                    recyclerView.smoothScrollToPosition(x);
                }, 3000);
            }
        });
    }

    private void initPresenterAndRecyclerView() {
        seriesName.setText(series.getName());
        seasonNumber.setText(getResources().getString(R.string.season_number, seasons.getSeasonNumber()));
        Picasso.get().load(EndPoints.Image200W + seasons.getPosterPath()).into(seasonImage);
        seasonOverview.setText(seasons.getOverview());
        seasonPresenter = new SeasonPresenter(this);
        seasonPresenter.getSeason(series.getId(), seasons.getSeasonNumber());
        recommendationsPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        imagesPresenter = new ImagesPresenter(this);
        recommendationsPresenter.getSeriesRecommendations(series.getId(), 1);
        similarPresenter.getSeriesSimilars(series.getId(), 1);
        imagesPresenter.getSeasonImages(series.getId(), seasons.getSeasonNumber());
        seasonImagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new InnerDetailsAdapter<>(context);
        similarRecyclerView.setAdapter(similarAdapter);
        seasonsRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        seasonAdapter = new SeasonAdapter(context, this);
        seasonsRecyclerView.setAdapter(seasonAdapter);
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        episodesAdapter = new InnerDetailsAdapter<>(context);
        episodesAdapter.setEpisodeCLickListener(this);
        episodesRecyclerView.setAdapter(episodesAdapter);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendationAdapter = new InnerDetailsAdapter<>(context);
        recommendationRecyclerView.setAdapter(recommendationAdapter);
        seasonAdapter.setMovies(series.getSeasons());
        seasonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSeasonClickListenr(SeriesSeasons season) {
        Intent i = new Intent(context, SeasonDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("season_object", season);
        bundle.putSerializable("series_object", series);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void seasonListener(SeasonDetailsModel season) {
        episodesAdapter.setList(season.getEpisodes());
        episodesAdapter.notifyDataSetChanged();
        dialog.dismiss();
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

    @Override
    protected void onStart() {
        super.onStart();
        scrollRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_icons, menu);
        return true;
    }

    @Override
    public void onEpisodeClickListener(SeasonEpisodes episodes) {
        Intent i = new Intent(context, EpisodeDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("season_object", seasons);
        bundle.putSerializable("series_object", series);
        bundle.putSerializable("episode_object", episodes);
        i.putExtras(bundle);
        startActivity(i);
    }
}