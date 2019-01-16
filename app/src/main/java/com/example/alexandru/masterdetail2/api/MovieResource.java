package com.example.alexandru.masterdetail2.api;

import com.example.alexandru.masterdetail2.model.Movie;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MovieResource {
    @GET("/movie")
    Call<List<Movie>> getMovies(@Header("Authorization") String token);

    @POST("/movie")
    Call<Movie> addMovie(@Header("Authorization") String token, @Body Movie movie);

    @DELETE("/movie/{id}")
    Call<Void> deleteMovie(@Header("Authorization") String token, @Path("id") Integer id);
}
