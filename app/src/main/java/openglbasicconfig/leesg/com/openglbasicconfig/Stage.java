package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.Matrix;

/**
 * Created by LeeSG on 2015-09-05.
 */
public class Stage {
    public Planet[] planetList;
    public int listSize;
    public int userNum;
    public int turnStartFrame;
    public int currentFrame;
    public int turn;
    //public boolean isAimMode;

    public Stage(int programImage, int listSize, int userNum) {
        this.listSize = listSize;
        this.userNum = userNum;
        turnStartFrame = 0;
        currentFrame = 0;
        turn = 0;
        planetList = new Planet[listSize];
    }

    public void updatePosInList(int currentFrame) {
        float tempMatrix[] = new float[16];
        float mModelMatrix[] = new float[16];
        for( int i = 0 ; i < listSize ; i++) {
            Vector3f pos = new Vector3f();
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, this.planetList[i].getRadius(), this.planetList[i].getRadius(), this.planetList[i].getRadius());
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, currentFrame * this.planetList[i].getRotationSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, this.planetList[i].getOrbitRadius(), 0.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, currentFrame * this.planetList[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            //현재 행성 위치 업데이트
            pos.setXYZ(0, 0, 0);
            pos.multM(mModelMatrix, 1.0f);
            this.planetList[i].setCurrentPos(pos);
        }
    }
}
