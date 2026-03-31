package com.safechain.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "community_reports")
public class CommunityReport {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double latitude;
    public double longitude;
    public String category;
    public long reportedAt;
    public int severityLevel; // 1=low, 2=medium, 3=high
    public boolean isSynced;
}
