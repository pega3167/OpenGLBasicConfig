package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by LeeSG on 2015-09-03.
 */
public class Aim {
    // 기본적인 변수
    private static int mProgramImage;
    private int mPositionHandle;
    private int mNormalLoc;
    //private int mTexCoordLoc;
    private int mtrxhandle;
    //private int mSamplerLoc;
    //private int mHandleBitmap;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer normBuffer;
    //private FloatBuffer texBuffer;
    private int vertexCount;
    // 초기값
    private Vector3f shootPos;
    private Vector3f shootVelocity;
    // 버퍼 업데이트를 위한 값
    private Vector3f currentVelocity;
    private boolean isAimed;

    public Aim(int programImage) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "position");
        mNormalLoc = GLES20.glGetAttribLocation(mProgramImage, "normal");
        //mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "texcoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        //mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
        shootPos = new Vector3f();
        shootVelocity = new Vector3f();
        currentVelocity = new Vector3f();
        isAimed = false;
        vertexCount = ConstMgr.MAX_AIM_VERTEXCOUNT;
        ByteBuffer mVertices = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
        mVertices.order(ByteOrder.nativeOrder());
        vertexBuffer = mVertices.asFloatBuffer();
        ByteBuffer mNormals = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
        mNormals.order(ByteOrder.nativeOrder());
        normBuffer = mNormals.asFloatBuffer();
        /*
        ByteBuffer mTexCoords = ByteBuffer.allocateDirect(vertexCount * 2 * 4);
        mTexCoords.order(ByteOrder.nativeOrder());
        texBuffer = mTexCoords.asFloatBuffer();
        */
        ByteBuffer mIndices = ByteBuffer.allocateDirect(vertexCount * 2);
        mIndices.order(ByteOrder.nativeOrder());
        indexBuffer = mIndices.asShortBuffer();
        for (int i = 0 ; i < vertexCount ; i++) {
            indexBuffer.put((short)i);
            normBuffer.put(0.0f);
            normBuffer.put(1.0f);
            normBuffer.put(0.0f);
            //texBuffer.put(0.0f);
            //texBuffer.put(0.0f);
        }
        normBuffer.position(0);
        //texBuffer.position(0);
        indexBuffer.position(0);
    }

    //public void setBitmap(int bitmapHandle) {mHandleBitmap = bitmapHandle;}

    public void setupVertexBuffer(Planet[] planetList, int listSize) {
        vertexBuffer.clear();
        Vector3f currentPos = new Vector3f(shootPos.x, shootPos.y, shootPos.z);
        currentVelocity.x = shootVelocity.x;
        currentVelocity.y = shootVelocity.y;
        currentVelocity.z = shootVelocity.z;
        vertexBuffer.put(0, currentPos.x);
        vertexBuffer.put(1, currentPos.y);
        vertexBuffer.put(2, currentPos.z);
        for( int i = 1 ; i < ConstMgr.MAX_AIM_VERTEXCOUNT ; i++ ) {
            currentPos.x += currentVelocity.x;
            currentPos.y += currentVelocity.y;
            currentPos.z += currentVelocity.z;
            vertexBuffer.put(3*i, currentPos.x);
            vertexBuffer.put(3*i+1, currentPos.y);
            vertexBuffer.put(3 * i + 2, currentPos.z);
            if(updateVelocity(planetList, listSize, currentPos)) {
                vertexCount = i+1;
                vertexBuffer.position(0);
                return;
            }
        }
        vertexCount = ConstMgr.MAX_AIM_VERTEXCOUNT;
        vertexBuffer.position(0);
    }
    private boolean updateVelocity(Planet[] planetList, int listSize, Vector3f currentPos) {
        float r;
        float a;
        Vector3f direction = new Vector3f();
        for(int i = 0 ; i < listSize ; i++) {
            r = currentPos.distance( planetList[i].getCurrentPos() );
            if(r < planetList[i].getRadius()) {
                return true;
            }
            else if( r < planetList[i].getGravityField() ) {
                a = planetList[i].getGravity() / r*r;
                direction.x = (planetList[i].getCurrentPos().x - currentPos.x) / r * a;
                direction.y = (planetList[i].getCurrentPos().y - currentPos.y) / r * a;
                direction.z = (planetList[i].getCurrentPos().z - currentPos.z) / r * a;
                currentVelocity.x += direction.x;
                currentVelocity.y += direction.y;
                currentVelocity.z += direction.z;
            }
        }
        return false;
    }

    public void setIsAimed(boolean isAimed) {
        this.isAimed = isAimed;
    }
    public void setShootVelocity(Vector3f shootVelocity) {
        this.shootVelocity = shootVelocity;
    }
    public void setShootVelocity(float x, float y, float z) {
        this.shootVelocity.x = x;
        this.shootVelocity.y = y;
        this.shootVelocity.z = z;
    }
    public void setShootPos(Vector3f shootPos) {
        this.shootPos = shootPos;
    }
    public void setShootPos(float x, float y, float z) {
        this.shootPos.x = x;
        this.shootPos.y = y;
        this.shootPos.z = z;
    }

    public Vector3f getShootVelocity() {
        return shootVelocity;
    }
    public Vector3f getShootPos() {
        return shootPos;
    }
    public boolean getIsAimed() { return this.isAimed; }

    public void draw(float[] m) {
        //GLES20.glLineWidth(5);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bAim"), 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mNormalLoc);
        GLES20.glVertexAttribPointer(mNormalLoc, 3, GLES20.GL_FLOAT, false, 0, normBuffer);
        //GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        //GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glDisable(GLES20.GL_BLEND);
        //이미지 핸들을 바인드 한다. 수정중
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, vertexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalLoc);
        //GLES20.glDisableVertexAttribArray(mTexCoordLoc);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bAim"), 0);
    }
}