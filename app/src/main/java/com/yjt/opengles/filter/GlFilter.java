package com.yjt.opengles.filter;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.yjt.opengles.utils.GlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * 文件描述:
 * <p>
 * ref:
 * https://github.com/doggycoder/AndroidOpenGLDemo/blob/master/app/src/main/java/edu/wuwang/opengl/filter/AFilter.java
 * https://github.com/MasayukiSuda/Mp4Composer-android/blob/master/mp4compose/src/main/java/com/daasuu/mp4compose/filter/GlFilter.java
 * https://github.com/lakeinchina/librestreaming/blob/master/librestreaming/src/main/java/me/lake/librestreaming/filter/hardvideofilter/OriginalHardVideoFilter.java
 *
 * @author yangjiantong
 * @date 2018/2/7
 */
public class GlFilter {

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private final float[] triangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f, 1.0f, 0, 0.f, 1.f,
            1.0f, 1.0f, 0, 1.f, 1.f,
    };
    private FloatBuffer triangleVertices;

    private String vertexShaderSource;
    private String fragmentShaderSource;


    private int program;
    private int textureID = -12345;
    private float[] clearColor = new float[]{0f, 0f, 0f, 0f};

    private final HashMap<String, Integer> handleMap = new HashMap<>();


    public GlFilter() {
        this(GlUtils.DEFAULT_VERTEX_SHADER, GlUtils.DEFAULT_FRAGMENT_SHADER);
    }

    public GlFilter(final Resources res, final int vertexShaderSourceResId, final int fragmentShaderSourceResId) {
        this(res.getString(vertexShaderSourceResId), res.getString(fragmentShaderSourceResId));
    }

    public GlFilter(final String vertexShaderSource, final String fragmentShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        triangleVertices = ByteBuffer.allocateDirect(
                triangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangleVertices.put(triangleVerticesData).position(0);
    }


    public int getTextureId() {
        return textureID;
    }


    public void drawOES(SurfaceTexture surfaceTexture, float[] STMatrix, float[] MVPMatrix) {
        GlUtils.checkGlError("onDrawFrame start");


        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(program);
        GlUtils.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);

        GLES20.glEnableVertexAttribArray(getHandle("aPosition"));
        GlUtils.checkGlError("glEnableVertexAttribArray aTextureHandle");
        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(getHandle("aPosition"), 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

        GLES20.glEnableVertexAttribArray(getHandle("aTextureCoord"));
        GlUtils.checkGlError("glEnableVertexAttribArray aTextureHandle");
        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(getHandle("aTextureCoord"), 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        GlUtils.checkGlError("glVertexAttribPointer aTextureHandle");


        surfaceTexture.getTransformMatrix(STMatrix);

        GLES20.glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(getHandle("uSTMatrix"), 1, false, STMatrix, 0);

        //pre draw
        onDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlUtils.checkGlError("glDrawArrays");
        GLES20.glFinish();

        //after draw

        GLES20.glDisableVertexAttribArray(getHandle("aPosition"));
        GLES20.glDisableVertexAttribArray(getHandle("aTextureCoord"));
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    protected void onDraw() {
    }


    public void setUpSurface() {
        final int vertexShader = GlUtils.loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER);
        final int fragmentShader = GlUtils.loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER);
        program = GlUtils.createProgram(vertexShader, fragmentShader);
        if (program == 0) {
            throw new RuntimeException("failed creating program");
        }

        getHandle("aPosition");
        getHandle("aTextureCoord");
        getHandle("uMVPMatrix");
        getHandle("uSTMatrix");

        textureID = GlUtils.generateTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    protected int getHandle(final String name) {
        final Integer value = handleMap.get(name);
        if (value != null) {
            return value;
        }

        int location = GLES20.glGetAttribLocation(program, name);
        if (location == -1) {
            location = GLES20.glGetUniformLocation(program, name);
        }
        if (location == -1) {
            throw new IllegalStateException("Could not get attrib or uniform location for " + name);
        }
        handleMap.put(name, location);
        return location;
    }

    protected int getAttribHandle(final String name) {
        final Integer value = handleMap.get(name);
        if (value != null) {
            return value;
        }

        int location = GLES20.glGetAttribLocation(program, name);
        if (location == -1) {
            throw new IllegalStateException("Could not get attrib location for " + name);
        }
        handleMap.put(name, location);
        return location;
    }

    protected int getUniformHandle(final String name) {
        final Integer value = handleMap.get(name);
        if (value != null) {
            return value;
        }

        int location = GLES20.glGetUniformLocation(program, name);
        if (location == -1) {
            throw new IllegalStateException("Could not get uniform location for " + name);
        }
        handleMap.put(name, location);
        return location;
    }

    public void release() {
        GlUtils.deleteTexture(textureID);
        GlUtils.deleteProgram(program);
    }

    public void setClearColor(float red,
                              float green,
                              float blue,
                              float alpha) {
        this.clearColor = new float[]{red, green, blue, alpha};
    }


    protected int viewWidth, viewHeight;

    public void onInit(int vWidth, int vHeight) {
        this.viewWidth = vWidth;
        this.viewHeight = vHeight;
        final int vertexShader = GlUtils.loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER);
        final int fragmentShader = GlUtils.loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER);
        program = GlUtils.createProgram(vertexShader, fragmentShader);
        if (program == 0) {
            throw new RuntimeException("failed creating program");
        }

        getAttribHandle("aPosition");
        getAttribHandle("aTextureCoord");
        getUniformHandle("uMVPMatrix");
        getUniformHandle("uSTMatrix");
    }

    public void drawTexture2D(int cameraTexture, int targetFrameBuffer, float[] mvpMatrix, float[] stMatrix) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, targetFrameBuffer);

        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(program);
        GlUtils.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cameraTexture);

        GLES20.glEnableVertexAttribArray(getHandle("aPosition"));
        GlUtils.checkGlError("glEnableVertexAttribArray aTextureHandle");
        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(getHandle("aPosition"), 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

        GLES20.glEnableVertexAttribArray(getHandle("aTextureCoord"));
        GlUtils.checkGlError("glEnableVertexAttribArray aTextureHandle");
        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(getHandle("aTextureCoord"), 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        GlUtils.checkGlError("glVertexAttribPointer aTextureHandle");


        GLES20.glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(getHandle("uSTMatrix"), 1, false, stMatrix, 0);

        GLES20.glViewport(0, 0, viewWidth, viewHeight);

        //pre draw
        onDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlUtils.checkGlError("glDrawArrays");
        GLES20.glFinish();

        //after draw

        GLES20.glDisableVertexAttribArray(getHandle("aPosition"));
        GLES20.glDisableVertexAttribArray(getHandle("aTextureCoord"));
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

}
