package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.Vector;

/**
 * Created by LeeSG on 2015-09-14.
 */
public class ParticleSystem {
    public Vector<Emitter> emitterList;
    private int positionLoc;
    private int velocityLoc;
    private int colorLoc;
    private int birthFrameLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    private int mHandleBitmap;
    public FloatBuffer posBuffer;
    public FloatBuffer velocityBuffer;
    public FloatBuffer colorBuffer;
    public FloatBuffer birthFrameBuffer;
    public int pointCount;

    public ParticleSystem (int mProgramImage) {
        positionLoc = GLES20.glGetAttribLocation(mProgramImage, "position");
        velocityLoc = GLES20.glGetAttribLocation(mProgramImage, "normal");
        colorLoc = GLES20.glGetAttribLocation(mProgramImage, "color");
        birthFrameLoc = GLES20.glGetAttribLocation(mProgramImage, "birthFrame");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
        emitterList = new Vector<Emitter>();
        initBuffer();
    }
    public void setBitmap(int handle) {
        mHandleBitmap = handle;
    }

    public class Emitter {
        public Vector3f pos;
        public Vector3f velocity;
        public boolean isActive;
        //생성자
        public Emitter() {
            this.pos = new Vector3f();
            this.velocity = new Vector3f();
            this.isActive = false;
        }
        public Emitter(Vector3f pos , Vector3f velocity) {
            this.pos = new Vector3f(pos.x, pos.y, pos.z);
            this.velocity = new Vector3f(velocity.x, velocity.y, velocity.z);
            this.isActive = false;
        }
    }

    public void clearEmitterList() { emitterList.removeAllElements(); }
    public void addEmitter(Vector3f pos, Vector3f velocity) {
        Emitter emitter = new Emitter(pos, velocity);
        emitterList.add(emitter);
    }
    public int addEmitter() {
        Vector3f temp = new Vector3f();
        Emitter emitter = new Emitter(temp, temp);
        emitterList.add(emitter);
        return emitterList.size()-1;
    }
    public void addParticle(int frame, Vector3f color) {
        Random random = new Random();
        //random.setSeed(System.currentTimeMillis());
        for( int i = 0 ; i < emitterList.size() ; i++) {
            if(emitterList.elementAt(i).isActive) {
                int pc3 = 3*pointCount;
                posBuffer.put(pc3,emitterList.elementAt(i).pos.x);
                posBuffer.put(pc3 + 1, emitterList.elementAt(i).pos.y);
                posBuffer.put(pc3 + 2, emitterList.elementAt(i).pos.z);
                colorBuffer.put(pc3, color.x);
                colorBuffer.put(pc3 + 1, color.y);
                colorBuffer.put(pc3 + 2, color.z);
                velocityBuffer.put(pc3, random.nextFloat() - 0.5f);
                velocityBuffer.put(pc3 + 1, random.nextFloat() - 0.5f);
                velocityBuffer.put(pc3 + 2, random.nextFloat() - 0.5f);
                birthFrameBuffer.put(pointCount, (float)frame);
                pointCount++;
                if(pointCount == 10000) {
                    float[] tempBuf = new float[15000];
                    float[] tempBuf2 = new float[5000];
                    posBuffer.position(15000);
                    posBuffer.get(tempBuf);
                    posBuffer.position(0);
                    posBuffer.put(tempBuf);
                    colorBuffer.position(15000);
                    colorBuffer.get(tempBuf);
                    colorBuffer.position(0);
                    colorBuffer.put(tempBuf);
                    velocityBuffer.position(15000);
                    velocityBuffer.get(tempBuf);
                    velocityBuffer.position(0);
                    velocityBuffer.put(tempBuf);
                    birthFrameBuffer.position(5000);
                    birthFrameBuffer.get(tempBuf2);
                    birthFrameBuffer.position(0);
                    birthFrameBuffer.put(tempBuf2);
                    pointCount = 5000;
                }
            }
        }
        posBuffer.position(0);
        colorBuffer.position(0);
        velocityBuffer.position(0);
        birthFrameBuffer.position(0);
    }

    public void deactivate() {
        for(int i = 0 ; i < emitterList.size() ; i++) {
            emitterList.elementAt(i).isActive = false;
        }
        clearBuffer();
    }
    public void activate() {
        for(int i = 0 ; i < emitterList.size() ; i++) {
            emitterList.elementAt(i).isActive = true;
        }
    }
    public void setEmitterPos(int index, Vector3f pos) {
        emitterList.elementAt(index).pos.copy(pos);
    }

    public void initBuffer() {
        ByteBuffer mPositions = (ByteBuffer.allocateDirect(10000 * 3 * 4)).order(ByteOrder.nativeOrder());
        posBuffer = mPositions.asFloatBuffer();
        ByteBuffer mColors = (ByteBuffer.allocateDirect(10000 * 3 * 4)).order(ByteOrder.nativeOrder());
        colorBuffer = mColors.asFloatBuffer();
        ByteBuffer mVelocities = (ByteBuffer.allocateDirect(10000 * 3 * 4)).order(ByteOrder.nativeOrder());
        velocityBuffer = mVelocities.asFloatBuffer();
        ByteBuffer mBFrames = (ByteBuffer.allocateDirect(10000 * 2 * 4)).order(ByteOrder.nativeOrder());
        birthFrameBuffer = mBFrames.asFloatBuffer();
        pointCount = 0;
    }
    public void clearBuffer() {
        posBuffer.clear();
        colorBuffer.clear();
        velocityBuffer.clear();
        birthFrameBuffer.clear();
        posBuffer.position(0);
        colorBuffer.position(0);
        velocityBuffer.position(0);
        birthFrameBuffer.position(0);
        pointCount = 0;
    }
    public void draw(float[] m) {
        GLES20.glEnableVertexAttribArray(positionLoc);
        GLES20.glVertexAttribPointer(positionLoc, 3, GLES20.GL_FLOAT, false, 0, posBuffer);
        GLES20.glEnableVertexAttribArray(colorLoc);
        GLES20.glVertexAttribPointer(colorLoc, 3, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(velocityLoc);
        GLES20.glVertexAttribPointer(velocityLoc, 3, GLES20.GL_FLOAT, false, 0, velocityBuffer);
        GLES20.glEnableVertexAttribArray(birthFrameLoc);
        GLES20.glVertexAttribPointer(birthFrameLoc, 1, GLES20.GL_FLOAT, false, 0, birthFrameBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap);
        GLES20.glUniform1i(mSamplerLoc, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, pointCount);
        GLES20.glDisableVertexAttribArray(positionLoc);
        GLES20.glDisableVertexAttribArray(colorLoc);
        GLES20.glDisableVertexAttribArray(velocityLoc);
        GLES20.glDisableVertexAttribArray(birthFrameLoc);
    }
}
