package com.xiaoqiang.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by wangqiangqiang on 2016/12/2.
 */

public class CaptureScreenManager {
    private static CaptureScreenManager captureScreenManager;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionManager projectionManager;
    private MediaProjection projection;
    private final static int ACTIVITY_REQUEST = 1;
    private  Surface mSurface;
    private Activity mActivity;
    private CaptureScreenManager(){}
    public static CaptureScreenManager getCaptureScreenManager(){
        if(captureScreenManager == null){
            captureScreenManager= new CaptureScreenManager();
        }
        return captureScreenManager;
    }

    public boolean startCaptureScreen(Activity mContext, Surface surface){
        mSurface = surface;
        mActivity= mContext;
        projectionManager = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        mContext.startActivityForResult(intent,ACTIVITY_REQUEST);
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        projection = projectionManager.getMediaProjection(resultCode,data);
        if(projection != null){
            startCaptureScreen(mSurface);
        }else{
//            Toast.makeText(getApplicationContext(),"无法开始录屏",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean stopCaptureScreen(){
        if(virtualDisplay != null){
            virtualDisplay.release();
        }
        return true;
    }
    private void startCaptureScreen(Surface surface){
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Toast.makeText(mActivity,"当前SurfaceView是否可用："+surface.isValid(),Toast.LENGTH_SHORT).show();
        virtualDisplay = projection.createVirtualDisplay("MAINACTIVITY",480,640,metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,surface,null,null);
    }
}
