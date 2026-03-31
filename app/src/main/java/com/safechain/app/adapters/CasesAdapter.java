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
import com.safechain.app.database.entities.Complaint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.ViewHolder> {

    public interface OnCaseAction {
        void onExportReport(Complaint complaint);
        void onViewLifecycle(Complaint complaint);
    }

    private List<Complaint> complaints;
    private final OnCaseAction listener;

    public CasesAdapter(List<Complaint> complaints, OnCaseAction listener) {
        this.complaints = complaints;
        this.listener   = listener;
    }

    public void updateData(List<Complaint> newComplaints) {
        this.complaints = newComplaints;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint c = complaints.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvCaseId.setText(c.caseId != null ? c.caseId : "cmp_???");
        holder.tvStatus.setText(c.status);
        holder.tvIpfsCid.setText(c.ipfsCid != null
                ? c.ipfsCid.substring(0, Math.min(c.ipfsCid.length(), 10)) + "…" : "Pending…");
        holder.tvTxHash.setText(c.txHash != null
                ? c.txHash.substring(0, Math.min(c.txHash.length(), 14)) + "…" : "Pending…");

        String submittedStr = new SimpleDateFormat("MMM dd · HH:mm", Locale.US)
                .format(new Date(c.submittedAt));
        holder.tvSubmittedTime.setText("Submitted: " + submittedStr);

        long remaining = c.deadlineAt - System.currentTimeMillis();
        if (remaining > 0) {
            long hours = remaining / (1000 * 60 * 60);
            holder.tvDeadlineTime.setText("Auto-escalates in: " + hours + "h via Smart Contract");
        } else {
            holder.tvDeadlineTime.setText("ESCALATED (Smart Contract triggered)");
        }

        int color;
        switch (c.status != null ? c.status : "") {
            case "SUBMITTED":  color = ctx.getColor(R.color.status_submitted); break;
            case "REVIEWED":   color = ctx.getColor(R.color.status_reviewed);  break;
            case "ESCALATED":  color = ctx.getColor(R.color.status_escalated); break;
            case "RESOLVED":   color = ctx.getColor(R.color.status_resolved);  break;
            default:           color = ctx.getColor(R.color.text_muted); break;
        }
        holder.tvStatus.setTextColor(color);
        holder.tvStatus.setBackgroundResource(statusBackground(c.status));

        holder.escalationWarning.setVisibility(
            "SUBMITTED".equals(c.status) ? View.VISIBLE : View.GONE);

        // Action buttons
        holder.btnExport.setOnClickListener(v -> {
            if (listener != null) listener.onExportReport(c);
        });
        holder.btnLifecycle.setOnClickListener(v -> {
            if (listener != null) listener.onViewLifecycle(c);
        });
    }

    private int statusBackground(String status) {
        if (status == null) return R.drawable.bg_badge_info;
        switch (status) {
            case "SUBMITTED":  return R.drawable.bg_badge_info;
            case "ESCALATED":  return R.drawable.bg_badge_danger;
            case "RESOLVED":   return R.drawable.bg_badge_success;
            default:           return R.drawable.bg_badge_info;
        }
    }

    @Override
    public int getItemCount() { return complaints != null ? complaints.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCaseId, tvStatus, tvIpfsCid, tvTxHash, tvSubmittedTime, tvDeadlineTime;
        View escalationWarning;
        Button btnExport, btnLifecycle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaseId         = itemView.findViewById(R.id.tvCaseId);
            tvStatus         = itemView.findViewById(R.id.tvStatus);
            tvIpfsCid        = itemView.findViewById(R.id.tvIpfsCid);
            tvTxHash         = itemView.findViewById(R.id.tvTxHash);
            tvSubmittedTime  = itemView.findViewById(R.id.tvSubmittedTime);
            tvDeadlineTime   = itemView.findViewById(R.id.tvDeadlineTime);
            escalationWarning = itemView.findViewById(R.id.escalationWarning);
            btnExport        = itemView.findViewById(R.id.btnExportReport);
            btnLifecycle     = itemView.findViewById(R.id.btnViewLifecycle);
        }
    }
}
