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
    public void setZoom(float zoom){
        /*
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, (float)Math.exp(zoom), (float)Math.exp(zoom), (float)Math.exp(zoom));
        Matrix.multiplyMM(viewMatrix, 0, tempMatrix, 0, viewMatrix, 0);
        //setViewMatrix();
        */

        eye[0] = at[0] + (eye[0]-at[0])*(float)Math.exp((float)zoom);
        eye[1] = at[1] + (eye[1]-at[1])*(float)Math.exp((float)zoom);
        eye[2] = at[2] + (eye[2]-at[2])*(float)Math.exp((float)zoom);
        setViewMatrix();

        /*
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix,0);
        Matrix.scaleM(tempMatrix, 0, (float) Math.exp(-zoom), (float) Math.exp(-zoom), (float) Math.exp(-zoom));
        Matrix.multiplyMM(viewMatrix,0,viewMatrix,0,tempMatrix,0);
        */
    }
    public void setAtMove(float x, float y, float z){
        Vector3f temp = new Vector3f(eye[0]-at[0],eye[1]-at[1],eye[2]-at[2]);
        at[0] = x;
        at[1] = y;
        at[2] = z;
        eye[0] = at[0] + temp.x;
        eye[1] = at[1] + temp.y;
        eye[2] = at[2] + temp.z;
        setViewMatrix();
    }
    public void setMove(float x, float y){
        Vector3f a1 = new Vector3f(eye[0]-at[0],eye[1]-at[1],eye[2]-at[2]);
        Vector3f a2 = new Vector3f(up[0],up[1],up[2]);
        Vector3f a3 = a1.Cross(a2);
        a3.y = 0;
        a3.x = a3.x/a3.length();
        a3.z = a3.z/a3.length();
        at[0] += y * a3.x;
        at[2] += y * a3.z;
        eye[0] += y * a3.x;
        eye[2] += y * a3.z;

        a2 = new Vector3f(up[0]-a1.x,up[1]-a1.y,up[2]-a1.z);
        a2.y = 0;
        a2.x = a2.x/a2.length();
        a2.z = a2.z/a2.length();
        at[0] += x * a2.x;
        at[2] += x * a2.z;
        eye[0] += x * a2.x;
        eye[2] += x * a2.z;
        /*
        float[] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix,0);
        Matrix.translateM(tempMatrix, 0, -(x * a2.x + y * a3.x), 0, -(x * a2.z + y * a3.z));
        Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, tempMatrix, 0);
        */
        setViewMatrix();
    }

    public void setRotateX(float a){
        Vector3f a1 = new Vector3f(eye[0]-at[0],eye[1]-at[1],eye[2]-at[2]);
        Vector3f a2 = new Vector3f(up[0],up[1],up[2]);
        Vector3f a3 = a1.Cross(a2);
        float[] tempMatrix = new float[16];
        float[] tempVec = new float[4];
        float[] tempVec2 = new float[4];
        tempVec[0] = eye[0]-at[0];
        tempVec[1] = eye[1]-at[1];
        tempVec[2] = eye[2]-at[2];
        tempVec[3] = 1.0f;
        tempVec2[0] = up[0];
        tempVec2[1] = up[1];
        tempVec2[2] = up[2];
        tempVec2[3] = 1.0f;
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, a, a3.x, a3.y, a3.z);
        Matrix.multiplyMV(tempVec, 0, tempMatrix, 0, tempVec, 0);
        Matrix.multiplyMV(tempVec2, 0, tempMatrix, 0, tempVec2, 0);
        eye[0] = tempVec[0]+at[0];
        eye[1] = tempVec[1]+at[1];
        eye[2] = tempVec[2]+at[2];
        up[0] = tempVec2[0];
        up[1] = tempVec2[1];
        up[2] = tempVec2[2];
        //Matrix.multiplyMM(viewMatrix, 0, tempMatrix, 0, viewMatrix, 0);
        setViewMatrix();
    }

    public float lenghToEye() {
        float x = eye[0]-at[0];
        float y = eye[1]-at[1];
        float z = eye[2]-at[2];
        return (float)(Math.sqrt((double)(x*x + y*y + z*z)));
    }

    public void setRotateY(float a){
        /*
        float[] tempMatrix = new float[16];
        float[] tempMatrix2 = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(tempMatrix2, 0);
        Matrix.translateM(tempMatrix, 0, -at[0], -at[1], -at[2]);
        Matrix.rotateM(tempMatrix2, 0, a, 0.f, 1.f, 0.f);
        Matrix.multiplyMM(tempMatrix2, 0, tempMatrix2, 0, tempMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, at[0], at[1], at[2]);
        Matrix.multiplyMM(tempMatrix2, 0, tempMatrix2, 0, tempMatrix, 0);
        Matrix.multiplyMM(viewMatrix,0,viewMatrix,0,tempMatrix2,0);
        */
        float[] tempMatrix = new float[16];
        float[] tempVec = new float[4];
        float[] tempVec2 = new float[4];
        tempVec[0] = eye[0]-at[0];
        tempVec[1] = eye[1]-at[1];
        tempVec[2] = eye[2]-at[2];
        tempVec[3] = 1.0f;
        tempVec2[0] = up[0];
        tempVec2[1] = up[1];
        tempVec2[2] = up[2];
        tempVec2[3] = 1.0f;
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setRotateM(tempMatrix, 0, a, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMV(tempVec, 0, tempMatrix, 0, tempVec, 0);
        Matrix.multiplyMV(tempVec2,0,tempMatrix,0,tempVec2,0);
        eye[0] = tempVec[0]+at[0];
        eye[1] = tempVec[1]+at[1];
        eye[2] = tempVec[2]+at[2];
        up[0] = tempVec2[0];
        up[1] = tempVec2[1];
        up[2] = tempVec2[2];
        //Matrix.multiplyMM(viewMatrix, 0, tempMatrix, 0, viewMatrix, 0);
        setViewMatrix();
    }
    public boolean checkX(float a){
        Vector3f a1 = new Vector3f(eye[0]-at[0],eye[1]-at[1],eye[2]-at[2]);
        a1.normalize();
        float temp = a1.dot(new Vector3f(0f,1f,0f));
        if(temp>0){
            if(Math.acos(temp)-(a*Math.PI/180)>0.01)
                return true;
            else
                return false;
        }
        else{
            if(Math.acos(temp)-(a*Math.PI/180)<3.13)
                return true;
            else
                return false;
        }
    }
}

