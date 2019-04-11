package com.nour.xtrivia;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nour.xtrivia.services.ApiService;
import com.nour.xtrivia.services.ServiceBuilder;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OptionsRecyclerAdapter.OnOptionClickListener {
    private static final String TAG = "DEBUG:";
    TextView questionText;
    private OptionsRecyclerAdapter optionsRecyclerAdapter;
    private RecyclerView recyclerOptions;
    private LinearLayoutManager layoutManager;
    private List<Result> data;
    private List<String> incorrectAnswers;
    private String correctAnswer;
    private int position = 0;
    private Call<Questions> call;
    private ApiService taskService;
    private boolean selectionIsLocked;
    private OptionsRecyclerAdapter.ViewHolder viewHolder;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionIsLocked = false;

        questionText = findViewById(R.id.questionText);
        recyclerOptions = findViewById(R.id.optionsRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerOptions.setLayoutManager(layoutManager);

        makeApiCall();


    }

    public void makeApiCall() {
        taskService = ServiceBuilder.buildService(ApiService.class);
        call = taskService.getQuestions();
        call.enqueue(new Callback<Questions>() {
            @Override
            public void onResponse(Call<Questions> call, Response<Questions> response) {
                data = response.body().getResults();
                populateViews();
            }

            @Override
            public void onFailure(Call<Questions> call, Throwable t) {
                questionText.setText("Akwai Matsala");
            }
        });
    }

    public void populateViews(){
        List<String> choices = processResponse(position);
        optionsRecyclerAdapter = new OptionsRecyclerAdapter(MainActivity.this, choices, data, position, this);
        recyclerOptions.setAdapter(optionsRecyclerAdapter);
        position++;
    }

    public List<String> processResponse(int position){
        incorrectAnswers = data.get(position).getIncorrectAnswers();
        correctAnswer = data.get(position).getCorrectAnswer();
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        for(String s: incorrectAnswers){
            options.add(s);
        }

        data.get(position).setAllOptions(options);
        List<String> choices = data.get(position).getAllOptions();
        Collections.shuffle(choices);
        questionText.setText(Jsoup.parse(data.get(position).getQuestion()).text());
        return choices;
    }

    public void updateViews(){
        if(position < data.size()){
            List<String> choices = processResponse(position);
            viewHolder.imgCorrect.setVisibility(View.GONE);
            viewHolder.imgWrong.setVisibility(View.GONE);
            String bgColor = "#d6d7d7";
            viewHolder.optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(bgColor)));
            optionsRecyclerAdapter.updateOptions(choices, position);
            position++;
        } else if(position >= data.size()){
            questionText.setText("Game Finished");
        }
    }

    public void makeDelay(){
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateViews();
        }
    };

    @Override
    public void onOptionClicked(int position) {
        viewHolder = (OptionsRecyclerAdapter.ViewHolder) recyclerOptions.findViewHolderForAdapterPosition(position);
        String answer = viewHolder.optionText.getText().toString();
        if(!selectionIsLocked){
            if(answer.equals(correctAnswer)){
                viewHolder.imgCorrect.setVisibility(View.VISIBLE);
                viewHolder.optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }else{
                viewHolder.imgWrong.setVisibility(View.VISIBLE);
                viewHolder.optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
            selectionIsLocked = true;
        }
        makeDelay();
    }
}
