package com.yjt.opengles.utils;

import android.opengl.GLES20;

/**
 * Shader辅助类
 * AUTHOR: yangjiantong
 * DATE: 2016/11/27
 */
public class ShaderHelper {

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译着色器
     *
     * @param type       着色器类型
     * @param shaderCode 着色器源码
     * @return
     */
    private static int compileShader(int type, String shaderCode) {
        //1.创建着色器对象，返回0表示创建失败，类似java的null
        final int shaderObjectId = GLES20.glCreateShader(type);
        //检查对象是否可用，这种检测方式在opengl中是通用的
        if (shaderObjectId == 0) {
            //could not create shader
            Logger.e("create sharder failed");
            return 0;
        }
        //2.上传和编译着色器源码，并把shaderObjectId和shaderCode建立关联
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        //3.编译着色器
        GLES20.glCompileShader(shaderObjectId);
        //4.取出编译状态
        final int[] compileStatus = new int[1];
        //最后一个参数0表示把结果写入到compileStatus第0个元素，这种写法在opengl中也是通用的
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        //取出着色器信息日志
        Logger.i("Compile shader result:" + GLES20.glGetShaderInfoLog(shaderObjectId));
        //5.验证编译状态
        if (compileStatus[0] == 0) {
            //if it failed ,delete shader id
            GLES20.glDeleteShader(shaderObjectId);
            Logger.e("compile of shader failed....");
            return 0;

        }
        return shaderObjectId;
    }

    /**
     * 链接着色器
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //1.创建程序对象
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            Logger.e("create program failed!");
            return 0;
        }
        //2.附上着色器
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        //3.链接程序
        GLES20.glLinkProgram(programObjectId);
        //4.取出链接状态
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        //取出程序日志
        Logger.i("link program result:" + GLES20.glGetProgramInfoLog(programObjectId));
        //5.验证链接状态
        if (linkStatus[0] == 0) {
            //if failed,delete program id
            GLES20.glDeleteProgram(programObjectId);
            Logger.e("Link of program failed!");
            return 0;
        }
        return programObjectId;
    }

    /**
     * 验证OpenGL程序的对象
     *
     * @param programObjectId 程序对象id
     * @return
     */
    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Logger.i("validate program result:" + validateStatus[0] + ","
                + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }


}
