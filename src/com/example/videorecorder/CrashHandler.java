package com.example.videorecorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import com.example.videorecorder.util.Vlog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

/**
 * @author caomengqi
 * 2015年2月10日
 * @JDK version 1.8
 * @brief handler to record uncaught exception
 * @version
 */
public class CrashHandler implements UncaughtExceptionHandler{
	
	private static CrashHandler handler;
	private Context mContext;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		if (handler == null) {
			handler = new CrashHandler();
		}
		return handler;
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context) {
		Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		String logDir = null;
		
		MobclickAgent.reportError(mContext, arg1);
		if (Environment.getExternalStorageDirectory() != null) {
			logDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "10090" + File.separator + "log";

			File file = new File(logDir);
			boolean mkSuccess;
			if (!file.isDirectory()) {
				mkSuccess = file.mkdirs();
				if (!mkSuccess) {
					mkSuccess = file.mkdirs();
				}
			}
			try {
				FileWriter fw = new FileWriter(logDir + File.separator + "error.log", true);
				fw.write(new Date() + "\n");
				StackTraceElement[] stackTrace = arg1.getStackTrace();
				fw.write("exception : " + arg1.getMessage() + "\n");
				Vlog.i("crash", "crash msg : " + arg1.getMessage());
				for (int i = 0; i < stackTrace.length; i++) {
					fw.write("file:" + stackTrace[i].getFileName() + " class:" + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + " line:" + stackTrace[i].getLineNumber() + "\n");
					Vlog.i("crash", stackTrace[i].getFileName() + " class:" + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + " line:" + stackTrace[i].getLineNumber());
				}
				fw.write("\n");
				fw.close();
			} catch (IOException e) {
				
			}
		}

		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
}
