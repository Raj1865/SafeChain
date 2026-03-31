package com.safechain.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity: The Stealth Mode launcher.
 * Appears as a normal "DailyNotes" app.
 * Triple-tap the title in 2 seconds to enter SafeChain.
 */
public class SplashActivity extends AppCompatActivity {

    private int tapCount = 0;
    private final Handler resetHandler = new Handler();
    private Runnable resetTapRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tvStealthTitle);

        resetTapRunnable = () -> tapCount = 0;

        tvTitle.setOnClickListener(v -> {
            tapCount++;
            resetHandler.removeCallbacks(resetTapRunnable);
            resetHandler.postDelayed(resetTapRunnable, 1200);

            if (tapCount >= 3) {
                // Unlock SafeChain
                tapCount = 0;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
