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

import java.util.ArrayList;
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
    private List<Result> data;
    private List<String> incorrectAnswers;
    private String correctAnswer;

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
                data = response.body().getResults();
                incorrectAnswers = data.get(0).getIncorrectAnswers();
                correctAnswer = data.get(0).getCorrectAnswer();
                List<String> options = new ArrayList<>();
                options.add(correctAnswer);

                for(String s: incorrectAnswers){
                    options.add(s);
                }

                data.get(0).setAllOptions(options);
                List<String> choices = data.get(0).getAllOptions();
                Log.d(TAG, data.get(0).getQuestion());
                questionText.setText(Jsoup.parse(data.get(0).getQuestion()).text());
                optionsRecyclerAdapter = new OptionsRecyclerAdapter(MainActivity.this, choices, data);
                optionsRecyclerAdapter.notifyDataSetChanged();
                recyclerOptions.setAdapter(optionsRecyclerAdapter);
            }

            @Override
            public void onFailure(Call<Questions> call, Throwable t) {
                questionText.setText("Akwai Matsala");
            }
        });
    }
}
