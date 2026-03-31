package com.safechain.app.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safechain.app.database.entities.Complaint;
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
public final class ComplaintDao_Impl implements ComplaintDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Complaint> __insertionAdapterOfComplaint;

  private final EntityDeletionOrUpdateAdapter<Complaint> __updateAdapterOfComplaint;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBlockchainData;

  public ComplaintDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfComplaint = new EntityInsertionAdapter<Complaint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `complaints` (`id`,`caseId`,`category`,`description`,`latitude`,`longitude`,`locationLabel`,`dateTime`,`status`,`ipfsCid`,`txHash`,`zkProof`,`submittedAt`,`deadlineAt`,`isSyncedToChain`,`evidenceFilePath`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Complaint entity) {
        statement.bindLong(1, entity.id);
        if (entity.caseId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.caseId);
        }
        if (entity.category == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.category);
        }
        if (entity.description == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.description);
        }
        statement.bindDouble(5, entity.latitude);
        statement.bindDouble(6, entity.longitude);
        if (entity.locationLabel == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.locationLabel);
        }
        if (entity.dateTime == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.dateTime);
        }
        if (entity.status == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.status);
        }
        if (entity.ipfsCid == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.ipfsCid);
        }
        if (entity.txHash == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.txHash);
        }
        if (entity.zkProof == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.zkProof);
        }
        statement.bindLong(13, entity.submittedAt);
        statement.bindLong(14, entity.deadlineAt);
        final int _tmp = entity.isSyncedToChain ? 1 : 0;
        statement.bindLong(15, _tmp);
        if (entity.evidenceFilePath == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.evidenceFilePath);
        }
      }
    };
    this.__updateAdapterOfComplaint = new EntityDeletionOrUpdateAdapter<Complaint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `complaints` SET `id` = ?,`caseId` = ?,`category` = ?,`description` = ?,`latitude` = ?,`longitude` = ?,`locationLabel` = ?,`dateTime` = ?,`status` = ?,`ipfsCid` = ?,`txHash` = ?,`zkProof` = ?,`submittedAt` = ?,`deadlineAt` = ?,`isSyncedToChain` = ?,`evidenceFilePath` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Complaint entity) {
        statement.bindLong(1, entity.id);
        if (entity.caseId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.caseId);
        }
        if (entity.category == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.category);
        }
        if (entity.description == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.description);
        }
        statement.bindDouble(5, entity.latitude);
        statement.bindDouble(6, entity.longitude);
        if (entity.locationLabel == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.locationLabel);
        }
        if (entity.dateTime == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.dateTime);
        }
        if (entity.status == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.status);
        }
        if (entity.ipfsCid == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.ipfsCid);
        }
        if (entity.txHash == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.txHash);
        }
        if (entity.zkProof == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.zkProof);
        }
        statement.bindLong(13, entity.submittedAt);
        statement.bindLong(14, entity.deadlineAt);
        final int _tmp = entity.isSyncedToChain ? 1 : 0;
        statement.bindLong(15, _tmp);
        if (entity.evidenceFilePath == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.evidenceFilePath);
        }
        statement.bindLong(17, entity.id);
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE complaints SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBlockchainData = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE complaints SET ipfsCid = ?, txHash = ?, isSyncedToChain = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Complaint complaint) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfComplaint.insertAndReturnId(complaint);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Complaint complaint) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfComplaint.handle(complaint);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateStatus(final int id, final String status) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
    int _argIndex = 1;
    if (status == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, status);
    }
    _argIndex = 2;
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
      __preparedStmtOfUpdateStatus.release(_stmt);
    }
  }

  @Override
  public void updateBlockchainData(final int id, final String cid, final String txHash) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBlockchainData.acquire();
    int _argIndex = 1;
    if (cid == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, cid);
    }
    _argIndex = 2;
    if (txHash == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, txHash);
    }
    _argIndex = 3;
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
      __preparedStmtOfUpdateBlockchainData.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Complaint>> getAllComplaints() {
    final String _sql = "SELECT * FROM complaints ORDER BY submittedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"complaints"}, false, new Callable<List<Complaint>>() {
      @Override
      @Nullable
      public List<Complaint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "caseId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
          final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
          final int _cursorIndexOfZkProof = CursorUtil.getColumnIndexOrThrow(_cursor, "zkProof");
          final int _cursorIndexOfSubmittedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "submittedAt");
          final int _cursorIndexOfDeadlineAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deadlineAt");
          final int _cursorIndexOfIsSyncedToChain = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncedToChain");
          final int _cursorIndexOfEvidenceFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenceFilePath");
          final List<Complaint> _result = new ArrayList<Complaint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Complaint _item;
            _item = new Complaint();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfCaseId)) {
              _item.caseId = null;
            } else {
              _item.caseId = _cursor.getString(_cursorIndexOfCaseId);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _item.description = null;
            } else {
              _item.description = _cursor.getString(_cursorIndexOfDescription);
            }
            _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
            if (_cursor.isNull(_cursorIndexOfLocationLabel)) {
              _item.locationLabel = null;
            } else {
              _item.locationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
            }
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _item.dateTime = null;
            } else {
              _item.dateTime = _cursor.getString(_cursorIndexOfDateTime);
            }
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _item.status = null;
            } else {
              _item.status = _cursor.getString(_cursorIndexOfStatus);
            }
            if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
              _item.ipfsCid = null;
            } else {
              _item.ipfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
            }
            if (_cursor.isNull(_cursorIndexOfTxHash)) {
              _item.txHash = null;
            } else {
              _item.txHash = _cursor.getString(_cursorIndexOfTxHash);
            }
            if (_cursor.isNull(_cursorIndexOfZkProof)) {
              _item.zkProof = null;
            } else {
              _item.zkProof = _cursor.getString(_cursorIndexOfZkProof);
            }
            _item.submittedAt = _cursor.getLong(_cursorIndexOfSubmittedAt);
            _item.deadlineAt = _cursor.getLong(_cursorIndexOfDeadlineAt);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSyncedToChain);
            _item.isSyncedToChain = _tmp != 0;
            if (_cursor.isNull(_cursorIndexOfEvidenceFilePath)) {
              _item.evidenceFilePath = null;
            } else {
              _item.evidenceFilePath = _cursor.getString(_cursorIndexOfEvidenceFilePath);
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
  public Complaint getById(final int id) {
    final String _sql = "SELECT * FROM complaints WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "caseId");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
      final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
      final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
      final int _cursorIndexOfZkProof = CursorUtil.getColumnIndexOrThrow(_cursor, "zkProof");
      final int _cursorIndexOfSubmittedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "submittedAt");
      final int _cursorIndexOfDeadlineAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deadlineAt");
      final int _cursorIndexOfIsSyncedToChain = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncedToChain");
      final int _cursorIndexOfEvidenceFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenceFilePath");
      final Complaint _result;
      if (_cursor.moveToFirst()) {
        _result = new Complaint();
        _result.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfCaseId)) {
          _result.caseId = null;
        } else {
          _result.caseId = _cursor.getString(_cursorIndexOfCaseId);
        }
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _result.category = null;
        } else {
          _result.category = _cursor.getString(_cursorIndexOfCategory);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _result.description = null;
        } else {
          _result.description = _cursor.getString(_cursorIndexOfDescription);
        }
        _result.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _result.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        if (_cursor.isNull(_cursorIndexOfLocationLabel)) {
          _result.locationLabel = null;
        } else {
          _result.locationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
        }
        if (_cursor.isNull(_cursorIndexOfDateTime)) {
          _result.dateTime = null;
        } else {
          _result.dateTime = _cursor.getString(_cursorIndexOfDateTime);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _result.status = null;
        } else {
          _result.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
          _result.ipfsCid = null;
        } else {
          _result.ipfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
        }
        if (_cursor.isNull(_cursorIndexOfTxHash)) {
          _result.txHash = null;
        } else {
          _result.txHash = _cursor.getString(_cursorIndexOfTxHash);
        }
        if (_cursor.isNull(_cursorIndexOfZkProof)) {
          _result.zkProof = null;
        } else {
          _result.zkProof = _cursor.getString(_cursorIndexOfZkProof);
        }
        _result.submittedAt = _cursor.getLong(_cursorIndexOfSubmittedAt);
        _result.deadlineAt = _cursor.getLong(_cursorIndexOfDeadlineAt);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSyncedToChain);
        _result.isSyncedToChain = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfEvidenceFilePath)) {
          _result.evidenceFilePath = null;
        } else {
          _result.evidenceFilePath = _cursor.getString(_cursorIndexOfEvidenceFilePath);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Complaint> getPendingSync() {
    final String _sql = "SELECT * FROM complaints WHERE isSyncedToChain = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "caseId");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
      final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfIpfsCid = CursorUtil.getColumnIndexOrThrow(_cursor, "ipfsCid");
      final int _cursorIndexOfTxHash = CursorUtil.getColumnIndexOrThrow(_cursor, "txHash");
      final int _cursorIndexOfZkProof = CursorUtil.getColumnIndexOrThrow(_cursor, "zkProof");
      final int _cursorIndexOfSubmittedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "submittedAt");
      final int _cursorIndexOfDeadlineAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deadlineAt");
      final int _cursorIndexOfIsSyncedToChain = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncedToChain");
      final int _cursorIndexOfEvidenceFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenceFilePath");
      final List<Complaint> _result = new ArrayList<Complaint>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Complaint _item;
        _item = new Complaint();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfCaseId)) {
          _item.caseId = null;
        } else {
          _item.caseId = _cursor.getString(_cursorIndexOfCaseId);
        }
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        if (_cursor.isNull(_cursorIndexOfLocationLabel)) {
          _item.locationLabel = null;
        } else {
          _item.locationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
        }
        if (_cursor.isNull(_cursorIndexOfDateTime)) {
          _item.dateTime = null;
        } else {
          _item.dateTime = _cursor.getString(_cursorIndexOfDateTime);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfIpfsCid)) {
          _item.ipfsCid = null;
        } else {
          _item.ipfsCid = _cursor.getString(_cursorIndexOfIpfsCid);
        }
        if (_cursor.isNull(_cursorIndexOfTxHash)) {
          _item.txHash = null;
        } else {
          _item.txHash = _cursor.getString(_cursorIndexOfTxHash);
        }
        if (_cursor.isNull(_cursorIndexOfZkProof)) {
          _item.zkProof = null;
        } else {
          _item.zkProof = _cursor.getString(_cursorIndexOfZkProof);
        }
        _item.submittedAt = _cursor.getLong(_cursorIndexOfSubmittedAt);
        _item.deadlineAt = _cursor.getLong(_cursorIndexOfDeadlineAt);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSyncedToChain);
        _item.isSyncedToChain = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfEvidenceFilePath)) {
          _item.evidenceFilePath = null;
        } else {
          _item.evidenceFilePath = _cursor.getString(_cursorIndexOfEvidenceFilePath);
        }
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
