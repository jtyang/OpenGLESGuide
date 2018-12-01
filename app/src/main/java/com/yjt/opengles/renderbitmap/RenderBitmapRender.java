package com.yjt.opengles.renderbitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.yjt.opengles.R;
import com.yjt.opengles.utils.MatrixUtils;
import com.yjt.opengles.utils.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 文件描述:
 *
 * @author yangjiantong
 * @date 2018/12/1
 */
public class RenderBitmapRender implements GLSurfaceView.Renderer {

    private Context context;
    private int bitmapWidth;
    private int bitmapHeight;

    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
//
//            0f, 0.5f,
//            0.5f, 0.5f,
//            0f, 0f,
//            0.5f, 0f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int textureid;
    private int sampler;


    private int umatrix;
    private float[] matrix = new float[16];

    public RenderBitmapRender(Context context) {
        this.context = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexSource = ShaderHelper.getRawResource(context, R.raw.vertex_shader_matrix);
        String fragmentSource = ShaderHelper.getRawResource(context, R.raw.fragment_shader);

        program = ShaderHelper.createProgram(vertexSource, fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");
        umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");


        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        textureid = textureIds[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(sampler, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.androids);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        log("bitmap w=" + bitmapWidth + ",h=" + bitmapHeight);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();
        bitmap = null;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        log("onSurfaceChanged w=" + width + ",h=" + height);
        GLES20.glViewport(0, 0, width, height);


//        if (width > height) {
//            Matrix.orthoM(matrix, 0, -width / ((height / 702f) * 526f), width / ((height / 702f) * 526f), -1f, 1f, -1f, 1f);
//        } else {
//            Matrix.orthoM(matrix, 0, -1, 1, -height / ((width / 526f) * 702f), height / ((width / 526f) * 702f), -1f, 1f);
//        }
        Matrix.setIdentityM(matrix, 0);
//        MatrixUtils.getMatrix(matrix, MatrixUtils.TYPE_CENTERCROP, bitmapWidth, bitmapHeight, width, height);
        MatrixUtils.getMatrix(matrix, MatrixUtils.TYPE_CENTERINSIDE, bitmapWidth, bitmapHeight, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(umatrix, 1, false, matrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexBuffer);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                fragmentBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    private void log(String msg) {
        Log.e("yjt", msg);
    }
}
