package com.nvhau.testlauncher.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nvhau.testlauncher.R;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class LocationWidgetUpdateService extends Service {

    /* List of active widget Ids */
    private int[] mAppWidgetIds;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAppWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        /* Get device's location */
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this.getApplicationContext());
        fusedLocationProviderClient.getLastLocation().
                addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            processLocation(location);
                        } else {
                            onUpdateFailure();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        onUpdateFailure();
                    }
                });

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Get country infos from device's location
     *
     * @param location
     */
    private void processLocation(@NonNull Location location) {
        /* Location coordinate */
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Geocoder geocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            /* Get first match address from location */
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0) {
            /* Unable to get address from location */
            onUpdateFailure();
        } else {
            /* Process address */
            Address address = addresses.get(0);
            String countryCode = address.getCountryCode();

            if (countryCode == null || countryCode.isEmpty()) {
                /* Unable to get country code from address */
                onUpdateFailure();
            } else {
                /* Request country infos by country code */
                String getUrl = "https://restcountries.eu/rest/v2/alpha/" + countryCode;
                JsonObjectRequest request = new JsonObjectRequest(getUrl, null,

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject job) {
                                try {
                                    String countryName = job.getString("name");
                                    String capital = job.getString("capital");
                                    String currency = job.getJSONArray("currencies").
                                            getJSONObject(0).getString("name");

                                    /* Localize country name */
                                    if (job.has("translations")) {
                                        String deviceLanguage = Locale.getDefault().getLanguage();
                                        JSONObject tranlastions = job.getJSONObject("translations");

                                        if (tranlastions.has(deviceLanguage)) {
                                            countryName = tranlastions.getString(deviceLanguage);
                                        }
                                    }

                                    updateWidgets(countryName, capital, currency);

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    /* Something went wrong */
                                    onUpdateFailure();
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                /* Request country infos failure */
                                onUpdateFailure();
                            }
                        }
                );

                /* Send country infos request */
                RequestQueue requestQueue =
                        Volley.newRequestQueue(this.getApplicationContext());
                requestQueue.add(request);
            }
        }
    }

    /**
     * Error happen on update widget process
     */
    private void onUpdateFailure() {
        /* Stop service */
        stopSelf();
    }

    /**
     * Show country infos on widgets
     *
     * @param countryName
     * @param capital
     * @param currency
     */
    private void updateWidgets(@Nullable String countryName,
                               @Nullable String capital,
                               @Nullable String currency) {

        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(this.getApplicationContext());

        for (int appWidgetId : mAppWidgetIds) {
            /* Create widget view */
            RemoteViews widgetView = new RemoteViews(
                    this.getApplicationContext().getPackageName(), R.layout.location_widget);

            /* Show country infos */
            if (countryName != null && !countryName.isEmpty()) {
                widgetView.setTextViewText(R.id.text_country, "Country: " + countryName);
            }
            if (capital != null && !capital.isEmpty()) {
                widgetView.setTextViewText(R.id.text_capital, "Capital: " + capital);
            }
            if (currency != null && !currency.isEmpty()) {
                widgetView.setTextViewText(R.id.text_currency, "Currency: " + currency);
            }

            /* Update widget view */
            appWidgetManager.updateAppWidget(appWidgetId, widgetView);
        }

        /* Job finished */
        stopSelf();
    }
}
