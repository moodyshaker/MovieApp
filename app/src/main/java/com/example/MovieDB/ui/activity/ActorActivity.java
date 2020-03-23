package com.example.MovieDB.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.MovieDB.contract.PersonContract;
import com.example.MovieDB.contract.PersonCreditsContract;
import com.example.MovieDB.contract.PersonImageContract;
import com.example.MovieDB.model.data.movie_credits.Cast;
import com.example.MovieDB.model.data.movie_credits.Crew;
import com.example.MovieDB.model.data.person.Person;
import com.example.MovieDB.model.data.person.PersonCast;
import com.example.MovieDB.model.data.person.PersonCrew;
import com.example.MovieDB.model.data.person_images.Profile;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.presenter.PersonCreditsPresenter;
import com.example.MovieDB.presenter.PersonImagesPresenter;
import com.example.MovieDB.presenter.PersonPresenter;
import com.example.MovieDB.ui.adapter.PersonCastAdapter;
import com.example.MovieDB.ui.adapter.PersonImageAdapter;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ActorActivity extends AppCompatActivity implements PersonImageContract, PersonCreditsContract, PersonContract {

    private RecyclerView personImagesRecyclerView, knownForRecyclerView;
    private TextView actorName, actorJob, actorOverView, actorDate, title;
    private ImageView actorImage;
    private PersonImageAdapter imageAdapter;
    private PersonCastAdapter personCastAdapter;
    private PersonImagesPresenter personImagesPresenter;
    private PersonPresenter personPresenter;
    private PersonCreditsPresenter personCreditsPresenter;
    private Toolbar toolbar;
    private Bundle bundle;
    private Crew crew;
    private Cast cast;
    private Intent intent;
    private int id;
    private ActionBar actionBar;
    private Context context = this;
    private SimpleDateFormat formatter;
    private Date date;
    private String finalDate;
    private LinearLayout dateContainer;

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
        intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            crew = (Crew) bundle.getSerializable("Crew");
            cast = (Cast) bundle.getSerializable("Cast");
        }
        if (intent.getStringExtra("type").equals("crew")) {
            title.setText(crew.getName());
            id = crew.getId();
        } else if (intent.getStringExtra("type").equals("cast")) {
            title.setText(cast.getName());
            id = cast.getId();
        }
        initPresenter();
    }

    private void initPresenter() {
        personCreditsPresenter = new PersonCreditsPresenter(this);
        personImagesPresenter = new PersonImagesPresenter(this);
        personPresenter = new PersonPresenter(this);
        personPresenter.getPerson(id);
    }

    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        actorName = findViewById(R.id.actor_name);
        actorJob = findViewById(R.id.actor_job);
        dateContainer = findViewById(R.id.birth_of_date_container);
        actorOverView = findViewById(R.id.actor_over_view);
        actorDate = findViewById(R.id.actor_date_of_birth);
        actorImage = findViewById(R.id.actor_image);
        personImagesRecyclerView = findViewById(R.id.actor_images);
        knownForRecyclerView = findViewById(R.id.known_for_movies);
        personImagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        knownForRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void personListener(Person person) {
        personCreditsPresenter.getPersonWork(person.getId());
        personImagesPresenter.getPersonImages(person.getId());
        actorName.setText(person.getName());
        actorJob.setText(person.getKnownForDepartment());
        if (person.getBiography() != null) {
            actorOverView.setText(person.getBiography());
        } else {
            actorOverView.setVisibility(View.GONE);
        }
        Picasso.get().load(EndPoints.Image200W + person.getProfilePath()).into(actorImage);
        if (person.getBirthday() != null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(person.getBirthday());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter = new SimpleDateFormat("MMM dd yyyy");
            finalDate = formatter.format(date);
            actorDate.setText(finalDate);
        } else {
            dateContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void creditCast(List<PersonCast> castList) {
        personCastAdapter = new PersonCastAdapter(context, castList);
        knownForRecyclerView.setAdapter(personCastAdapter);
    }

    @Override
    public void creditCrew(List<PersonCrew> crewList) {

    }

    @Override
    public void personImageListener(List<Profile> profiles) {
        Log.e("profiles size", String.valueOf(profiles.size()));
        imageAdapter = new PersonImageAdapter(this, profiles);
        personImagesRecyclerView.setAdapter(imageAdapter);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
