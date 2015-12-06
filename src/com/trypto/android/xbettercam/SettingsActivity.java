package com.trypto.android.xbettercam;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		public static final String KEY_LOCATION_MODE_PREFERENCE = "location_mode_preference";

		public static final String KEY_SYSTEM_LOCATION_PREFERENCE = "system_location_preference";

		public static final String KEY_DISABLE_SYSTEM_LOCATION_PREFERENCE = "disable_system_location_preference";

		private ListPreference mListLocationModePreference;

		private CheckBoxPreference mSystemLocationPreference;

		private CheckBoxPreference mDisableSystemLocationPreference;

		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			getPreferenceManager().setSharedPreferencesMode(Activity.MODE_WORLD_READABLE);
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			mListLocationModePreference = (ListPreference) getPreferenceManager()
					.findPreference(KEY_LOCATION_MODE_PREFERENCE);
			mSystemLocationPreference = (CheckBoxPreference) getPreferenceManager()
					.findPreference(KEY_SYSTEM_LOCATION_PREFERENCE);
			mDisableSystemLocationPreference = (CheckBoxPreference) getPreferenceManager()
					.findPreference(KEY_DISABLE_SYSTEM_LOCATION_PREFERENCE);
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

			mListLocationModePreference
					.setSummary("Current value is " + mListLocationModePreference.getEntry().toString());
		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

			if (key.equals(KEY_LOCATION_MODE_PREFERENCE)) {
				mListLocationModePreference
						.setSummary("Current value is " + mListLocationModePreference.getEntry().toString());
			}
			if (key.equals(KEY_SYSTEM_LOCATION_PREFERENCE)) {
				mListLocationModePreference.setEnabled(mSystemLocationPreference.isChecked());
				mDisableSystemLocationPreference.setEnabled(mSystemLocationPreference.isChecked());
			}
		}

	}
}