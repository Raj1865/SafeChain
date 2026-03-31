package com.safechain.app.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.safechain.app.database.entities.Complaint;
import com.safechain.app.database.entities.Evidence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ReportExporter generates a legally-structured, exportable complaint report.
 * The exported file includes:
 *  - Complaint ID and timestamp
 *  - Category and description
 *  - GPS coordinates
 *  - IPFS CID (decentralized evidence link)
 *  - Polygon transaction hash (immutable blockchain proof)
 *  - ZK proof reference
 *  - SHA-256 evidence hashes for all attached files
 *  - Auto-escalation deadline
 */
public class ReportExporter {
    private static final String TAG = "ReportExporter";

    public interface ExportCallback {
        void onSuccess(String filePath);
        void onError(String error);
    }

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ReportExporter(Context context) {
        this.context = context;
    }

    /**
     * Export a complaint to a legally-structured text report file.
     */
    public void export(Complaint complaint, List<Evidence> evidenceList, ExportCallback callback) {
        executor.execute(() -> {
            try {
                String content = buildReport(complaint, evidenceList);

                // Save to external files
                File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (!dir.exists()) dir.mkdirs();

                String fileName = "SafeChain_Report_" + complaint.caseId + ".txt";
                File outFile = new File(dir, fileName);

                FileWriter fw = new FileWriter(outFile);
                fw.write(content);
                fw.close();

                Log.d(TAG, "Report exported: " + outFile.getAbsolutePath());

                if (callback != null) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onSuccess(outFile.getAbsolutePath()));
                }
            } catch (IOException e) {
                Log.e(TAG, "Export failed: " + e.getMessage());
                if (callback != null) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    private String buildReport(Complaint complaint, List<Evidence> evidenceList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
        String now = sdf.format(new Date());
        String submittedAt = sdf.format(new Date(complaint.submittedAt));
        String deadlineAt  = sdf.format(new Date(complaint.deadlineAt));

        StringBuilder sb = new StringBuilder();

        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("         SAFECHAIN — OFFICIAL COMPLAINT REPORT\n");
        sb.append("         Decentralized · Tamper-Proof · Verifiable\n");
        sb.append("════════════════════════════════════════════════════════════\n\n");

        sb.append("REPORT METADATA\n");
        sb.append("───────────────────────────────────\n");
        sb.append("Report ID       : ").append(complaint.caseId).append("\n");
        sb.append("Generated On    : ").append(now).append("\n");
        sb.append("Filed On        : ").append(submittedAt).append("\n");
        sb.append("Status          : ").append(complaint.status).append("\n");
        sb.append("Escalation By   : ").append(deadlineAt).append("\n\n");

        sb.append("INCIDENT DETAILS\n");
        sb.append("───────────────────────────────────\n");
        sb.append("Category        : ").append(complaint.category).append("\n");
        sb.append("Location        : ").append(complaint.locationLabel).append("\n");
        sb.append("GPS Coordinates : ").append(complaint.latitude).append(", ").append(complaint.longitude).append("\n");
        sb.append("Description     : ").append(complaint.description != null ? complaint.description : "[Not provided — privacy protected]").append("\n\n");

        sb.append("BLOCKCHAIN VERIFICATION\n");
        sb.append("───────────────────────────────────\n");
        sb.append("IPFS Evidence CID   : ").append(complaint.ipfsCid != null ? complaint.ipfsCid : "Pending upload").append("\n");
        sb.append("Polygon TX Hash     : ").append(complaint.txHash != null ? complaint.txHash : "Pending confirmation").append("\n");
        sb.append("ZK Proof Reference  : ").append(complaint.zkProof != null ? complaint.zkProof.substring(0, Math.min(40, complaint.zkProof.length())) + "..." : "N/A").append("\n");
        sb.append("Chain Status        : ").append(complaint.isSyncedToChain ? "✓ SEALED ON-CHAIN" : "⚠ PENDING SYNC").append("\n\n");

        sb.append("DECENTRALIZED STORAGE\n");
        sb.append("───────────────────────────────────\n");
        sb.append("Storage Network     : IPFS (InterPlanetary File System)\n");
        sb.append("Pinning Service     : Pinata / Web3.Storage\n");
        sb.append("Content Addressing  : SHA-256 multihash\n");
        sb.append("Immutability        : ✓ Content cannot be altered once CID issued\n");
        sb.append("Censorship-Resistant: ✓ No single entity controls storage\n\n");

        sb.append("ATTACHED EVIDENCE (").append(evidenceList.size()).append(" file(s))\n");
        sb.append("───────────────────────────────────\n");
        if (evidenceList.isEmpty()) {
            sb.append("No media evidence attached.\n");
        } else {
            for (int i = 0; i < evidenceList.size(); i++) {
                Evidence e = evidenceList.get(i);
                sb.append("Evidence #").append(i + 1).append(" (").append(e.type).append(")\n");
                sb.append("  SHA-256 Hash    : ").append(e.sha256Hash != null ? e.sha256Hash : "Computing...").append("\n");
                sb.append("  IPFS CID        : ").append(e.ipfsCid != null ? e.ipfsCid : "Uploading...").append("\n");
                sb.append("  LSB Metadata    : ").append(e.lsbMetadata != null ? e.lsbMetadata : "N/A").append("\n");
                sb.append("  Blockchain Seal : ").append(e.isBlockchainSealed ? "✓ Verified" : "Pending").append("\n");
                sb.append("  Captured At     : ").append(sdf.format(new Date(e.capturedAt))).append("\n\n");
            }
        }

        sb.append("SMART CONTRACT WORKFLOW\n");
        sb.append("───────────────────────────────────\n");
        sb.append("Contract Network    : Polygon (Amoy Testnet)\n");
        sb.append("Auto-Escalation     : 48 hours from submission\n");
        sb.append("Lifecycle Stages    : SUBMITTED → REVIEWED → ESCALATED → RESOLVED\n");
        sb.append("Public Audit Log    : All state changes emit on-chain events\n");
        sb.append("Tamper Detection    : SHA-256 hash stored immutably at submission\n\n");

        sb.append("LEGAL NOTICE\n");
        sb.append("───────────────────────────────────\n");
        sb.append("This report was generated by the SafeChain platform.\n");
        sb.append("The IPFS CID and Polygon transaction hash constitute a\n");
        sb.append("cryptographically verifiable proof of evidence integrity.\n");
        sb.append("This report may be submitted to law enforcement, courts,\n");
        sb.append("or any regulatory body as tamper-evident documentation.\n\n");

        sb.append("Verify evidence at: https://ipfs.io/ipfs/").append(complaint.ipfsCid).append("\n");
        sb.append("Verify TX at      : https://polygonscan.com/tx/").append(complaint.txHash).append("\n\n");

        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("                  END OF REPORT\n");
        sb.append("════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }
}
