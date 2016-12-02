package com.xiaoqiang.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by wangqiangqiang on 2016/12/1.
 */

public class CameraManager {
    private static CameraManager cameraManager;
    private Camera mCamera;
    private CameraManager(){}
    public static CameraManager getCameraManager(){
        if(cameraManager == null){
            cameraManager= new CameraManager();
        }
        return cameraManager;
    }

    private boolean initCamera(){
            if(mCamera == null){
                    mCamera = Camera.open();
            }
            Camera.Parameters parameters = mCamera.getParameters();
            buildCameraParameters(parameters);
            mCamera.setParameters(parameters);
        return true;
    }

    private void buildCameraParameters(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(sizes.get(0).width,sizes.get(0).height);
        mCamera.setDisplayOrientation(90);
    }

    public boolean startCamera(SurfaceTexture surfaceTexture){
        try {
            initCamera();
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean stopCamera(){
        Log.d("wqq","停止摄像头");
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
        return true;
    }
}
