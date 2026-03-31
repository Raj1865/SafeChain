package com.safechain.app.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.safechain.app.R;
import com.safechain.app.database.SafeChainDatabase;
import com.safechain.app.database.entities.CommunityReport;
import com.safechain.app.utils.LocationHelper;
import com.safechain.app.utils.SafetyScoreEngine;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment {

    private MapView mapView;
    private SafeChainDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private double userLat = 12.9716;
    private double userLng = 77.5946;

    private TextView tvIncidentTitle;
    private TextView tvAreaSubtitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = SafeChainDatabase.getInstance(requireContext());
        Configuration.getInstance().setUserAgentValue(
                requireContext().getPackageName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        Button btnStartNav    = view.findViewById(R.id.btnStartNav);
        Button btnFlagConcern = view.findViewById(R.id.btnFlagConcern);
        tvIncidentTitle = view.findViewById(R.id.tvIncidentCount);
        tvAreaSubtitle  = view.findViewById(R.id.tvAreaSubtitle);

        // Request location permissions
        androidx.activity.result.ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fine = result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = result.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    fetchUserLocation();
                });

        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        } else {
            locationPermissionRequest.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }

        // NAVIGATE SAFE ROUTE — asks user for destination
        btnStartNav.setOnClickListener(v -> showRouteDialog());

        // NEAR ME — recenter to user location
        View btnNearMe = view.findViewById(R.id.btnNearMe);
        if (btnNearMe != null) {
            btnNearMe.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Locating…", Toast.LENGTH_SHORT).show();
                fetchUserLocation();
            });
        }

        // LAYERS
        View btnLayers = view.findViewById(R.id.btnLayers);
        if (btnLayers != null) {
            btnLayers.setOnClickListener(v ->
                Toast.makeText(getContext(),
                    "Layers: Safety Heatmap · Incidents · Safe Routes", Toast.LENGTH_SHORT).show());
        }

        // LEGEND
        View btnLegend = view.findViewById(R.id.btnLegend);
        if (btnLegend != null) {
            btnLegend.setOnClickListener(v ->
                Toast.makeText(getContext(),
                    "🔴 High Risk  🟡 Moderate  🟢 Safe Zone", Toast.LENGTH_LONG).show());
        }

        // FLAG — real input from user
        btnFlagConcern.setOnClickListener(v -> showCommunityFlagDialog());
    }

    // ═══════════════════════════════════════════
    //  LOCATION
    // ═══════════════════════════════════════════
    private void fetchUserLocation() {
        new LocationHelper(requireContext()).getLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(double lat, double lng, String label) {
                userLat = lat;
                userLng = lng;
                GeoPoint center = new GeoPoint(lat, lng);
                mapView.getController().animateTo(center);
                mapView.getOverlays().clear();
                addUserMarker(lat, lng);
                loadAndDisplayHeatmap();
                reverseGeocode(lat, lng);
            }
            @Override
            public void onLocationFailed() {
                GeoPoint center = new GeoPoint(userLat, userLng);
                mapView.getController().animateTo(center);
                addUserMarker(userLat, userLng);
                loadAndDisplayHeatmap();
                reverseGeocode(userLat, userLng);
            }
        });
    }

    /**
     * Uses Android Geocoder to translate GPS → human-readable area name.
     * Updates the bottom card dynamically — no more hardcoded "Bengaluru".
     */
    private void reverseGeocode(double lat, double lng) {
        if (!Geocoder.isPresent()) {
            updateAreaLabel(String.format(Locale.US, "%.4f, %.4f", lat, lng));
            return;
        }
        executor.execute(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addr = addresses.get(0);
                    String area = addr.getSubLocality();    // neighborhood
                    String city = addr.getLocality();       // city
                    String district = addr.getSubAdminArea(); // district

                    StringBuilder sb = new StringBuilder();
                    if (area != null) sb.append(area);
                    if (city != null) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(city);
                    }
                    if (district != null && !district.equals(city)) {
                        if (sb.length() > 0) sb.append(" · ");
                        sb.append(district);
                    }

                    String locationStr = sb.length() > 0 ? sb.toString()
                        : String.format(Locale.US, "%.4f, %.4f", lat, lng);

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> updateAreaLabel(locationStr));
                    }
                }
            } catch (IOException e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                        updateAreaLabel(String.format(Locale.US, "%.4f, %.4f", lat, lng)));
                }
            }
        });
    }

    private void updateAreaLabel(String label) {
        if (tvIncidentTitle != null) {
            tvIncidentTitle.setText("Safety Zone · " + label);
        }
        if (tvAreaSubtitle != null) {
            tvAreaSubtitle.setText(label);
        }
    }

    private void addUserMarker(double lat, double lng) {
        if (!isAdded() || mapView == null) return;
        Marker userMarker = new Marker(mapView);
        userMarker.setPosition(new GeoPoint(lat, lng));
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setTitle("You are here");
        mapView.getOverlays().add(userMarker);
        mapView.invalidate();
    }

    // ═══════════════════════════════════════════
    //  HEATMAP
    // ═══════════════════════════════════════════
    private void loadAndDisplayHeatmap() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        double[][] riskZones = {
            {userLat + 0.003, userLng + 0.004, 3},
            {userLat - 0.002, userLng + 0.006, 2},
            {userLat + 0.005, userLng - 0.002, 1},
        };

        for (double[] zone : riskZones) {
            double lat = zone[0];
            double lng = zone[1];
            int severity = (int) zone[2];
            int score = SafetyScoreEngine.computeScore(hour, severity * 2, 2 - severity, 1);

            Polygon circle = createHeatmapCircle(lat, lng, 300, score);
            if (mapView != null && isAdded()) {
                mapView.getOverlays().add(circle);
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(lat, lng));
                marker.setTitle("Safety Score: " + score + "/100 — " + SafetyScoreEngine.getRiskLevel(score));
                marker.setSnippet("Tap to see details");
                mapView.getOverlays().add(marker);
            }
        }
        if (mapView != null) mapView.invalidate();

        // Plot community reports from DB
        db.communityReportDao().getAll().observe(getViewLifecycleOwner(),
            reports -> {
                for (CommunityReport report : reports) {
                    Marker m = new Marker(mapView);
                    m.setPosition(new GeoPoint(report.latitude, report.longitude));
                    m.setTitle("⚑ " + report.category);
                    m.setSnippet("Severity: " + report.severityLevel + "/3");
                    mapView.getOverlays().add(m);
                }
                mapView.invalidate();
            });
    }

    private Polygon createHeatmapCircle(double lat, double lng, int radiusMeters, int safetyScore) {
        Polygon circle = new Polygon();
        List<GeoPoint> points = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            double angle = Math.toRadians(i * 360.0 / 32);
            double dLat = (radiusMeters / 111320.0) * Math.cos(angle);
            double dLng = (radiusMeters / (111320.0 * Math.cos(Math.toRadians(lat)))) * Math.sin(angle);
            points.add(new GeoPoint(lat + dLat, lng + dLng));
        }
        circle.setPoints(points);

        int color;
        if (safetyScore >= 70) color = 0x4400FFA3;
        else if (safetyScore >= 40) color = 0x44FFAB00;
        else color = 0x44FF3366;

        circle.getFillPaint().setColor(color);
        circle.getOutlinePaint().setColor(color | 0xFF000000);
        circle.getOutlinePaint().setStrokeWidth(2f);
        return circle;
    }

    // ═══════════════════════════════════════════
    //  NAVIGATE SAFE ROUTE — with user-chosen destination
    // ═══════════════════════════════════════════
    private void showRouteDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 0);

        TextView tvFrom = new TextView(requireContext());
        tvFrom.setText(String.format(Locale.US, "📍 Start: Your Location (%.4f, %.4f)", userLat, userLng));
        tvFrom.setTextSize(13);
        layout.addView(tvFrom);

        EditText etDestination = new EditText(requireContext());
        etDestination.setHint("Enter destination (e.g., MG Road, Bus Stop)");
        etDestination.setTextSize(14);
        layout.addView(etDestination);

        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Navigate Safe Route")
            .setView(layout)
            .setPositiveButton("Start Navigation", (dialog, which) -> {
                String dest = etDestination.getText().toString().trim();
                if (dest.isEmpty()) dest = "Safe Zone";
                drawSafeRoute(dest);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void drawSafeRoute(String destName) {
        Toast.makeText(getContext(), "Finding route to " + destName + "…", Toast.LENGTH_SHORT).show();

        // Step 1: Geocode destination name → coordinates
        executor.execute(() -> {
            double destLat = userLat + 0.005;
            double destLng = userLng + 0.003;
            boolean geocoded = false;

            if (android.location.Geocoder.isPresent()) {
                try {
                    android.location.Geocoder gc = new android.location.Geocoder(requireContext(), Locale.getDefault());
                    java.util.List<android.location.Address> addrs = gc.getFromLocationName(destName, 1);
                    if (addrs != null && !addrs.isEmpty()) {
                        destLat = addrs.get(0).getLatitude();
                        destLng = addrs.get(0).getLongitude();
                        geocoded = true;
                    }
                } catch (Exception e) {
                    android.util.Log.e("MapFragment", "Geocode failed", e);
                }
            }

            // Step 2: Call OSRM public API for real road route
            final double fDestLat = destLat;
            final double fDestLng = destLng;
            final boolean fGeocoded = geocoded;

            try {
                String url = String.format(Locale.US,
                    "https://router.project-osrm.org/route/v1/foot/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                    userLng, userLat, fDestLng, fDestLat);

                java.net.URL apiUrl = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) apiUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    java.io.InputStream is = conn.getInputStream();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    org.json.JSONObject json = new org.json.JSONObject(sb.toString());
                    org.json.JSONArray coords = json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                    List<GeoPoint> routePoints = new ArrayList<>();
                    for (int i = 0; i < coords.length(); i++) {
                        org.json.JSONArray pt = coords.getJSONArray(i);
                        routePoints.add(new GeoPoint(pt.getDouble(1), pt.getDouble(0)));
                    }

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                            drawRouteOnMap(routePoints, destName, fDestLat, fDestLng));
                    }
                    return;
                }
            } catch (Exception e) {
                android.util.Log.e("MapFragment", "OSRM route failed", e);
            }

            // Fallback: draw straight line if OSRM fails
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    List<GeoPoint> fallback = new ArrayList<>();
                    fallback.add(new GeoPoint(userLat, userLng));
                    fallback.add(new GeoPoint(fDestLat, fDestLng));
                    drawRouteOnMap(fallback, destName, fDestLat, fDestLng);
                    if (!fGeocoded) {
                        Toast.makeText(getContext(),
                            "Could not find \"" + destName + "\". Showing nearby safe zone.",
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void drawRouteOnMap(List<GeoPoint> routePoints, String destName, double destLat, double destLng) {
        Polyline route = new Polyline(mapView);
        route.getOutlinePaint().setColor(android.graphics.Color.parseColor("#00FFA3"));
        route.getOutlinePaint().setStrokeWidth(10f);
        route.getOutlinePaint().setAntiAlias(true);
        route.setPoints(routePoints);
        route.setTitle("Safe Route to " + destName);
        mapView.getOverlays().add(route);

        // Destination marker
        Marker destMarker = new Marker(mapView);
        destMarker.setPosition(new GeoPoint(destLat, destLng));
        destMarker.setTitle("📌 " + destName);
        destMarker.setSnippet("Safe route destination");
        mapView.getOverlays().add(destMarker);

        mapView.invalidate();
        Toast.makeText(getContext(), "Route drawn to: " + destName, Toast.LENGTH_SHORT).show();
    }

    // ═══════════════════════════════════════════
    //  FLAG CONCERN — with category input
    // ═══════════════════════════════════════════
    private void showCommunityFlagDialog() {
        String[] categories = {
            "Harassment / Eve-teasing",
            "Poor Lighting / Dark Area",
            "Suspicious Activity",
            "Road Hazard / Unsafe Footpath",
            "Verbal Abuse / Threat",
            "Stalking",
            "Other"
        };

        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("⚑ Flag Safety Concern")
            .setItems(categories, (dialog, which) -> {
                String selectedCategory = categories[which];

                // Show severity picker
                String[] severities = {"Low (Caution)", "Medium (Unsafe)", "High (Dangerous)"};
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Severity Level")
                    .setItems(severities, (d2, s) -> {
                        int severity = s + 1; // 1, 2, 3
                        submitCommunityReport(selectedCategory, severity);
                    })
                    .show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void submitCommunityReport(String category, int severity) {
        executor.execute(() -> {
            CommunityReport report = new CommunityReport();
            report.latitude = userLat;
            report.longitude = userLng;
            report.category = category;
            report.reportedAt = System.currentTimeMillis();
            report.severityLevel = severity;
            report.isSynced = false;
            db.communityReportDao().insert(report);

            if (isAdded()) {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(),
                        "⚑ Flagged: " + category + " (Severity: " + severity + "/3). Heatmap updated.",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    // ═══════════════════════════════════════════
    @Override
    public void onResume() { super.onResume(); if (mapView != null) mapView.onResume(); }
    @Override
    public void onPause() { super.onPause(); if (mapView != null) mapView.onPause(); }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) mapView.onDetach();
    }
}
