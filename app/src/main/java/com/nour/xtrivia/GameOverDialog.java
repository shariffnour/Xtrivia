package com.nour.xtrivia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverDialog extends AppCompatDialogFragment {
    public TextView finalScore;
    private ImageView backButton;
    private Button playButton;
    public TextView highScore;
    private Animation bounce;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        finalScore = view.findViewById(R.id.finalScore);
        backButton = view.findViewById(R.id.backbtn);
        playButton = view.findViewById(R.id.playAgain);
        highScore = view.findViewById(R.id.highScore);

        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        playButton.startAnimation(bounce);

        setCancelable(false);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Bundle scores = getArguments();
        finalScore.setText(Integer.toString(scores.getInt("score")));
        highScore.setText(Integer.toString(scores.getInt("highScore")));

        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));


        return dialog;
    }

}
