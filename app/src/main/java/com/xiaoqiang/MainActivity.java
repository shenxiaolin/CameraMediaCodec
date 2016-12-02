package com.xiaoqiang;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by wangqiangqiang on 2016/12/1.
 */

public class MainActivity extends Activity {
//    private GLSurfaceView glSurfaceView;
//    private GLRenderer glRenderer;
    private SurfaceView mSurfaceView;
    private SurfaceCallback callback;
    private boolean isRecorder = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        glSurfaceView = new GLSurfaceView(this);
//        glSurfaceView.setEGLContextClientVersion(2);
//        glRenderer = new GLRenderer(glSurfaceView);
//        glSurfaceView.setRenderer(glRenderer);
        setContentView(R.layout.activity_main);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        callback = new SurfaceCallback(screenWidth,screenHeight);
        mSurfaceView.getHolder().addCallback(callback);

        findViewById(R.id.start_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecorder = !isRecorder;
                if(isRecorder){
                    callback.startEncodec();
                }else{
                    callback.stopEncodec();;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        glSurfaceView.onPause();
//        glRenderer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
