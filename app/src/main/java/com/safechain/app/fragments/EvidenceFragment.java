package com.safechain.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safechain.app.R;
import com.safechain.app.adapters.EvidenceAdapter;
import com.safechain.app.database.SafeChainDatabase;

import java.util.ArrayList;

public class EvidenceFragment extends Fragment {

    private RecyclerView recyclerView;
    private EvidenceAdapter adapter;
    private SafeChainDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evidence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = SafeChainDatabase.getInstance(requireContext());
        recyclerView = view.findViewById(R.id.recyclerEvidence);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EvidenceAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Observe evidence LiveData from Room
        db.evidenceDao().getAllEvidence().observe(getViewLifecycleOwner(), evidenceList -> {
            adapter.updateData(evidenceList);
        });
    }
}
