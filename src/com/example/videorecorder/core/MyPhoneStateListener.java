package com.example.videorecorder.core;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.example.videorecorder.util.Vlog;

import android.content.Context;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneStateListener extends PhoneStateListener {
	public static final String TAG = "MyPhoneStateListener";

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		Vlog.i(TAG, "onCallStateChanged");
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			Vlog.i(TAG, "CALL_STATE_IDLE");
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Vlog.i(TAG, "CALL_STATE_RINGING");
			endCall();
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Vlog.i(TAG, "CALL_STATE_OFFHOOK");
			break;
		}
		super.onCallStateChanged(state, incomingNumber);
	}

	private void endCall() {
		try{
			Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
			ITelephony iTelePhony = ITelephony.Stub.asInterface(binder);
			boolean result = iTelePhony.endCall();
			Vlog.i(TAG, "endcall : " + result);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
