
//vertex attributes
attribute vec3 position;
attribute vec3 normal;  //velocity for particle system
attribute vec2 texcoord;
//particle system attributes
attribute vec3 color;

// outputs of vertex shader
varying vec4 ecPos;	// eye-coordinate position
varying vec3 norm;	// per-vertex normal before interpolation
varying vec2 tc;
varying vec3 v_color;
varying float alpha;

// uniform matrices
uniform mat4 uMVPMatrix;
uniform mat4 modelViewMatrix;
uniform bool bPS;
uniform int frame;
uniform int deathFrame;
uniform int life;







    void main() {
        if(bPS) {
            gl_PointSize = 10.0;
            if(deathFrame < frame) {
                alpha = 0.0;
            } else {
                alpha = float(deathFrame - frame) / float(life);
            }
            v_color = color;
            //float move = float(frame - deathFrame + life);
            //vec3 velocity = vec3(normal.x*move, normal.y*move, normal.z*move);
            gl_Position = uMVPMatrix * vec4((position + normal * float(frame - deathFrame + life)) , 1);
        } else {
            ecPos = modelViewMatrix * vec4(position, 1);
            gl_Position = uMVPMatrix * vec4(position, 1);
            norm = normalize(mat3(modelViewMatrix)*normal);
            tc = texcoord;
        }
    }