package com.example.MovieDB.ui.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.MovieDB.persistance.database.entity.SeriesEntity;
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

public class SeenlistSeries extends Fragment implements Observer<List<SeriesEntity>>, SeenWishAdapter.OnMoreListener<SeriesEntity> {

    private RecyclerView seenlistSeriesRV;
    private SeenWishAdapter<SeriesEntity> adapter;
    private DatabaseRepository repository;
    private MovieSharedPreference.UserPreferences userPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seenlist_series, container, false);
        initUi(v);
        return v;
    }

    private void initUi(View v) {
        seenlistSeriesRV = v.findViewById(R.id.seenlist_series_rv);
        userPref = MovieSharedPreference.UserPreferences.getUserPreference(getActivity());
        seenlistSeriesRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        repository = DatabaseRepository.getRepo(getActivity());
        repository.getSeenSeriesList(userPref.getID()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private void showBottomDialog(SeriesEntity series) {
        Date date = null;
        SimpleDateFormat formatter;
        String finalDate;
        BottomSheetDialog d = new BottomSheetDialog(getActivity());
        d.setContentView(R.layout.bottom_sheet_dialog);
        ListView listView = d.findViewById(R.id.list_view_bottom_sheet);
        TextView name = d.findViewById(R.id.bottom_dialog_name);
        TextView dateTV = d.findViewById(R.id.bottom_dialog_date);
        ImageView image = d.findViewById(R.id.bottom_dialog_image);
        name.setText(series.getName());
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(series.getFirstAirDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("MMM dd yyyy");
        finalDate = formatter.format(date);
        dateTV.setText(finalDate);
        Picasso.get().load(EndPoints.Image200W + series.getPosterPath()).into(image);
        List<BSDObject> list = new ArrayList<>();
        list.add(new BSDObject("Delete", R.drawable.ic_baseline_delete_24, R.color.delete_red));
        list.add(new BSDObject("Add To WishList", R.drawable.ic_baseline_favorite_24, R.color.wish_red));
        listView.setAdapter(new BottomSheetAdapter(getActivity(), list));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                repository.deleteSeenSeries(series.getSeries_id());
                d.dismiss();
            } else if (position == 1) {
                repository.setSeriesToWish(series.getSeries_id());
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(List<SeriesEntity> seriesEntities) {
        Log.d("TAG", "onNext: " + seriesEntities.size());
        adapter = new SeenWishAdapter<>(getActivity(), seriesEntities);
        adapter.setOnMoreListener(this);
        seenlistSeriesRV.setAdapter(adapter);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onMoreClickItem(SeriesEntity object) {
        showBottomDialog(object);
    }
}
