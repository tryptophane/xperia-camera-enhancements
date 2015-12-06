package com.trypto.android.xbettercam;

import static com.trypto.android.xbettercam.Constants.*;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XBetterCam implements IXposedHookLoadPackage {

	private static final String PACKAGE_NAME = XBetterCam.class.getPackage().getName();
	private final XSharedPreferences prefs = new XSharedPreferences(PACKAGE_NAME);
	private final SystemLocationHandler locationHandler = new SystemLocationHandler(prefs);

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

		if (lpparam.packageName.equals(APP_PACKAGE_CAMERA)) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final Class<? extends Enum> CapturingMode = (Class<? extends Enum>) findClass(PATH_CAPTURING_MODE,
					lpparam.classLoader);

			@SuppressWarnings("unchecked")
			final Enum<?> enumPhoto = Enum.valueOf(CapturingMode, "NORMAL");

			@SuppressWarnings("unchecked")
			final Enum<?> enumVideo = Enum.valueOf(CapturingMode, "VIDEO");

			modLastCapturingMode(lpparam, CapturingMode, enumPhoto, enumVideo);
		}

		if (lpparam.packageName.equals(APP_PACKAGE_CAMERA) || lpparam.packageName.equals(APP_PACKAGE_CAMERA_3D)
				|| lpparam.packageName.equals(APP_PACKAGE_ART_CAMERA)
				|| lpparam.packageName.equals(APP_PACKAGE_SOUND_PHOTO)
				|| lpparam.packageName.equals(APP_PACKAGE_TIMESHIFT)) {

			final Class<?> AlbumLauncher = findClass(PATH_ALBUM_LAUNCHER, lpparam.classLoader);

			hookOnResumeAndExitMethods(lpparam);

			findAndHookMethod(PATH_ALBUM_LAUNCHER, lpparam.classLoader, "launchAlbum", Activity.class, Uri.class,
					String.class, int.class, boolean.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							if (!prefs.getBoolean("launcher_preference", true))
								return;

							final Activity activity = (Activity) param.args[0];
							final Uri uri = (Uri) param.args[1];
							final String s = (String) param.args[2];
							final boolean b = (Boolean) param.args[4];

							if (needsSonyGallery(activity, uri, s, b)) {
								return;
							}

							XposedBridge.log("XBetterCam: Starting user gallery...");
							callStaticMethod(AlbumLauncher, "launchPlayer", activity, uri, s);
							param.setResult(null);
						}
					});

			findAndHookMethod(PATH_LOCATION_SETTINGS_READER, lpparam.classLoader, "getIsGpsLocationAllowed",
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							if (!prefs.getBoolean("geotag_preference", true))
								return;
							param.setResult(true);
						}
					});
		} else if (lpparam.packageName.equals(APP_PACKAGE_AR_EFFECT)) {

			hookOnResumeAndExitMethods(lpparam);

			findAndHookMethod(PATH_AR_EFFECT_MAIN_UI, lpparam.classLoader, "launchAlbum", Uri.class,
					new XC_MethodHook() {

						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							if (!prefs.getBoolean("launcher_preference", true))
								return;

							XposedBridge.log("XBetterCam: Hooking Method launchAlbum in MainUi of AR Effect addon...");

							final Uri uri = (Uri) param.args[0];
							Activity activity = (Activity) callMethod(param.thisObject, "getActivity");

							if (needsSonyGallery(activity, uri, "", false)) {
								return;
							}

							startUserGallery(uri, param, activity);
						}
					});
		} else if (lpparam.packageName.equals(APP_PACKAGE_BACKGROUND_DEFOCUS)) {

			hookOnResumeAndExitMethods(lpparam);

			findAndHookMethod(PATH_ANDROID_APP_ACTIVITY, lpparam.classLoader, "startActivityForResult", Intent.class,
					int.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							if (!prefs.getBoolean("launcher_preference", true))
								return;

							XposedBridge.log(
									"XBetterCam: Hooking Method startActivityForResult in superclass Activity from ViewFinderActivity of Background Defocus addon...");
							final Intent intent = (Intent) param.args[0];

							if (intent.getAction().equals("android.intent.action.VIEW")
									|| !intent.getPackage().equals("com.sonyericsson.album")) {
								return;
							}

							final Uri uri = intent.getData();
							if (needsSonyGallery((Activity) param.thisObject, uri, "", false)) {
								return;
							}

							startUserGallery(uri, param, (Activity) param.thisObject);
							param.setResult(null);
						}
					});
		}

	}

	@SuppressWarnings("rawtypes")
	private void modLastCapturingMode(final LoadPackageParam lpparam, final Class<? extends Enum> CapturingMode,
			final Enum<?> enumPhoto, final Enum<?> enumVideo) {
		findAndHookMethod(PATH_CAMERA_ACTIVITY, lpparam.classLoader, "getLastCapturingMode", CapturingMode,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						prefs.reload();
						if (!prefs.getBoolean("capture_mode_preference", false))
							return;
						XposedBridge.log("XBetterCam: hooking getLastCapturingMode()");
						if (param.getResult().equals(enumVideo)) {
							param.setResult(enumPhoto);
						}
					}
				});
	}

	private void hookOnResumeAndExitMethods(final LoadPackageParam lpparam) {
		hookOnResume(lpparam);
		hookOnExitMethod(lpparam, "onPause");
		hookOnExitMethod(lpparam, "onStop");
		hookOnExitMethod(lpparam, "onDestroy");
	}

	private void hookOnResume(final LoadPackageParam lpparam) {
		findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				XposedBridge.log("XBetterCam: Entering Activity.performResume()...");
				prefs.reload();
				if (prefs.getBoolean("system_location_preference", false)) {
					final Activity activity = (Activity) param.thisObject;
					if (!locationHandler.applyLocationSettings(activity))
						XposedBridge.log("XBetterCam: turnGpsOn() returned false");
				}
			}
		});
	}

	private void hookOnExitMethod(final LoadPackageParam lpparam, final String methodName) {
		findAndHookMethod("android.app.Activity", lpparam.classLoader, methodName, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!prefs.getBoolean("system_location_preference", false))
					return;

				XposedBridge.log("XBetterCam: Entering Activity." + methodName + "()...");
				final Activity activity = (Activity) param.thisObject;
				locationHandler.restoreLocationSettings(activity);
			}
		});
	}

	private void startUserGallery(Uri uri, MethodHookParam param, Activity activity) {
		final Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra("android.intent.extra.finishOnCompletion", true);
		intent.setDataAndType(uri, MimeType.PHOTO.getText());
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			XposedBridge.log("XBetterCam: Starting user gallery...");
			activity.startActivity(intent);
			param.setResult(null);
		}
	}

	@SuppressLint("DefaultLocale")
	private boolean needsSonyGallery(final Activity activity, final Uri uri, final String s, final boolean b) {
		final String realPath = getRealPathFromURI(activity, uri);
		XposedBridge.log("XBetterCam: Path to medium: " + realPath);

		boolean mpoPresent = false;

		if (realPath.toUpperCase().endsWith(".JPG") && (new File(realPath.replaceAll("(?i)\\.JPG$", ".MPO")).exists()
				|| new File(realPath.replaceAll("(?i)\\.JPG$", ".mpo")).exists())) {
			mpoPresent = true;
		}

		if (MimeType.fromText(s) == MimeType.MPO || b || realPath.contains(TIMESHIFT_IDENT) || mpoPresent) {
			XposedBridge.log("XBetterCam: Fallback to original method");
			return true;
		}
		return false;
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			final String[] proj = { MediaStore.Images.Media.DATA };
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

		public String getText() {
			return mText;
		}
	}
}
