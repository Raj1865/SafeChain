package com.safechain.app.utils;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * LSBEmbedder implements Least Significant Bit steganography.
 * Embeds GPS coordinates, timestamp, and device hash invisibly into image pixels.
 * This makes evidence tamper-evident: the data is fused into pixel values.
 */
public class LSBEmbedder {
    private static final String TAG = "LSBEmbedder";

    /**
     * Embed a text payload into the LSB of pixel data.
     * The payload is encoded in the least significant bit of each red channel byte.
     * Max payload size = (width * height) / 8 bytes.
     *
     * @param bitmap  The source image bitmap (mutable copy recommended)
     * @param payload The string to embed (e.g. "GPS:12.97,77.59|TS:2026-03-30T14:30:00Z|DEV:abc123")
     * @return Modified bitmap with payload embedded
     */
    public static Bitmap embed(Bitmap bitmap, String payload) {
        Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        byte[] payloadBytes = payload.getBytes();
        int payloadLength = payloadBytes.length;

        // First 32 bits: encode length
        int bitIndex = 0;
        for (int i = 31; i >= 0; i--) {
            int bit = (payloadLength >> i) & 1;
            int x = bitIndex % mutable.getWidth();
            int y = bitIndex / mutable.getWidth();
            int pixel = mutable.getPixel(x, y);
            int r = (pixel >> 16) & 0xFF;
            r = (r & 0xFE) | bit;
            mutable.setPixel(x, y, (pixel & 0xFF00FFFF) | (r << 16));
            bitIndex++;
        }

        // Then encode each bit of payload
        for (byte b : payloadBytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                int x = bitIndex % mutable.getWidth();
                int y = bitIndex / mutable.getWidth();
                if (y >= mutable.getHeight()) break;
                int pixel = mutable.getPixel(x, y);
                int r = (pixel >> 16) & 0xFF;
                r = (r & 0xFE) | bit;
                mutable.setPixel(x, y, (pixel & 0xFF00FFFF) | (r << 16));
                bitIndex++;
            }
        }

        Log.d(TAG, "Embedded " + payloadLength + " bytes into image LSB.");
        return mutable;
    }

    /**
     * Extract embedded payload from an LSB-encoded bitmap.
     */
    public static String extract(Bitmap bitmap) {
        try {
            int bitIndex = 0;
            int length = 0;

            // Read first 32 bits as length
            for (int i = 31; i >= 0; i--) {
                int x = bitIndex % bitmap.getWidth();
                int y = bitIndex / bitmap.getWidth();
                int pixel = bitmap.getPixel(x, y);
                int bit = (pixel >> 16) & 1;
                length |= (bit << i);
                bitIndex++;
            }

            if (length <= 0 || length > 1024) return "[No embedded data]";

            byte[] payloadBytes = new byte[length];
            for (int byteIdx = 0; byteIdx < length; byteIdx++) {
                int b = 0;
                for (int i = 7; i >= 0; i--) {
                    int x = bitIndex % bitmap.getWidth();
                    int y = bitIndex / bitmap.getWidth();
                    if (y >= bitmap.getHeight()) break;
                    int pixel = bitmap.getPixel(x, y);
                    int bit = (pixel >> 16) & 1;
                    b |= (bit << i);
                    bitIndex++;
                }
                payloadBytes[byteIdx] = (byte) b;
            }

            return new String(payloadBytes);
        } catch (Exception e) {
            Log.e(TAG, "Extraction failed: " + e.getMessage());
            return "[Extraction error]";
        }
    }
}
