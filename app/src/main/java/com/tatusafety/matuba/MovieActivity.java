package com.tatusafety.matuba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.tatusafety.matuba.adapter.MoviesAdapter;
import com.tatusafety.matuba.model.Journey;
import com.tatusafety.matuba.model.JourneyResponse;
import com.tatusafety.matuba.model.Movie;
import com.tatusafety.matuba.model.MovieResponse;
import com.tatusafety.matuba.rest.ApiClient;
import com.tatusafety.matuba.rest.ApiInterface;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // TODO - insert your themoviedb.org API KEY here
    private final static String From_A = "18.37755";
    private final static String To_A = "-33.94393";
    private final static String From_B = "18.41489";
    private final static String To_B = "-33.91252";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (From_B.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return;
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JourneyResponse> call = apiService.getJourneys(From_A,To_A,From_B,To_B);
        call.enqueue(new Callback<JourneyResponse>() {
            @Override
            public void onResponse(Call<JourneyResponse> call, Response<JourneyResponse> response) {
                int statusCode = response.code();
                List<Journey> movies = response.body().getLegs();
                Log.d("Here are the movies", String.valueOf(movies));
//                recyclerView.setAdapter(new MoviesAdapter(movies, R.layout.list_item_movie, getApplicationContext()));
            }

            @Override
            public void onFailure(Call<JourneyResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

}
