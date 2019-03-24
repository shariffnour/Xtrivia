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
    private List<String> resultList;
    private final LayoutInflater layoutInflater;

    public OptionsRecyclerAdapter(Context context, List<String> resultList){
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
            holder.optionText.setText(resultList.get(position));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateOptions(List<String> result){
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
