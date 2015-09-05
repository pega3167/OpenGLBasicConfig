package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-09-01.
 */
public class Planet extends Sphere {
    private float orbitRadius;       // 공전 궤도 반지름
    private float radius;            // 행성 반지름
    private float revolutionSpeed;  // 공전속도
    private float rotationSpeed;    // 자잔속도
    private int hitPoint;            // 행성 현재 체력
    private int maxHitPoint;         // 행성 풀체력
    private float gravity;           // 중력 세기
    private float gravityField;     // 중력장 범위
    private Vector3f currentPos;     // 현재위치

    public Planet(int mProgramImage, float orbitRadius, float radius, float revolutionSpeed, float rotationSpeed, int hitPoint, int maxHitPoint, float gravity, float gravityField, Vector3f Pos) {
        super(mProgramImage);
        this.orbitRadius = orbitRadius;
        this.radius = radius;
        this.revolutionSpeed = revolutionSpeed;
        this.rotationSpeed = rotationSpeed;
        this.hitPoint = hitPoint;
        this.maxHitPoint = maxHitPoint;
        this.gravity = gravity;
        this.gravityField = gravityField;
        this.currentPos = new Vector3f(Pos.x, Pos.y, Pos.z);
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
    public Vector3f getCurrentPos() { return this.currentPos; }

    class Cannon{
        private Vector3f currentPos; //cannon 위치
        private int attackPoint; //공력력 or 실드 체력
        private float speed; //미사일 속도 or 실드 반지름
        private int type; //cannon or 실드 type

        public Cannon(){
            currentPos =  new Vector3f(0.0f, 0.0f, 0.0f);
            attackPoint = 0;
            speed = 0.0f;
            type = 0;
        }

        public Cannon(Vector3f Pos,int power, float sp, int t){
            currentPos =  new Vector3f(Pos.x,Pos.y, Pos.z);
            attackPoint = power;
            speed = sp;
            type = t;
        }

        public void setCurrentPos(Vector3f Pos){
            currentPos.x = Pos.x;
            currentPos.y = Pos.y;
            currentPos.z = Pos.z;
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
