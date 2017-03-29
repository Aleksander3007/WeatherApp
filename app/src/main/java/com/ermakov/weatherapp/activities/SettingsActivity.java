package com.ermakov.weatherapp.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.ermakov.weatherapp.R;

/**
 * Общие настройки приложения.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_TEMPERATURE_UNITS = "pref_temperature_units";
    public static final String PREF_WIND_SPEED_UNITS = "pref_wind_speed_units";
    public static final String PREF_FIRST_RUN = "pref_first_run";

    public static final String NAME_CELSIUS = "celsius";
    public static final String NAME_KELVIN = "kelvin";

    public static final String NAME_METERS_PER_SECOND = "m/s";
    public static final String NAME_KILOMETERS_PER_HOUR = "km/h";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ListPreference temperaturePreference = (ListPreference) findPreference(PREF_TEMPERATURE_UNITS);
            temperaturePreference.setSummary(temperaturePreference.getEntry());

            ListPreference windSpeedPreference = (ListPreference) findPreference(PREF_WIND_SPEED_UNITS);
            windSpeedPreference.setSummary(windSpeedPreference.getEntry());
        }

        @Override
        public void onStart() {
            super.onStart();
        /* Register the preference change listener */
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            /* Unregister the preference change listener */
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            try {
                Preference preference = findPreference(key);
                if (preference != null) {
                    ListPreference temperaturePreference = (ListPreference) preference;
                    temperaturePreference.setSummary(temperaturePreference.getEntry());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
