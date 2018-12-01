package com.yjt.opengles.utils;

import android.opengl.Matrix;

/**
 * Filter的坐标变换矩阵工具类
 *
 * @author yangjiantong
 * @date 2018/11/30
 */
public class MatrixUtils {

    public static final int TYPE_FITXY = 0;
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITEND = 4;


    /**
     * 获取vertex shader顶点坐标的变化矩阵
     * gl_Position  = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_Position;
     * 这个函数得到的是 u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix 并将结果赋值给参数matrix
     *
     * @param matrix     将要赋值的矩阵
     * @param type       变化的类型：取值为上面定义的常量 TYPE_CENTERCROP、TYPE_CENTERINSIDE
     * @param imgWidth   图片或视频的宽
     * @param imgHeight  图片或视频的高
     * @param viewWidth  显示view的宽
     * @param viewHeight 显示view的高
     */
    public static void getMatrix(float[] matrix, int type, int imgWidth, int imgHeight, int viewWidth,
                                 int viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (type == TYPE_FITXY) {
                /**
                 * Computes an orthographic projection matrix.
                 *
                 * @param m returns the result 正交投影矩阵
                 * @param mOffset 偏移量，默认为 0 ,不偏移
                 * @param left 左平面距离
                 * @param right 右平面距离
                 * @param bottom 下平面距离
                 * @param top 上平面距离
                 * @param near 近平面距离
                 * @param far 远平面距离
                 */
                Matrix.orthoM(projection, 0, -1, 1, -1, 1, 1, 3);
            }

            float viewAspectRatio = (float) viewWidth / viewHeight;
            float imgAspectRatio = (float) imgWidth / imgHeight;
            if (imgAspectRatio > viewAspectRatio) {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -viewAspectRatio / imgAspectRatio, viewAspectRatio / imgAspectRatio, -1, 1, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -1, 1, -imgAspectRatio / viewAspectRatio, imgAspectRatio / viewAspectRatio, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 1, 1 - 2 * imgAspectRatio / viewAspectRatio, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, -1, 1, -1, 2 * imgAspectRatio / viewAspectRatio - 1, 1, 3);
                        break;
                    default:
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -1, 1, -imgAspectRatio / viewAspectRatio, imgAspectRatio / viewAspectRatio, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -viewAspectRatio / imgAspectRatio, viewAspectRatio / imgAspectRatio, -1, 1, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 2 * viewAspectRatio / imgAspectRatio - 1, -1, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, 1 - 2 * viewAspectRatio / imgAspectRatio, 1, -1, 1, 1, 3);
                        break;
                    default:
                        break;
                }
            }

            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }


    public static float[] rotate(float[] m, float angle) {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public static float[] scale(float[] m, float x, float y) {
        Matrix.scaleM(m, 0, x, y, 1);
        return m;
    }

    public static float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }

}
