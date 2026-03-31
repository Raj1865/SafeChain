package com.safechain.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "safety_alerts")
public class SafetyAlert {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String alertId;
    public String type;        // PREDICTIVE, COMMUNITY, SOS_NEARBY, HEATMAP_SPIKE
    public String title;
    public String message;
    public String severity;    // LOW, MEDIUM, HIGH, CRITICAL
    public double latitude;
    public double longitude;
    public String areaName;
    public long timestamp;
    public boolean isRead;
    public String triggerSource; // TIME_PATTERN, INCIDENT_CLUSTER, COMMUNITY_FLAG
    public int incidentCount;
    public String timeWindow;   // e.g. "WEEKNIGHT_10PM", "WEEKEND_LATE"
}
