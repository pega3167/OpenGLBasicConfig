precision mediump float;

//vertex attributes
attribute vec3 position;
attribute vec3 normal;  //velocity for particle system
attribute vec2 texcoord;
//particle system attributes
attribute vec3 color;
attribute float birthFrame;

// outputs of vertex shader
varying vec4 ecPos;	// eye-coordinate position
varying vec3 norm;	// per-vertex normal before interpolation
varying vec2 tc;
varying vec3 v_color;
varying float alpha;

// uniform matrices
uniform mat4 uMVPMatrix;
uniform mat4 modelViewMatrix;
uniform float renderMode;
uniform int frame;
uniform int life;
uniform float pointSize;


    void main() {
        if(renderMode == 1.0) {
            gl_PointSize = pointSize;
            if(int(birthFrame) + life < frame) {
                v_color = vec3(0, 0, 0);
            } else {
                float age = float(life - frame + int(birthFrame)) / float(life);
                v_color = vec3(color.x*age, color.y*age, color.z*age);
            }
            alpha = 0.0;
            float move = (float(frame) - birthFrame) / 500.0;
            vec3 velocity = vec3(normal.x*move, normal.y*move, normal.z*move);
            gl_Position = uMVPMatrix * vec4(position + velocity, 1);
        } else {
            ecPos = modelViewMatrix * vec4(position, 1);
            gl_Position = uMVPMatrix * vec4(position, 1);
            norm = normalize(mat3(modelViewMatrix)*normal);
            tc = texcoord;
        }
    }