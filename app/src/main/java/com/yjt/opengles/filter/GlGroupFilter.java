package com.yjt.opengles.filter;

import android.opengl.GLES20;

import com.yjt.opengles.utils.GlUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 滤镜组
 *
 * @author yangjiantong
 * @date 2018/2/7
 */
public class GlGroupFilter extends GlFilter {

    private LinkedList<FilterWrapper> filterWrappers;

    public GlGroupFilter(List<GlFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            throw new IllegalArgumentException("can not create empty GroupFilter");
        }
        filterWrappers = new LinkedList<>();
        for (GlFilter filter : filters) {
            filterWrappers.add(new FilterWrapper(filter));
        }
    }

    @Override
    public void onInit(int vWidth, int vHeight) {
        super.onInit(vWidth, vHeight);
        for (FilterWrapper wrapper : filterWrappers) {
            wrapper.filter.onInit(vWidth, vHeight);
            int[] frameBuffer = new int[1];
            int[] frameBufferTexture = new int[1];
            GlUtils.createFrameBuff(frameBuffer,
                    frameBufferTexture,
                    viewWidth,
                    viewHeight);
            wrapper.frameBuffer = frameBuffer[0];
            wrapper.frameBufferTexture = frameBufferTexture[0];
        }
    }


    @Override
    public void drawTexture2D(int cameraTexture, int targetFrameBuffer, float[] mvpMatrix, float[] stMatrix) {
        FilterWrapper preFilterWrapper = null;
        int i = 0;
        int texture;
        for (FilterWrapper wrapper : filterWrappers) {
            if (preFilterWrapper == null) {
                texture = cameraTexture;
            } else {
                texture = preFilterWrapper.frameBufferTexture;
            }
            if (i == (filterWrappers.size() - 1)) {
                wrapper.filter.drawTexture2D(texture, targetFrameBuffer, mvpMatrix, stMatrix);
            } else {
                wrapper.filter.drawTexture2D(texture, wrapper.frameBuffer, mvpMatrix, stMatrix);
            }
            preFilterWrapper = wrapper;
            i++;
        }
    }


    @Override
    public void release() {
        super.release();
        if (filterWrappers != null && filterWrappers.size() > 0) {
            for (FilterWrapper wrapper : filterWrappers) {
                wrapper.filter.release();
                GLES20.glDeleteFramebuffers(1, new int[]{wrapper.frameBuffer}, 0);
                GLES20.glDeleteTextures(1, new int[]{wrapper.frameBufferTexture}, 0);
            }
            filterWrappers.clear();
        }
    }

    private class FilterWrapper {
        GlFilter filter;
        int frameBuffer;
        int frameBufferTexture;

        FilterWrapper(GlFilter filter) {
            this.filter = filter;
        }
    }

}
