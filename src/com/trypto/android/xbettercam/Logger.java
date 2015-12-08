package com.trypto.android.xbettercam;

import de.robv.android.xposed.XposedBridge;

public class Logger {

	public static void debug(String msg) {
		XposedBridge.log("XBetterCam: " + msg);
	}
}
