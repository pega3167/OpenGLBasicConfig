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
    private int deathFrameLoc;
    private int mtrxhandle;
    private int mSamplerLoc;
    private int mHandleBitmap;
    public FloatBuffer posBuffer;
    public FloatBuffer velocityBuffer;
    public FloatBuffer colorBuffer;
    public IntBuffer deathFrameBuffer;
    public int pointCount;

    public ParticleSystem (int mProgramImage) {
        positionLoc = GLES20.glGetAttribLocation(mProgramImage, "position");
        velocityLoc = GLES20.glGetAttribLocation(mProgramImage, "normal");
        colorLoc = GLES20.glGetUniformLocation(mProgramImage, "color");
        deathFrameLoc = GLES20.glGetUniformLocation(mProgramImage, "deathFrame");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "TEX");
        emitterList = new Vector<Emitter>();
        initBuffer();
    }
    public void setBitmap(int handle) {
        mHandleBitmap = handle;
    }
    /*
    public class Particle {
        public Vector3f pos;
        public Vector3f velocity;
        public Vector3f color;
        public int deathFrame;
        //생성자
        public Particle() {
            this.pos = new Vector3f();
            this.velocity = new Vector3f();
            this.color = new Vector3f();
            this.deathFrame = 0;
        }
    }
    */
    public class Emitter {
        public Vector3f pos;
        public Vector3f velocity;
        public boolean isActive;
        //생성자
        public Emitter() {
            this.pos = new Vector3f();
            this.velocity = new Vector3f();
            this.isActive = true;
        }
        public Emitter(Vector3f pos , Vector3f velocity) {
            this.pos = new Vector3f(pos.x, pos.y, pos.z);
            this.velocity = new Vector3f(velocity.x, velocity.y, velocity.z);
            this.isActive = true;
        }
    }

    public void addEmitter(Vector3f pos, Vector3f velocity) {
        Emitter emitter = new Emitter(pos, velocity);
        emitterList.add(emitter);
    }
    public void addParticle(int frame, int life, Vector3f color) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        for( int i = 0 ; i < emitterList.size() ; i++) {
            if(emitterList.elementAt(i).isActive) {
                int pc3 = 3*pointCount;
                posBuffer.put(pc3,emitterList.elementAt(i).pos.x);
                posBuffer.put(pc3 + 1, emitterList.elementAt(i).pos.y);
                posBuffer.put(pc3 + 2, emitterList.elementAt(i).pos.z);
                colorBuffer.put(pc3, color.x);
                colorBuffer.put(pc3 + 1, color.y);
                colorBuffer.put(pc3 + 2, color.z);
                velocityBuffer.put(pc3, random.nextFloat());
                velocityBuffer.put(pc3 + 1, random.nextFloat());
                velocityBuffer.put(pc3 + 2, random.nextFloat());
                deathFrameBuffer.put(pointCount, frame + life);
                pointCount++;
            }
        }
        posBuffer.position(0);
        colorBuffer.position(0);
        velocityBuffer.position(0);
        deathFrameBuffer.position(0);
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
        ByteBuffer mDFrames = (ByteBuffer.allocateDirect(10000 * 3 * 4)).order(ByteOrder.nativeOrder());
        deathFrameBuffer = mDFrames.asIntBuffer();
        pointCount = 0;
    }
    public void clearBuffer() {
        posBuffer.clear();
        colorBuffer.clear();
        velocityBuffer.clear();
        deathFrameBuffer.clear();
        posBuffer.position(0);
        colorBuffer.position(0);
        velocityBuffer.position(0);
        pointCount = 0;
    }
    public void draw(float[] m) {
        GLES20.glEnableVertexAttribArray(positionLoc);
        GLES20.glVertexAttribPointer(positionLoc, 3, GLES20.GL_FLOAT, false, 0, posBuffer);
        GLES20.glEnableVertexAttribArray(colorLoc);
        GLES20.glVertexAttribPointer(colorLoc, 3, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(velocityLoc);
        GLES20.glVertexAttribPointer(velocityLoc, 3, GLES20.GL_FLOAT, false, 0, velocityBuffer);
        GLES20.glEnableVertexAttribArray(deathFrameLoc);
        GLES20.glVertexAttribPointer(deathFrameLoc, 1, GLES20.GL_FLOAT, false, 0, deathFrameBuffer);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //이미지 핸들을 바인드 한다. 수정중
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mHandleBitmap);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, pointCount);
        GLES20.glDisableVertexAttribArray(positionLoc);
        GLES20.glDisableVertexAttribArray(colorLoc);
        GLES20.glDisableVertexAttribArray(velocityLoc);
        GLES20.glDisableVertexAttribArray(deathFrameLoc);
    }
}
