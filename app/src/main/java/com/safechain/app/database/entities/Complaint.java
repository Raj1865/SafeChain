package com.safechain.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "complaints")
public class Complaint {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String caseId;
    public String category;
    public String description;
    public double latitude;
    public double longitude;
    public String locationLabel;
    public String dateTime;
    public String status; // SUBMITTED, REVIEWED, ESCALATED, RESOLVED
    public String ipfsCid;
    public String txHash;
    public String zkProof;
    public long submittedAt;
    public long deadlineAt;
    public boolean isSyncedToChain;
    public String evidenceFilePath;
}
