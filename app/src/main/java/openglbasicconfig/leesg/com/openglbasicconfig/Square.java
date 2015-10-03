package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-23.
 */
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by LeeSG on 2015-08-14.
 */
public class Square {
    // 기본적인 이미지 처리를 위한 변수
    protected static int mProgramImage;
    protected int mPositionHandle;
    protected int mTexCoordLoc;
    protected int mtrxhandle;
    protected int mSamplerLoc;
    protected final int COORDS_PER_VERTEX = 3;
    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mTexCoordBuffer;
    protected ShortBuffer mIndexBuffer;
    protected int indexCount;
    // 매트릭스 변환을 위한 변수
    protected final float[] mMVPMatrix = new float[16];
    protected float[] mRotationMatrix = new float[16];
    protected float[] mScaleMatrix = new float[16];
    protected float[] mTranslationMatrix = new float[16];
    // 비트맵 이미지 핸들 관리(여러건 처리를 위해 배열로 정의)
    protected int mHandleBitmap;
    protected int mBitmapCount = 0;
    protected Bitmap mBitmap[];
    //현재의 위치정보
    protected float mPosX = 0;
    protected float mPosY = 0;
    // 이미지의 가로 세로 설정
    protected float mWidth = 0;
    protected float mHeight = 0;
    //이미지의 기울기 설정
    protected int mAngle = 0;
    //이미지의 확대, 축소 설정
    protected float mScaleX = 1.0f;
    protected float mScaleY = 1.0f;
    //이미지의 활성화 여부
    protected boolean mIsActive = false;

    public Square(int programImage) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "texcoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
    }
    // 이미지 핸들, 가로, 세로 값을 받아와 설정
    public void setBitmap(int handle, int width, int height) {
        mBitmapCount = 1;
        this.mWidth = width;
        this.mHeight = height;
        setupBuffer();
        mHandleBitmap = handle;
    }
    // 이미지 가로, 세로 반환
    //객체의 높이를 반환
    public float getHeight() {return this.mHeight;}
    //객체의 폭을 반환
    public float getWidth(){return this.mWidth;}
    // 이미지 위치 설정
    public void setPos(float posX, float posY) {
        this.mPosX = posX;
        this.mPosY = posY;
    }
    //기울기를 설정함
    public void setangle(int angle) {
        this.mAngle = angle;
    }
    //객체의 활성화여부를 설정함
    public void setIsActive(boolean isActive) {
        this.mIsActive = isActive;
    }
    //객체의 활성화 여부 반환
    public boolean getIsActive() {
        return this.mIsActive;
    }
    // 버퍼 설정
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
                0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f
        };
        mTexCoordBuffer.put(mUvs);
        mVertexBuffer.position(0);
        mTexCoordBuffer.position(0);
        mIndexBuffer.position(0);
    }
    //현재 객체가 선택되었는지를 반환함
    public boolean isSelected(int x, int y) {
        boolean isSelected = false;
        if(mIsActive == true) {
            if((x>=mPosX - mWidth/2 && x<= mPosX + mWidth /2 ) && (y >= mPosY - mHeight /2 && y<= mPosY + mHeight / 2)) {
                isSelected = true;
            }
        }
        return isSelected;
    }
    //그리기
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

