package com.safechain.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safechain.app.database.entities.Evidence;

import java.util.List;

@Dao
public interface EvidenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Evidence evidence);

    @Query("SELECT * FROM evidence ORDER BY capturedAt DESC")
    LiveData<List<Evidence>> getAllEvidence();

    @Query("SELECT * FROM evidence WHERE complaintId = :complaintId")
    List<Evidence> getByComplaintId(String complaintId);

    @Query("UPDATE evidence SET ipfsCid = :cid, isBlockchainSealed = 1 WHERE evidenceId = :evidenceId")
    void updateIpfsCid(String evidenceId, String cid);
}
