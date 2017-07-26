package com.yjt.opengles.v2.lesson1;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 搭建OpenGL ES 环境框架
 * AUTHOR: yangjiantong
 * DATE: 2017/7/26
 */
public class Lesson1Activity extends AppCompatActivity {

    private MyGLView mMyGLView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyGLView = new MyGLView(this);
        setContentView(mMyGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMyGLView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

class MyGLView extends GLSurfaceView {

    public MyGLView(Context context) {
        super(context);
        init();
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);//设置opengl版本为2.0
        setRenderer(new MyRender());
    }
}

class MyRender implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        //初始化清空画布颜色 rgba
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置opengl窗口大小
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空画布
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // TODO: 2017/7/26 draw something
    }
}
