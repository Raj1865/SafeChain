package com.safechain.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safechain.app.R;
import com.safechain.app.adapters.CasesAdapter;
import com.safechain.app.database.SafeChainDatabase;
import com.safechain.app.database.entities.Complaint;
import com.safechain.app.utils.ReportExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CasesFragment extends Fragment implements CasesAdapter.OnCaseAction {

    private RecyclerView recyclerView;
    private CasesAdapter adapter;
    private SafeChainDatabase db;
    private ReportExporter reportExporter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = SafeChainDatabase.getInstance(requireContext());
        reportExporter = new ReportExporter(requireContext());

        recyclerView = view.findViewById(R.id.recyclerCases);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CasesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // "How It Works" card is tappable → navigates to Evidence Locker
        View cardInfo = view.findViewById(R.id.cardHowItWorks);
        if (cardInfo != null) {
            cardInfo.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.evidenceFragment));
        }

        // Observe Room LiveData — auto-updates when new complaints are filed
        db.complaintDao().getAllComplaints().observe(getViewLifecycleOwner(),
            complaints -> adapter.updateData(complaints));
    }

    @Override
    public void onExportReport(Complaint complaint) {
        // Fetch evidence and export
        executor.execute(() -> {
            java.util.List<com.safechain.app.database.entities.Evidence> evidence =
                db.evidenceDao().getByComplaintId(complaint.caseId);

            reportExporter.export(complaint, evidence, new ReportExporter.ExportCallback() {
                @Override
                public void onSuccess(String filePath) {
                    // Share the file via Intent
                    File file = new File(filePath);
                    Uri uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        file
                    );
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "SafeChain Complaint Report – " + complaint.caseId);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent,
                        "Share Complaint Report With Authorities"));

                    Toast.makeText(getContext(),
                        "Report exported: " + file.getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(),
                        "Export failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onViewLifecycle(Complaint complaint) {
        // Show lifecycle dialog
        String[] stages = {"SUBMITTED", "REVIEWED", "ESCALATED", "RESOLVED"};
        boolean[] done = new boolean[4];
        String currentStatus = complaint.status != null ? complaint.status : "SUBMITTED";
        for (int i = 0; i < stages.length; i++) {
            done[i] = true;
            if (stages[i].equals(currentStatus)) break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Complaint Lifecycle\n\n");
        for (int i = 0; i < stages.length; i++) {
            sb.append(done[i] ? "✓ " : "○ ").append(stages[i]);
            if (stages[i].equals(currentStatus)) sb.append("  ← CURRENT");
            sb.append("\n");
        }
        sb.append("\nBlockchain Audit Trail:");
        sb.append("\n• Filed:      " + complaint.caseId);
        sb.append("\n• IPFS:       " + (complaint.ipfsCid != null ? complaint.ipfsCid : "Pending"));
        sb.append("\n• Polygon TX: " + (complaint.txHash  != null ? complaint.txHash  : "Pending"));
        sb.append("\n\nAll state transitions are logged on-chain.");

        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Case #" + complaint.caseId)
            .setMessage(sb.toString())
            .setPositiveButton("Export Full Report", (d, w) -> onExportReport(complaint))
            .setNegativeButton("Close", null)
            .show();
    }
}
