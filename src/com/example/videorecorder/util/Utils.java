package com.example.videorecorder.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;

public class Utils {
	public static Bitmap createVideoThumbnail(String filePath, int width, int height) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
        return bitmap;
	}

	public static void playVideo(String mFilePath, Context context) {
		if(TextUtils.isEmpty(mFilePath)){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/mp4";
        Uri uri = Uri.parse("file://" + mFilePath);
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
	}
    
}
