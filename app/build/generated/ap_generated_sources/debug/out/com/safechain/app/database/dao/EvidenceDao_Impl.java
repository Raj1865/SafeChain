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
import com.safechain.app.database.entities.Evidence;
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
public final class EvidenceDao_Impl implements EvidenceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Evidence> __insertionAdapterOfEvidence;

  private final SharedSQLiteStatement __preparedStmtOfUpdateIpfsCid;

  public EvidenceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEvidence = new EntityInsertionAdapter<Evidence>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `evidence` (`id`,`evidenceId`,`complaintId`,`type`,`filePath`,`sha256Hash`,`ipfsCid`,`lsbMetadata`,`capturedAt`,`isBlockchainSealed`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Evidence entity) {
        statement.bindLong(1, entity.id);
        if (entity.evidenceId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.evidenceId);
        }
        if (entity.complaintId == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.complaintId);
        }
        if (entity.type == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.type);
        }
        if (entity.filePath == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.filePath);
        }
        if (entity.sha256Hash == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.sha256Hash);
        }
        if (entity.ipfsCid == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.ipfsCid);
        }
        if (entity.lsbMetadata == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.lsbMetadata);
        }
        statement.bindLong(9, entity.capturedAt);
        final int _tmp = entity.isBlockchainSealed ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__preparedStmtOfUpdateIpfsCid = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE evidence SET ipfsCid = ?, isBlockchainSealed = 1 WHERE evidenceId = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Evidence evidence) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfEvidence.insertAndReturnId(evidence);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateIpfsCid(final String evidenceId, final String cid) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateIpfsCid.acquire();
    int _argIndex = 1;
    if (cid == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, cid);
    }
    _argIndex = 2;
    if (evidenceId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, evidenceId);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateIpfsCid.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Evidence>> getAllEvidence() {
    final String _sql = "SELECT * FROM evidence ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"evidence"}, false, new Callable<List<Evidence>>() {
      @Override
      @Nullable
      public List<Evidence> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEvidenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenceId");
          final int _cursorIndexOfComplaintId = CursorUtil.getColumnIndexOrThrow(_cursor, "complaintId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfSha256Hash = CursorUtil.getColumnIndexOrThrow(_cursor, "sha256Hash");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfLsbMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "lsbMetadata");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfIsBlockchainSealed = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlockchainSealed");
          final List<Evidence> _result = new ArrayList<Evidence>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Evidence _item;
            _item = new Evidence();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfEvidenceId)) {
              _item.evidenceId = null;
            } else {
              _item.evidenceId = _cursor.getString(_cursorIndexOfEvidenceId);
            }
            if (_cursor.isNull(_cursorIndexOfComplaintId)) {
              _item.complaintId = null;
            } else {
              _item.complaintId = _cursor.getString(_cursorIndexOfComplaintId);
            }
            if (_cursor.isNull(_cursorIndexOfType)) {
              _item.type = null;
            } else {
              _item.type = _cursor.getString(_cursorIndexOfType);
            }
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _item.filePath = null;
            } else {
              _item.filePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            if (_cursor.isNull(_cursorIndexOfSha256Hash)) {
              _item.sha256Hash = null;
            } else {
              _item.sha256Hash = _cursor.getString(_cursorIndexOfSha256Hash);
            }
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _item.ipfsCid = null;
            } else {
              _item.ipfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            if (_cursor.isNull(_cursorIndexOfLsbMetadata)) {
              _item.lsbMetadata = null;
            } else {
              _item.lsbMetadata = _cursor.getString(_cursorIndexOfLsbMetadata);
            }
            _item.capturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBlockchainSealed);
            _item.isBlockchainSealed = _tmp != 0;
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
  public List<Evidence> getByComplaintId(final String complaintId) {
    final String _sql = "SELECT * FROM evidence WHERE complaintId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (complaintId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, complaintId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEvidenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenceId");
      final int _cursorIndexOfComplaintId = CursorUtil.getColumnIndexOrThrow(_cursor, "complaintId");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
      final int _cursorIndexOfSha256Hash = CursorUtil.getColumnIndexOrThrow(_cursor, "sha256Hash");
      final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
      final int _cursorIndexOfLsbMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "lsbMetadata");
      final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
      final int _cursorIndexOfIsBlockchainSealed = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlockchainSealed");
      final List<Evidence> _result = new ArrayList<Evidence>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Evidence _item;
        _item = new Evidence();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfEvidenceId)) {
          _item.evidenceId = null;
        } else {
          _item.evidenceId = _cursor.getString(_cursorIndexOfEvidenceId);
        }
        if (_cursor.isNull(_cursorIndexOfComplaintId)) {
          _item.complaintId = null;
        } else {
          _item.complaintId = _cursor.getString(_cursorIndexOfComplaintId);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfFilePath)) {
          _item.filePath = null;
        } else {
          _item.filePath = _cursor.getString(_cursorIndexOfFilePath);
        }
        if (_cursor.isNull(_cursorIndexOfSha256Hash)) {
          _item.sha256Hash = null;
        } else {
          _item.sha256Hash = _cursor.getString(_cursorIndexOfSha256Hash);
        }
        if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
          _item.ipfsCid = null;
        } else {
          _item.ipfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
        }
        if (_cursor.isNull(_cursorIndexOfLsbMetadata)) {
          _item.lsbMetadata = null;
        } else {
          _item.lsbMetadata = _cursor.getString(_cursorIndexOfLsbMetadata);
        }
        _item.capturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsBlockchainSealed);
        _item.isBlockchainSealed = _tmp != 0;
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
