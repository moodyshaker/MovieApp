package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.ui.adapter.ImageAdapter;
import com.example.MovieDB.ui.adapter.InnerDetailsAdapter;
import com.example.MovieDB.ui.adapter.SeasonAdapter;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeasonDetails extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener, RecommendationsSeriesContract, SimilarSeriesContract, SeasonContract, MovieSeriesImageContract, SeasonAdapter.SeasonClickListener, InnerDetailsAdapter.EpisodeCLickListener {

    private Context context = this;
    private Activity activity = this;
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
    private AlertDialog dialog;
    private ActionBar actionBar;
    private Gson g;
    private Intent i;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;
    private String seasonsJson, seriesJson;


    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        unregisterReceiver(receiver);
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
        g = new Gson();
        i = getIntent();
        seasonsJson = i.getStringExtra("season_object");
        seriesJson = i.getStringExtra("series_object");
        seasons = g.fromJson(seasonsJson, SeriesSeasons.class);
        series = g.fromJson(seriesJson, SeriesDetailsModel.class);
        initPresenterAndRecyclerView();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
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
        dialog = Utils.showLoadingDialog(context);
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
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
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
        seriesName.setText(seasons.getName());
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
    public void onSeasonClickListener(SeriesSeasons season) {
        Intent i = new Intent(context, SeasonDetails.class);
        String seasonJson = g.toJson(season);
        String seriesJson = g.toJson(series);
        i.putExtra("season_object", seasonJson);
        i.putExtra("series_object", seriesJson);
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
        registerReceiver(receiver, filter);
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
        String seasonJson = g.toJson(seasons);
        String seriesJson = g.toJson(series);
        String episodesJson = g.toJson(episodes);
        i.putExtra("season_object", seasonJson);
        i.putExtra("series_object", seriesJson);
        i.putExtra("episode_object", episodesJson);
        startActivity(i);
    }

    @Override
    public void callbackListener(boolean isConnected) {
        if (isConnected) {
            connectedContainer.setVisibility(View.VISIBLE);
            disconnectedContainer.setVisibility(View.GONE);
            h.postDelayed(() -> connectionDialog.dismiss(), 1000);
        } else {
            connectedContainer.setVisibility(View.GONE);
            disconnectedContainer.setVisibility(View.VISIBLE);
            connectionDialog.show();
        }
    }
}
