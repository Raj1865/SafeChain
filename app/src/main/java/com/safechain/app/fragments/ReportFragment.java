package com.safechain.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.widget.FrameLayout;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.safechain.app.R;
import com.safechain.app.blockchain.BlockchainManager;
import com.safechain.app.database.SafeChainDatabase;
import com.safechain.app.database.entities.Complaint;
import com.safechain.app.database.entities.Evidence;
import com.safechain.app.utils.LSBEmbedder;
import com.safechain.app.utils.LocationHelper;
import com.safechain.app.utils.SafetyScoreEngine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ReportFragment extends Fragment {

    // Steps
    private static final int STEP_EVIDENCE = 1;
    private static final int STEP_DETAILS  = 2;
    private static final int STEP_SHIELD   = 3;
    private static final int STEP_SUCCESS  = 4;

    private int currentStep = STEP_EVIDENCE;
    private ViewGroup stepContainer;
    private View progressFill;
    private TextView tvStepCounter;

    // Evidence state
    private Uri photoUri;
    private File photoFile;
    private String capturedImagePath;
    private String lsbSealedMetadata;
    private boolean evidenceCaptured = false;

    // Location state
    private double currentLat = 12.9716;
    private double currentLng = 77.5946;
    private String currentLocationLabel = "Detecting…";

    // Blockchain state
    private String finalIpfsCid;
    private String finalTxHash;

    private final BlockchainManager blockchainManager = new BlockchainManager();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SafeChainDatabase db;

    // Camera launcher
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = SafeChainDatabase.getInstance(requireContext());

        // Camera Permission
        cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required for Steganography evidence.", Toast.LENGTH_SHORT).show();
                }
            }
        );

        // Camera capture launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && photoUri != null) {
                    handleCapturedImage(photoUri);
                }
            }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    handleCapturedImage(uri);
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stepContainer = view.findViewById(R.id.stepContainer);
        progressFill  = view.findViewById(R.id.progressFill);
        tvStepCounter = view.findViewById(R.id.tvStepCounter);
        showStep(STEP_EVIDENCE);
    }

    // ─── Step navigation ───────────────────────────────────────────────────────

    private void showStep(int step) {
        currentStep = step;
        stepContainer.removeAllViews();
        tvStepCounter.setText("Step " + Math.min(step, 4) + " of 4");

        // Animate progress bar width
        int maxSteps = 4;
        float fraction = (float) Math.min(step, maxSteps) / maxSteps;
        stepContainer.post(() -> {
            int maxWidth = progressFill.getParent() instanceof View
                    ? ((View) progressFill.getParent()).getWidth() : 0;
            ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
            lp.width = (int) (maxWidth * fraction);
            progressFill.setLayoutParams(lp);
        });

        switch (step) {
            case STEP_EVIDENCE: inflateStep1(); break;
            case STEP_DETAILS:  inflateStep2(); break;
            case STEP_SHIELD:   inflateStep3(); break;
            case STEP_SUCCESS:  inflateStep4(); break;
        }
    }

    // ─── Step 1: Evidence Capture ───────────────────────────────────────────────

    // Audio recording state
    private android.media.MediaRecorder mediaRecorder;
    private String audioFilePath;
    private ActivityResultLauncher<String> audioPermissionLauncher;

    private void inflateStep1() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.step1_evidence, stepContainer, false);
        stepContainer.addView(v);

        FrameLayout captureArea = v.findViewById(R.id.captureArea);
        ImageView ivCaptured = v.findViewById(R.id.ivCaptured);
        View placeholder = v.findViewById(R.id.capturePlaceholder);
        TextView tvLsbSealed = v.findViewById(R.id.tvLsbSealed);
        Button btnGallery = v.findViewById(R.id.btnChooseGallery);
        Button btnRecordAudio = v.findViewById(R.id.btnRecordAudio);
        Button btnNext = v.findViewById(R.id.btnStep1Next);

        // Audio permission
        if (audioPermissionLauncher == null) {
            audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(getContext(), "Microphone permission is required.", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }

        // Restore UI if image already captured
        if (evidenceCaptured && capturedImagePath != null) {
            Bitmap bm = BitmapFactory.decodeFile(capturedImagePath);
            if (bm != null) {
                ivCaptured.setImageBitmap(bm);
                ivCaptured.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.GONE);
                tvLsbSealed.setVisibility(View.VISIBLE);
            }
        } else if (evidenceCaptured && audioFilePath != null) {
            placeholder.setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.capturePlaceholder).findViewById(R.id.tvLsbSealed)).setVisibility(View.GONE); // workaround
            Toast.makeText(getContext(), "Audio evidence ready.", Toast.LENGTH_SHORT).show();
            // In a better UI we'd show an audio waveform or icon
        }

        captureArea.setOnClickListener(view -> {
            if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });
        btnGallery.setOnClickListener(view -> galleryLauncher.launch("image/*"));
        
        btnRecordAudio.setOnTouchListener((view, event) -> {
            if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO);
                }
                return false;
            }

            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                btnRecordAudio.setText("Recording...");
                btnRecordAudio.setBackgroundResource(R.drawable.bg_badge_danger);
                startRecording();
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                btnRecordAudio.setText("Hold to Record");
                btnRecordAudio.setBackgroundResource(R.drawable.bg_glass_card);
                stopRecording();
            }
            return true;
        });

        btnNext.setOnClickListener(view -> showStep(STEP_DETAILS));
    }

    private void startRecording() {
        try {
            audioFilePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/SC_AUDIO_" + System.currentTimeMillis() + ".m4a";
            mediaRecorder = new android.media.MediaRecorder();
            mediaRecorder.setAudioSource(android.media.MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException | IllegalStateException e) {
            Toast.makeText(getContext(), "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                evidenceCaptured = true;
                Toast.makeText(getContext(), "Audio recorded successfully", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                // If stopped too quickly
                if (audioFilePath != null) new File(audioFilePath).delete();
                audioFilePath = null;
                evidenceCaptured = false;
            }
        }
    }

    private void openCamera() {
        try {
            photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "SC_EVIDENCE_" + timeStamp;
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void handleCapturedImage(Uri uri) {
        try {
            Bitmap bitmap;
            if (uri.getScheme().equals("content")) {
                java.io.InputStream is = requireContext().getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } else {
                bitmap = BitmapFactory.decodeFile(uri.getPath());
            }

            if (bitmap == null) return;

            // Generate metadata for LSB
            String deviceHash = blockchainManager.computeSha256(
                    android.os.Build.SERIAL + android.os.Build.ID);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                    .format(new Date());
            lsbSealedMetadata = "GPS:" + currentLat + "," + currentLng +
                    "|TS:" + timestamp + "|DEV:" + deviceHash.substring(0, 8);

            // Embed LSB data into image
            Bitmap sealed = LSBEmbedder.embed(bitmap, lsbSealedMetadata);

            // Save sealed image to file
            if (photoFile == null) {
                photoFile = createImageFile();
            }
            java.io.FileOutputStream fos = new java.io.FileOutputStream(photoFile);
            sealed.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();
            capturedImagePath = photoFile.getAbsolutePath();
            evidenceCaptured = true;

            // Update UI
            View v = stepContainer.getChildAt(0);
            if (v != null) {
                ImageView ivCaptured = v.findViewById(R.id.ivCaptured);
                View placeholder = v.findViewById(R.id.capturePlaceholder);
                TextView tvLsbSealed = v.findViewById(R.id.tvLsbSealed);
                if (ivCaptured != null) {
                    ivCaptured.setImageBitmap(sealed);
                    ivCaptured.setVisibility(View.VISIBLE);
                    placeholder.setVisibility(View.GONE);
                    tvLsbSealed.setVisibility(View.VISIBLE);
                }
            }

            Toast.makeText(getContext(), "✓ Evidence LSB-sealed with GPS + timestamp", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    // ─── Step 2: Incident Details ───────────────────────────────────────────────

    private String selectedCategory = "Harassment";
    private String descriptionText = "";

    private void inflateStep2() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.step2_details, stepContainer, false);
        stepContainer.addView(v);

        android.widget.Spinner spinnerCategory = v.findViewById(R.id.spinnerCategory);
        TextView tvLocation = v.findViewById(R.id.tvAutoLocation);
        TextView tvDateTime = v.findViewById(R.id.tvDateTime);
        android.widget.EditText etDesc = v.findViewById(R.id.etDescription);
        Button btnBack = v.findViewById(R.id.btnStep2Back);
        Button btnNext = v.findViewById(R.id.btnStep2Next);

        // Set up category spinner
        String[] categories = {"Harassment", "Assault", "Stalking", "Suspicious Activity", "Other"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set current time
        String now = new SimpleDateFormat("EEEE, MMM dd yyyy · HH:mm", Locale.US).format(new Date());
        tvDateTime.setText(now);

        // Get location
        tvLocation.setText(currentLocationLabel);
        new LocationHelper(requireContext()).getLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(double lat, double lng, String label) {
                currentLat = lat;
                currentLng = lng;
                currentLocationLabel = label;
                if (tvLocation != null) tvLocation.setText(label);
            }
            @Override
            public void onLocationFailed() {
                if (tvLocation != null) tvLocation.setText("Location unavailable (GPS off)");
            }
        });

        btnBack.setOnClickListener(b -> showStep(STEP_EVIDENCE));
        btnNext.setOnClickListener(b -> {
            selectedCategory = spinnerCategory.getSelectedItem().toString();
            descriptionText = etDesc.getText().toString();
            showStep(STEP_SHIELD);
        });
    }

    // ─── Step 3: ZK Shield + Submit ────────────────────────────────────────────

    private void inflateStep3() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.step3_shield, stepContainer, false);
        stepContainer.addView(v);

        TextView tvZkProof = v.findViewById(R.id.tvZkProof);
        LinearLayout llSubmitting = v.findViewById(R.id.llSubmitting);
        Button btnSubmit = v.findViewById(R.id.btnSubmit);
        Button btnBack = v.findViewById(R.id.btnStep3Back);

        // Show generated ZK proof
        String zkProof = blockchainManager.generateZkProof("user_" + UUID.randomUUID(), "district_BLR");
        tvZkProof.setText(zkProof + "\nVerified: User in valid jurisdiction");

        btnBack.setOnClickListener(b -> showStep(STEP_DETAILS));

        btnSubmit.setOnClickListener(b -> {
            btnSubmit.setEnabled(false);
            btnBack.setEnabled(false);
            llSubmitting.setVisibility(View.VISIBLE);

            // Step 1: Seal evidence on blockchain
            String path = "no_media";
            String type = "DOC";
            if (capturedImagePath != null) {
                path = capturedImagePath;
                type = "PHOTO";
            } else if (audioFilePath != null) {
                path = audioFilePath;
                type = "AUDIO";
            }
            
            final String evidencePath = path;
            final String evidenceType = type;
            final String evidenceMeta = lsbSealedMetadata != null ? lsbSealedMetadata : descriptionText;

            blockchainManager.sealEvidence(evidencePath, evidenceMeta,
                new BlockchainManager.BlockchainCallback() {
                    @Override
                    public void onSuccess(String ipfsCid, String txHash) {
                        finalIpfsCid = ipfsCid;
                        finalTxHash = txHash;

                        // Save complaint to Room DB
                        executor.execute(() -> {
                            Complaint c = new Complaint();
                            c.caseId = "cmp_" + (int)(Math.random() * 90000 + 10000);
                            c.category = selectedCategory;
                            c.description = descriptionText;
                            c.latitude = currentLat;
                            c.longitude = currentLng;
                            c.locationLabel = currentLocationLabel;
                            c.status = "SUBMITTED";
                            c.ipfsCid = ipfsCid;
                            c.txHash = txHash;
                            c.zkProof = zkProof;
                            c.submittedAt = System.currentTimeMillis();
                            c.deadlineAt = System.currentTimeMillis() + (48L * 60 * 60 * 1000);
                            c.isSyncedToChain = true;
                            c.evidenceFilePath = evidencePath;
                            db.complaintDao().insert(c);

                            // Also save evidence record
                            if (!evidencePath.equals("no_media")) {
                                Evidence e = new Evidence();
                                e.evidenceId = "ev_" + UUID.randomUUID().toString().substring(0, 8);
                                e.complaintId = c.caseId;
                                e.type = evidenceType;
                                e.filePath = evidencePath;
                                e.sha256Hash = blockchainManager.computeSha256(evidenceMeta);
                                e.ipfsCid = ipfsCid;
                                e.lsbMetadata = lsbSealedMetadata;
                                e.capturedAt = System.currentTimeMillis();
                                e.isBlockchainSealed = true;
                                db.evidenceDao().insert(e);
                            }

                            new Handler(Looper.getMainLooper()).post(() -> {
                                llSubmitting.setVisibility(View.GONE);
                                showStep(STEP_SUCCESS);
                            });
                        });
                    }

                    @Override
                    public void onError(String error) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            llSubmitting.setVisibility(View.GONE);
                            btnSubmit.setEnabled(true);
                            btnBack.setEnabled(true);
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
        });
    }

    // ─── Step 4: Success ────────────────────────────────────────────────────────

    private void inflateStep4() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.step4_success, stepContainer, false);
        stepContainer.addView(v);

        TextView tvIpfs = v.findViewById(R.id.tvIpfsCid);
        TextView tvTx   = v.findViewById(R.id.tvTxHash);
        Button btnCases     = v.findViewById(R.id.btnGoToCases);
        Button btnNewReport = v.findViewById(R.id.btnNewReport);

        tvIpfs.setText(finalIpfsCid != null ? finalIpfsCid : "QmPending…");
        tvTx.setText(finalTxHash != null ? finalTxHash : "0xPending…");

        btnCases.setOnClickListener(b -> {
            // Navigate to Cases tab
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView nav =
                    getActivity().findViewById(R.id.bottomNavView);
                if (nav != null) nav.setSelectedItemId(R.id.casesFragment);
            }
        });

        btnNewReport.setOnClickListener(b -> {
            // Reset and go back to step 1
            evidenceCaptured = false;
            capturedImagePath = null;
            photoFile = null;
            photoUri = null;
            lsbSealedMetadata = null;
            finalIpfsCid = null;
            finalTxHash = null;
            showStep(STEP_EVIDENCE);
        });
    }
}
