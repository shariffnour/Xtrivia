package com.nour.xtrivia;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nour.xtrivia.services.ApiService;
import com.nour.xtrivia.services.ServiceBuilder;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OptionsRecyclerAdapter.OnOptionClickListener {
    private static final String TAG = "DEBUG:";
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
    TextView scoreView, questionText, questionCount, category;
    private int score = 0;
    private ProgressBar progressBar;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String HIGH_SCORE = "highScore";
    private String categoryName;
    private Map<String, Integer> categoryNames;
    private int categoryNumber;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionIsLocked = false;

        scoreView = findViewById(R.id.score);
        questionCount = findViewById(R.id.questioncount);
        category = findViewById(R.id.category);
        progressBar = findViewById(R.id.progress_circular);
        questionText = findViewById(R.id.questionText);
        recyclerOptions = findViewById(R.id.optionsRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerOptions.setLayoutManager(layoutManager);

        realm = Realm.getDefaultInstance();

        getCategoryNumber();
        makeApiCall();


    }

    private int getCategoryNumber() {
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("Category");
        category.setText(categoryName);

        categoryNames = Categories.getCategories();
        for(Map.Entry<String, Integer> entry: categoryNames.entrySet()){
            if(entry.getKey().equals(categoryName)){
                categoryNumber = entry.getValue();
            }
        }
        return categoryNumber;
    }

    public void makeApiCall() {
        taskService = ServiceBuilder.buildService(ApiService.class);
        call = taskService.getQuestions(10, getCategoryNumber(), "multiple");
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
        progressBar.setVisibility(View.GONE);
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

        saveScoresToRealm(categoryName, "Easy", score);
        scores.putInt("score", score);
        scores.putInt("highScore", getHighScore());

        GameOverDialog dialog = new GameOverDialog();
        dialog.setArguments(scores);
        dialog.show(getSupportFragmentManager(), "GameOver Dialog");
    }

    //Inserts Scores to the Realm database
    private void saveScoresToRealm(final String category, final String difficulty, final int points) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number maxId = realm.where(Scores.class).max("id");
                int newId = (maxId == null) ? 1 : maxId.intValue()+1;
                Scores score = realm.createObject(Scores.class, newId);

                score.setCategory(category);
                score.setDifficulty(difficulty);
                score.setPoints(points);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
               // Toast.makeText(MainActivity.this, "Value Stored Successfully", Toast.LENGTH_LONG).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
               // Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }});
    }

    public void makeDelay() {
        handler.postDelayed(runnable, 1000);
    }

    public void addPoints() {
        ValueAnimator animator = ValueAnimator.ofInt(score, score+5);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scoreView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        score = score + 5;
    }

    public void minusPoints() {
        ValueAnimator animator = ValueAnimator.ofInt(score, score-2);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scoreView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        score = score - 2;
    }

    public void blinkOption(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.option_fade);
        view.startAnimation(anim);
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
                View view = viewHolder.optionCard;
                blinkOption(view);
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
                View view = v.optionCard;
                blinkOption(view);
            }
        }
    }
}
