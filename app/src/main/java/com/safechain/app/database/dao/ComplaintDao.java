package com.safechain.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safechain.app.database.entities.Complaint;

import java.util.List;

@Dao
public interface ComplaintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Complaint complaint);

    @Update
    void update(Complaint complaint);

    @Query("SELECT * FROM complaints ORDER BY submittedAt DESC")
    LiveData<List<Complaint>> getAllComplaints();

    @Query("SELECT * FROM complaints WHERE id = :id")
    Complaint getById(int id);

    @Query("UPDATE complaints SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);

    @Query("UPDATE complaints SET ipfsCid = :cid, txHash = :txHash, isSyncedToChain = 1 WHERE id = :id")
    void updateBlockchainData(int id, String cid, String txHash);

    @Query("SELECT * FROM complaints WHERE isSyncedToChain = 0")
    List<Complaint> getPendingSync();
}
