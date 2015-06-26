package com.example.videorecorder.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class Utils {
	public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
	}
    
}
