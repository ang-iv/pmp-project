package com.example.pmpproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String favId = intent.getStringExtra("favId");
        boolean favorite = intent.getBooleanExtra("favorite", false);

        TextView nameText = (TextView) findViewById(R.id.name);
        nameText.setText(name);

        TextView descText = (TextView) findViewById(R.id.description);
        descText.setText(description);

        ImageView favIconText = (ImageView) findViewById(R.id.favImage);
        favIconText.setImageResource(favorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}