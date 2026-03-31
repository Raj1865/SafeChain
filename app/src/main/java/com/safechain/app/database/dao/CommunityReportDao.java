package com.safechain.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safechain.app.database.entities.CommunityReport;

import java.util.List;

@Dao
public interface CommunityReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CommunityReport report);

    @Query("SELECT * FROM community_reports ORDER BY reportedAt DESC")
    LiveData<List<CommunityReport>> getAll();

    @Query("SELECT * FROM community_reports WHERE isSynced = 0")
    List<CommunityReport> getPendingSync();

    @Query("SELECT * FROM community_reports WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    List<CommunityReport> getNearby(double minLat, double maxLat, double minLng, double maxLng);
}
