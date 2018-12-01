package com.yjt.opengles.renderbitmap;

import android.opengl.GLSurfaceView;

import com.yjt.opengles.R;
import com.yjt.opengles.base.BaseGLActivity;

/**
 * 使用gl渲染bitmap纹理
 *
 * @author yangjiantong
 * @date 2018/12/1
 */
public class RenderBitmapActivity extends BaseGLActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_renderbitmap;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsurfaceview;
    }

    @Override
    public GLSurfaceView.Renderer getGLRender() {
        return new RenderBitmapRenderWithVBO(getContext());
    }

}
