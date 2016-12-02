package com.xiaoqiang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by wangqiangqiang on 2016/12/2.
 */

public class CaptureScreenActivity extends Activity {
    private VirtualDisplay virtualDisplay;
    private MediaProjectionManager projectionManager;
    private MediaProjection projection;


    private Button startButton;
    private Button saveButton;
    private final static int ACTIVITY_REQUEST = 1;
    private Surface mSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_screen);
        startButton = (Button) findViewById(R.id.btn_capture);
        saveButton = (Button) findViewById(R.id.btn_save_video);
        saveButton.setVisibility(View.INVISIBLE);
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(intent,ACTIVITY_REQUEST);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCaptureScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        projection = projectionManager.getMediaProjection(resultCode,data);

        if(projection != null){

        }else{
            Toast.makeText(getApplicationContext(),"无法开始录屏",Toast.LENGTH_SHORT).show();
        }
    }

    public void startCaptureScreen(Surface surface){
        Toast.makeText(this,"开始屏幕录制",Toast.LENGTH_SHORT).show();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Toast.makeText(getApplicationContext(),"当前SurfaceView是否可用："+surface.isValid(),Toast.LENGTH_SHORT).show();
        virtualDisplay = projection.createVirtualDisplay("MAINACTIVITY",480,640,metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,surface,null,null);
    }
    public void stopCaptureScreen(){
        if(virtualDisplay != null){
            virtualDisplay.release();
        }
    }
}
