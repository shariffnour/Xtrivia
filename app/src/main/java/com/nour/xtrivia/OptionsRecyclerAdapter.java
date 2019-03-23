package com.nour.xtrivia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class OptionsRecyclerAdapter extends RecyclerView.Adapter<OptionsRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Result> resultList;
    private final LayoutInflater layoutInflater;
    private int optionIndex;
    private String TAG = "LOGLOGLOG";
    private List<String> choices = new ArrayList<>();

    public OptionsRecyclerAdapter(Context context, List<Result> resultList){
        this.context = context;
        this.resultList = resultList;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.option_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<String> incorrectAnswers = resultList.get(position).getIncorrectAnswers();
        String correctAnswer = resultList.get(position).getCorrectAnswer();
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        for(String s: incorrectAnswers){
            options.add(s);
        }

        resultList.get(position).setAllOptions(options);
        choices = resultList.get(position).getAllOptions();


            Log.d(TAG, choices.get(position));

            holder.optionText.setText(choices.get(position));

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateOptions(List<Result> result){
        resultList = result;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView optionText;
        public ViewHolder(View itemView){
            super(itemView);
            optionText = (TextView) itemView.findViewById(R.id.option);
        }
    }
}
