package com.safechain.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.safechain.app.database.dao.ComplaintDao;
import com.safechain.app.database.dao.CommunityReportDao;
import com.safechain.app.database.dao.EvidenceDao;
import com.safechain.app.database.dao.SafetyAlertDao;
import com.safechain.app.database.entities.Complaint;
import com.safechain.app.database.entities.CommunityReport;
import com.safechain.app.database.entities.Evidence;
import com.safechain.app.database.entities.SafetyAlert;

@Database(
    entities = {Complaint.class, Evidence.class, CommunityReport.class, SafetyAlert.class},
    version = 2,
    exportSchema = false
)
public abstract class SafeChainDatabase extends RoomDatabase {
    private static SafeChainDatabase instance;

    public abstract ComplaintDao complaintDao();
    public abstract EvidenceDao evidenceDao();
    public abstract CommunityReportDao communityReportDao();
    public abstract SafetyAlertDao safetyAlertDao();

    public static synchronized SafeChainDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    SafeChainDatabase.class,
                    "safechain_db"
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
