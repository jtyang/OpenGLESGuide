package com.yjt.opengles.lesson04;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.yjt.opengles.BuildConfig;
import com.yjt.opengles.utils.Logger;
import com.yjt.opengles.utils.ShaderHelper;

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
 * 4.OpenGL坐标系
 * OpenGL坐标系的原点是在屏幕中心的(x轴向右，y轴向上)，而android的坐标系原点是在屏幕左上角(x轴向右，y轴向下)。
 * 所以opengl坐标对应屏幕的坐标大概是：左上角(-1,1),右上角(1,1),左下角(1,-1),右下角(1,-1)
 * <p>
 * <p>
 * <p>
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class AirHockey04Render implements GLSurfaceView.Renderer {

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
            "   gl_PointSize = 10.0;"+//告诉opengl点的大小为10，不然绘制矩形桌子上的两个点的时候会看不到任何东西，gl_PointSize是一个片段(矩形)的一条边的长度
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


    private Context context;

    public AirHockey04Render(Context context) {
        this.context = context;

        //顶点属性数组：定义桌子的四个顶点，按顺时针方向定义
        float[] tableVertices = new float[]{
                0f, 0f,
                0f, 14f,
                9f, 14f,
                9f, 0f
        };

        //顶点属性数组：用两个三角形来绘制一个矩形
        //这是android的坐标系
        /*float[] tableVerticesWithTriangles = new float[]{
                //triangle 1 三角形1
                0f, 0f,
                9f, 14f,
                0f, 14f,

                //triangle 2 三角形2
                0f, 14f,
                9f, 0f,
                9f, 14f,

                //line 桌子中间横向分割线
                0f, 7f,
                9f, 7f,

                //mallets 两个木追点
                4.5f, 2f,
                4.5f, 12f
        };*/
        //转换成opengl的坐标系，如果想在屏幕中心绘制一个矩形桌子加中心分割线加两个点，坐标如下
        float[] tableVerticesWithTriangles = new float[]{
                //triangle 1 三角形1
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,

                //triangle 2 三角形2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                //line 桌子中间横向分割线
                -0.5f, 0f,
                0.5f, 0f,

                //mallets 两个木追点
                0f, -0.25f,
                0f, 0.25f
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


    private int uColorLocation;
    private int aPositionLocation;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置清屏颜色为黑色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //初始化准备操作
        //1.编译着色器
        int vertexShader = ShaderHelper.compileVertexShader(SIMPLE_VERTEX_SHADER);
        int fragmentShader = ShaderHelper.compileFragmentShader(SIMPLE_FRAGMENT_SHADER);

        //2.链接着色器
        if (vertexShader != 0 && fragmentShader != 0) {
            int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
            if (program != 0) {
                //3.拼接
                //3.1验证程序对象,只有开发调试时才去验证
                if (BuildConfig.DEBUG) {
                    boolean validateProgramStatus = ShaderHelper.validateProgram(program);
                    Logger.i("validateProgram result:" + validateProgramStatus);
                }
                //3.2 使用OpenGL程序
                GLES20.glUseProgram(program);

                //4.获取uniform和position位置，注意，参数2要和shader源码中定义的变量名一样
                //uniform位置不是事先指定的，一旦程序链接成功，就要查询uniform位置，一个uniform位置在一个程序中是唯一的
                uColorLocation = GLES20.glGetUniformLocation(program, "u_Color");
                aPositionLocation = GLES20.glGetAttribLocation(program, "a_Position");

                //5.关联属性和顶点数据数组，告诉OpenGL去哪找a_Position对应的数据
                vertexData.position(0);//从开头位置读数据
                /**
                 * 告诉opengl去哪读取a_Position属性的数据
                 * 参数1：属性位置。
                 * 参数2：每个属性的数据的计数，或对于这个属性，有多少个分量与每个顶点相关联。
                 * 比如，我们定义矩形，决定使用两个浮点数来表示一个顶点的x坐标和y坐标，这就意味着它需要2个分量，所以参数二传的值为2。
                 * 参数3：数据的类型，我们的数据使用的是浮点数数组。
                 * 参数4：只有使用整形数据的时候，这个参数才有意义，暂时忽略。
                 * 参数5：stride，只有当一个数组存储多于一个属性时，这个参数才有意义，我们目前只有一个属性，因此传了0进去，以忽略此参数。
                 * 参数6：告诉OpenGL去哪里读取数据。要注意，它会从缓冲区的当前位置开始读取数据，因此，一定要记得加上vertexData.position(0);
                 *
                 */
                GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData);

                //6.使顶点数组可用
                GLES20.glEnableVertexAttribArray(aPositionLocation);

            }
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //设置窗口大小，前两个参数是x和y的偏移量，后两个参数是surface宽高
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //清空屏幕所有颜色，并用之前设置的glClearColor设置的颜色填充屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //在屏幕上绘制

        //1.绘制桌子。
        //指定颜色。更新着色器代码的uniform值，与属性不同，uniform的分量没有默认值。
        //由于我们定义的uniform是vec4，有四个分量，因此需要传递4个分量的默认值。
        //前3个参数表示rgb的亮度值(范围取值0~1f)，参数4是alpha值
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        //参数1：告诉opengl要绘制三角形。要绘制三角形，至少要传递三个顶点。
        //参数2：告诉opengl从顶点数组的开头处开始读顶点。
        //参数3：告诉opengl要读取6个顶点。因为每个三角形有3个顶点，我们要用两个三角形来绘制矩形。
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        //2.绘制桌子中心的横向分割线
        //设置uniform为红色
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        //参数和上面一样，要求绘制直线，一条线段有两个顶点；参数2从顶点数据的第6个位置开始读数据，参数3表示读取两个顶点数据。
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        //4.绘制木追的两个点，注意要在顶点的shader源码中指定gl_PointSize，不然绘制点的时候看不到任何东西
        //点1 蓝色
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        //点2 蓝色
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

    }
}
