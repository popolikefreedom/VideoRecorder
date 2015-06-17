package com.example.videorecorder.util;

import android.util.Log;

public class Vlog {
	public static final String TAG = "Vlog";

	public static final boolean DEBUG = true;

	public static void i(String tag, String msg) {
		if (DEBUG)
			Log.i(tag, msg);
	}
}
