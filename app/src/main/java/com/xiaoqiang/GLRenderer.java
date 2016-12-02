package com.xiaoqiang;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.xiaoqiang.camera.CameraManager;
import com.xiaoqiang.encoder.TextureMovieEncoder;
import com.xiaoqiang.opengl.FullFrameRect;
import com.xiaoqiang.opengl.Texture2dProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangqiangqiang on 2016/12/1.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private SurfaceTexture mSurfaceTexture;
    private FullFrameRect mFullScreen;
    private int mTextureId;
    private static TextureMovieEncoder mVideoEncoder = new TextureMovieEncoder();;
    private final float[] mSTMatrix = new float[16];
    private GLSurfaceView mGLSurfaceView;

    public GLRenderer(GLSurfaceView mGLSurfaceView){
        this.mGLSurfaceView = mGLSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullScreen = new FullFrameRect( new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mTextureId = mFullScreen.createTextureObject();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        CameraManager.getCameraManager().startCamera(mSurfaceTexture);
    }

    public void pause(){
        CameraManager.getCameraManager().stopCamera();
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mVideoEncoder.setTextureId(mTextureId);
        mVideoEncoder.frameAvailable(mSurfaceTexture);
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix);
    }
    private SurfaceTexture.OnFrameAvailableListener listener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            mGLSurfaceView.requestRender();
        }
    };
}
