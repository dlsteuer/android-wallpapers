package com.steuernol.android.wallpaper;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.List;

public class WallpaperPreferencesActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class WallpaperPreferencesFragment extends PreferenceFragment {
        private Preference.OnPreferenceChangeListener numberCheckListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")) {
                    return true;
                }

                Toast.makeText(WallpaperPreferencesFragment.this.getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference_fragment);

            Preference circlePreference = getPreferenceScreen().findPreference("numberOfCircles");
            circlePreference.setOnPreferenceChangeListener(numberCheckListener);
        }
    }
}