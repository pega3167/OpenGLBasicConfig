precision mediump float;


// input from the rasterizer
varying vec4 ecPos;
varying vec3 norm;
varying vec2 tc;

// light properties
uniform vec4 lightPosition, Ia, Id, Is;

// material properties
uniform vec4	Ka, Kd, Ks;
uniform float	shininess;
uniform bool bUI;
uniform sampler2D TEX;

uniform mat4 viewMatrix;


    void main() {
        if(bUI) {
            gl_FragColor = texture2D(TEX, tc);
        } else {
            vec4 lPos = viewMatrix*lightPosition;	// light position in the eye-space coordinate
            vec3 n = normalize(norm);	// norm interpolated via rasterizer should be normalized again here
            vec3 p = ecPos.xyz;			// 3D position of this fragment
            vec3 l = normalize(lPos.xyz-(lPos.a==0.0?vec3(0):p));	// lPos.a==0 means directional light
            vec3 v = normalize(-p);									// eye-ecPos = vec3(0)-ecPos
            vec3 h = normalize(l+v);	// the halfway vector
            vec4 Ira = Ka*Ia;									// ambient reflection
            vec4 Ird = max(Kd*dot(l,n)*Id,0.0);					// diffuse reflection
            vec4 Irs = max(Ks*pow(dot(h,n),shininess)*Is,0.0);	// specular reflection
            vec4 result =texture2D(TEX,tc) * (Ira+Ird+Irs);
            //result.a = 1.0;
            gl_FragColor = result;
            //gl_FragColor = texture2D(TEX, tc);

        }
    }