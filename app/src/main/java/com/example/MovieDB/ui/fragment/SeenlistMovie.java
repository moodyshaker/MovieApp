package com.example.MovieDB.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.BSDObject;
import com.example.MovieDB.persistance.database.entity.MovieEntity;
import com.example.MovieDB.persistance.database.roomdb.DatabaseRepository;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.ui.adapter.BottomSheetAdapter;
import com.example.MovieDB.ui.adapter.SeenWishAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SeenlistMovie extends Fragment implements Observer<List<MovieEntity>>, SeenWishAdapter.OnMoreListener<MovieEntity> {

    private RecyclerView seenlistMovieRV;
    private SeenWishAdapter<MovieEntity> adapter;
    private DatabaseRepository repository;
    private MovieSharedPreference.UserPreferences userPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seenlist_movie, container, false);
        initUi(v);
        return v;
    }

    private void initUi(View v) {
        seenlistMovieRV = v.findViewById(R.id.seenlist_movie_rv);
        userPref = MovieSharedPreference.UserPreferences.getUserPreference(getActivity());
        seenlistMovieRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        repository = DatabaseRepository.getRepo(getActivity());
        repository.getSeenMovies(userPref.getID()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(List<MovieEntity> entity) {
        adapter = new SeenWishAdapter<>(getActivity(), entity);
        adapter.setOnMoreListener(this);
        seenlistMovieRV.setAdapter(adapter);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    private void showBottomDialog(MovieEntity movie) {
        Date date = null;
        SimpleDateFormat formatter;
        String finalDate;
        BottomSheetDialog d = new BottomSheetDialog(getActivity());
        d.setContentView(R.layout.bottom_sheet_dialog);
        ListView listView = d.findViewById(R.id.list_view_bottom_sheet);
        TextView name = d.findViewById(R.id.bottom_dialog_name);
        TextView dateTV = d.findViewById(R.id.bottom_dialog_date);
        ImageView image = d.findViewById(R.id.bottom_dialog_image);
        name.setText(movie.getTitle());
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(movie.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("MMM dd yyyy");
        finalDate = formatter.format(date);
        dateTV.setText(finalDate);
        Picasso.get().load(EndPoints.Image200W + movie.getPosterPath()).into(image);
        List<BSDObject> list = new ArrayList<>();
        list.add(new BSDObject("Delete", R.drawable.ic_baseline_delete_24, R.color.delete_red));
        list.add(new BSDObject("Add To WishList", R.drawable.ic_baseline_favorite_24, R.color.wish_red));
        listView.setAdapter(new BottomSheetAdapter(getActivity(), list));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                repository.deleteSeenMovie(movie.getMovie_id());
                d.dismiss();
            } else if (position == 1) {
                repository.setMovieToWish(movie.getMovie_id());
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onMoreClickItem(MovieEntity object) {
        showBottomDialog(object);
    }
}
