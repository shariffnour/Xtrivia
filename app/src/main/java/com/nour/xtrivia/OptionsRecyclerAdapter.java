package com.nour.xtrivia;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.jsoup.Jsoup;

import java.util.List;

public class OptionsRecyclerAdapter extends RecyclerView.Adapter<OptionsRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<String> optionList;
    private String correctAnswer;
    private final LayoutInflater layoutInflater;
    private int position;
    private OnOptionClickListener optionClickListener;


    public OptionsRecyclerAdapter(Context context, List<String> optionList, List<Result> resultList, int position, OnOptionClickListener optionClickListener){
        this.context = context;
        this.optionList = optionList;
        this.position = position;
        this.optionClickListener = optionClickListener;
        this.correctAnswer = resultList.get(position).getCorrectAnswer();
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.option_item, parent, false);
        return new ViewHolder(itemView, optionClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.optionText.setText(Jsoup.parse(optionList.get(position)).text());
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public void updateOptions(List<String> result, int newPosition){
        optionList = result;
        position = newPosition;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView optionText;
        ImageView imgCorrect;
        ImageView imgWrong;
        CardView optionCard;
        OnOptionClickListener optionClickListener;

        public ViewHolder(View itemView, final OnOptionClickListener optionClickListener){
            super(itemView);
            optionText = itemView.findViewById(R.id.option);
            imgCorrect = itemView.findViewById(R.id.imgCorrect);
            imgWrong = itemView.findViewById(R.id.imgWrong);
            optionCard = itemView.findViewById(R.id.optionCard);
            this.optionClickListener = optionClickListener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionClickListener.onOptionClicked(getAdapterPosition());
                }

            });
        }
    }


    public interface OnOptionClickListener{
        void onOptionClicked(int position);
    }
}
