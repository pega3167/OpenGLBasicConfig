package openglbasicconfig.leesg.com.openglbasicconfig;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by LeeSG on 2015-08-14.
 */
public class Sphere {
    // 기본적인 이미지 처리를 위한 변수
    private static int mProgramImage;
    private int mPositionHandle;
    private int mNormalLoc;
    private int mTexCoordLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    static final int COORDS_PER_VERTEX = 3;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTexCoordBuffer;
    private ShortBuffer mIndexBuffer;
    private int indexCount;
    // 비트맵 이미지 핸들 관리(여러건 처리를 위해 배열로 정의)
    private int mHandleBitmap;
    private int mBitmapCount = 0;
    private Bitmap mBitmap[];
    // 이미지의 가로 세로 설정
    private float mWidth = 0;
    private float mHeight = 0;
    // 행성 y축 회전값
    private float rotateY = 0;

    public Sphere(int programImage) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "position");
        mNormalLoc = GLES20.glGetAttribLocation(mProgramImage, "normal");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "texcoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
    }

    public static int getmProgramImage() {
        return mProgramImage;
    }

    // 이미지 핸들, 가로, 세로 값을 받아와 설정
    public void setBitmap(int handle, int width, int height, int slices) {
        mBitmapCount = 1;
        this.mWidth = width;
        this.mHeight = height;
        setupBuffer(1.0f, slices, slices);
        mHandleBitmap = handle;
    }
    // 버퍼 설정. 구의 vertexCoord, normalCoord, texCoord, indexOrder 를 계산하여 버퍼에 저장
    public void setupBuffer(float radius, int numSlices, int numParallels) {
        int numVertices = (numSlices + 1) * (numParallels + 1);
        int numIndices = numSlices * numParallels * 6;
        indexCount = numIndices;
        ByteBuffer mVertices = ByteBuffer.allocateDirect(numVertices* 3 * 4);
        mVertices.order(ByteOrder.nativeOrder());
        mVertexBuffer = mVertices.asFloatBuffer();
        ByteBuffer mNormals = ByteBuffer.allocateDirect(numVertices* 3 * 4);
        mNormals.order(ByteOrder.nativeOrder());
        mNormalBuffer = mNormals.asFloatBuffer();
        ByteBuffer mTexCoords = ByteBuffer.allocateDirect(numVertices * 2 * 4);
        mTexCoords.order(ByteOrder.nativeOrder());
        mTexCoordBuffer = mTexCoords.asFloatBuffer();
        ByteBuffer mIndices = ByteBuffer.allocateDirect(numIndices * 2);
        mIndices.order(ByteOrder.nativeOrder());
        mIndexBuffer = mIndices.asShortBuffer();

        int i,j;
        double thetaParallel, thetaSlice;
        double sinThetaParallel, cosThetaParallel, sinThetaSlice, cosThetaSlice;

        for(i = 0 ; i < numParallels + 1 ; i++) {
            thetaParallel = Math.PI / (double) numParallels * i;
            sinThetaParallel = Math.sin(thetaParallel);
            cosThetaParallel = Math.cos(thetaParallel);
            //vertex buffer
            for (j = 0; j < numSlices + 1; j++) {
                int vertex = (i * (numSlices + 1) + j) * 3;
                thetaSlice = 2 * Math.PI / (double) numSlices * j;
                sinThetaSlice = Math.sin(thetaSlice);
                cosThetaSlice = Math.cos(thetaSlice);

                mVertexBuffer.put(vertex + 0, (float) (radius * (-1) * sinThetaParallel * cosThetaSlice));
                mVertexBuffer.put(vertex + 1, (float) (radius * cosThetaParallel));
                mVertexBuffer.put(vertex + 2, (float) (radius * sinThetaParallel * sinThetaSlice));
                mNormalBuffer.put(vertex + 0, (float) (-sinThetaParallel * cosThetaSlice));
                mNormalBuffer.put(vertex + 1, (float) cosThetaParallel);
                mNormalBuffer.put(vertex + 2, (float) (sinThetaParallel * sinThetaSlice));
                int texIndex = (i * (numSlices + 1) + j) * 2;
                mTexCoordBuffer.put(texIndex + 0, j / (float) (numSlices));
                mTexCoordBuffer.put(texIndex + 1, (numParallels - i) / (float) (numParallels));
            }
        }
        //index buffer
        int index = 0;
        for (i = 0; i < numParallels; i++) {
            for (j = 0; j < numSlices; j++) {
                mIndexBuffer.put(index + 0, (short) (i * (numSlices + 1) + j));
                mIndexBuffer.put(index + 1, (short) ((i + 1) * (numSlices + 1) + j));
                mIndexBuffer.put(index + 2, (short) ((i + 1) * (numSlices + 1) + j + 1));
                mIndexBuffer.put(index + 3, (short) (i * (numSlices + 1) + j));
                mIndexBuffer.put(index + 4, (short) ((i + 1) * (numSlices + 1) + j + 1));
                mIndexBuffer.put(index + 5, (short) (i * (numSlices + 1) + j + 1));
                index = index + 6;
            }
        }
        mVertexBuffer.position(0);
        mNormalBuffer.position(0);
        mTexCoordBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void flip() {
        short[] tempBuffer = new short[indexCount];
        for(int i = 0 ; i < indexCount ; i++) {
            tempBuffer[i] = mIndexBuffer.get(indexCount - i);
            Log.e("", "" + tempBuffer[i]);
        }
        for(int i = 0 ; i < indexCount ; i++) {
            mIndexBuffer.put(i, tempBuffer[i]);
        }
        //mIndexBuffer.position(0);
    }

    public void setRotateY(float rotateY) { this.rotateY = rotateY; }
    public float getRotateY() { return rotateY; }
    public void addRotateY(float add) { this.rotateY += add; }

    //그리기
    public void draw(float[] m) {
        //Matrix.setIdentityM(m, 0);
        //GLES20.glLineWidth(10);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mNormalLoc);
        GLES20.glVertexAttribPointer(mNormalLoc, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer);
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
