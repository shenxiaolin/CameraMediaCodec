package com.xiaoqiang;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import com.xiaoqiang.camera.CameraManager;
import com.xiaoqiang.encoder.CircularEncoder;
import com.xiaoqiang.opengl.EglCore;
import com.xiaoqiang.opengl.FullFrameRect;
import com.xiaoqiang.opengl.Texture2dProgram;
import com.xiaoqiang.opengl.WindowSurface;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by wangqiangqiang on 2016/12/1.
 */

public class SurfaceCallback implements SurfaceHolder.Callback,SurfaceTexture.OnFrameAvailableListener{
    private static final String TAG = "SurfaceCallback";
    private EglCore mEglCore;
    private WindowSurface mDisplaySurface;
    private WindowSurface mEncoderSurface;
    private FullFrameRect mFullFrameBlit;
    private int mTextureId;
    private SurfaceTexture mCameraTexture;
    private CircularEncoder mCircEncoder;
    private final float[] mTmpMatrix = new float[16];
    private int mFrameNum;
    private static final int VIDEO_WIDTH = 368;
    private static final int VIDEO_HEIGHT = 640;
    private int width;
    private int height;
    private boolean isRecorder = false;

    public  SurfaceCallback(int width,int height){
        this.width = width;
        this.height = height;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, holder.getSurface(), false);
        mDisplaySurface.makeCurrent();

        mFullFrameBlit = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mTextureId = mFullFrameBlit.createTextureObject();
        mCameraTexture = new SurfaceTexture(mTextureId);
        mCameraTexture.setOnFrameAvailableListener(this);

        Log.d(TAG, "starting camera preview");
        CameraManager.getCameraManager().startCamera(mCameraTexture);

        // TODO: adjust bit rate based on frame rate?
        // TODO: adjust video width/height based on what we're getting from the camera preview?
        //       (can we guarantee that camera preview size is compatible with AVC video encoder?)
        file = new File(Environment.getExternalStorageDirectory(),"/push/");
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(file,"camera_"+new Date().getTime()+".mp4");
        try {
            mCircEncoder = new CircularEncoder(VIDEO_WIDTH, VIDEO_HEIGHT, 6000000,25, 7, mHandler,file);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mEncoderSurface = new WindowSurface(mEglCore, mCircEncoder.getInputSurface(), true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onPause();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        drawFrame();
    }
    private void drawFrame() {
        //Log.d(TAG, "drawFrame");
        if (mEglCore == null) {
            Log.d(TAG, "Skipping drawFrame after shutdown");
            return;
        }

        // Latch the next frame from the camera.
        mDisplaySurface.makeCurrent();
        mCameraTexture.updateTexImage();
        mCameraTexture.getTransformMatrix(mTmpMatrix);

        GLES20.glViewport(0, 0, width, height);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
        mDisplaySurface.swapBuffers();

        // Send it to the video encoder.
        if (isRecorder) {
            mEncoderSurface.makeCurrent();
            GLES20.glViewport(0, 0, VIDEO_WIDTH, VIDEO_HEIGHT);
            mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
            mCircEncoder.frameAvailableSoon();
            mEncoderSurface.setPresentationTime(mCameraTexture.getTimestamp());
            mEncoderSurface.swapBuffers();
        }

        mFrameNum++;
    }
    protected void onPause() {
        if (mCircEncoder != null) {
            mCircEncoder.shutdown();
            mCircEncoder = null;
        }
        if (mCameraTexture != null) {
            mCameraTexture.release();
            mCameraTexture = null;
        }
        if (mDisplaySurface != null) {
            mDisplaySurface.release();
            mDisplaySurface = null;
        }
        if (mFullFrameBlit != null) {
            mFullFrameBlit.release(false);
            mFullFrameBlit = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        Log.d(TAG, "onPause() done");
    }

    private  File file;
    public void startEncodec(){
        isRecorder= true;
    }
    public void stopEncodec(){
        isRecorder = false;
    }
    private CircularEncoder.Callback mHandler = new CircularEncoder.Callback() {
        @Override
        public void fileSaveComplete(int status) {
            Log.d("wqq","写文件结果"+status);
        }

        @Override
        public void bufferStatus(long totalTimeMsec) {

        }
    };
}
