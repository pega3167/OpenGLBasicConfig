package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by LeeSG on 2015-09-05.
 */
public class Light {
    private int mProgramImage;
    //light properties
    private float[] position;
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    //material properties
    private float[] mAmbient;
    private float[] mDiffuse;
    private float[] mSpecular;
    private float shininess;

    public Light(int programImange) {
        mProgramImage = programImange;
        //light 초기화
        this.position = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        this.ambient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        this.diffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        this.specular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        // Material.shininess 초기화
        this.mAmbient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        this.mDiffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        this.mSpecular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        shininess = 0.0f;
    }

    public void setPosition(float x, float y, float z, float w) {
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = z;
        this.position[3] = w;
    }
    public void setAmbient(float x, float y, float z, float w) {
        this.ambient[0] = x;
        this.ambient[1] = y;
        this.ambient[2] = z;
        this.ambient[3] = w;
    }
    public void setDiffuse(float x, float y, float z, float w) {
        this.diffuse[0] = x;
        this.diffuse[1] = y;
        this.diffuse[2] = z;
        this.diffuse[3] = w;
    }
    public void setSpecular(float x, float y, float z, float w) {
        this.specular[0] = x;
        this.specular[1] = y;
        this.specular[2] = z;
        this.specular[3] = w;
    }
    public void setMAmbient(float x, float y, float z, float w) {
        this.mAmbient[0] = x;
        this.mAmbient[1] = y;
        this.mAmbient[2] = z;
        this.mAmbient[3] = w;
    }
    public void setMDiffuse(float x, float y, float z, float w) {
        this.mDiffuse[0] = x;
        this.mDiffuse[1] = y;
        this.mDiffuse[2] = z;
        this.mDiffuse[3] = w;
    }
    public void setMSpecular(float x, float y, float z, float w) {
        this.mSpecular[0] = x;
        this.mSpecular[1] = y;
        this.mSpecular[2] = z;
        this.mSpecular[3] = w;
    }
    public void setShininess(float shininess) { this.shininess = shininess; }
    public void sendLight() {
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "lightPosition"), 1, position, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Ia"), 1, ambient, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Id"), 1, diffuse, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Is"), 1, specular, 0);
    }
    public void sendMaterial() {
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Ka"), 1, mAmbient, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Kd"), 1, mDiffuse, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramImage, "Ks"), 1, mSpecular, 0);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "shininess"), shininess);
    }
}
