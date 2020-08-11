package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.MovieByKeywordContract;
import com.example.MovieDB.contract.SearchContract;
import com.example.MovieDB.model.credit_search.CreditResult;
import com.example.MovieDB.model.keywords_from_search.KeywordResult;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.presenter.MovieByKeywordPresenter;
import com.example.MovieDB.presenter.MovieSearchPresenter;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.ui.adapter.KeywordAdapter;
import com.example.MovieDB.ui.adapter.MovieReleaseYearAdapter;
import com.example.MovieDB.ui.adapter.PersonSearchAdapter;
import com.example.MovieDB.ui.adapter.SearchAdapter;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Search extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener, SearchContract, MovieReleaseYearAdapter.ReleaseDateOnClickListener, MovieByKeywordContract, KeywordAdapter.OnMovieKeywordClickListener<KeywordResult>, TabLayout.OnTabSelectedListener {
    private Context context = this;
    private Activity activity = this;
    private Toolbar toolbar;
    private TextView title, pagesCounter;
    private ActionBar actionBar;
    private RecyclerView searchRecyclerView, releaseYearRecyclerView;
    private SearchView searchView;
    private TabLayout tabLayout;
    private ImageView closeIcon;
    private SearchAdapter<Movies> movieAdapter;
    private PersonSearchAdapter personAdapter;
    private KeywordAdapter<KeywordResult> keywordAdapter;
    private SearchAdapter<SeriesResult> seriesAdapter;
    private MovieSearchPresenter presenter;
    private int visibleItems, totalItem, currentItemPosition, pagesCount, currentPage;
    private boolean isLoading = false;
    private SwipeRefreshLayout refreshLayout;
    private Handler handler;
    private TextView searchText;
    private int searchType;
    private LinearLayout noDataContainer;
    private String releaseYear = "";
    private ArrayList<String> years = new ArrayList<>();
    private MovieReleaseYearAdapter releaseYearAdapter;
    private String query = "";
    private Intent intent;
    private MovieByKeywordPresenter movieByKeywordPresenter;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        searchView = toolbar.findViewById(R.id.search_view);
        searchRecyclerView = findViewById(R.id.search_recycler_view);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        noDataContainer = findViewById(R.id.no_data_container);
        pagesCounter = findViewById(R.id.pages_counter);
        tabLayout = findViewById(R.id.search_type_tab_layout);
        releaseYearRecyclerView = findViewById(R.id.release_year_rc);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        title.setText("MovieDB");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        searchView.setIconified(false);
        tabLayout.addTab(tabLayout.newTab().setText("Movies"));
        tabLayout.addTab(tabLayout.newTab().setText("Persons"));
        tabLayout.addTab(tabLayout.newTab().setText("TV Shows"));
        tabLayout.addTab(tabLayout.newTab().setText("Movie Keyword"));
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(this);
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.gray_600));
        drawable.setSize(2, 1);
        linearLayout.setDividerPadding(25);
        linearLayout.setDividerDrawable(drawable);
        closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHintTextColor(getResources().getColor(R.color.blue_gray_100));
        searchText.setTextColor(getResources().getColor(R.color.blue_gray_100));
        closeIcon.setColorFilter(getResources().getColor(R.color.blue_gray_100));
        presenter = new MovieSearchPresenter(this);
        handler = new Handler();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        releaseYearRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        searchRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        movieAdapter = new SearchAdapter<>(context);
        seriesAdapter = new SearchAdapter<>(context);
        personAdapter = new PersonSearchAdapter(context);
        keywordAdapter = new KeywordAdapter<>(context, this);
        intent = getIntent();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                searchRecyclerView.setAdapter(movieAdapter);
                break;
            case 1:
                searchRecyclerView.setAdapter(personAdapter);
                break;
            case 2:
                searchRecyclerView.setAdapter(seriesAdapter);
                break;
            case 3:
                searchRecyclerView.setAdapter(keywordAdapter);
                break;
        }
        populateYear();
        refreshLayout.setOnRefreshListener(() -> {
            if (movieAdapter.getList() != null) {
                refreshLayout.setRefreshing(true);
                movieAdapter.getList().clear();
                searchRecyclerView.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                releaseYearRecyclerView.setVisibility(View.GONE);
                handler.postDelayed(() -> {
                    presenter.getMovieSearch(1, "", "", searchType);
                    pagesCounter.setVisibility(View.GONE);
                    releaseYear = "";
                    searchView.setQuery("", false);
                    refreshLayout.setRefreshing(false);
                    tabLayout.setVisibility(View.VISIBLE);
                }, 1500);
            } else {
                refreshLayout.setRefreshing(true);
                searchRecyclerView.setVisibility(View.GONE);
                releaseYearRecyclerView.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                handler.postDelayed(() -> {
                    pagesCounter.setVisibility(View.GONE);
                    releaseYear = "";
                    searchView.setQuery("", false);
                    refreshLayout.setRefreshing(false);
                    tabLayout.setVisibility(View.VISIBLE);
                }, 1500);
            }
        });
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                visibleItems = manager.getChildCount();
                totalItem = manager.getItemCount();
                currentItemPosition = manager.findFirstVisibleItemPosition();
                if (currentPage < pagesCount) {
                    if ((visibleItems + currentItemPosition) >= totalItem) {
                        if (!isLoading) {
                            isLoading = true;
                            if ((movieByKeywordPresenter != null) && (tabLayout.getSelectedTabPosition() == 3)) {
                                movieByKeywordPresenter.increasePage();
                            } else {
                                presenter.increasePages();
                            }
                        }
                    } else {
                        isLoading = false;
                    }
                }
            }
        });
        searchView.setOnSearchClickListener(v -> {
            title.setVisibility(View.GONE);
            searchView.requestFocus();
        });
        searchView.setOnCloseListener(() -> {
            title.setVisibility(View.VISIBLE);
            searchView.clearFocus();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (noDataContainer.getVisibility() == View.VISIBLE) {
                        noDataContainer.setVisibility(View.GONE);
                    }
                    releaseYearRecyclerView.setVisibility(View.GONE);
                    pagesCounter.setVisibility(View.GONE);
                    query = "";
                } else {
                    switch (tabLayout.getSelectedTabPosition()) {
                        case 0:
                            if (movieAdapter.getList() != null && movieAdapter.getList().size() > 0 || newText.isEmpty()) {
                                movieAdapter.getList().clear();
                            }
                            releaseYearAdapter.setSelected(-1);
                            releaseYearAdapter.notifyDataSetChanged();
                            searchType = 0;
                            presenter.getMovieSearch(1, newText, releaseYear, searchType);
                            query = presenter.getQuery();
                            break;
                        case 1:
                            if (personAdapter.getList() != null && personAdapter.getList().size() > 0 || newText.isEmpty()) {
                                personAdapter.getList().clear();
                            }
                            releaseYearAdapter.setSelected(-1);
                            releaseYearAdapter.notifyDataSetChanged();
                            searchType = 1;
                            presenter.getMovieSearch(1, newText, releaseYear, searchType);
                            query = presenter.getQuery();
                            break;
                        case 2:
                            if (seriesAdapter.getList() != null && seriesAdapter.getList().size() > 0 || newText.isEmpty()) {
                                seriesAdapter.getList().clear();
                            }
                            releaseYearAdapter.setSelected(-1);
                            releaseYearAdapter.notifyDataSetChanged();
                            searchType = 2;
                            presenter.getMovieSearch(1, newText, releaseYear, searchType);
                            query = presenter.getQuery();
                            break;
                        case 3:
                            if (!searchRecyclerView.getAdapter().equals(KeywordAdapter.class)) {
                                searchRecyclerView.setAdapter(keywordAdapter);
                            }
                            if (keywordAdapter.getKeywordList() != null && keywordAdapter.getKeywordList().size() > 0 || newText.isEmpty()) {
                                keywordAdapter.getKeywordList().clear();
                            }
                            releaseYearAdapter.setSelected(-1);
                            releaseYearAdapter.notifyDataSetChanged();
                            searchType = 3;
                            presenter.getMovieSearch(1, newText, releaseYear, searchType);
                            query = presenter.getQuery();
                            break;
                    }
                }
                return false;
            }
        });
        if (intent.hasExtra("movie_keyword")) {
            movieByKeywordPresenter = new MovieByKeywordPresenter(this);
            TabLayout.Tab tab = tabLayout.getTabAt(3);
            if (tab != null) {
                tab.select();
            }
            String name = intent.getStringExtra("keyword_name");
            searchView.setQuery(name, false);
            int id = intent.getIntExtra("keyword_id", 0);
            movieByKeywordPresenter.getMoviesByKeyword(id, 1);
            searchRecyclerView.setAdapter(movieAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void MovieSearchList(List<Movies> movies, int pagesCount, int currentPages) {
        this.pagesCount = pagesCount;
        this.currentPage = currentPages;
        pagesCounter.setVisibility(View.VISIBLE);
        pagesCounter.setText(currentPages + "/" + pagesCount);
        if (movies.size() <= 0) {
            searchRecyclerView.setVisibility(View.GONE);
            releaseYearRecyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
        } else {
            noDataContainer.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            releaseYearRecyclerView.setVisibility(View.VISIBLE);
            movieAdapter.setList(movies);
            movieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void CreditSearchList(List<CreditResult> creditResults, int pagesCount, int currentPages) {
        this.pagesCount = pagesCount;
        this.currentPage = currentPages;
        pagesCounter.setVisibility(View.VISIBLE);
        pagesCounter.setText(currentPages + "/" + pagesCount);
        if (creditResults.size() <= 0) {
            searchRecyclerView.setVisibility(View.GONE);
            releaseYearRecyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
        } else {
            noDataContainer.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            releaseYearRecyclerView.setVisibility(View.VISIBLE);
            personAdapter.setList(creditResults);
            personAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void SeriesSearchList(List<SeriesResult> seriesList, int pagesCount, int currentPages) {
        this.pagesCount = pagesCount;
        this.currentPage = currentPages;
        pagesCounter.setVisibility(View.VISIBLE);
        pagesCounter.setText(currentPages + "/" + pagesCount);
        if (seriesList.size() <= 0) {
            searchRecyclerView.setVisibility(View.GONE);
            releaseYearRecyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
        } else {
            noDataContainer.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            releaseYearRecyclerView.setVisibility(View.VISIBLE);
            seriesAdapter.setList(seriesList);
            seriesAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void KeywordSearchList(List<KeywordResult> keywordList, int pagesCount, int currentPages) {
        this.pagesCount = pagesCount;
        this.currentPage = currentPages;
        if (keywordList.size() <= 0) {
            searchRecyclerView.setVisibility(View.GONE);
            releaseYearRecyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
        } else {
            noDataContainer.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            releaseYearRecyclerView.setVisibility(View.VISIBLE);
            keywordAdapter.setKeywordList(keywordList);
            keywordAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void removeLoading() {

    }

    private void populateYear() {
        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        for (int i = 1980; i <= year; i++) {
            years.add(String.valueOf(i));
        }
        releaseYearAdapter = new MovieReleaseYearAdapter(context, years, this);
        releaseYearRecyclerView.setAdapter(releaseYearAdapter);
    }

    @Override
    public void releaseDateOnClick(String releaseItem) {
        this.releaseYear = releaseItem;
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                if (movieAdapter.getList() != null && movieAdapter.getList().size() > 0) {
                    movieAdapter.getList().clear();
                }
                searchType = 0;
                presenter.getMovieSearch(currentPage, query, releaseYear, searchType);
                break;
            case 1:
                if (personAdapter.getList() != null && personAdapter.getList().size() > 0) {
                    personAdapter.getList().clear();
                }
                searchType = 1;
                presenter.getMovieSearch(currentPage, query, releaseYear, searchType);
                break;
            case 2:
                if (seriesAdapter.getList() != null && seriesAdapter.getList().size() > 0) {
                    seriesAdapter.getList().clear();
                }
                searchType = 2;
                presenter.getMovieSearch(currentPage, query, releaseYear, searchType);
                break;
            case 3:
                if (keywordAdapter.getKeywordList() != null && keywordAdapter.getKeywordList().size() > 0) {
                    keywordAdapter.getKeywordList().clear();
                }
                searchType = 3;
                presenter.getMovieSearch(currentPage, query, releaseYear, searchType);
                break;
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch (position) {
            case 0:
                searchView.setQueryHint("Movie name...");
                searchRecyclerView.setAdapter(movieAdapter);
                searchType = 0;
                presenter.getMovieSearch(1, query, releaseYear, searchType);
                break;
            case 1:
                searchView.setQueryHint("Character name...");
                searchRecyclerView.setAdapter(personAdapter);
                searchType = 1;
                presenter.getMovieSearch(1, query, releaseYear, searchType);
                break;
            case 2:
                searchView.setQueryHint("Series name...");
                searchRecyclerView.setAdapter(seriesAdapter);
                searchType = 2;
                presenter.getMovieSearch(1, query, releaseYear, searchType);
                break;
            case 3:
                searchView.setQueryHint("Keyword...");
                searchRecyclerView.setAdapter(keywordAdapter);
                searchType = 3;
                presenter.getMovieSearch(1, query, releaseYear, searchType);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void OnMovieKeywordClick(KeywordResult keyword) {
        if (context.getClass().equals(Search.class)) {
            searchView.setQuery(keyword.getName(), false);
            movieByKeywordPresenter = new MovieByKeywordPresenter(this);
            movieByKeywordPresenter.getMoviesByKeyword(keyword.getId(), 1);
            searchRecyclerView.setAdapter(movieAdapter);
        }
    }

    @Override
    public void MovieByKeyword(List<Movies> moviesList, int totalPages, int currentPage) {
        releaseYearRecyclerView.setVisibility(View.GONE);
        this.pagesCount = totalPages;
        this.currentPage = currentPage;
        pagesCounter.setVisibility(View.VISIBLE);
        pagesCounter.setText(currentPage + "/" + totalPages);
        if (moviesList.size() <= 0) {
            searchRecyclerView.setVisibility(View.GONE);
            releaseYearRecyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
        } else {
            noDataContainer.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            releaseYearRecyclerView.setVisibility(View.VISIBLE);
            movieAdapter.setList(moviesList);
            movieAdapter.notifyDataSetChanged();
        }
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
