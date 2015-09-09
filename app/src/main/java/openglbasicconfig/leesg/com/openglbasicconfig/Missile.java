package openglbasicconfig.leesg.com.openglbasicconfig;

import android.util.Log;

/**
 * Created by LeeSG on 2015-08-28.
 */
public class Missile {
    private Vector3f currentPos;
    private Vector3f velocity;
    private float angle;
    private boolean isActive = false;
    private int life = ConstMgr.MAX_AIM_VERTEXCOUNT;
    public float[] angleBuffer = new float[ConstMgr.FRAME_PER_TURN];
    public Vector3f[] positionBuffer = new Vector3f[ConstMgr.FRAME_PER_TURN];
    //생성자
    public Missile() {
        currentPos = new Vector3f();
        velocity = new Vector3f();
        angle = 0;
        for(int i = 0 ; i < ConstMgr.FRAME_PER_TURN ; i++) {
            angleBuffer[i] = 0;
            positionBuffer[i] = new Vector3f();
        }
    }
    public boolean updateBuffer(int index, Planet[] planetList, int listSize) {
        this.positionBuffer[index].copy(currentPos);
        this.angleBuffer[index] = angle;
        if(this.updateVelocity(planetList, listSize) && index > 10) {
            this.life = index;
            this.updateCurrentPos();
            this.updateAngle();
            return true;
        }
        this.updateCurrentPos();
        this.updateAngle();
        return false;
    }

    // 변수 설정 함수
    public void setCurrentPos(Vector3f pos) {
        this.currentPos.x = pos.x;
        this.currentPos.y = pos.y;
        this.currentPos.z = pos.z;
    }
    public void setCurrentPos(float x, float y, float z) {
        this.currentPos.x = x;
        this.currentPos.y = y;
        this.currentPos.z = z;
    }
    public void setVelocity(Vector3f velocity) {
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
        this.velocity.z = velocity.z;
    }
    public void setVelocity(float vx, float vy, float vz) {
        this.velocity.x = vx;
        this.velocity.y = vy;
        this.velocity.z = vz;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setAngle(float angle) { this.angle = angle; }

    public void setLife(int life) {
        this.life = life;
    }

    // get함수
    public Vector3f getCurrentPos() { return this.currentPos; }
    public Vector3f getVelocity() { return this.velocity; }
    public float getAngle() { return angle; }
    public boolean getIsActive() { return this.isActive; }

    public int getLife() {
        return life;
    }

    // 업데이트 함수
    public boolean updateVelocity(Planet[] planetList, int listSize) {
        float r;
        float a;
        Vector3f direction = new Vector3f();
        for(int i = 0 ; i < listSize ; i++) {
            r = this.currentPos.distance( planetList[i].getCurrentPos() );
            if(r < planetList[i].getRadius()) {
                return true;
            }
            if( r < planetList[i].getGravityField() ) {
                a = planetList[i].getGravity() / r*r;
                direction.x = (planetList[i].getCurrentPos().x - this.currentPos.x) / r * a;
                direction.y = (planetList[i].getCurrentPos().y - this.currentPos.y) / r * a;
                direction.z = (planetList[i].getCurrentPos().z - this.currentPos.z) / r * a;
                velocity.x += direction.x;
                velocity.y += direction.y;
                velocity.z += direction.z;
            }
        }
        return false;
    }
    public void updateCurrentPos() {
        this.currentPos.x += this.velocity.x;
        this.currentPos.y += this.velocity.y;
        this.currentPos.z += this.velocity.z;
    }
    public void updateAngle() {
        double cosTheta;
        cosTheta = this.velocity.x / Math.sqrt(this.velocity.x * this.velocity.x + this.velocity.z * this.velocity.z);
        angle = (float)(Math.acos(cosTheta) / Math.PI * 180);
        if(velocity.z > 0) { angle = 360 - angle; }
    }
}
