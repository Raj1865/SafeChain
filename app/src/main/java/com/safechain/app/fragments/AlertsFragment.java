package com.safechain.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safechain.app.R;
import com.safechain.app.adapters.AlertsAdapter;
import com.safechain.app.database.SafeChainDatabase;
import com.safechain.app.database.entities.SafetyAlert;
import com.safechain.app.utils.SafetyScoreEngine;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * AlertsFragment — shows ONLY real-time alerts from the Room DB.
 *
 * Alerts are created by:
 * 1. BLE SOS Scanner catching a distress signal → saves to safetyAlertDao
 * 2. Community reports flagged on the Map → PredictiveAlertEngine detects clusters
 *
 * NO fake alerts are generated on button click. Refresh just re-reads the DB.
 */
public class AlertsFragment extends Fragment implements AlertsAdapter.OnAlertAction {

    private RecyclerView recyclerAlerts;
    private AlertsAdapter adapter;
    private SafeChainDatabase db;
    private TextView tvCurrentRisk, tvUnreadCount;
    private View llGenerating, llEmptyAlerts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = SafeChainDatabase.getInstance(requireContext());

        recyclerAlerts = view.findViewById(R.id.recyclerAlerts);
        tvCurrentRisk  = view.findViewById(R.id.tvCurrentRisk);
        tvUnreadCount  = view.findViewById(R.id.tvUnreadCount);
        llGenerating   = view.findViewById(R.id.llGenerating);
        llEmptyAlerts  = view.findViewById(R.id.llEmptyAlerts);
        Button btnRefresh = view.findViewById(R.id.btnRefreshAlerts);

        recyclerAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AlertsAdapter(new ArrayList<>(), this);
        recyclerAlerts.setAdapter(adapter);

        // Observe real alerts from Room DB (LiveData — auto-updates)
        db.safetyAlertDao().getAll().observe(getViewLifecycleOwner(), alerts -> {
            adapter.updateData(alerts);
            llEmptyAlerts.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerAlerts.setVisibility(alerts.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Observe unread count
        db.safetyAlertDao().getUnread().observe(getViewLifecycleOwner(), unread -> {
            tvUnreadCount.setText(String.valueOf(unread.size()));
        });

        // Dynamic risk summary based on actual DB data
        updateDynamicRisk();

        // Refresh button — just shows a toast, data is already live via LiveData
        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Alerts are live — listening for BLE SOS signals and community flags in real-time.", Toast.LENGTH_LONG).show();
        });
    }

    private void updateDynamicRisk() {
        // Risk level is driven by actual alerts + time of day
        db.communityReportDao().getAll().observe(getViewLifecycleOwner(), reports -> {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int incidentCount = reports.size();
            int lightingScore = (hour >= 8 && hour < 20) ? 2 : 0;
            int crowdDensity  = (hour >= 9 && hour < 21) ? 2 : 0;

            int score = SafetyScoreEngine.computeScore(hour, incidentCount, lightingScore, crowdDensity);
            String risk = SafetyScoreEngine.getRiskLevel(score);

            tvCurrentRisk.setText(risk);
            int colorRes;
            if (score >= 70)       colorRes = R.color.accent_green;
            else if (score >= 40)  colorRes = R.color.accent_amber;
            else                   colorRes = R.color.secondary;
            tvCurrentRisk.setTextColor(requireContext().getColor(colorRes));
        });
    }

    @Override
    public void onViewMap(SafetyAlert alert) {
        if (getActivity() != null) {
            com.google.android.material.bottomnavigation.BottomNavigationView nav =
                getActivity().findViewById(R.id.bottomNavView);
            if (nav != null) nav.setSelectedItemId(R.id.mapFragment);
        }
    }

    @Override
    public void onMarkRead(SafetyAlert alert) {
        new Thread(() -> db.safetyAlertDao().markRead(alert.id)).start();
    }
}
