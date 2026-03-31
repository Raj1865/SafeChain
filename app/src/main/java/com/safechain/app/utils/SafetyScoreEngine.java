package com.safechain.app.utils;

/**
 * SafetyScoreEngine computes a safety score (0-100) for a route segment.
 * Inputs: time of day, historical incident count, reported incidents nearby.
 * This simulates the XGBoost ML model described in the SafeChain architecture.
 */
public class SafetyScoreEngine {

    /**
     * Compute safety score for a location at a given time.
     * @param hourOfDay      Hour of day (0-23)
     * @param incidentCount  Number of recent incidents in 500m radius
     * @param lightingScore  0=dark, 1=dim, 2=well-lit
     * @param crowdDensity   0=isolated, 1=sparse, 2=moderate, 3=crowded
     * @return Safety score 0-100 (100 = safest)
     */
    public static int computeScore(int hourOfDay, int incidentCount, int lightingScore, int crowdDensity) {
        int score = 100;

        // Night penalty: 10PM - 5AM
        if (hourOfDay >= 22 || hourOfDay < 5) {
            score -= 25;
        } else if (hourOfDay >= 19 || hourOfDay < 7) {
            score -= 10;
        }

        // Incident count penalty
        score -= Math.min(incidentCount * 8, 40);

        // Lighting penalty
        score -= (2 - lightingScore) * 8;

        // Crowd density: moderate crowds safer, isolation risky
        if (crowdDensity == 0) score -= 15; // isolated = unsafe
        else if (crowdDensity == 3) score -= 10; // too crowded = medium risk
        else score += 5; // moderate crowd = safer

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Get risk level label from score.
     */
    public static String getRiskLevel(int score) {
        if (score >= 70) return "LOW RISK";
        if (score >= 40) return "MODERATE";
        return "HIGH RISK";
    }

    /**
     * Get color resource ID name for score.
     */
    public static String getScoreColor(int score) {
        if (score >= 70) return "accent_green";
        if (score >= 40) return "accent_amber";
        return "secondary";
    }
}
