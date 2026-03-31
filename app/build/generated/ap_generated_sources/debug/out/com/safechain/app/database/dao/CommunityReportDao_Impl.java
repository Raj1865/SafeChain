package com.safechain.app.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safechain.app.database.entities.CommunityReport;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CommunityReportDao_Impl implements CommunityReportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CommunityReport> __insertionAdapterOfCommunityReport;

  public CommunityReportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCommunityReport = new EntityInsertionAdapter<CommunityReport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `community_reports` (`id`,`latitude`,`longitude`,`category`,`reportedAt`,`severityLevel`,`isSynced`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CommunityReport entity) {
        statement.bindLong(1, entity.id);
        statement.bindDouble(2, entity.latitude);
        statement.bindDouble(3, entity.longitude);
        if (entity.category == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.category);
        }
        statement.bindLong(5, entity.reportedAt);
        statement.bindLong(6, entity.severityLevel);
        final int _tmp = entity.isSynced ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
  }

  @Override
  public long insert(final CommunityReport report) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfCommunityReport.insertAndReturnId(report);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<CommunityReport>> getAll() {
    final String _sql = "SELECT * FROM community_reports ORDER BY reportedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"community_reports"}, false, new Callable<List<CommunityReport>>() {
      @Override
      @Nullable
      public List<CommunityReport> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfReportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reportedAt");
          final int _cursorIndexOfSeverityLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "severityLevel");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final List<CommunityReport> _result = new ArrayList<CommunityReport>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CommunityReport _item;
            _item = new CommunityReport();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.reportedAt = _cursor.getLong(_cursorIndexOfReportedAt);
            _item.severityLevel = _cursor.getInt(_cursorIndexOfSeverityLevel);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _item.isSynced = _tmp != 0;
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<CommunityReport> getPendingSync() {
    final String _sql = "SELECT * FROM community_reports WHERE isSynced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfReportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reportedAt");
      final int _cursorIndexOfSeverityLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "severityLevel");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final List<CommunityReport> _result = new ArrayList<CommunityReport>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CommunityReport _item;
        _item = new CommunityReport();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        _item.reportedAt = _cursor.getLong(_cursorIndexOfReportedAt);
        _item.severityLevel = _cursor.getInt(_cursorIndexOfSeverityLevel);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
        _item.isSynced = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<CommunityReport> getNearby(final double minLat, final double maxLat,
      final double minLng, final double maxLng) {
    final String _sql = "SELECT * FROM community_reports WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minLat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxLat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, minLng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, maxLng);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfReportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reportedAt");
      final int _cursorIndexOfSeverityLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "severityLevel");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final List<CommunityReport> _result = new ArrayList<CommunityReport>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CommunityReport _item;
        _item = new CommunityReport();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        _item.reportedAt = _cursor.getLong(_cursorIndexOfReportedAt);
        _item.severityLevel = _cursor.getInt(_cursorIndexOfSeverityLevel);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
        _item.isSynced = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
