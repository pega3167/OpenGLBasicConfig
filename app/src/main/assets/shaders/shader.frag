precision mediump float;


// input from the rasterizer
varying vec4 ecPos;
varying vec3 norm;
varying vec2 tc;
varying vec3 v_color;
varying float alpha;

// light properties
uniform vec4 lightPosition, Ia, Id, Is;

// material properties
uniform vec4	Ka, Kd, Ks;
uniform float	shininess;
uniform bool bUI;
uniform bool bAim;
uniform bool bPS;
uniform sampler2D TEX;

uniform mat4 viewMatrix;
//color uniform for RGBA
uniform float color_R;
uniform float color_G;
uniform float color_B;
uniform float color_A;


    void main() {
        if(bUI) {
            gl_FragColor = bAim? vec4(1,1,1,1): texture2D(TEX, tc);
        } else if (bPS) {
            vec4 tex = texture2D(TEX, gl_PointCoord);
            gl_FragColor = vec4(v_color, alpha) * tex;
            //gl_FragColor = tex;
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