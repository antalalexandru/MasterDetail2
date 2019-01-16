package com.example.alexandru.masterdetail2.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.alexandru.masterdetail2.model.Movie;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDAO movieDAO();

    private volatile static MovieDatabase INSTANCE;

    public static MovieDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, "movie_db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
