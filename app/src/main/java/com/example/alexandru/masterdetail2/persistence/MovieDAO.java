package com.example.alexandru.masterdetail2.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.alexandru.masterdetail2.model.Movie;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM Movie")
    List<Movie> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(Movie movie);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultipleMovies(List<Movie> movieList);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateMultipleMovie(List<Movie> moviesList);

    @Delete
    void deleteMovie (Movie movies);

    default void upsert(List<Movie> movies) { // update existent movies, insert new ones :)
        insertMultipleMovies(movies);
        updateMultipleMovie(movies);
    }

 }
