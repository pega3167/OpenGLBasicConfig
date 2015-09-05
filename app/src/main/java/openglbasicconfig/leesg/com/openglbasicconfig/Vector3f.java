package openglbasicconfig.leesg.com.openglbasicconfig;

import android.util.Log;

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

    public void setXYZ(float x, float y, float z) {
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
    // normalize
    public void normalize() {
        float l = this.length();
        this.x /= l;
        this.y /= l;
        this.z /= l;
    }

    public Vector3f multM(float[] m, float w) {
        float X, Y, Z, W;
        X = m[0]*this.x + m[4]*this.y + m[8]*this.z + m[12]*w;
        Y = m[1]*this.x + m[5]*this.y + m[9]*this.z + m[13]*w;
        Z = m[2]*this.x + m[6]*this.y + m[10]*this.z + m[14]*w;
        //W = m[3]*this.x + m[7]*this.y + m[11]*this.z + m[15]*w;
        this.x = X;
        this.y = Y;
        this.z = Z;
        return this;
    }
    public Vector3f Cross(Vector3f v) {
        Vector3f temp = new Vector3f();
        temp.setXYZ(y*v.z - z*v.y,z*v.x - x*v.z,x*v.y - y*v.x);
        return temp;
    }
}
