package com.trypto.android.xbettercam;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import de.robv.android.xposed.XposedBridge;

public class SystemLocationHandler {
	private String beforeEnable = null;

	@SuppressWarnings("deprecation")
	public boolean turnGpsOn(Context context) {
		beforeEnable = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		final String newSet = String.format("%s,%s", beforeEnable, LocationManager.GPS_PROVIDER);
		try {
			Settings.Secure.putString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, newSet);
		} catch (Exception e) {
			XposedBridge.log(e.getMessage());
			return false;
		}
		return isGpsTurnedOn(context);
	}

	@SuppressWarnings("deprecation")
	public void turnGpsOff(Context context) {
		if (!isGpsTurnedOn(context)) {
			return;
		}

		if (null == beforeEnable) {
			String str = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (null == str) {
				str = "";
			} else {
				final String[] list = str.split(",");
				str = "";
				int j = 0;
				for (int i = 0; i < list.length; i++) {
					if (!list[i].equals(LocationManager.GPS_PROVIDER)) {
						if (j > 0) {
							str += ",";
						}
						str += list[i];
						j++;
					}
				}
				beforeEnable = str;
			}
		}
		try {
			Settings.Secure.putString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
					beforeEnable);
		} catch (Exception e) {
		}
	}

	public boolean isGpsTurnedOn(Context context) {
		final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

}
