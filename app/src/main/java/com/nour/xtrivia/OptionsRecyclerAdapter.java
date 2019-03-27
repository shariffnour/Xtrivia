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
    private boolean selectionIsLocked;

    public OptionsRecyclerAdapter(Context context, List<String> optionList, List<Result> resultList){
        this.context = context;
        this.optionList = optionList;
        this.correctAnswer = resultList.get(0).getCorrectAnswer();
        layoutInflater = LayoutInflater.from(context);
        selectionIsLocked = false;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.option_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.optionText.setText(Jsoup.parse(optionList.get(position)).text());
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public void updateOptions(List<String> result){
        optionList = result;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView optionText;
        ImageView imgCorrect;
        ImageView imgWrong;
        CardView optionCard;

        public ViewHolder(View itemView){
            super(itemView);
            optionText = (TextView) itemView.findViewById(R.id.option);
            imgCorrect = (ImageView) itemView.findViewById(R.id.imgCorrect);
            imgWrong = (ImageView) itemView.findViewById(R.id.imgWrong);
            optionCard = (CardView) itemView.findViewById(R.id.optionCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = optionText.getText().toString();
                    if(!selectionIsLocked){
                        if(answer.equals(correctAnswer)){
                            imgCorrect.setVisibility(View.VISIBLE);
                            optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        }else{
                            imgWrong.setVisibility(View.VISIBLE);
                            optionCard.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                        selectionIsLocked = true;
                    }
                }
            });
        }
    }
}
