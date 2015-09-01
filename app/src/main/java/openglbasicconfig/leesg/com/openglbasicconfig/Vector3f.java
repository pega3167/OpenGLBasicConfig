package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-09-01.
 */
public class Vector3f {
    public float x;
    public float y;
    public float z;
    // 생성자
    public Vector3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    // 벡터 크기
    public float length() {
        return (float)Math.sqrt((double)( this.x*this.x + this.y*this.y + this.z*this.z ));
    }
    // 두점간의 거리
    public float distance (Vector3f point) {
        double r = (double)((point.x - this.x) * (point.x - this.x) + (point.y - this.y) * (point.y - this.y) + (point.z - this.z) * (point.z - this.z));
        return (float)Math.sqrt(r);
    }
}
