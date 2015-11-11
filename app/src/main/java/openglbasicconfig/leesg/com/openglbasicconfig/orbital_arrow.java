package openglbasicconfig.leesg.com.openglbasicconfig;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by user on 2015-10-09.
 */
public class orbital_arrow {
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
    private float radius;

    public orbital_arrow(int programImage, int direction) {
        mProgramImage = programImage;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "position");
        mNormalLoc = GLES20.glGetAttribLocation(mProgramImage, "normal");
        //mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "texcoord");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        //mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
        vertexCount = 6;
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
        ByteBuffer mIndices = ByteBuffer.allocateDirect(vertexCount * 2 * 2/*for opposite side*/);
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
        int j = vertexCount;
        for (int i = vertexCount ; i < vertexCount*2 ; i++) {
            j--;
            indexBuffer.put((short)j);
        }
        normBuffer.position(0);
        //texBuffer.position(0);
        indexBuffer.position(0);
        setupVertexBuffer(direction);
    }

    //public void setBitmap(int bitmapHandle) {mHandleBitmap = bitmapHandle;}

    public void setupVertexBuffer(float direction){//Planet[] planetList, int index) {
        vertexBuffer.clear();
        //int i_temp = 0;
        radius = 1.0f;//planetList[index].getOrbitRadius();
        Vector3f currentPos = new Vector3f(0, 0, 0);
        /*
        for(int i=0;i<100;i++){
            currentPos.x = radius* (float)Math.sin(i * 2.0 * Math.PI / 100.0);
            currentPos.y = 0;
            currentPos.z = radius* (float)Math.cos(i * 2.0 * Math.PI / 100.0);
            double temp1 = planetList[index].getCurrentPos().x-currentPos.x;
            double temp2 = planetList[index].getCurrentPos().z-currentPos.z;
            if(Math.sqrt(temp1 * temp1 + temp2 * temp2) < 0.00001){
                i_temp = i;
                break;
            }
        }*/
        if(direction>0) {
            currentPos.x = radius*0.95f * (float) Math.sin(2 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*0.95f * (float) Math.cos(2 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(0, currentPos.x);
            vertexBuffer.put(1, currentPos.y);
            vertexBuffer.put(2, currentPos.z);
            currentPos.x = radius * (float) Math.sin(3 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius * (float) Math.cos(3 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(3, currentPos.x);
            vertexBuffer.put(4, currentPos.y);
            vertexBuffer.put(5, currentPos.z);
            currentPos.x = radius*1.05f * (float) Math.sin(2 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*1.05f * (float) Math.cos(2 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(6, currentPos.x);
            vertexBuffer.put(7, currentPos.y);
            vertexBuffer.put(8, currentPos.z);

            currentPos.x = radius*0.95f * (float) Math.sin(103 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*0.95f * (float) Math.cos(103 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(9, currentPos.x);
            vertexBuffer.put(10, currentPos.y);
            vertexBuffer.put(11, currentPos.z);
            currentPos.x = radius * (float) Math.sin(104 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius * (float) Math.cos(104 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(12, currentPos.x);
            vertexBuffer.put(13, currentPos.y);
            vertexBuffer.put(14, currentPos.z);
            currentPos.x = radius*1.05f * (float) Math.sin(103 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*1.05f * (float) Math.cos(103 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(15, currentPos.x);
            vertexBuffer.put(16, currentPos.y);
            vertexBuffer.put(17, currentPos.z);
        }
        else{
            currentPos.x = radius*0.95f * (float) Math.sin(3 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*0.95f * (float) Math.cos(3 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(0, currentPos.x);
            vertexBuffer.put(1, currentPos.y);
            vertexBuffer.put(2, currentPos.z);
            currentPos.x = radius * (float) Math.sin(2 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius * (float) Math.cos(2 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(3, currentPos.x);
            vertexBuffer.put(4, currentPos.y);
            vertexBuffer.put(5, currentPos.z);
            currentPos.x = radius*1.05f * (float) Math.sin(3 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*1.05f * (float) Math.cos(3 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(6, currentPos.x);
            vertexBuffer.put(7, currentPos.y);
            vertexBuffer.put(8, currentPos.z);

            currentPos.x = radius*0.95f * (float) Math.sin(104 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*0.95f * (float) Math.cos(104 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(9, currentPos.x);
            vertexBuffer.put(10, currentPos.y);
            vertexBuffer.put(11, currentPos.z);
            currentPos.x = radius * (float) Math.sin(103 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius * (float) Math.cos(103 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(12, currentPos.x);
            vertexBuffer.put(13, currentPos.y);
            vertexBuffer.put(14, currentPos.z);
            currentPos.x = radius*1.05f * (float) Math.sin(104 * 2.0 * Math.PI / 105.0);
            currentPos.y = 0;
            currentPos.z = radius*1.05f * (float) Math.cos(104 * 2.0 * Math.PI / 105.0);
            vertexBuffer.put(15, currentPos.x);
            vertexBuffer.put(16, currentPos.y);
            vertexBuffer.put(17, currentPos.z);
        }
/*
        for( int i = 0 ; i < 95 ; i++ ) {
            currentPos.x = radius* (float)Math.sin((i+i_temp + 3) * 2.0 * Math.PI / 100.0);
            currentPos.y = 0;
            currentPos.z = radius* (float)Math.cos((i+i_temp + 3) * 2.0 * Math.PI / 100.0);
            vertexBuffer.put(3*i, currentPos.x);
            vertexBuffer.put(3*i+1, currentPos.y);
            vertexBuffer.put(3 * i + 2, currentPos.z);
        }*/
        vertexCount = 6;
        vertexBuffer.position(0);
    }

    public void draw(float[] m) {
        //GLES20.glLineWidth(5);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_AIM);
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
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexCount * 2, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalLoc);
        //GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}
