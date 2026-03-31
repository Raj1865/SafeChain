package com.safechain.app.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safechain.app.database.entities.SafetyAlert;
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
public final class SafetyAlertDao_Impl implements SafetyAlertDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SafetyAlert> __insertionAdapterOfSafetyAlert;

  private final SharedSQLiteStatement __preparedStmtOfMarkRead;

  private final SharedSQLiteStatement __preparedStmtOfMarkAllRead;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public SafetyAlertDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSafetyAlert = new EntityInsertionAdapter<SafetyAlert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `safety_alerts` (`id`,`alertId`,`type`,`title`,`message`,`severity`,`latitude`,`longitude`,`areaName`,`timestamp`,`isRead`,`triggerSource`,`incidentCount`,`timeWindow`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final SafetyAlert entity) {
        statement.bindLong(1, entity.id);
        if (entity.alertId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.alertId);
        }
        if (entity.type == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.type);
        }
        if (entity.title == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.title);
        }
        if (entity.message == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.message);
        }
        if (entity.severity == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.severity);
        }
        statement.bindDouble(7, entity.latitude);
        statement.bindDouble(8, entity.longitude);
        if (entity.areaName == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.areaName);
        }
        statement.bindLong(10, entity.timestamp);
        final int _tmp = entity.isRead ? 1 : 0;
        statement.bindLong(11, _tmp);
        if (entity.triggerSource == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.triggerSource);
        }
        statement.bindLong(13, entity.incidentCount);
        if (entity.timeWindow == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.timeWindow);
        }
      }
    };
    this.__preparedStmtOfMarkRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE safety_alerts SET isRead = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAllRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE safety_alerts SET isRead = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM safety_alerts WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final SafetyAlert alert) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSafetyAlert.insertAndReturnId(alert);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void markRead(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkRead.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkRead.release(_stmt);
    }
  }

  @Override
  public void markAllRead() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAllRead.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkAllRead.release(_stmt);
    }
  }

  @Override
  public void deleteOlderThan(final long cutoff) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, cutoff);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteOlderThan.release(_stmt);
    }
  }

  @Override
  public LiveData<List<SafetyAlert>> getAll() {
    final String _sql = "SELECT * FROM safety_alerts ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"safety_alerts"}, false, new Callable<List<SafetyAlert>>() {
      @Override
      @Nullable
      public List<SafetyAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlertId = CursorUtil.getColumnIndexOrThrow(_cursor, "alertId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfTriggerSource = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerSource");
          final int _cursorIndexOfIncidentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentCount");
          final int _cursorIndexOfTimeWindow = CursorUtil.getColumnIndexOrThrow(_cursor, "timeWindow");
          final List<SafetyAlert> _result = new ArrayList<SafetyAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SafetyAlert _item;
            _item = new SafetyAlert();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfAlertId)) {
              _item.alertId = null;
            } else {
              _item.alertId = _cursor.getString(_cursorIndexOfAlertId);
            }
            if (_cursor.isNull(_cursorIndexOfType)) {
              _item.type = null;
            } else {
              _item.type = _cursor.getString(_cursorIndexOfType);
            }
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _item.title = null;
            } else {
              _item.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _item.message = null;
            } else {
              _item.message = _cursor.getString(_cursorIndexOfMessage);
            }
            if (_cursor.isNull(_cursorIndexOfSeverity)) {
              _item.severity = null;
            } else {
              _item.severity = _cursor.getString(_cursorIndexOfSeverity);
            }
            _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _item.areaName = null;
            } else {
              _item.areaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _item.isRead = _tmp != 0;
            if (_cursor.isNull(_cursorIndexOfTriggerSource)) {
              _item.triggerSource = null;
            } else {
              _item.triggerSource = _cursor.getString(_cursorIndexOfTriggerSource);
            }
            _item.incidentCount = _cursor.getInt(_cursorIndexOfIncidentCount);
            if (_cursor.isNull(_cursorIndexOfTimeWindow)) {
              _item.timeWindow = null;
            } else {
              _item.timeWindow = _cursor.getString(_cursorIndexOfTimeWindow);
            }
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
  public LiveData<List<SafetyAlert>> getUnread() {
    final String _sql = "SELECT * FROM safety_alerts WHERE isRead = 0 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"safety_alerts"}, false, new Callable<List<SafetyAlert>>() {
      @Override
      @Nullable
      public List<SafetyAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlertId = CursorUtil.getColumnIndexOrThrow(_cursor, "alertId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAreaName = CursorUtil.getColumnIndexOrThrow(_cursor, "areaName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfTriggerSource = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerSource");
          final int _cursorIndexOfIncidentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentCount");
          final int _cursorIndexOfTimeWindow = CursorUtil.getColumnIndexOrThrow(_cursor, "timeWindow");
          final List<SafetyAlert> _result = new ArrayList<SafetyAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SafetyAlert _item;
            _item = new SafetyAlert();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfAlertId)) {
              _item.alertId = null;
            } else {
              _item.alertId = _cursor.getString(_cursorIndexOfAlertId);
            }
            if (_cursor.isNull(_cursorIndexOfType)) {
              _item.type = null;
            } else {
              _item.type = _cursor.getString(_cursorIndexOfType);
            }
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _item.title = null;
            } else {
              _item.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _item.message = null;
            } else {
              _item.message = _cursor.getString(_cursorIndexOfMessage);
            }
            if (_cursor.isNull(_cursorIndexOfSeverity)) {
              _item.severity = null;
            } else {
              _item.severity = _cursor.getString(_cursorIndexOfSeverity);
            }
            _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
            if (_cursor.isNull(_cursorIndexOfAreaName)) {
              _item.areaName = null;
            } else {
              _item.areaName = _cursor.getString(_cursorIndexOfAreaName);
            }
            _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _item.isRead = _tmp != 0;
            if (_cursor.isNull(_cursorIndexOfTriggerSource)) {
              _item.triggerSource = null;
            } else {
              _item.triggerSource = _cursor.getString(_cursorIndexOfTriggerSource);
            }
            _item.incidentCount = _cursor.getInt(_cursorIndexOfIncidentCount);
            if (_cursor.isNull(_cursorIndexOfTimeWindow)) {
              _item.timeWindow = null;
            } else {
              _item.timeWindow = _cursor.getString(_cursorIndexOfTimeWindow);
            }
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
  public int getUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM safety_alerts WHERE isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
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
