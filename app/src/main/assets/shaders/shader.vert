
//vertex attributes
attribute vec3 position;
attribute vec3 normal;
attribute vec2 texcoord;

// outputs of vertex shader
varying vec4 ecPos;	// eye-coordinate position
varying vec3 norm;	// per-vertex normal before interpolation
varying vec2 tc;

// uniform matrices
uniform mat4 uMVPMatrix;
uniform mat4 modelViewMatrix;







    void main() {
        ecPos = modelViewMatrix * vec4(position, 1);
        gl_Position = uMVPMatrix * vec4(position, 1);
        norm = normalize(mat3(modelViewMatrix)*normal);
        tc = texcoord;
    }