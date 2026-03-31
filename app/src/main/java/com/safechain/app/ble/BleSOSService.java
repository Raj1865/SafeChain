package com.safechain.app.ble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.nio.ByteBuffer;

/**
 * SOS Broadcaster — sends GPS over BLE ManufacturerData.
 *
 * Payload: [0x53, 0x4F, lat(4 bytes), lng(4 bytes)] = 10 bytes total
 * Company ID: 0xBEEF (avoids 0xFFFF which some stacks treat specially)
 */
public class BleSOSService extends Service {
    private static final String TAG = "BleSOSService";
    private static final String CHANNEL_ID = "SOS_Service_Channel";
    private static final int NOTIFICATION_ID = 911;

    // Company ID — use 0xBEEF instead of 0xFFFF (some chips handle 0xFFFF specially)
    public static final int COMPANY_ID = 0xBEEF;
    public static final byte MAGIC_1 = 0x53; // 'S'
    public static final byte MAGIC_2 = 0x4F; // 'O'

    private BluetoothLeAdvertiser advertiser;
    private AdvertiseCallback advertiseCallback;
    private NotificationManager notifManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        double lat = 0, lng = 0;
        if (intent != null) {
            lat = intent.getDoubleExtra("SOS_LAT", 0.0);
            lng = intent.getDoubleExtra("SOS_LNG", 0.0);
        }

        Notification notification = buildNotification("🚨 Preparing SOS broadcast…");

        try {
            // CRITICAL FIX: Android 14+ requires foregroundServiceType in startForeground()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
            } else {
                startForeground(NOTIFICATION_ID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "startForeground failed", e);
            stopSelf();
            return START_NOT_STICKY;
        }

        startAdvertising(lat, lng);
        return START_NOT_STICKY;
    }

    private void startAdvertising(double lat, double lng) {
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bm == null) { updateNotif("❌ No Bluetooth hardware"); return; }

        BluetoothAdapter adapter = bm.getAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            updateNotif("❌ Bluetooth is OFF — cannot broadcast SOS");
            return;
        }

        advertiser = adapter.getBluetoothLeAdvertiser();
        if (advertiser == null) {
            updateNotif("❌ This phone does not support BLE advertising");
            return;
        }

        // Build 10-byte payload: 2 magic + 4 lat + 4 lng
        byte[] payload = new byte[10];
        payload[0] = MAGIC_1;
        payload[1] = MAGIC_2;
        ByteBuffer.wrap(payload, 2, 8).putFloat((float) lat).putFloat((float) lng);

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .setTimeout(0) // advertise indefinitely
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(COMPANY_ID, payload)
                .build();

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "✅ BLE ADVERTISING — lat=" + lat + " lng=" + lng);
                updateNotif(String.format("📡 Broadcasting SOS (%.4f, %.4f)", lat, lng));
            }

            @Override
            public void onStartFailure(int errorCode) {
                String reason;
                switch (errorCode) {
                    case ADVERTISE_FAILED_DATA_TOO_LARGE: reason = "Data too large"; break;
                    case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS: reason = "Too many advertisers"; break;
                    case ADVERTISE_FAILED_ALREADY_STARTED: reason = "Already started"; break;
                    case ADVERTISE_FAILED_FEATURE_UNSUPPORTED: reason = "BLE advertising unsupported"; break;
                    default: reason = "Internal error (code " + errorCode + ")"; break;
                }
                Log.e(TAG, "❌ ADVERTISE FAILED: " + reason);
                updateNotif("❌ SOS broadcast failed: " + reason);
            }
        };

        try {
            advertiser.startAdvertising(settings, data, advertiseCallback);
            Log.i(TAG, "startAdvertising called with COMPANY_ID=0xBEEF, payload=" + payload.length + " bytes");
        } catch (SecurityException e) {
            Log.e(TAG, "Missing BLUETOOTH_ADVERTISE permission", e);
            updateNotif("❌ Missing Bluetooth permission");
        }
    }

    private Notification buildNotification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SafeChain SOS")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();
    }

    private void updateNotif(String text) {
        if (notifManager != null) {
            notifManager.notify(NOTIFICATION_ID, buildNotification(text));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID,
                    "Emergency SOS", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (advertiser != null && advertiseCallback != null) {
            try { advertiser.stopAdvertising(advertiseCallback); }
            catch (SecurityException ignored) {}
        }
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) { return null; }
}
