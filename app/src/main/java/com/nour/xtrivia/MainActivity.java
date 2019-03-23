package com.nour.xtrivia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.nour.xtrivia.services.ApiService;
import com.nour.xtrivia.services.ServiceBuilder;

import org.jsoup.Jsoup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG:";
    TextView questionText;
    private OptionsRecyclerAdapter optionsRecyclerAdapter;
    private RecyclerView recyclerOptions;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText = findViewById(R.id.questionText);
        recyclerOptions = findViewById(R.id.optionsRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerOptions.setLayoutManager(layoutManager);

        ApiService taskService = ServiceBuilder.buildService(ApiService.class);
        Call<Questions> call = taskService.getQuestions();

        call.enqueue(new Callback<Questions>() {
            @Override
            public void onResponse(Call<Questions> call, Response<Questions> response) {
                List<Result> data = response.body().getResults();
                Log.d(TAG, data.get(0).getQuestion());
                questionText.setText(Jsoup.parse(data.get(0).getQuestion()).text());
                optionsRecyclerAdapter = new OptionsRecyclerAdapter(MainActivity.this, data);
                recyclerOptions.setAdapter(optionsRecyclerAdapter);
                optionsRecyclerAdapter.updateOptions(data);
            }

            @Override
            public void onFailure(Call<Questions> call, Throwable t) {
                questionText.setText("Akwai Matsala");
            }
        });
    }
}