package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.RingtoneManager;
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
import com.example.MovieDB.contract.CreditContract;
import com.example.MovieDB.contract.MovieSeriesImageContract;
import com.example.MovieDB.contract.RecommendationsSeriesContract;
import com.example.MovieDB.contract.SeasonContract;
import com.example.MovieDB.contract.SimilarSeriesContract;
import com.example.MovieDB.design.CirclePagerIndicatorDecoration;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;
import com.example.MovieDB.model.images_models.MovieSeriesImageDetails;
import com.example.MovieDB.model.series.SeriesDetailsModel;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.model.series.SeriesSeasons;
import com.example.MovieDB.model.series_seasons.SeasonDetailsModel;
import com.example.MovieDB.model.series_seasons.SeasonEpisodes;
import com.example.MovieDB.presenter.ImagesPresenter;
import com.example.MovieDB.presenter.MovieCreditsPresenter;
import com.example.MovieDB.presenter.RecommendationsPresenter;
import com.example.MovieDB.presenter.SeasonPresenter;
import com.example.MovieDB.presenter.SimilarPresenter;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.ui.adapter.ImageAdapter;
import com.example.MovieDB.ui.adapter.InnerDetailsAdapter;
import com.example.MovieDB.ui.adapter.SeasonAdapter;
import com.example.MovieDB.ui.adapter.StarsAdapter;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EpisodeDetails extends AppCompatActivity implements RecommendationsSeriesContract, CreditContract, InnerDetailsAdapter.EpisodeCLickListener, SimilarSeriesContract, SeasonContract, MovieSeriesImageContract, SeasonAdapter.SeasonClickListener, NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private Intent i;
    private Toolbar toolbar;
    private SeriesSeasons seasons;
    private SeriesDetailsModel series;
    private SeasonEpisodes episode;
    private LinearLayout recommendationContainer, similarContainer, episodeContainer, seasonContainer, castContainer, guestStarsContainer;
    private RecyclerView recommendationRecyclerView, similarRecyclerView, castRecyclerView, guestStarsRecyclerView, episodesRecyclerView, seasonsRecyclerView, episodeImagesRecyclerView;
    private TextView episodeName, seasonNumber, episodeOverview;
    private SeasonAdapter seasonAdapter;
    private InnerDetailsAdapter<SeriesResult> similarAdapter;
    private InnerDetailsAdapter<SeriesResult> recommendationAdapter;
    private InnerDetailsAdapter<SeasonEpisodes> episodesAdapter;
    private ImageView episodeImage;
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
    private StarsAdapter<Cast> castAdapter;
    private StarsAdapter<Crew> guestStarsAdapter;
    private MovieCreditsPresenter creditsPresenter;
    private Gson g;
    private String seasonsJson, seriesJson, episodeJson;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;
    RingtoneManager ringtoneManager;


    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_details);
        initView();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        g = new Gson();
        i = getIntent();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        seasonsJson = i.getStringExtra("season_object");
        seriesJson = i.getStringExtra("series_object");
        episodeJson = i.getStringExtra("episode_object");
        seasons = g.fromJson(seasonsJson, SeriesSeasons.class);
        series = g.fromJson(seriesJson, SeriesDetailsModel.class);
        episode = g.fromJson(episodeJson, SeasonEpisodes.class);
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
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) ->

        {
            if (i == 0) {
                collapsingToolbarLayout.setTitle("");
                collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                scrollRecycler();
            } else {
                handler.removeCallbacksAndMessages(null);
                collapsingToolbarLayout.setTitle(seasons.getName());
            }
        });

        scrollRecycler();

    }

    private void initView() {
        dialog = Utils.showLoadingDialog(context);
        dialog.show();
        toolbar = findViewById(R.id.toolbar);
        castContainer = findViewById(R.id.cast_container);
        guestStarsContainer = findViewById(R.id.guest_container);
        recommendationRecyclerView = findViewById(R.id.recommendation_recyclerView);
        castRecyclerView = findViewById(R.id.cast_recyclerView);
        guestStarsRecyclerView = findViewById(R.id.guest_recyclerView);
        similarRecyclerView = findViewById(R.id.similar_recyclerView);
        episodesRecyclerView = findViewById(R.id.episodes_recyclerView);
        seasonsRecyclerView = findViewById(R.id.seasons_recyclerView);
        episodeImagesRecyclerView = findViewById(R.id.episode_images_recyclerView);
        seasonNumber = findViewById(R.id.season_number);
        episodeName = findViewById(R.id.episode_name);
        episodeOverview = findViewById(R.id.episode_over_view);
        episodeImage = findViewById(R.id.episode_image);
        collapsingToolbarLayout = findViewById(R.id.actor_collapsing_layout);
        appBarLayout = findViewById(R.id.actor_appbar_layout);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        episodeImagesRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
        dialog.dismiss();
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
        episodeImagesRecyclerView.setAdapter(imageAdapter);
        episodeImagesRecyclerView.addItemDecoration(new CirclePagerIndicatorDecoration());
    }

    private void scrollRecycler() {
        episodeImagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        episodeName.setText(episode.getName());
        seasonNumber.setText(getResources().getString(R.string.season_number, seasons.getSeasonNumber()));
        Picasso.get().load(EndPoints.Image200W + episode.getStillPath()).into(episodeImage);
        episodeOverview.setText(episode.getOverview());
        seasonPresenter = new SeasonPresenter(this);
        seasonPresenter.getSeason(episode.getShowId(), seasons.getSeasonNumber());
        creditsPresenter = new MovieCreditsPresenter(this);
        creditsPresenter.getEpisodeCredit(episode.getId(), episode.getSeasonNumber(), episode.getEpisodeNumber());
        recommendationsPresenter = new RecommendationsPresenter(this);
        similarPresenter = new SimilarPresenter(this);
        imagesPresenter = new ImagesPresenter(this);
        recommendationsPresenter.getSeriesRecommendations(episode.getShowId(), 1);
        similarPresenter.getSeriesSimilars(episode.getShowId(), 1);
        imagesPresenter.getEpisodeImages(episode.getShowId(), episode.getSeasonNumber(), episode.getEpisodeNumber());
        episodeImagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        similarAdapter = new InnerDetailsAdapter<>(context);
        similarRecyclerView.setAdapter(similarAdapter);
        seasonsRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        seasonAdapter = new SeasonAdapter(context, this);
        seasonsRecyclerView.setAdapter(seasonAdapter);
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        episodesAdapter = new InnerDetailsAdapter<>(context);
        episodesRecyclerView.setAdapter(episodesAdapter);
        episodesAdapter.setEpisodeCLickListener(this);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recommendationAdapter = new InnerDetailsAdapter<>(context);
        recommendationRecyclerView.setAdapter(recommendationAdapter);
        seasonAdapter.setMovies(series.getSeasons());
        seasonAdapter.notifyDataSetChanged();
        castRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        guestStarsRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        castAdapter = new StarsAdapter<>(context);
        guestStarsAdapter = new StarsAdapter<>(context);
        castRecyclerView.setAdapter(castAdapter);
        guestStarsRecyclerView.setAdapter(guestStarsAdapter);
    }

    @Override
    public void onSeasonClickListener(SeriesSeasons season) {
        Intent i = new Intent(context, SeasonDetails.class);
        String seasonJson = g.toJson(seasons);
        String seriesJson = g.toJson(series);
        i.putExtra("season_object", seasonJson);
        i.putExtra("series_object", seriesJson);
        startActivity(i);
    }

    @Override
    public void seasonListener(SeasonDetailsModel season) {
        episodesAdapter.setList(season.getEpisodes());
        episodesAdapter.notifyDataSetChanged();
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
    public void crewListener(List<Crew> crews) {
        guestStarsAdapter.setStarList(crews);
        guestStarsAdapter.notifyDataSetChanged();
    }

    @Override
    public void castListener(List<Cast> casts) {
        castAdapter.setStarList(casts);
        castAdapter.notifyDataSetChanged();
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
