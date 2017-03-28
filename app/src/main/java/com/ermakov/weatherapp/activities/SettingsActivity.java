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

            ListPreference temperaturePreference = (ListPreference) findPreference("pref_temperature_units");
            temperaturePreference.setSummary(temperaturePreference.getEntry());
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
