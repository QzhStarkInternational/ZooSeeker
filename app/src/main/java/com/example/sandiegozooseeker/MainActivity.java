package com.example.sandiegozooseeker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VertexViewModel viewModel = new ViewModelProvider(this)
                .get(VertexViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.planFragment);
        badge.isVisible();

        final Observer<Integer> nameObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer count) {
                badge.setNumber(count != null ? count : 0);
            }
        };

        viewModel.getVertexCount().observe(this, nameObserver);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}