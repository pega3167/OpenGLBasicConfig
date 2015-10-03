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
        float X, Y, Z;
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

    public void copy(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    public void copy(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float dot(Vector3f v){
        return x*v.x + y*v.y + z*v.z;
    }

    public Vector3f multScalar(float s) {
        Vector3f result = new Vector3f(this.x, this.y ,this.z);
        result.x *= s;
        result.y *= s;
        result.z *= s;
        return result;
    }

    public Vector3f minus(Vector3f minVal) {
        Vector3f result = new Vector3f(this.x, this.y ,this.z);
        result.x -= minVal.x;
        result.y -= minVal.y;
        result.z -= minVal.z;
        return result;
    }

    boolean inverse(float[] matrix) {
        float _11, _12, _13, _14, _21, _22, _23, _24, _31, _32, _33, _34, _41, _42, _43, _44;
        _11 = matrix[0];        _12 = matrix[1];        _13 = matrix[2];        _14 = matrix[3];
        _21 = matrix[4];        _22 = matrix[5];        _23 = matrix[6];        _24 = matrix[7];
        _31 = matrix[8];        _32 = matrix[9];        _33 = matrix[10];        _34 = matrix[11];
        _41 = matrix[12];        _42 = matrix[13];        _43 = matrix[14];        _44 = matrix[15];
        float det;
        det = _11*_22*_33*_44 + _11*_23*_34*_42 + _11*_24*_32*_43 + _12*_21*_34*_43
                + _12*_23*_31*_44 + _12*_24*_33*_41	+ _13*_21*_32*_44 + _13*_22*_34*_41
                + _13*_24*_31*_42 + _14*_21*_33*_42 + _14*_22*_31*_43 + _14*_23*_32*_41
                - _11*_22*_34*_43 - _11*_23*_32*_44 - _11*_24*_33*_42 - _12*_21*_33*_44
                - _12*_23*_34*_41 - _12*_24*_31*_43	- _13*_21*_34*_42 - _13*_22*_31*_44
                - _13*_24*_32*_41 - _14*_21*_32*_43 - _14*_22*_33*_41 - _14*_23*_31*_42;
        if(det == 0) return false;
        matrix[0] = (_22*_33*_44 + _23*_34*_42 + _24*_32*_43 - _22*_34*_43 - _23*_32*_44 - _24*_33*_42)/det;
        matrix[1] = (_12*_34*_43 + _13*_32*_44 + _14*_33*_42 - _12*_33*_44 - _13*_34*_42 - _14*_32*_43)/det;
        matrix[2] = (_12*_23*_44 + _13*_24*_42 + _14*_22*_43 - _12*_24*_43 - _13*_22*_44 - _14*_23*_42)/det;
        matrix[3] = (_12*_24*_33 + _13*_22*_34 + _14*_23*_32 - _12*_23*_34 - _13*_24*_32 - _14*_22*_33)/det;
        matrix[4] = (_21*_34*_43 + _23*_31*_44 + _24*_33*_41 - _21*_33*_44 - _23*_34*_41 - _24*_31*_43)/det;
        matrix[5] = (_11*_33*_44 + _13*_34*_41 + _14*_31*_43 - _11*_34*_43 - _13*_31*_44 - _14*_33*_41)/det;
        matrix[6] = (_11*_24*_43 + _13*_21*_44 + _14*_23*_41 - _11*_23*_44 - _13*_24*_41 - _14*_21*_43)/det;
        matrix[7] = (_11*_23*_34 + _13*_24*_31 + _14*_21*_33 - _11*_24*_33 - _13*_21*_34 - _14*_23*_31)/det;
        matrix[8] = (_21*_32*_44 + _22*_34*_41 + _24*_31*_42 - _21*_34*_42 - _22*_31*_44 - _24*_32*_41)/det;
        matrix[9] = (_11*_34*_42 + _12*_31*_44 + _14*_32*_41 - _11*_32*_44 - _12*_34*_41 - _14*_31*_42)/det;
        matrix[10] = (_11*_22*_44 + _12*_24*_41 + _14*_21*_42 - _11*_24*_42 - _12*_21*_44 - _14*_22*_41)/det;
        matrix[11] = (_11*_24*_32 + _12*_21*_34 + _14*_22*_31 - _11*_22*_34 - _12*_24*_31 - _14*_21*_32)/det;
        matrix[12] = (_21*_33*_42 + _22*_31*_43 + _23*_32*_41 - _21*_32*_43 - _22*_33*_41 - _23*_31*_42)/det;
        matrix[13] = (_11*_32*_43 + _12*_33*_41 + _13*_31*_42 - _11*_33*_42 - _12*_31*_43 - _13*_32*_41)/det;
        matrix[14] = (_11*_23*_42 + _12*_21*_43 + _13*_22*_41 - _11*_22*_43 - _12*_23*_41 - _13*_21*_42)/det;
        matrix[15] = (_11*_22*_33 + _12*_23*_31 + _13*_21*_32 - _11*_23*_32 - _12*_21*_33 - _13*_22*_31)/det;
        return true;
    }
}
