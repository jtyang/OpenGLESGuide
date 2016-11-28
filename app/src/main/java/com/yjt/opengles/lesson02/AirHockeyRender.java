package com.yjt.opengles.lesson02;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer实现类
 * <p>
 * 1.opengl绘制图形基础
 * OpenGL中，每个物体都是由点、直线、三角形构成
 * 因此，在OpenGL中，只能绘制点、直线、三角形。
 * <p>
 * 顶点：一个几何对象的拐角的点。如长方形有四个顶点。顶点包含很多属性，比如位置、颜色等。
 * <p>
 * <p>
 * 2.使数据可以被opengl读取
 * OpenGL作为本地系统库，直接运行在硬件上，没有虚拟机、垃圾回收和内存压缩等机制。
 * <p>
 * 方法一：使用JNI，其实android的java层api就是使用这种方式访问本地系统库。
 * 方法二：把内存从java堆复制到本地堆。java的nio中有几个特殊集合，可以在本地堆中申请内存，FloatBuffer,ByteBuffer等
 * 顶点定义：定义三角形时，用的是逆时针，称为卷曲顺序，任何地方都使用卷曲顺序，可以优化性能。
 * java的ByteBuffer使用
 * <p>
 * 3.OpenGL管道
 * 着色器：
 * 顶点着色器（vertex shader）：生成顶点的最终位置，每个顶点执行一次，一旦最终位置确定了，gl会把顶点集合组装成点、直线、三角形。
 * 片段着色器（fragment shader）:为组成点、直接、三角形的每个片段着色。每个片段执行一次，一个片段是小的单颜色的矩形区域。类似屏幕像素。
 * <p>
 * 一旦最后的颜色生成了，opengl会把他们写到一块称为帧缓冲区（frame buffer）的内存块中，然后android会把这个缓冲区显示到屏幕上。
 * <p>
 * 流程：
 * 读取顶点数据---执行顶点着色器---组装图元---光栅化图元---执行片段着色器---写入帧缓冲区---显示到屏幕
 * <p>
 * 光栅化技术：把点、直接、三角形分解成大量的小片段。这些小片段可以映射到屏幕上。每个片段都包含单一的颜色（RGBA）
 * <p>
 * 定义简单的顶点着色器和片段着色器
 * <p>
 * <p>
 * <p>
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class AirHockeyRender implements GLSurfaceView.Renderer {

    public static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PRE_FLOAT = 4;//一个float类型占4个字节
    public FloatBuffer vertexData;//本地堆中存储顶点数据

    /**
     * 定义简单的顶点着色器
     * <p>
     * 我们定义的每个顶点，顶点着色器都会被调用一次，被调用的时候a_Position属性会接收当前顶点的位置，
     * vec4是一个包含4个分量的向量，在位置的上下文中，4个分量可分别表示x,y,z,
     * w,x,y,z是一个三维坐标，而w是个特殊坐标。
     * 默认情况，opengl会把x,y,z坐标设置为0，w坐标设置为1
     */
    public static final String SIMPLE_VERTEX_SHADER = "" +
            "attribute vec4 a_Position;" +//定义顶点属性
            "void main()" +
            "{" +
            "   gl_Position = a_Position;" +//gl_Position存储当前顶点的最终位置
            "}";

    /**
     * 定义简单的片段着色器
     */
    public static final String SIMPLE_FRAGMENT_SHADER = "" +
            //定义精度等级，可选择lowp,mediump,highp，分别表示低精度，中精度，高精度，但只有部分硬件支持片段着色使用highp
            "precision mediump float;" +
            //uniform和attribute不同的是，uniform是每个顶点使用同一个值(除非再次改变它)，而attribute是每个顶点都要设置一个，
            //uniform和attribute一样也定义了4个分量rgba。
            "uniform vec4 u_Color;" +
            "void main()" +
            "{" +
            "   gl_FragColor = u_Color;" +//gl_FragColor是当前片段的最终颜色值
            "}";

    public AirHockeyRender() {
        //顶点属性数组：定义桌子的四个顶点，按顺时针方向定义
        float[] tableVertices = new float[]{
                0f, 0f,
                0f, 14f,
                9f, 14f,
                9f, 0f
        };

        //顶点属性数组：用两个三角形来绘制一个矩形
        float[] tableVerticesWithTriangles = new float[]{
                //triangle 1
                0f, 0f,
                9f, 14f,
                0f, 14f,

                //triangle 2
                0f, 14f,
                9f, 0f,
                9f, 14f
        };

        /**
         * 申请本地内存，由于申请的是字节数，所以要按乘以基础数据类型所占的字节数
         * 一个java float有4个字节，每个字节8位，共32bit。
         * ByteBuffer在进程结束的时候会被释放掉，一般不用关心，如果创建了很多ByteBuffer，可以手动释放。
         */
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PRE_FLOAT)
                .order(ByteOrder.nativeOrder())//字节对其方式
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}
