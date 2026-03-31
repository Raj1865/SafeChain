package com.safechain.app;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.safechain.app.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private TextView tvOfflineBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        NavigationUI.setupWithNavController(bottomNav, navController);

        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);

        // Show/hide offline banner based on connectivity
        updateOfflineBanner();
    }

    private void updateOfflineBanner() {
        boolean online = NetworkUtils.isOnline(this);
        tvOfflineBanner.setVisibility(online ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateOfflineBanner();
    }
}
