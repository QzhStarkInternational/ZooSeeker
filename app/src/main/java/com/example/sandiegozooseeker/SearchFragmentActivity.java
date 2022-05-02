package com.example.sandiegozooseeker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SearchFragmentActivity extends AppCompatActivity {
    public SearchFragmentActivity() {
        super(R.layout.search_fragment_activity);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, SearchFragment.class, null)
                    .commit();
        }
    }
}
