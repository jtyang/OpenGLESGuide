package com.yjt.opengles.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * 文件描述:
 *
 * @author yangjiantong
 * @date 2018/12/1
 */
public abstract class BaseGLActivity extends AppCompatActivity {

    private static final String TAG = "GLActivity";
    protected GLSurfaceView mGLSurfaceView;
    private boolean renderSet = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        int glViewId = getGLSurfaceViewId();
        if (glViewId > 0) {
            mGLSurfaceView = findViewById(glViewId);
            //step1: check is support opengl
            if (isSupportGL()) {
                //step2: set opengl version is opengles 2.0
                mGLSurfaceView.setEGLContextClientVersion(2);
                //step3: set GLRender to view
                mGLSurfaceView.setRenderer(getGLRender());
                mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                renderSet = true;
            } else {
                Toast.makeText(this, "current device do not support opengles 2.0", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setKeepScreenOn(true);
            if (renderSet) {
                mGLSurfaceView.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setKeepScreenOn(false);
            if (renderSet) {
                mGLSurfaceView.onPause();
            }
        }
    }


    private boolean isSupportGL() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    protected Context getContext(){
        return getBaseContext();
    }

    protected void log(String msg) {
        Log.i(TAG, msg);
    }

    public abstract int getLayoutId();

    public abstract int getGLSurfaceViewId();

    public abstract GLSurfaceView.Renderer getGLRender();

}
