package com.trypto.android.xbettercam;

import static android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static android.provider.Settings.Secure.LOCATION_MODE_OFF;
import static android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class SystemLocationHandler {
	private int beforeEnable = LOCATION_MODE_OFF;

	private XSharedPreferences prefs;

	public SystemLocationHandler(XSharedPreferences prefs) {
		this.prefs = prefs;
	}

	public boolean applyLocationSettings(Context context) {
		try {
			String locationModePref = prefs.getString("location_mode_preference", null);

			if (locationModePref == null) {
				return false;
			}

			beforeEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
			int newLocationMode;

			// makes sure that GPS- or network-location never gets turned OFF
			// here! Only enable new location modes, never disable existing.
			if (locationModePref.equals("0")) {
				newLocationMode = beforeEnable == LOCATION_MODE_BATTERY_SAVING
						|| beforeEnable == LOCATION_MODE_HIGH_ACCURACY ? LOCATION_MODE_HIGH_ACCURACY
								: LOCATION_MODE_SENSORS_ONLY;
			} else if (locationModePref.equals("1")) {
				newLocationMode = beforeEnable == LOCATION_MODE_SENSORS_ONLY
						|| beforeEnable == LOCATION_MODE_HIGH_ACCURACY ? LOCATION_MODE_HIGH_ACCURACY
								: LOCATION_MODE_BATTERY_SAVING;
			} else if (locationModePref.equals("2")) {
				newLocationMode = LOCATION_MODE_HIGH_ACCURACY;
			} else {
				return false;
			}

			Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, newLocationMode);
		} catch (Exception e) {
			XposedBridge.log(e);
			return false;
		}
		return isSystemLocationEnabled(context);
	}

	public void restoreLocationSettings(Context context) {
		if (!prefs.getBoolean("system_location_preference", false)
				|| !prefs.getBoolean("disable_system_location_preference", true)) {
			return;
		}

		try {
			Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, beforeEnable);
		} catch (Exception e) {
			XposedBridge.log(e);
		}
	}

	public boolean isSystemLocationEnabled(Context context) {
		final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

}
