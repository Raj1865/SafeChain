package com.safechain.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safechain.app.R;
import com.safechain.app.database.entities.Evidence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvidenceAdapter extends RecyclerView.Adapter<EvidenceAdapter.ViewHolder> {

    private List<Evidence> evidenceList;

    public EvidenceAdapter(List<Evidence> evidenceList) {
        this.evidenceList = evidenceList;
    }

    public void updateData(List<Evidence> newList) {
        this.evidenceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evidence, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Evidence e = evidenceList.get(position);

        holder.tvEvidenceType.setText(formatType(e.type));
        holder.tvHash.setText(e.sha256Hash != null
                ? e.sha256Hash.substring(0, Math.min(e.sha256Hash.length(), 10)) + "…" : "Computing…");
        holder.tvIpfsCid.setText(e.ipfsCid != null
                ? e.ipfsCid.substring(0, Math.min(e.ipfsCid.length(), 10)) + "…" : "Uploading…");

        String dateStr = new SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.US)
                .format(new Date(e.capturedAt));
        holder.tvEvidenceDate.setText(dateStr);

        // Load actual image thumbnail from file path
        if (e.filePath != null && !e.filePath.isEmpty()) {
            java.io.File file = new java.io.File(e.filePath);
            if (file.exists()) {
                try {
                    android.graphics.BitmapFactory.Options opts = new android.graphics.BitmapFactory.Options();
                    opts.inSampleSize = 4; // Scale down for thumbnail
                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(e.filePath, opts);
                    if (bmp != null) {
                        holder.ivEvidenceThumb.setImageBitmap(bmp);
                        holder.ivEvidenceThumb.setPadding(0, 0, 0, 0);
                        holder.ivEvidenceThumb.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);

                        // Make thumbnail clickable to view full image
                        holder.itemView.setOnClickListener(v -> {
                            android.content.Intent viewIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                                v.getContext(),
                                v.getContext().getPackageName() + ".fileprovider",
                                file);
                            viewIntent.setDataAndType(uri, "image/*");
                            viewIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            v.getContext().startActivity(viewIntent);
                        });
                        return;
                    }
                } catch (Exception ex) {
                    // Fallback to icon
                }
            }
        }

        // Fallback: set icon based on type
        int iconRes;
        switch (e.type != null ? e.type : "") {
            case "AUDIO":      iconRes = android.R.drawable.ic_btn_speak_now; break;
            case "SCREENSHOT": iconRes = android.R.drawable.ic_menu_gallery;  break;
            default:           iconRes = android.R.drawable.ic_menu_camera;   break;
        }
        holder.ivEvidenceThumb.setImageResource(iconRes);
    }

    private String formatType(String type) {
        if (type == null) return "Evidence";
        switch (type) {
            case "PHOTO":      return "Photo Evidence";
            case "AUDIO":      return "Audio Recording";
            case "SCREENSHOT": return "Screenshot";
            default:           return "Evidence";
        }
    }

    @Override
    public int getItemCount() { return evidenceList != null ? evidenceList.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEvidenceThumb;
        TextView tvEvidenceType, tvEvidenceDate, tvHash, tvIpfsCid;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEvidenceThumb  = itemView.findViewById(R.id.ivEvidenceThumb);
            tvEvidenceType   = itemView.findViewById(R.id.tvEvidenceType);
            tvEvidenceDate   = itemView.findViewById(R.id.tvEvidenceDate);
            tvHash           = itemView.findViewById(R.id.tvHash);
            tvIpfsCid        = itemView.findViewById(R.id.tvIpfsCid);
        }
    }
}
