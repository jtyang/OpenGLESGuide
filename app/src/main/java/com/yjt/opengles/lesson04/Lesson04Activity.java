package com.yjt.opengles.lesson04;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yjt.opengles.R;

/**
 * OpenglES 2.0 第四课
 * 增加颜色和着色
 * 未开始。。。。。。。
 * <p>
 * <p>
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class Lesson04Activity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson02);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);

        //step1: check is support opengl
        if (isSupportGL()) {
            //step2: set opengl version is opengles 2.0
            glSurfaceView.setEGLContextClientVersion(2);
            //step3: set GLRender to view
            glSurfaceView.setRenderer(new AirHockey04Render(this));
            renderSet = true;
        } else {
            Toast.makeText(this, "current device do not support opengles 2.0", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.setKeepScreenOn(true);
        if (renderSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.setKeepScreenOn(false);
        if (renderSet) {
            glSurfaceView.onPause();
        }
    }

    private boolean isSupportGL() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

}
