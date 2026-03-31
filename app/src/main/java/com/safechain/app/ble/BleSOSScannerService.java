package com.safechain.app.ble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.safechain.app.MainActivity;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * BLE SOS Scanner — catches distress signals from nearby SafeChain users.
 *
 * CRITICAL REQUIREMENTS (all must be true for BLE scan to work):
 * 1. Bluetooth must be ON
 * 2. Location Services (GPS) must be ON (Android requirement for BLE scan)
 * 3. BLUETOOTH_SCAN permission granted (Android 12+) OR ACCESS_FINE_LOCATION (Android 11-)
 * 4. Foreground service must specify type on Android 14+
 */
public class BleSOSScannerService extends Service {
    private static final String TAG = "BleSOSScanner";
    private static final String SCAN_CHANNEL = "sos_scan_channel";
    private static final String ALERT_CHANNEL = "sos_alert_channel";
    private static final int SCAN_NOTIF_ID = 404;

    private BluetoothLeScanner scanner;
    private NotificationManager notifManager;
    private long lastAlertTime = 0;
    private int totalScanned = 0;
    private boolean scanActive = false;

    @Nullable @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel(SCAN_CHANNEL, "SOS Scanner", NotificationManager.IMPORTANCE_LOW);
        createChannel(ALERT_CHANNEL, "SOS Alerts", NotificationManager.IMPORTANCE_HIGH);
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notif = scanNotif("Starting BLE scanner…");
        try {
            // CRITICAL FIX: Android 14+ requires specifying foregroundServiceType
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(SCAN_NOTIF_ID, notif,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
            } else {
                startForeground(SCAN_NOTIF_ID, notif);
            }
        } catch (Exception e) {
            Log.e(TAG, "startForeground FAILED", e);
            stopSelf();
            return START_NOT_STICKY;
        }

        startScanning();
        return START_STICKY;
    }

    private void startScanning() {
        // CHECK 1: Bluetooth hardware
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bm == null) {
            updateScanNotif("❌ No Bluetooth hardware");
            return;
        }

        BluetoothAdapter adapter = bm.getAdapter();
        if (adapter == null) {
            updateScanNotif("❌ Bluetooth not available");
            return;
        }

        if (!adapter.isEnabled()) {
            updateScanNotif("❌ Bluetooth is OFF — turn it on!");
            return;
        }

        // CHECK 2: Location Services (GPS) — REQUIRED for BLE scan on most Android devices
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsOn = false;
        if (lm != null) {
            gpsOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                 || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        if (!gpsOn) {
            updateScanNotif("⚠ Turn on Location/GPS — required for BLE scanning");
            Log.e(TAG, "Location services are OFF — BLE scanning will NOT work");
            // Continue anyway — some devices work without it, but warn the user
        }

        // CHECK 3: Get scanner
        scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) {
            updateScanNotif("❌ BLE Scanner unavailable");
            return;
        }

        // Start unfiltered scan — NO hardware filters
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build();

        try {
            scanner.startScan(null, settings, scanCallback);
            scanActive = true;
            updateScanNotif("🔍 Scanning for SOS signals… (BT ✓ GPS " + (gpsOn ? "✓" : "✗") + ")");
            Log.i(TAG, "✅ BLE scan started — unfiltered, low latency");
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
            updateScanNotif("❌ Permission denied for BLE scan");
        } catch (Exception e) {
            Log.e(TAG, "startScan failed: " + e.getMessage());
            updateScanNotif("❌ Scan failed: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════
    //  SCAN CALLBACK — processes EVERY BLE packet
    // ════════════════════════════════════════════
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            totalScanned++;

            // Update notification every 50 packets so user can see it's alive
            if (totalScanned % 50 == 0) {
                updateScanNotif("🔍 Scanning… " + totalScanned + " BLE devices seen");
            }

            ScanRecord record = result.getScanRecord();
            if (record == null) return;

            // Check for our SafeChain manufacturer data
            byte[] mfgData = record.getManufacturerSpecificData(BleSOSService.COMPANY_ID);
            if (mfgData == null) return;

            // Verify magic bytes
            if (mfgData.length < 10) return;
            if (mfgData[0] != BleSOSService.MAGIC_1 || mfgData[1] != BleSOSService.MAGIC_2) return;

            // 🚨 THIS IS A SAFECHAIN SOS SIGNAL
            // Debounce — don't spam
            long now = System.currentTimeMillis();
            if (now - lastAlertTime < 15000) return;
            lastAlertTime = now;

            // Extract GPS
            ByteBuffer buf = ByteBuffer.wrap(mfgData, 2, 8);
            float lat = buf.getFloat();
            float lng = buf.getFloat();
            int rssi = result.getRssi();

            Log.i(TAG, "🚨🚨🚨 SOS CAUGHT! lat=" + lat + " lng=" + lng + " rssi=" + rssi);
            updateScanNotif("🚨 SOS DETECTED! " + lat + ", " + lng);

            fireAlert(lat, lng, rssi);
            saveToDb(lat, lng, rssi);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult r : results) {
                onScanResult(0, r);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            String reason;
            switch (errorCode) {
                case SCAN_FAILED_ALREADY_STARTED: reason = "Already started"; break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED: reason = "App registration failed"; break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED: reason = "Feature unsupported"; break;
                case SCAN_FAILED_INTERNAL_ERROR: reason = "Internal error"; break;
                default: reason = "Error " + errorCode; break;
            }
            Log.e(TAG, "❌ SCAN FAILED: " + reason);
            updateScanNotif("❌ Scan failed: " + reason);
            scanActive = false;
        }
    };

    // ════════════════════════════════════════════
    //  ALERT + DB
    // ════════════════════════════════════════════
    private void fireAlert(float lat, float lng, int rssi) {
        double dist = Math.pow(10, (-59.0 - rssi) / 20.0);
        String distStr = dist < 5 ? "< 5m" : String.format("~%.0fm", dist);

        Intent tap = new Intent(this, MainActivity.class);
        tap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, tap,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification alert = new NotificationCompat.Builder(this, ALERT_CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🚨 EMERGENCY — SOS DETECTED")
                .setContentText("Someone needs help " + distStr + " away!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                    "🚨 A nearby SafeChain user has activated SOS!\n\n" +
                    "📍 Location: " + lat + ", " + lng + "\n" +
                    "📡 Distance: " + distStr + "\n" +
                    "📶 Signal: " + rssi + " dBm\n\n" +
                    "Tap to open SafeChain."))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pi, true)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 800, 200, 800, 200, 800})
                .build();

        if (notifManager != null) notifManager.notify(9110, alert);
    }

    private void saveToDb(float lat, float lng, int rssi) {
        try {
            com.safechain.app.database.SafeChainDatabase db =
                com.safechain.app.database.SafeChainDatabase.getInstance(this);

            // Community report
            com.safechain.app.database.entities.CommunityReport rpt =
                new com.safechain.app.database.entities.CommunityReport();
            rpt.latitude = lat;
            rpt.longitude = lng;
            rpt.category = "BLE SOS Distress Signal";
            rpt.reportedAt = System.currentTimeMillis();
            rpt.severityLevel = 3;
            rpt.isSynced = false;

            // Safety alert
            com.safechain.app.database.entities.SafetyAlert sa =
                new com.safechain.app.database.entities.SafetyAlert();
            sa.alertId = "sos_" + System.currentTimeMillis();
            sa.type = "SOS";
            sa.severity = "CRITICAL";
            sa.title = "🚨 SOS Distress Signal";
            sa.message = String.format("Emergency signal from %.4f, %.4f (%d dBm)", lat, lng, rssi);
            sa.timestamp = System.currentTimeMillis();
            sa.latitude = lat;
            sa.longitude = lng;
            sa.isRead = false;
            sa.triggerSource = "BLE_SOS";

            new Thread(() -> {
                db.communityReportDao().insert(rpt);
                db.safetyAlertDao().insert(sa);
                Log.i(TAG, "SOS saved to DB");
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "DB save failed", e);
        }
    }

    // ════════════════════════════════════════════
    //  NOTIFICATIONS
    // ════════════════════════════════════════════
    private Notification scanNotif(String text) {
        return new NotificationCompat.Builder(this, SCAN_CHANNEL)
                .setContentTitle("SafeChain Guardian")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setOngoing(true)
                .build();
    }

    private void updateScanNotif(String text) {
        Log.d(TAG, "STATUS: " + text);
        if (notifManager != null) {
            notifManager.notify(SCAN_NOTIF_ID, scanNotif(text));
        }
    }

    private void createChannel(String id, String name, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(id, name, importance);
            if (importance == NotificationManager.IMPORTANCE_HIGH) {
                ch.enableVibration(true);
                ch.setVibrationPattern(new long[]{0, 500, 200, 500});
                ch.setBypassDnd(true);
            }
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scanner != null && scanActive) {
            try { scanner.stopScan(scanCallback); }
            catch (SecurityException ignored) {}
        }
    }
}
