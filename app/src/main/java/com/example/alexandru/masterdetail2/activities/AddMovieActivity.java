package com.example.alexandru.masterdetail2.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexandru.masterdetail2.R;
import com.example.alexandru.masterdetail2.api.MovieResource;
import com.example.alexandru.masterdetail2.api.RetrofitClient;
import com.example.alexandru.masterdetail2.model.Movie;
import com.example.alexandru.masterdetail2.persistence.MovieDatabase;
import com.example.alexandru.masterdetail2.util.Token;

import retrofit2.Call;
import retrofit2.Callback;

public class AddMovieActivity extends AppCompatActivity {

    private static final String TAG = "AddMovie";

    private EditText title;
    private EditText year;
    private Spinner score;
    private EditText image;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        //get the spinner from the xml.
        score = findViewById(R.id.scorSpinner);
        score.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));

        Button addButton = findViewById(R.id.addButton);
        title = findViewById(R.id.titleInput);
        year = findViewById(R.id.anInput);
        image = findViewById(R.id.imageInput);
        description = findViewById(R.id.descriptionInput);

        addButton.setOnClickListener(event -> {
            try {
                int int_year = Integer.parseInt(year.getText().toString().trim());
                String str_title = title.getText().toString().trim();
                int int_score = Integer.parseInt(score.getSelectedItem().toString());
                String str_image = image.getText().toString().trim();
                String str_description = description.getText().toString().trim();

                if(int_year < 0) {
                    throw new Exception("Invalid year .. ");
                }

                if(str_title.trim().equals("")) {
                    throw  new Exception("Invalid title");
                }

                if(str_image.equals("")) {
                    str_image = null;
                }

                if(str_description.equals("")) {
                    str_description = null;
                }

                addMovie(new Movie("0", str_title, str_description, int_score, int_year, str_image));
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMovie(Movie movie) {
        MovieResource api = RetrofitClient.getClient().create(MovieResource.class);
        Call<Movie> call = api.addMovie(Token.getInstance().getToken(), movie);
        call.enqueue(new Callback<Movie>() {
            public void onResponse(@NonNull Call<Movie> call, @NonNull retrofit2.Response<Movie> response) {
                Movie result = response.body();
                // add movie to local storage
                MovieDatabase.getDatabase(getApplicationContext()).movieDAO().insertMovie(result);
                Log.d(TAG, "adaugat film " + result.toString());
                redirectToMoviePage(result);
            }
            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Server error...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Server error: " + t.getMessage() );
            }
        });
    }

    private void redirectToMoviePage(Movie movie) {
        startActivity(new Intent(this, MovieDetailsActivity.class)
            .putExtra("movie", movie)
        );
    }
}
