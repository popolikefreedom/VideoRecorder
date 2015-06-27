package com.example.videorecorder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.videorecorder.core.TelephoneControll;
import com.example.videorecorder.util.Utils;
import com.example.videorecorder.util.Vlog;

public class MainActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {

	private static final String TAG = "MainActivity";

	private ImageButton mRecord;
	private ImageButton mPlayLast;
	
	private Button mDelete;
	
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	private Camera mCamera;

	private String mFilePath;

	private MediaRecorder mMediaRecorder;

	private boolean isRecording;

	private int mVideoFps;
	private int mVideoWidth;
	private int mVideoHeight;
	
	
	private TelephoneControll mTelephoneControll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFilePath = getFilePath();
		findViews();
		openCamera();
		mTelephoneControll = new TelephoneControll(this);
		mTelephoneControll.startInterceptPhone();
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTelephoneControll.stopInterceptPhone();
	}




	private String getFilePath() {
		long time = System.currentTimeMillis();
		String str = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "10090"
				+ File.separator
				+ time + ".3gp";
		File file = new File(str).getParentFile();
		if (!file.exists()) {
			file.mkdirs();
		}

		return str;
	}

	private void openCamera() {
		try {
			mCamera = Camera.open();
		} catch (Exception e) {

		}
		if (mCamera != null) {
			Camera.Parameters cameraParameters = mCamera.getParameters();
			List<int[]> fpsList = cameraParameters
					.getSupportedPreviewFpsRange();
			List<Size> sizeList = cameraParameters.getSupportedVideoSizes();
			Vlog.i(TAG,
					"fpsList.size : " + fpsList.size() + ", fps"
							+ fpsList.get(0).length + ", sizeList.size :"
							+ sizeList.size() + ", first size : "
							+ sizeList.get(0).width + ","
							+ sizeList.get(0).height);
			mVideoFps = fpsList.get(0)[1] / 1000;
			Vlog.i(TAG, "mVideoFps : " + mVideoFps + "," + fpsList.get(0)[0]);
			mVideoWidth = sizeList.get(0).width;
			mVideoHeight = sizeList.get(0).height;
			Vlog.i(TAG, "mVideoWidth : " + mVideoWidth + "," + mVideoHeight);
			// cameraParameters.set("orientation", "portrait");
			mCamera.setDisplayOrientation(90);
		}

	}

	private void findViews() {
		mSurfaceView = (SurfaceView) findViewById(R.id.main_surface_View);
		mRecord = (ImageButton) findViewById(R.id.main_record);
		mDelete = (Button) findViewById(R.id.main_delete);
		mPlayLast = (ImageButton) findViewById(R.id.main_video);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);

		mRecord.setOnClickListener(this);
		mPlayLast.setOnClickListener(this);
		// mDelete.setOnClickListener(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Vlog.i(TAG, "surfaceChanged");
		if (mSurfaceHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// make any resize, rotate or reformatting changes here
		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Vlog.i(TAG, "Error starting camera preview: " + e.getMessage());
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Vlog.i(TAG, "surfaceCreated");
		openCamera();
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Vlog.i(TAG, "surfaceDestroyed");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.main_record:
			if (!isRecording) {
				startRecording();
			} else {
				stopRecording();
			}
			break;
		case R.id.main_delete:
			deleteVideos();
			break;
		case R.id.main_video:
			Utils.playVideo(mFilePath, this);
			break;
			
			
		}
	}

	private void deleteVideos() {
		String str = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "10090";
		File parentFile = new File(str);
		if (!parentFile.exists()) {
			return;
		}

		File[] files = parentFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file != null) {
				file.delete();
			}
		}
	}

	public synchronized void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
		if (isRecording) {
			mRecord.setImageResource(R.drawable.btn_stop);
		} else {
			mRecord.setImageResource(R.drawable.btn_recod_point);
		}
	}

	public void startRecording() {
		Vlog.i(TAG, "startRecording");
		setRecording(true);
		mCamera.unlock();
		videoRecordStart();
	}

	public void stopRecording() {
		Vlog.i(TAG, "stopRecording");
		setRecording(false);
		mCamera.lock();
		videoRecordStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void videoRecordStart() {
		mMediaRecorder = new MediaRecorder();// 创建mediarecorder对象
		// 设置录制视频源为Camera(相机)
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 设置录制的视频编码h263 h264
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		mMediaRecorder.setVideoFrameRate(20);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		mMediaRecorder.setVideoSize(mVideoWidth, mVideoHeight);
		mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
		//mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		// 设置视频文件输出的路径
		mMediaRecorder.setOutputFile(mFilePath);
		try {
			// 准备录制
			mMediaRecorder.prepare();
			// 开始录制
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void videoRecordStop() {
		if (mMediaRecorder != null) {
			// 停止
			mMediaRecorder.stop();
			mMediaRecorder.release();
		}
		Bitmap bitmap = Utils.createVideoThumbnail(mFilePath, 0 , 0);
		if(bitmap != null){
			mPlayLast.setImageBitmap(bitmap);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Vlog.i(TAG, "dispatchKeyEvent" + event.getAction());
		int keyCode = event.getAction();
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Vlog.i(TAG, "onKeyDown" + ev.getAction());
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Vlog.i(TAG, "onKeyDown" + event.getAction());
		if (KeyEvent.KEYCODE_HOME == keyCode) {
			Vlog.i(TAG, "HOME has been pressed yet ...");
			// android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onKeyDown(keyCode, event);

	}

}
