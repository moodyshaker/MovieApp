package com.example.MovieDB.persistance.database.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.MovieDB.persistance.database.dao.MovieDao;
import com.example.MovieDB.persistance.database.date_convertor.DateConverter;
import com.example.MovieDB.persistance.database.entity.MovieEntity;

@Database(entities = {MovieEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovieDataBase extends RoomDatabase {
    private static final String DATABASE_NAME = "movie_database";
    public abstract MovieDao getDao();
    private static volatile MovieDataBase sInstance;

    public static MovieDataBase getMovie(Context context) {
        if (sInstance == null) {
            synchronized (MovieDataBase.class) {
                if (sInstance == null) {
                    sInstance = Room
                            .databaseBuilder(context, MovieDataBase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }
}
