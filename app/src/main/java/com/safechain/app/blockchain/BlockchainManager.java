package com.safechain.app.blockchain;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * BlockchainManager handles:
 * - SHA-256 hashing of evidence files
 * - Simulated IPFS upload (returns deterministic CID for demo)
 * - Simulated Polygon smart contract interaction (returns mock TX hash)
 * - ZK proof generation simulation
 *
 * For production: replace simulated calls with real Web3j + Pinata SDK calls.
 */
public class BlockchainManager {

    private static final String TAG = "BlockchainManager";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface BlockchainCallback {
        void onSuccess(String ipfsCid, String txHash);
        void onError(String error);
    }

    public interface HashCallback {
        void onHashComputed(String sha256Hash);
    }

    // Compute SHA-256 hash of raw bytes
    public String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA-256 not available", e);
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    // Compute SHA-256 of a string (e.g., evidence metadata)
    public String computeSha256(String input) {
        return computeSha256(input.getBytes());
    }

    /**
     * Generate a simulated IPFS CID.
     * In production: use Pinata SDK to pin the file and get real CID.
     */
    public String generateIpfsCid(String sha256Hash) {
        // IPFS CIDs start with "Qm" for SHA2-256 multihash
        String shortHash = sha256Hash.substring(0, 16);
        return "QmSC" + shortHash.toUpperCase();
    }

    /**
     * Generate a simulated Polygon transaction hash.
     * In production: call SafeChain.sol's fileComplaint() via Web3j.
     */
    public String generateTxHash(String ipfsCid) {
        String seed = "polygon_" + ipfsCid + System.currentTimeMillis();
        String hash = computeSha256(seed);
        return "0x" + hash.substring(0, 64);
    }

    /**
     * Generate a simulated ZK proof.
     * In production: run snarkjs circuit locally and produce real SNARK proof.
     */
    public String generateZkProof(String userHash, String districtCode) {
        String input = "zk_" + userHash + "_" + districtCode;
        String proofHash = computeSha256(input);
        return "π=0x" + proofHash.substring(0, 32) + "…" + proofHash.substring(56);
    }

    /**
     * Seal evidence to blockchain asynchronously.
     * Simulates:
     * 1. Hashing the evidence file
     * 2. Uploading to IPFS
     * 3. Writing hash to Polygon smart contract
     */
    public void sealEvidence(String evidenceFilePath, String metadata, BlockchainCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Step 1: Compute SHA-256 hash
                String sha256 = computeSha256(evidenceFilePath + "_" + metadata + "_" + System.currentTimeMillis());
                Log.d(TAG, "SHA-256: " + sha256);

                // Step 2: Simulate IPFS upload delay (1-2 second)
                Thread.sleep(1500);
                String ipfsCid = generateIpfsCid(sha256);
                Log.d(TAG, "IPFS CID: " + ipfsCid);

                // Step 3: Simulate Polygon TX (1 second)
                Thread.sleep(1000);
                String txHash = generateTxHash(ipfsCid);
                Log.d(TAG, "TX Hash: " + txHash);

                // Return results on main thread
                mainHandler.post(() -> callback.onSuccess(ipfsCid, txHash));

            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Interrupted: " + e.getMessage()));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Error: " + e.getMessage()));
            }
        });
    }

    /**
     * Verify evidence integrity by recomputing hash.
     * In production: query the Polygon contract for the stored hash.
     */
    public boolean verifyEvidence(String sha256Hash, String ipfsCid) {
        // Simulate verification: check if CID matches expected pattern
        String expectedCidPrefix = "QmSC" + sha256Hash.substring(0, 16).toUpperCase();
        return ipfsCid.startsWith(expectedCidPrefix.substring(0, 6));
    }
}
