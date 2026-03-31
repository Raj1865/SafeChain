package com.safechain.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "evidence")
public class Evidence {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String evidenceId;
    public String complaintId;
    public String type; // PHOTO, AUDIO, SCREENSHOT
    public String filePath;
    public String sha256Hash;
    public String ipfsCid;
    public String lsbMetadata; // embedded GPS+timestamp
    public long capturedAt;
    public boolean isBlockchainSealed;
}
