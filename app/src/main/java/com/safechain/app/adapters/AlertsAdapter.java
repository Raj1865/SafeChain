package com.safechain.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safechain.app.R;
import com.safechain.app.database.entities.SafetyAlert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    public interface OnAlertAction {
        void onViewMap(SafetyAlert alert);
        void onMarkRead(SafetyAlert alert);
    }

    private List<SafetyAlert> alerts;
    private final OnAlertAction listener;

    public AlertsAdapter(List<SafetyAlert> alerts, OnAlertAction listener) {
        this.alerts   = alerts;
        this.listener = listener;
    }

    public void updateData(List<SafetyAlert> newAlerts) {
        this.alerts = newAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SafetyAlert alert = alerts.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvAlertTitle.setText(alert.title);
        holder.tvAlertMessage.setText(alert.message);
        holder.tvAlertSeverity.setText(alert.severity);
        holder.tvAlertType.setText(alert.type);

        // Relative timestamp
        long ago = System.currentTimeMillis() - alert.timestamp;
        String timeStr;
        if (ago < 60_000)       timeStr = "Just now";
        else if (ago < 3600_000) timeStr = (ago / 60_000) + " min ago";
        else if (ago < 86400_000) timeStr = (ago / 3600_000) + "h ago";
        else timeStr = new SimpleDateFormat("MMM dd", Locale.US).format(new Date(alert.timestamp));
        holder.tvAlertTime.setText(timeStr);

        // Source label
        switch (alert.triggerSource != null ? alert.triggerSource : "") {
            case "TIME_PATTERN":     holder.tvAlertSource.setText("⚡ ML Pattern Analysis"); break;
            case "INCIDENT_CLUSTER": holder.tvAlertSource.setText("📍 Incident Cluster Data"); break;
            case "COMMUNITY_FLAG":   holder.tvAlertSource.setText("👥 Community Reports"); break;
            default:                 holder.tvAlertSource.setText("📊 Safety Analysis"); break;
        }

        // Severity color
        int severityColor;
        int dotColor;
        switch (alert.severity != null ? alert.severity : "") {
            case "CRITICAL":
            case "HIGH":     severityColor = ctx.getColor(R.color.secondary);    dotColor = ctx.getColor(R.color.secondary);    break;
            case "MEDIUM":   severityColor = ctx.getColor(R.color.accent_amber); dotColor = ctx.getColor(R.color.accent_amber); break;
            default:         severityColor = ctx.getColor(R.color.accent_green); dotColor = ctx.getColor(R.color.accent_green); break;
        }
        holder.tvAlertSeverity.setTextColor(severityColor);
        holder.viewSeverityDot.setBackgroundColor(dotColor);

        // Unread highlight
        holder.itemView.setAlpha(alert.isRead ? 0.7f : 1.0f);

        holder.btnViewMap.setOnClickListener(v -> {
            if (listener != null) listener.onViewMap(alert);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMarkRead(alert);
            holder.itemView.setAlpha(0.7f);
        });
    }

    @Override
    public int getItemCount() { return alerts != null ? alerts.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewSeverityDot;
        TextView tvAlertTitle, tvAlertMessage, tvAlertTime, tvAlertSeverity, tvAlertType, tvAlertSource;
        Button btnViewMap;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSeverityDot  = itemView.findViewById(R.id.viewSeverityDot);
            tvAlertTitle     = itemView.findViewById(R.id.tvAlertTitle);
            tvAlertMessage   = itemView.findViewById(R.id.tvAlertMessage);
            tvAlertTime      = itemView.findViewById(R.id.tvAlertTime);
            tvAlertSeverity  = itemView.findViewById(R.id.tvAlertSeverity);
            tvAlertType      = itemView.findViewById(R.id.tvAlertType);
            tvAlertSource    = itemView.findViewById(R.id.tvAlertSource);
            btnViewMap       = itemView.findViewById(R.id.btnAlertViewMap);
        }
    }
}
