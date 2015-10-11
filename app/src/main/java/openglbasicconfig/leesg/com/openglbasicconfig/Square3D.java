package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by LeeSG on 2015-10-11.
 */
public class Square3D  {
    private static int mProgramImage;
    private int mPositionHandle;
    private int mTexCoordLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    private int mHandleBitmap;
    private final int COORDS_PER_VERTEX = 3;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoordBuffer;
    private ShortBuffer mIndexBuffer;
    private final int indexCount = 6;
    public Square3D(int programImage, float width, float height) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "texcoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
        setupBuffer(width, height);
    }

    public void setBitmap(int handle) {
        mHandleBitmap = handle;
    }

    public void setupBuffer(float mWidth, float mHeight) {
        int numVertices = 4;
        int numIndices = 6;
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
                0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f
        };
        mTexCoordBuffer.put(mUvs);
        mVertexBuffer.position(0);
        mTexCoordBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(float[] m) {
        //Matrix.setIdentityM(m, 0);
        //GLES20.glLineWidth(10);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap);
        GLES20.glUniform1i(mSamplerLoc, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

}
