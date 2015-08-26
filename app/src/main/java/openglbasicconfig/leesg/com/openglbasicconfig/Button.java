package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by LeeSG on 2015-08-23.
 */
public class Button extends Square{
    MainGLRenderer mMainGLRenderer;
    //생성자
    public Button(int programImage, int programSolidColor, MainGLRenderer mainGLRenderer) {
        super(programImage);
        mMainGLRenderer = mainGLRenderer;
    }
    public boolean isSelected(int x, int y) {
        boolean returnValue = super.isSelected(x,y);
        if(returnValue == true) {
            mMainGLRenderer.mActivity.soundButton();
        }
        return returnValue;
    }
    public void draw(float[] m) {
        //Matrix.setIdentityM(m, 0);
        if(!mIsActive) {return;}
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.scaleM(mScaleMatrix, 0, mScaleX, mScaleY, 1.0f);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
        Matrix.translateM(mTranslationMatrix, 0, mPosX, mPosY, 0.0f);
        //모델 매트릭스 계산 T X R X S
        Matrix.multiplyMM(mMVPMatrix, 0 , mScaleMatrix, 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0 , mRotationMatrix, 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0 , mTranslationMatrix, 0, mMVPMatrix, 0);
        //MVP 계산
        Matrix.multiplyMM(mMVPMatrix, 0, m , 0, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mMVPMatrix, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mHandleBitmap);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}