package com.example.alexandru.masterdetail2.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alexandru.masterdetail2.R;
import com.example.alexandru.masterdetail2.api.MovieResource;
import com.example.alexandru.masterdetail2.api.RetrofitClient;
import com.example.alexandru.masterdetail2.api.WebSocketClient;
import com.example.alexandru.masterdetail2.model.Movie;
import com.example.alexandru.masterdetail2.persistence.MovieDatabase;
import com.example.alexandru.masterdetail2.util.Token;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MoviesListActivity extends AppCompatActivity {

    private static final String TAG = "Movies List";

    private final static int ITEMS_PER_PAGE = 8;
    private int numberOfPages;
    private int currentPage = 1;

    private ListView listView;
    private Button previousPageButton;
    private Button nextPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        this.listView = findViewById(R.id.listview);
        this.previousPageButton = findViewById(R.id.previousButton);
        this.nextPageButton = findViewById(R.id.nextButton);

        listView.setOnItemClickListener(((parent, view, position, id) -> {
            Movie movie = (Movie) listView.getItemAtPosition(position);
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        }));

        this.previousPageButton.setEnabled(false);

        findViewById(R.id.addMovieButton).setOnClickListener(event -> startActivity( new Intent( MoviesListActivity.this, AddMovieActivity.class )));

        this.previousPageButton.setOnClickListener(event -> {
            if(currentPage > 1) {
                currentPage--;
                loadMovies();
            }
        });

        this.nextPageButton.setOnClickListener(event -> {
            currentPage++;
            loadMovies();
        });

        // Graph
        GraphView graph = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

        // Contact form
        findViewById(R.id.contactButton).setOnClickListener(l -> {
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.putExtra(Intent.EXTRA_EMAIL,new String[]{"alexandru.antal@outlook.com"});
                mail.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                mail.putExtra(Intent.EXTRA_TEXT, "Message content");
                mail.setType("message/rfc822");
                startActivity(Intent.createChooser(mail, "Send email via:"));
        });

        loadMovies();

        WebSocketClient.handleMessages(message -> runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            loadMovies();
        }));
    }

    private void loadMovies() {
        listView.setAdapter(null);

        MovieResource api = RetrofitClient.getClient().create(MovieResource.class);
        Call<List<Movie>> call = api.getMovies(Token.getInstance().getToken());
        call.enqueue(new Callback<List<Movie>>() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull retrofit2.Response<List<Movie>> response) {
                if(response.body() != null) {
                    List<Movie> receivedMovies = response.body();
                    numberOfPages = (int) Math.ceil(1.0 * receivedMovies.size()/ITEMS_PER_PAGE);
                    nextPageButton.setEnabled(numberOfPages != currentPage);
                    previousPageButton.setEnabled(1 != currentPage);
                    List<Movie> shownMovies = new ArrayList<>();
                    for(int i = (currentPage - 1) * ITEMS_PER_PAGE; i < currentPage * ITEMS_PER_PAGE; i++) {
                        if(i < receivedMovies.size()) {
                            shownMovies.add(receivedMovies.get(i));
                        }
                        else {
                            break;
                        }
                    }
                    listView.setAdapter(new ArrayAdapter<>(MoviesListActivity.this, android.R.layout.simple_list_item_1, shownMovies));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                // load items from local storage
                List<Movie> localMovies = MovieDatabase.getDatabase(MoviesListActivity.this).movieDAO().getAll();

                numberOfPages = (int) Math.ceil(localMovies.size()/ITEMS_PER_PAGE);
                nextPageButton.setEnabled(numberOfPages != currentPage);
                previousPageButton.setEnabled(1 != currentPage);

                Log.e(TAG, "Failed to connect to server; loading movies from local database ");

                List<Movie> shownMovies = new ArrayList<>();
                for(int i = (currentPage - 1) * ITEMS_PER_PAGE; i < currentPage * ITEMS_PER_PAGE; i++) {
                    if(i < localMovies.size()) {
                        shownMovies.add(localMovies.get(i));
                    }
                    else {
                        break;
                    }
                }
                listView.setAdapter(new ArrayAdapter<>(MoviesListActivity.this, android.R.layout.simple_list_item_1, shownMovies));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketClient.closeConnection();
    }

}
