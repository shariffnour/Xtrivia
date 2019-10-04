package com.nour.xtrivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CategoryActivity extends AppCompatActivity implements CategoryRecyclerAdapter.OnCategoryClickListener {
    private RecyclerView recyclerView;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    LinearLayoutManager layoutManager;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.categoryRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        categoryRecyclerAdapter = new CategoryRecyclerAdapter(this, this);
        recyclerView.setAdapter(categoryRecyclerAdapter);
    }

    @Override
    public void onCategoryClicked(String name) {
        categoryName = name;
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.putExtra("Category", categoryName);

        startActivity(intent);
    }
}
