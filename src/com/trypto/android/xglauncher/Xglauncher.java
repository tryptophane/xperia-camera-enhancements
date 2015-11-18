package com.trypto.android.xglauncher;

import static com.trypto.android.xglauncher.Constants.ALBUM_LAUNCHER_PATH;
import static com.trypto.android.xglauncher.Constants.APP_PACKAGE;
import static com.trypto.android.xglauncher.Constants.TIMESHIFT_IDENT;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Xglauncher implements IXposedHookLoadPackage {

	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(APP_PACKAGE))
			return;

		final Class<?> AlbumLauncher = findClass(ALBUM_LAUNCHER_PATH, lpparam.classLoader);

		findAndHookMethod(ALBUM_LAUNCHER_PATH, lpparam.classLoader, "launchAlbum", Activity.class, Uri.class,
				String.class, int.class, boolean.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

						final Activity activity = (Activity) param.args[0];
						final Uri uri = (Uri) param.args[1];
						final String s = (String) param.args[2];
						final boolean b = (Boolean) param.args[4];

						final String realPath = getRealPathFromURI(activity, uri);
						XposedBridge.log("Xglauncher: Path to medium: " + realPath);

						boolean mpoPresent = false;

						if (realPath.endsWith(".JPG") && new File(realPath.replaceAll(".JPG$", ".MPO")).exists()) {
							mpoPresent = true;
						}

						if (MimeType.fromText(s) == MimeType.MPO || b || realPath.contains(TIMESHIFT_IDENT)
								|| mpoPresent) {
							XposedBridge.log("Xglauncher: Fallback to original method");
							return;
						}

						XposedBridge.log("Xglauncher: Start user gallery...");
						callStaticMethod(AlbumLauncher, "launchPlayer", activity, uri, s);
						param.setResult(null);

					}
				});
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private enum MimeType {
		MP4("video/mp4"), MPO("image/mpo"), PHOTO("image/jpeg"), THREEGPP("video/3gpp"), UNKOWN("");

		final String mText;

		private MimeType(final String mText) {
			this.mText = mText;
		}

		static MimeType fromText(final String s) {
			final MimeType[] values = values();
			for (int length = values.length, i = 0; i < length; ++i) {
				final MimeType mimeType = values[i];
				if (mimeType.mText.equals(s)) {
					return mimeType;
				}
			}
			return MimeType.UNKOWN;
		}
	}

}
