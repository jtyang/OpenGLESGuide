package com.yjt.opengles.lesson04;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yjt.opengles.R;
import com.yjt.opengles.base.BaseGLActivity;

/**
 * OpenglES 2.0 第四课
 * 增加颜色和着色
 * 未开始。。。。。。。
 * <p>
 * <p>
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class Lesson04Activity extends BaseGLActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lesson02;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsurfaceview;
    }

    @Override
    public GLSurfaceView.Renderer getGLRender() {
        return new AirHockey04Render(this);
    }

}
