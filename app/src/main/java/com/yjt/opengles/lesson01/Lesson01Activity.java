package com.yjt.opengles.lesson01;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yjt.opengles.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenglES 2.0 第一课
 * 主要了解GLSurfaceView的基本使用流程以及Render函数的作用和调用时机
 * <p>
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class Lesson01Activity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson01);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);

        //step1: check is support OpenGL
        if (isSupportOpenGL()) {
            //step2: set OpenGL version is OpenGLES 2.0
            glSurfaceView.setEGLContextClientVersion(2);
            //step3: set GLRender to view
            glSurfaceView.setRenderer(new MyGLRender());
            renderSet = true;
        } else {
            Toast.makeText(this, "current device do not support opengles 2.0", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet) {
            glSurfaceView.onPause();
        }
    }

    /**
     * 检查设备是否支持OpenGL
     *
     * @return
     */
    private boolean isSupportOpenGL() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    /**
     * 检查是否模拟器是否支持OpenGL
     *
     * @return
     */
    private boolean isProbablyEmulator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));
    }


    private class MyGLRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            //surface被创建的时候被调用。
            //一般程序第一次执行；另外activity被重新显示或者设备被唤醒，也有可能触发

            //清屏，参数分别为红色，绿色，蓝色，alpha值
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            //surface尺寸发生变化时调用；GL10是opengl 1.0遗留下来的参数，2.0版本后需使用，可直接通过静态方法调用gl函数

            //设置窗口大小，前两个参数是x和y的偏移量，后两个参数是surface宽高
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            //1.绘制一帧时被调用，注意，这个方法一定要绘制一些东西，没有东西可绘制的时候，就执行清屏操作，不然界面可能出现闪烁
            //2.默认情况，GLSurfaceView会一直主动刷新渲染，
            // 如果想要主动刷新渲染，可以glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            //3.GLSurfaceView 默认在后台线程渲染，gl相关的操作都要在相同后台线程执行，若要和UI线程通信，
            //主线程可调用glSurfaceView.queueEvent(new Runnable(){});访问gl线程
            //gl线程可调用runOnUiThread(new Runnable(){});访问主线程


            //清空屏幕所有颜色，并用之前设置的glClearColor设置的颜色填充屏幕
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
    }


}
