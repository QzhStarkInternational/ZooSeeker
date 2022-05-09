package com.example.sandiegozooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
<<<<<<< Updated upstream
=======

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.planFragment);
        badge.isVisible();

        final Observer<Integer> nameObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer count) {
                badge.setNumber(count != null ? count : 0);
            }
        };
>>>>>>> Stashed changes

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}