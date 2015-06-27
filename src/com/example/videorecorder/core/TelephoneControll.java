package com.example.videorecorder.core;

import com.example.videorecorder.util.Vlog;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TelephoneControll {
	public static final String TAG = "TelephoneControll";
	
	private Context mContext;
	private TelephonyManager mTelephonyManager;
	
	private MyPhoneStateListener myPhoneStateListener;
	
	public TelephoneControll(Context context){
		mContext = context;
		mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
	}
	
	
	public void startInterceptPhone(){
		Vlog.i(TAG, "startInterceptPhone");
		mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	
	public void stopInterceptPhone(){
		Vlog.i(TAG, "stopInterceptPhone");
		mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
}
