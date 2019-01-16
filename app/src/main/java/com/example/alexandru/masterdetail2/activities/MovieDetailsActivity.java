package com.example.alexandru.masterdetail2.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexandru.masterdetail2.R;
import com.example.alexandru.masterdetail2.api.MovieResource;
import com.example.alexandru.masterdetail2.api.RetrofitClient;
import com.example.alexandru.masterdetail2.model.Movie;
import com.example.alexandru.masterdetail2.persistence.MovieDatabase;
import com.example.alexandru.masterdetail2.util.Token;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;

public class MovieDetailsActivity extends AppCompatActivity {

    private Movie movie;

    private TextView movieTitle;
    private ImageView movieImage;
    private TextView movieScore;
    private TextView movieDescription;

    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movie = (Movie) getIntent().getSerializableExtra("movie");

        movieTitle = findViewById(R.id.movieTitle);
        movieImage = findViewById(R.id.movieImage);
        movieScore = findViewById(R.id.movieScore);
        movieDescription = findViewById(R.id.movieDescription);
        deleteButton = findViewById(R.id.deleteButton);

        movieTitle.setText(movie.getTitle());

        if(movie.getImage() != null) {
            // Load remote image
            Picasso.get().load(movie.getImage()).into(movieImage);
            movieImage.setMaxHeight(300);
            movieImage.setMinimumHeight(299);

            movieImage.setOnClickListener(l -> {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise);
                movieImage.startAnimation(animation);
            });
        }

        movieScore.setText(String.format("Score: %d/10", movie.getScore()));

        if(movie.getDetails() != null) {
            movieDescription.setText(movie.getDetails());
        }

        deleteButton.setOnClickListener(l -> {
            MovieResource api = RetrofitClient.getClient().create(MovieResource.class);
            Call<Void> call = api.deleteMovie(Token.getInstance().getToken(), Integer.parseInt(movie.getId()));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                    startActivity(new Intent(MovieDetailsActivity.this, MoviesListActivity.class));
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();

                }
            });
            // delete from local storage
            MovieDatabase.getDatabase(MovieDetailsActivity.this).movieDAO().deleteMovie(movie);
        });
    }
}
