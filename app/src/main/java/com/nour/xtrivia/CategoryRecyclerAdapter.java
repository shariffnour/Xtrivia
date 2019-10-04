package com.nour.xtrivia;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private Context context;
    private final LayoutInflater layoutInflater;
    private Map<String, Integer> categoryNames;
    OnCategoryClickListener categoryClickListener;

    public CategoryRecyclerAdapter(Context context, OnCategoryClickListener categoryClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.categoryClickListener = categoryClickListener;
        categoryNames = Categories.getCategories();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.option_item, parent, false);
        return new ViewHolder(itemView, categoryClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.categoryName.setText(categoryNames.keySet().toArray()[position].toString());
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView categoryName;
        CardView categoryCard;
        OnCategoryClickListener categoryClickListener;
        public ViewHolder(View itemView, final OnCategoryClickListener categoryClickListener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.option);
            categoryCard = itemView.findViewById(R.id.optionCard);
            this.categoryClickListener = categoryClickListener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryClickListener.onCategoryClicked(categoryName.getText().toString());
                }
            });
        }
    }

    public interface OnCategoryClickListener{
        void onCategoryClicked(String name);
    }
}
