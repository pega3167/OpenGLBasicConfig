package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by LeeSG on 2015-09-29.
 */
public class RTTQuad extends Square {
    public RTTQuad(int programImage) {
        super(programImage);
    }

    public void drawWithoutTex(float[] m) {
        //Matrix.setIdentityM(m, 0);
        if (!mIsActive) {
            return;
        }
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.scaleM(mScaleMatrix, 0, mScaleX, mScaleY, 1.0f);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
        Matrix.translateM(mTranslationMatrix, 0, mPosX, mPosY, 0.0f);
        //모델 매트릭스 계산 T X R X S
        Matrix.multiplyMM(mMVPMatrix, 0, mScaleMatrix, 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mTranslationMatrix, 0, mMVPMatrix, 0);
        //MVP 계산
        Matrix.multiplyMM(mMVPMatrix, 0, m, 0, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mMVPMatrix, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //이미지 핸들을 바인드 한다. 수정중
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        setupBuffer();
    }

    public void setupBuffer() {
        int numVertices = 4;
        int numIndices = 6;
        indexCount = numIndices;
        ByteBuffer mVertices = ByteBuffer.allocateDirect(numVertices * 3 * 4);
        mVertices.order(ByteOrder.nativeOrder());
        mVertexBuffer = mVertices.asFloatBuffer();
        ByteBuffer mTexCoords = ByteBuffer.allocateDirect(numVertices * 2 * 4);
        mTexCoords.order(ByteOrder.nativeOrder());
        mTexCoordBuffer = mTexCoords.asFloatBuffer();
        ByteBuffer mIndices = ByteBuffer.allocateDirect(numIndices * 2);
        mIndices.order(ByteOrder.nativeOrder());
        mIndexBuffer = mIndices.asShortBuffer();
        float [] vertices = new float[] {
                mWidth / (-2), mHeight / 2, 0.0f,
                mWidth / (-2), mHeight / (-2), 0.0f,
                mWidth / 2, mHeight / (-2), 0.0f,
                mWidth / 2, mHeight / 2, 0.0f
        };
        mVertexBuffer.put(vertices);
        short[] indices = new short[] { 0, 1, 2, 0, 2, 3};
        mIndexBuffer.put(indices);
        float[] mUvs = new float[] {
                0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f
        };
        mTexCoordBuffer.put(mUvs);
        mVertexBuffer.position(0);
        mTexCoordBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void drawElements()
    {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}
