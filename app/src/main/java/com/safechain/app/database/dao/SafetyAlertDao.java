package com.safechain.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safechain.app.database.entities.SafetyAlert;

import java.util.List;

@Dao
public interface SafetyAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SafetyAlert alert);

    @Query("SELECT * FROM safety_alerts ORDER BY timestamp DESC")
    LiveData<List<SafetyAlert>> getAll();

    @Query("SELECT * FROM safety_alerts WHERE isRead = 0 ORDER BY timestamp DESC")
    LiveData<List<SafetyAlert>> getUnread();

    @Query("SELECT COUNT(*) FROM safety_alerts WHERE isRead = 0")
    int getUnreadCount();

    @Query("UPDATE safety_alerts SET isRead = 1 WHERE id = :id")
    void markRead(int id);

    @Query("UPDATE safety_alerts SET isRead = 1")
    void markAllRead();

    @Query("DELETE FROM safety_alerts WHERE timestamp < :cutoff")
    void deleteOlderThan(long cutoff);
}
