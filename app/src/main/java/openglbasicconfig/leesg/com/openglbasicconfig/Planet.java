package openglbasicconfig.leesg.com.openglbasicconfig;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by LeeSG on 2015-09-01.
 */
public class Planet extends Sphere {
    private static int mProgramImage;
    private float orbitRadius;       // 공전 궤도 반지름
    private float radius;            // 행성 반지름
    private float revolutionSpeed;  // 공전속도
    private float rotationSpeed;    // 자잔속도
    private int hitPoint;            // 행성 현재 체력
    private int maxHitPoint;         // 행성 풀체력
    private float gravity;           // 중력 세기
    private float gravityField;     // 중력장 범위
    private Vector3f currentPos;     // 현재위치
    public Cannon[] cannons = new Cannon[5];        // 캐논
    private int cannonListSize;     // 캐논갯수

    public Planet(int mProgramImage, float orbitRadius, float radius, float revolutionSpeed, float rotationSpeed, int hitPoint, int maxHitPoint, float gravity, float gravityField, Vector3f Pos) {
        super(mProgramImage);
        this.mProgramImage = mProgramImage;
        this.orbitRadius = orbitRadius;
        this.radius = radius;
        this.revolutionSpeed = revolutionSpeed;
        this.rotationSpeed = rotationSpeed;
        this.hitPoint = hitPoint;
        this.maxHitPoint = maxHitPoint;
        this.gravity = gravity;
        this.gravityField = gravityField;
        this.currentPos = new Vector3f(Pos.x, Pos.y, Pos.z);
        this.cannonListSize = 0;
        Vector3f temp = new Vector3f();
        temp.setXYZ(1,1,1);
        for(int i = 0 ; i < 5 ; i++) {
            this.cannons[i] = new Cannon(temp, 0, 0f, ConstMgr.MISSILE_NOTHING);
        }
        for(int i = 0 ; i < 5 ; i++) {
            this.cannons[i].aim = new Aim(mProgramImage);
            this.cannons[i].missile = new Missile();
        }
    }

    public void copyUserData(Planet planet) {
        this.hitPoint = planet.hitPoint;
        this.maxHitPoint = planet.maxHitPoint;
        this.cannonListSize = 0;
        for( int i = 0 ; i < planet.cannonListSize ; i++) {
            this.addCannon(planet.cannons[i].relativePos, planet.cannons[i].attackPoint, planet.cannons[i].speed, planet.cannons[i].type, planet.cannons[i].emitterIndex);
        }
        this.updateCannon(0);
    }

    public Planet(int mProgramImage) {
        super(mProgramImage);
        this.orbitRadius = 1.0f;
        this.radius = 1.0f;
        this.revolutionSpeed = 1.0f;
        this.rotationSpeed = 1.0f;
        this.hitPoint = 0;
        this.maxHitPoint = 0;
        this.gravity = 1.0f;
        this.gravityField = 1.0f;
        this.currentPos = new Vector3f();
        Vector3f temp = new Vector3f();
        temp.setXYZ(1,1,1);
        for(int i = 0 ; i < 5 ; i++) {
            this.cannons[i] = new Cannon(temp, 0, 0f, ConstMgr.MISSILE_NOTHING);
        }
        for(int i = 0 ; i < 5 ; i++) {
            this.cannons[i].aim = new Aim(mProgramImage);
            this.cannons[i].missile = new Missile();
        }
    }
    //값 설정 함수
    public void setOrbitRadius(float orbitRadius) { this.orbitRadius = orbitRadius; }
    public void setRadius(float radius) { this.radius = radius; }
    public void setRevolutionSpeed(float revolutionSpeed) { this.revolutionSpeed = revolutionSpeed; }
    public void setRotationSpeed(float rotationSpeed) { this.rotationSpeed = rotationSpeed; }
    public void setHitPoint(int hitPoint) { this.hitPoint = hitPoint; }
    public void setMaxHitPoint(int maxHitPoint) { this.maxHitPoint = maxHitPoint; }
    public void setGravity(float gravity) { this.gravity = gravity; }
    public void setGravityField(float gravityField) { this.gravityField = gravityField; }
    public void setCurrentPos(Vector3f Pos) {
        this.currentPos.x = Pos.x;
        this.currentPos.y = Pos.y;
        this.currentPos.z = Pos.z;
    }
    public void setCurrentPos(float x, float y, float z) {
        this.currentPos.x = x;
        this.currentPos.y = y;
        this.currentPos.z = z;
    }
    //값 반환 함수
    public float getOrbitRadius() { return this.orbitRadius; }
    public float getRadius() { return this.radius; }
    public float getRevolutionSpeed() { return this.revolutionSpeed; }
    public float getRotationSpeed() { return this.rotationSpeed; }
    public int getHitPoint() { return this.hitPoint; }
    public int getMaxHitPoint() { return this.maxHitPoint; }
    public float getGravity() { return this.gravity; }
    public float getGravityField() { return this.gravityField; }
    public int getCannonListSize() {
        return cannonListSize;
    }
    public Vector3f getCurrentPos() { return this.currentPos; }

    public void clearCannonList() {
        for(int i = 0 ; i < 5 ; i++) {
            this.cannons[i].currentPos =  new Vector3f(0.0f, 0.0f, 0.0f);
            this.cannons[i].relativePos =  new Vector3f(0.0f, 0.0f, 0.0f);
            this.cannons[i].attackPoint = 0;
            this.cannons[i].speed = 0.0f;
            this.cannons[i].type = 0;
        }
        cannonListSize = 0;
    }

    public void changeCannon(int index, Vector3f pos, int attackPoint, float speed, int type, int emitterIndex) {
        this.cannons[index].relativePos.x = pos.x;
        this.cannons[index].relativePos.y = pos.y;
        this.cannons[index].relativePos.z = pos.z;
        this.cannons[index].relativePos.normalize();
        this.cannons[index].attackPoint = attackPoint;
        this.cannons[index].speed = speed;
        this.cannons[index].type = type;
        this.cannons[index].emitterIndex = emitterIndex;
        float modelMatrix[] = new float[16];
        float tempMatrix[] = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, this.cannons[index].relativePos.x, this.cannons[index].relativePos.y, this.cannons[index].relativePos.z);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, this.radius, this.radius, this.radius);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, this.orbitRadius, 0, 0);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);

        cannons[index].currentPos.multM(modelMatrix, 0);
        cannons[index].aim.setShootPos(cannons[index].currentPos);
        Vector3f temp = this.cannons[index].currentPos.minus(this.currentPos);
        temp.normalize();
        cannons[index].aim.setShootVelocity(temp.multScalar(speed));
        cannons[index].missile.setCurrentPos(cannons[index].currentPos);
        cannons[index].missile.setVelocity(temp.multScalar(speed));
        cannons[index].missile.updateAngle();
    }

    public void addCannon(Vector3f pos, int attackPoint, float speed, int type, int emitterIndex) {
        if(cannonListSize >= 5) {
            // 캐논수 꽉참
        }
        else {
            this.cannons[cannonListSize].relativePos.x = pos.x;
            this.cannons[cannonListSize].relativePos.y = pos.y;
            this.cannons[cannonListSize].relativePos.z = pos.z;
            this.cannons[cannonListSize].relativePos.normalize();
            this.cannons[cannonListSize].attackPoint = attackPoint;
            this.cannons[cannonListSize].speed = speed;
            this.cannons[cannonListSize].type = type;
            this.cannons[cannonListSize].emitterIndex = emitterIndex;
            float modelMatrix[] = new float[16];
            float tempMatrix[] = new float[16];
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, this.cannons[cannonListSize].relativePos.x, this.cannons[cannonListSize].relativePos.y, this.cannons[cannonListSize].relativePos.z);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, this.radius, this.radius, this.radius);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, this.orbitRadius, 0, 0);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);

            cannons[cannonListSize].currentPos.multM(modelMatrix, 0);
            cannons[cannonListSize].aim.setShootPos(cannons[cannonListSize].currentPos);
            Vector3f temp = this.cannons[cannonListSize].currentPos.minus(this.currentPos);
            temp.normalize();
            cannons[cannonListSize].aim.setShootVelocity(temp.multScalar(speed));
            cannons[cannonListSize].missile.setCurrentPos(cannons[cannonListSize].currentPos);
            cannons[cannonListSize].missile.setVelocity(temp.multScalar(speed));
            cannons[cannonListSize].missile.updateAngle();
            this.cannonListSize += 1;
        }
    }
    public void updateCannon(int frame) {
        float modelMatrix[] = new float[16];
        float tempMatrix[] = new float[16];
        for(int i = 0; i < this.cannonListSize ; i++) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, this.cannons[i].relativePos.x, this.cannons[i].relativePos.y, this.cannons[i].relativePos.z);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, this.radius, this.radius, this.radius);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, frame * this.rotationSpeed, 0, 1, 0);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, this.orbitRadius, 0, 0);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, frame * this.revolutionSpeed, 0, 1, 0);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            cannons[i].currentPos.setXYZ(0, 0, 0);
            cannons[i].currentPos.multM(modelMatrix, 1.0f);
            cannons[i].aim.setShootPos(cannons[i].currentPos);
            Vector3f temp = this.cannons[i].aim.getShootPos().minus(this.currentPos);
            temp.normalize();
            cannons[i].aim.setShootVelocity(temp.multScalar(this.cannons[i].speed));
            cannons[i].missile.setCurrentPos( cannons[i].currentPos);
            cannons[i].missile.setVelocity(temp.multScalar(this.cannons[i].speed));
            cannons[i].missile.updateAngle();
        }
    }
    class Cannon{
        private Vector3f currentPos; //cannon 위치
        private Vector3f relativePos;//cannon 행성에서의 상대위치(반지름 1.0f인 행성으로 간주함)
        private int attackPoint; //공력력 or 실드 체력
        private float speed; //미사일 속도 or 실드 반지름
        private int type; //cannon or 실드 type

        public int emitterIndex;
        public Missile missile;
        public Aim aim;

        public Cannon(){
            currentPos =  new Vector3f(0.0f, 0.0f, 0.0f);
            relativePos =  new Vector3f(0.0f, 0.0f, 0.0f);
            attackPoint = 0;
            speed = 0.0f;
            type = 0;
        }

        public Cannon(Vector3f Pos,int power, float sp, int t){
            relativePos = new Vector3f(Pos.x, Pos.y, Pos.z);
            currentPos =  new Vector3f();
            attackPoint = power;
            speed = sp;
            type = t;
        }
        public void setRelativePos(Vector3f Pos){
            relativePos.x = Pos.x;
            relativePos.y = Pos.y;
            relativePos.z = Pos.z;
        }
        public void setRelativePos(float x, float y, float z){
            relativePos.x = x;
            relativePos.y = y;
            relativePos.z = z;
        }
        public void setCurrentPos(Vector3f Pos){
            currentPos.x = Pos.x;
            currentPos.y = Pos.y;
            currentPos.z = Pos.z;
        }
        public void setCurrentPos(float x, float y, float z){
            currentPos.x = x;
            currentPos.y = y;
            currentPos.z = z;
        }
        public void setAttackPoint(int power){
            attackPoint = power;
        }
        public void setSpeed(float sp){
            speed = sp;
        }
        public void setType(int t){
            type = t;
        }
        public Vector3f getCurrentPos(){
            return currentPos;
        }
        public Vector3f getRelativePos() {
            return relativePos;
        }
        public int getAttackPoint(){
            return attackPoint;
        }
        public int getType(){
            return type;
        }
        public float getSpeed(){
            return speed;
        }
    }
}
