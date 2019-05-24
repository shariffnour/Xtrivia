package com.nour.xtrivia;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView scoreView;
    TextView questionCount;
    private int score = 0;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String HIGH_SCORE = "highScore";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionIsLocked = false;

        scoreView = findViewById(R.id.score);
        questionCount = findViewById(R.id.questioncount);
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
                Toast.makeText(MainActivity.this, "No Internet Connection. Please connect to the Internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateViews() {
        List<String> choices = processResponse(position);
        optionsRecyclerAdapter = new OptionsRecyclerAdapter(MainActivity.this, choices, data, position, this);
        recyclerOptions.setAdapter(optionsRecyclerAdapter);
        position++;
        changeQuestionCount();
    }

    private void changeQuestionCount() {
        String count = position + "/" + data.size();
        questionCount.setText(count);
    }

    public List<String> processResponse(int position) {
        incorrectAnswers = data.get(position).getIncorrectAnswers();
        correctAnswer = data.get(position).getCorrectAnswer();
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        for (String s : incorrectAnswers) {
            options.add(s);
        }

        data.get(position).setAllOptions(options);
        List<String> choices = data.get(position).getAllOptions();
        Collections.shuffle(choices);
        questionText.setText(Jsoup.parse(data.get(position).getQuestion()).text());
        return choices;
    }

    public void updateViews() {
        if (position < data.size()) {
            for (int i = 0; i < 4; i++) {
                OptionsRecyclerAdapter.ViewHolder v = (OptionsRecyclerAdapter.ViewHolder) recyclerOptions.findViewHolderForAdapterPosition(i);
                v.imgCorrect.setVisibility(View.GONE);
                v.imgWrong.setVisibility(View.GONE);
                v.optionCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            }
            List<String> choices = processResponse(position);
            optionsRecyclerAdapter.updateOptions(choices, position);
            selectionIsLocked = false;
            position++;
            changeQuestionCount();
        } else if (position >= data.size()) {
            showDialog();
        }
    }

    public int getHighScore(){
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPrefs.getInt(HIGH_SCORE, 0);
    }

    public void showDialog() {
        Bundle scores = new Bundle();

        if(score > getHighScore()){
            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(HIGH_SCORE, score);
            editor.apply();
        }


        scores.putInt("score", score);
        scores.putInt("highScore", getHighScore());

        GameOverDialog dialog = new GameOverDialog();
        dialog.setArguments(scores);
        dialog.show(getSupportFragmentManager(), "GameOver Dialog");
    }

    public void makeDelay() {
        handler.postDelayed(runnable, 1000);
    }

    public void addPoints() {
        score = score + 5;
        scoreView.setText(Integer.toString(score));
    }

    public void minusPoints() {
        score = score - 2;
        scoreView.setText(Integer.toString(score));
    }

    public void blinkOption() {
        ObjectAnimator anim = ObjectAnimator.ofInt(viewHolder.optionCard, "cardBackgroundColor",
                Color.WHITE, Color.GREEN, Color.WHITE);
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
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
        if (!selectionIsLocked) {
            if (answer.equals(correctAnswer)) {
                viewHolder.imgCorrect.setVisibility(View.VISIBLE);
                viewHolder.optionCard.setCardBackgroundColor(Color.GREEN);
                addPoints();
            } else {
                viewHolder.imgWrong.setVisibility(View.VISIBLE);
                viewHolder.optionCard.setCardBackgroundColor(Color.RED);
                if (score >= 2)
                    minusPoints();
                showCorrectAnswer();
            }
            selectionIsLocked = true;
        }
        makeDelay();
    }

    private void showCorrectAnswer() {
        for (int i = 0; i < 4; i++) {
            OptionsRecyclerAdapter.ViewHolder v = (OptionsRecyclerAdapter.ViewHolder) recyclerOptions.findViewHolderForAdapterPosition(i);
            if (v.optionText.getText().toString().equals(correctAnswer)) {
                v.optionCard.setCardBackgroundColor(Color.GREEN);
            }
        }
    }
}
