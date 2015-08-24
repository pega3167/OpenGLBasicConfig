package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.Matrix;

/**
 * Created by LeeSG on 2015-08-20.
 */
public class Camera {
    public float[] viewMatrix;       //3D camera
    public float[] twoDViewMatrix;  //2D camera
    public float[] eye;
    public float[] at;
    public float[] up;

    public float[] projectionMatrix;        //3D camera
    public float[] orthoProjectionMatrix;  //2D camera
    public float fovy;
    public float aspectRatio;
    public float dNear;
    public float dFar;

    public Camera()
    {
        viewMatrix = new float[16];
        twoDViewMatrix = new float[16];
        Matrix.setIdentityM(viewMatrix , 0);
        Matrix.setIdentityM(twoDViewMatrix, 0);
        eye = new float[3];
        at = new float[3];
        up = new float[3];
        projectionMatrix = new float[16];
        orthoProjectionMatrix = new float[16];
        Matrix.setIdentityM(viewMatrix , 0);
        Matrix.setIdentityM(orthoProjectionMatrix, 0);
    }

    public void setEye(float eyeX, float eyeY, float eyeZ) {
        eye[0] = eyeX;
        eye[1] = eyeY;
        eye[2] = eyeZ;
    }

    public void setAt(float atX, float atY, float atZ) {
        at[0] = atX;
        at[1] = atY;
        at[2] = atZ;
    }

    public void setUp(float upX, float upY, float upZ) {
        up[0] = upX;
        up[1] = upY;
        up[2] = upZ;
    }

    public void setViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], at[0], at[1], at[2], up[0], up[1], up[2]);
    }
    public void setTwoDViewMatrix() {
        Matrix.setLookAtM(twoDViewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
    }
    public void setProjectionMatrix() {
        Matrix.perspectiveM(projectionMatrix, 0, fovy, aspectRatio, dNear, dFar);
    }
    public void setOrthoProjectionMatrix(int virtualX, int virtualY) {
        Matrix.orthoM(orthoProjectionMatrix, 0, 0, virtualX, 0, virtualY, 0, 50);
    }

    public void setViewBox(float inputFovy, float inputAspectRatio, float inputDNear, float inputDFar) {
        fovy = inputFovy;
        aspectRatio = inputAspectRatio;
        dNear = inputDNear;
        dFar = inputDFar;
    }
}
