package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;
import android.util.Log;

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
    private int mTexCoordLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    private int mHandleBitmap;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer normBuffer;
    private FloatBuffer texBuffer;
    private int vertexCount;
    // 초기값
    private Vector3f shootPos;
    private Vector3f shootVelocity;

    public Aim(int programImage) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "vPosition");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "a_texCoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "s_texture");
        shootPos = new Vector3f();
        shootVelocity = new Vector3f();
        vertexCount = 1000;
        ByteBuffer mVertices = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
        mVertices.order(ByteOrder.nativeOrder());
        vertexBuffer = mVertices.asFloatBuffer();
        ByteBuffer mNormals = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
        mNormals.order(ByteOrder.nativeOrder());
        normBuffer = mNormals.asFloatBuffer();
        ByteBuffer mTexCoords = ByteBuffer.allocateDirect(vertexCount * 2 * 4);
        mTexCoords.order(ByteOrder.nativeOrder());
        texBuffer = mTexCoords.asFloatBuffer();
        ByteBuffer mIndices = ByteBuffer.allocateDirect(vertexCount * 2);
        mIndices.order(ByteOrder.nativeOrder());
        indexBuffer = mIndices.asShortBuffer();
        for (int i = 0 ; i < vertexCount ; i++) {
            indexBuffer.put((short)i);
            normBuffer.put(0.0f);
            normBuffer.put(1.0f);
            normBuffer.put(0.0f);
            texBuffer.put(0.0f);
            texBuffer.put(0.0f);
        }
        normBuffer.position(0);
        texBuffer.position(0);
        indexBuffer.position(0);
    }

    public void setBitmap(int bitmapHandle) {
        mHandleBitmap = bitmapHandle;
    }

    public void setupVertexBuffer(Planet[] planetList, int listSize) {
        vertexBuffer.clear();
        Vector3f currentPos = new Vector3f(shootPos.x, shootPos.y, shootPos.z);
        for( int i = 0 ; i < vertexCount ; i++ ) {
            currentPos.x += shootVelocity.x;
            currentPos.y += shootVelocity.y;
            currentPos.z += shootVelocity.z;
            //Log.e("","" + currentPos.x + ", "+ currentPos.y + ", "+ currentPos.z);
            vertexBuffer.put(3*i, currentPos.x);
            vertexBuffer.put(3*i+1, currentPos.y);
            vertexBuffer.put(3*i+2, currentPos.z);
            updateVelocity(planetList, listSize, currentPos);
        }
        vertexBuffer.position(0);
    }

    private void updateVelocity(Planet[] planetList, int listSize, Vector3f currentPos) {
        float r;
        float a;
        Vector3f direction = new Vector3f();
        for(int i = 0 ; i < listSize ; i++) {
            r = currentPos.distance( planetList[i].getCurrentPos() );
            if( r < planetList[i].getGravityField() ) {
                a = planetList[i].getGravity() / r*r;
                direction.x = (planetList[i].getCurrentPos().x - currentPos.x) / r * a;
                direction.y = (planetList[i].getCurrentPos().y - currentPos.y) / r * a;
                direction.z = (planetList[i].getCurrentPos().z - currentPos.z) / r * a;
                shootVelocity.x += direction.x;
                shootVelocity.y += direction.y;
                shootVelocity.z += direction.z;
            }
        }
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

    public void draw(float[] m) {
        //GLES20.glLineWidth(5);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glDisable(GLES20.GL_BLEND);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, vertexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}