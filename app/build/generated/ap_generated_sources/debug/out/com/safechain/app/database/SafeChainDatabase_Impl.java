package com.safechain.app.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.safechain.app.database.dao.CommunityReportDao;
import com.safechain.app.database.dao.CommunityReportDao_Impl;
import com.safechain.app.database.dao.ComplaintDao;
import com.safechain.app.database.dao.ComplaintDao_Impl;
import com.safechain.app.database.dao.EvidenceDao;
import com.safechain.app.database.dao.EvidenceDao_Impl;
import com.safechain.app.database.dao.SafetyAlertDao;
import com.safechain.app.database.dao.SafetyAlertDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SafeChainDatabase_Impl extends SafeChainDatabase {
  private volatile ComplaintDao _complaintDao;

  private volatile EvidenceDao _evidenceDao;

  private volatile CommunityReportDao _communityReportDao;

  private volatile SafetyAlertDao _safetyAlertDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `complaints` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `caseId` TEXT, `category` TEXT, `description` TEXT, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `locationLabel` TEXT, `dateTime` TEXT, `status` TEXT, `ipfsCid` TEXT, `txHash` TEXT, `zkProof` TEXT, `submittedAt` INTEGER NOT NULL, `deadlineAt` INTEGER NOT NULL, `isSyncedToChain` INTEGER NOT NULL, `evidenceFilePath` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `evidence` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `evidenceId` TEXT, `complaintId` TEXT, `type` TEXT, `filePath` TEXT, `sha256Hash` TEXT, `ipfsCid` TEXT, `lsbMetadata` TEXT, `capturedAt` INTEGER NOT NULL, `isBlockchainSealed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `community_reports` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `category` TEXT, `reportedAt` INTEGER NOT NULL, `severityLevel` INTEGER NOT NULL, `isSynced` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `safety_alerts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `alertId` TEXT, `type` TEXT, `title` TEXT, `message` TEXT, `severity` TEXT, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `areaName` TEXT, `timestamp` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `triggerSource` TEXT, `incidentCount` INTEGER NOT NULL, `timeWindow` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5217f6ea0439ba0654abab62b66000cd')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `complaints`");
        db.execSQL("DROP TABLE IF EXISTS `evidence`");
        db.execSQL("DROP TABLE IF EXISTS `community_reports`");
        db.execSQL("DROP TABLE IF EXISTS `safety_alerts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsComplaints = new HashMap<String, TableInfo.Column>(16);
        _columnsComplaints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("caseId", new TableInfo.Column("caseId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("locationLabel", new TableInfo.Column("locationLabel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("dateTime", new TableInfo.Column("dateTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("ipfsCid", new TableInfo.Column("ipfsCid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("txHash", new TableInfo.Column("txHash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("zkProof", new TableInfo.Column("zkProof", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("submittedAt", new TableInfo.Column("submittedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("deadlineAt", new TableInfo.Column("deadlineAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("isSyncedToChain", new TableInfo.Column("isSyncedToChain", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComplaints.put("evidenceFilePath", new TableInfo.Column("evidenceFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysComplaints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesComplaints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoComplaints = new TableInfo("complaints", _columnsComplaints, _foreignKeysComplaints, _indicesComplaints);
        final TableInfo _existingComplaints = TableInfo.read(db, "complaints");
        if (!_infoComplaints.equals(_existingComplaints)) {
          return new RoomOpenHelper.ValidationResult(false, "complaints(com.safechain.app.database.entities.Complaint).\n"
                  + " Expected:\n" + _infoComplaints + "\n"
                  + " Found:\n" + _existingComplaints);
        }
        final HashMap<String, TableInfo.Column> _columnsEvidence = new HashMap<String, TableInfo.Column>(10);
        _columnsEvidence.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("evidenceId", new TableInfo.Column("evidenceId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("complaintId", new TableInfo.Column("complaintId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("filePath", new TableInfo.Column("filePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("sha256Hash", new TableInfo.Column("sha256Hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("ipfsCid", new TableInfo.Column("ipfsCid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("lsbMetadata", new TableInfo.Column("lsbMetadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("capturedAt", new TableInfo.Column("capturedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvidence.put("isBlockchainSealed", new TableInfo.Column("isBlockchainSealed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEvidence = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEvidence = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEvidence = new TableInfo("evidence", _columnsEvidence, _foreignKeysEvidence, _indicesEvidence);
        final TableInfo _existingEvidence = TableInfo.read(db, "evidence");
        if (!_infoEvidence.equals(_existingEvidence)) {
          return new RoomOpenHelper.ValidationResult(false, "evidence(com.safechain.app.database.entities.Evidence).\n"
                  + " Expected:\n" + _infoEvidence + "\n"
                  + " Found:\n" + _existingEvidence);
        }
        final HashMap<String, TableInfo.Column> _columnsCommunityReports = new HashMap<String, TableInfo.Column>(7);
        _columnsCommunityReports.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("reportedAt", new TableInfo.Column("reportedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("severityLevel", new TableInfo.Column("severityLevel", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommunityReports.put("isSynced", new TableInfo.Column("isSynced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCommunityReports = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCommunityReports = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCommunityReports = new TableInfo("community_reports", _columnsCommunityReports, _foreignKeysCommunityReports, _indicesCommunityReports);
        final TableInfo _existingCommunityReports = TableInfo.read(db, "community_reports");
        if (!_infoCommunityReports.equals(_existingCommunityReports)) {
          return new RoomOpenHelper.ValidationResult(false, "community_reports(com.safechain.app.database.entities.CommunityReport).\n"
                  + " Expected:\n" + _infoCommunityReports + "\n"
                  + " Found:\n" + _existingCommunityReports);
        }
        final HashMap<String, TableInfo.Column> _columnsSafetyAlerts = new HashMap<String, TableInfo.Column>(14);
        _columnsSafetyAlerts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("alertId", new TableInfo.Column("alertId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("severity", new TableInfo.Column("severity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("areaName", new TableInfo.Column("areaName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("triggerSource", new TableInfo.Column("triggerSource", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("incidentCount", new TableInfo.Column("incidentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSafetyAlerts.put("timeWindow", new TableInfo.Column("timeWindow", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSafetyAlerts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSafetyAlerts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSafetyAlerts = new TableInfo("safety_alerts", _columnsSafetyAlerts, _foreignKeysSafetyAlerts, _indicesSafetyAlerts);
        final TableInfo _existingSafetyAlerts = TableInfo.read(db, "safety_alerts");
        if (!_infoSafetyAlerts.equals(_existingSafetyAlerts)) {
          return new RoomOpenHelper.ValidationResult(false, "safety_alerts(com.safechain.app.database.entities.SafetyAlert).\n"
                  + " Expected:\n" + _infoSafetyAlerts + "\n"
                  + " Found:\n" + _existingSafetyAlerts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5217f6ea0439ba0654abab62b66000cd", "252b7ca5ebe7438f0fc42badf363bb0f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "complaints","evidence","community_reports","safety_alerts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `complaints`");
      _db.execSQL("DELETE FROM `evidence`");
      _db.execSQL("DELETE FROM `community_reports`");
      _db.execSQL("DELETE FROM `safety_alerts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ComplaintDao.class, ComplaintDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EvidenceDao.class, EvidenceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommunityReportDao.class, CommunityReportDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SafetyAlertDao.class, SafetyAlertDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ComplaintDao complaintDao() {
    if (_complaintDao != null) {
      return _complaintDao;
    } else {
      synchronized(this) {
        if(_complaintDao == null) {
          _complaintDao = new ComplaintDao_Impl(this);
        }
        return _complaintDao;
      }
    }
  }

  @Override
  public EvidenceDao evidenceDao() {
    if (_evidenceDao != null) {
      return _evidenceDao;
    } else {
      synchronized(this) {
        if(_evidenceDao == null) {
          _evidenceDao = new EvidenceDao_Impl(this);
        }
        return _evidenceDao;
      }
    }
  }

  @Override
  public CommunityReportDao communityReportDao() {
    if (_communityReportDao != null) {
      return _communityReportDao;
    } else {
      synchronized(this) {
        if(_communityReportDao == null) {
          _communityReportDao = new CommunityReportDao_Impl(this);
        }
        return _communityReportDao;
      }
    }
  }

  @Override
  public SafetyAlertDao safetyAlertDao() {
    if (_safetyAlertDao != null) {
      return _safetyAlertDao;
    } else {
      synchronized(this) {
        if(_safetyAlertDao == null) {
          _safetyAlertDao = new SafetyAlertDao_Impl(this);
        }
        return _safetyAlertDao;
      }
    }
  }
}
