package com.example.MovieDB.ui.activity;

import android.annotation.SuppressLint;
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
import com.example.MovieDB.contract.PersonContract;
import com.example.MovieDB.contract.PersonCreditsContract;
import com.example.MovieDB.contract.PersonImageContract;
import com.example.MovieDB.design.CirclePagerIndicatorDecoration;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;
import com.example.MovieDB.model.credit_search.CreditResult;
import com.example.MovieDB.model.images_models.PersonImageDetails;
import com.example.MovieDB.model.person.Person;
import com.example.MovieDB.model.person.PersonCast;
import com.example.MovieDB.model.person.PersonCrew;
import com.example.MovieDB.model.series_episodes.SeriesCrew;
import com.example.MovieDB.model.series_episodes.SeriesGuestStar;
import com.example.MovieDB.presenter.ImagesPresenter;
import com.example.MovieDB.presenter.PersonCreditsPresenter;
import com.example.MovieDB.presenter.PersonPresenter;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.ui.adapter.ImageAdapter;
import com.example.MovieDB.ui.adapter.PersonCastAdapter;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActorActivity extends AppCompatActivity implements PersonImageContract, PersonCreditsContract, PersonContract, NetworkReceiver.NetworkCallbackListener {

    private RecyclerView personImagesRecyclerView, knownForRecyclerView;
    private TextView actorName, actorJob, actorOverView, actorDate, title, actorAge;
    private ImageView actorImage;
    private Handler handler;
    private ImageAdapter<PersonImageDetails> imageAdapter;
    private PersonCastAdapter personCastAdapter;
    private ImagesPresenter imagesPresenter;
    private PersonPresenter personPresenter;
    private PersonCreditsPresenter personCreditsPresenter;
    private Toolbar toolbar;
    private Bundle bundle;
    private Crew crew;
    private Cast cast;
    private SeriesCrew seriesCrew;
    private SeriesGuestStar seriesGuestStar;
    private CreditResult creditResult;
    private Intent intent;
    private int id;
    private ActionBar actionBar;
    private Context context = this;
    private Activity activity = this;
    private SimpleDateFormat formatter;
    private Date date;
    private String finalDate;
    private LinearLayout dateContainer;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int year;
    private AlertDialog dialog;
    private Gson g;
    private String crewJson, castJson, creditResultJson, seriesCrewJson, seriesGuestStarJson;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        initUi();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        g = new Gson();
        intent = getIntent();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        crewJson = intent.getStringExtra("Crew");
        castJson = intent.getStringExtra("Cast");
        creditResultJson = intent.getStringExtra("credit_result");
        seriesCrewJson = intent.getStringExtra("SeriesCrew");
        seriesGuestStarJson = intent.getStringExtra("SeriesGuestCrew");
        crew = g.fromJson(crewJson, Crew.class);
        cast = g.fromJson(castJson, Cast.class);
        creditResult = g.fromJson(creditResultJson, CreditResult.class);
        seriesCrew = g.fromJson(seriesCrewJson, SeriesCrew.class);
        seriesGuestStar = g.fromJson(seriesGuestStarJson, SeriesGuestStar.class);
        if (intent.getStringExtra("type").equals("crew")) {
            id = crew.getId();
        } else if (intent.getStringExtra("type").equals("cast")) {
            id = cast.getId();
        } else if (intent.getStringExtra("type").equals("search")) {
            id = creditResult.getId();
        } else if (intent.getStringExtra("type").equals("series_crew")) {
            id = seriesCrew.getId();
        } else if (intent.getStringExtra("type").equals("series_guest")) {
            id = seriesGuestStar.getId();
        }
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i == 0) {
                if (intent.getStringExtra("type").equals("crew")) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.gray_100));
                } else if (intent.getStringExtra("type").equals("cast")) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                } else if (intent.getStringExtra("type").equals("search")) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                } else if (intent.getStringExtra("type").equals("series_crew")) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                } else if (intent.getStringExtra("type").equals("series_guest")) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.gray_100));
                }
                scrollRecycler();
            } else {
                handler.removeCallbacksAndMessages(null);
                if (intent.getStringExtra("type").equals("crew")) {
                    collapsingToolbarLayout.setTitle(crew.getName());
                } else if (intent.getStringExtra("type").equals("cast")) {
                    collapsingToolbarLayout.setTitle(cast.getName());
                } else if (intent.getStringExtra("type").equals("search")) {
                    collapsingToolbarLayout.setTitle(creditResult.getName());
                } else if (intent.getStringExtra("type").equals("series_crew")) {
                    collapsingToolbarLayout.setTitle(seriesCrew.getName());
                } else if (intent.getStringExtra("type").equals("series_guest")) {
                    collapsingToolbarLayout.setTitle(seriesGuestStar.getName());
                }
            }
        });
        scrollRecycler();
        initPresenter();
    }

    private void initPresenter() {
        personCreditsPresenter = new PersonCreditsPresenter(this);
        imagesPresenter = new ImagesPresenter(this);
        personPresenter = new PersonPresenter(this);
        personPresenter.getPerson(id);
    }

    private void initUi() {
        dialog = Utils.showLoadingDialog(context);
        dialog.show();
        toolbar = findViewById(R.id.toolbar);
        actorName = findViewById(R.id.actor_name);
        actorJob = findViewById(R.id.actor_job);
        dateContainer = findViewById(R.id.birth_of_date_container);
        actorOverView = findViewById(R.id.actor_over_view);
        actorDate = findViewById(R.id.actor_date_of_birth);
        actorImage = findViewById(R.id.actor_image);
        personImagesRecyclerView = findViewById(R.id.actor_images);
        knownForRecyclerView = findViewById(R.id.known_for_movies);
        collapsingToolbarLayout = findViewById(R.id.actor_collapsing_layout);
        appBarLayout = findViewById(R.id.actor_appbar_layout);
        actorAge = findViewById(R.id.actor_age);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        personImagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        knownForRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        personImagesRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public void personListener(Person person) {
        personCreditsPresenter.getPersonWork(person.getId());
        imagesPresenter.getPersonImages(person.getId());
        actorName.setText(person.getName());
        actorJob.setText(person.getKnownForDepartment());
        if (person.getBiography() != null) {
            actorOverView.setText(person.getBiography());
        } else {
            actorOverView.setVisibility(View.GONE);
        }
        Picasso.get().load(EndPoints.Image200W + person.getProfilePath()).into(actorImage);
        if (person.getBirthday() != null) {
            String[] birthdayrArr = person.getBirthday().split("-");
            int realActorYear = Integer.parseInt(birthdayrArr[0]);
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(person.getBirthday());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter = new SimpleDateFormat("MMM dd yyyy");
            finalDate = formatter.format(date);
            actorDate.setText(finalDate);
            year = Calendar.getInstance().get(Calendar.YEAR);
            int realAge = year - realActorYear;
            actorAge.setText(getResources().getString(R.string.actor_age_text, realAge));
        } else {
            dateContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void creditCast(List<PersonCast> castList) {
        personCastAdapter = new PersonCastAdapter(context, castList);
        knownForRecyclerView.setAdapter(personCastAdapter);
        dialog.dismiss();
    }

    @Override
    public void creditCrew(List<PersonCrew> crewList) {

    }

    @Override
    public void personImageListener(List<PersonImageDetails> movieImageDetails) {
        imageAdapter = new ImageAdapter<>(this, movieImageDetails);
        personImagesRecyclerView.setAdapter(imageAdapter);
        personImagesRecyclerView.addItemDecoration(new CirclePagerIndicatorDecoration());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void removeLoading() {

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

    private void scrollRecycler() {
        personImagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_icons, menu);
        return true;
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
