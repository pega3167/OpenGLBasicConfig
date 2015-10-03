	attribute vec3 position;
    attribute vec2 texcoord;
    attribute vec3 normal;

    uniform mat4 uMVPMatrix;
    varying vec2 tc;
    void main()
    {
    	gl_Position = uMVPMatrix * vec4(position,1);
    	tc = texcoord;
    }