package com.yjt.opengles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;

    private int mProgram;


    private FloatBuffer vertexBuffer;

    // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // 按逆时针方向顺序:
            0.0f,  0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                // Set the background clear color to red. The first component is
                // red, the second is green, the third is blue, and the last
                // component is alpha, which we don't use in this lesson.
                //设置清空屏幕用的颜色，分别对应红色、绿色和蓝色，最后一个为透明度。
                GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

                int vertexShader = Shader.loadShader(GLES20.GL_VERTEX_SHADER, Shader.vertexShaderCode);
                int fragmentShader = Shader.loadShader(GLES20.GL_FRAGMENT_SHADER, Shader.fragmentShaderCode);

                mProgram = GLES20.glCreateProgram();             // 创建一个空的OpenGL ES Program
                GLES20.glAttachShader(mProgram, vertexShader);   // 将vertex shader添加到program
                GLES20.glAttachShader(mProgram, fragmentShader); // 将fragment shader添加到program
                GLES20.glLinkProgram(mProgram);                  // 创建可执行的 OpenGL ES program



                // 为存放形状的坐标，初始化顶点字节缓冲
                ByteBuffer bb = ByteBuffer.allocateDirect(
                        // (坐标数 * 4)float占四字节
                        triangleCoords.length * 4);
                // 设用设备的本点字节序
                bb.order(ByteOrder.nativeOrder());

                // 从ByteBuffer创建一个浮点缓冲
                vertexBuffer = bb.asFloatBuffer();
                // 把坐标们加入FloatBuffer中
                vertexBuffer.put(triangleCoords);
                // 设置buffer，从第一个坐标开始读
                vertexBuffer.position(0);
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int width, int height) {
                // Set the OpenGL viewport to fill the entire surface.
                //设置了视口尺寸，告诉 OpenGL 可以用来渲染的 surface 的大小。
                GLES20.glViewport(0, 0, width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                // Clear the rendering surface.
                //会擦除屏幕上的所有颜色，并用 glClearColor 中的颜色填充整个屏幕。
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                // 将program加入OpenGL ES环境中
                GLES20.glUseProgram(mProgram);

                // 获取指向vertex shader的成员vPosition的 handle
                int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

                // 启用一个指向三角形的顶点数组的handle
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                // 准备三角形的坐标数据
//                GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
//                        GLES20.GL_FLOAT, false,
//                        vertexStride, vertexBuffer);

                // 获取指向fragment shader的成员vColor的handle
                int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

                // 设置三角形的颜色
                GLES20.glUniform4fv(mColorHandle, 1, color, 0);

                // 画三角形
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

                // 禁用指向三角形的顶点数组
                GLES20.glDisableVertexAttribArray(mPositionHandle);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}
