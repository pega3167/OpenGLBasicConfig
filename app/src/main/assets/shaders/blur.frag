precision mediump float;
varying vec2 tc;
uniform sampler2D TEX;
//blur
uniform int direction;
uniform float blurScale;
uniform float blurAmount;
uniform float blurStrength;
 
float GaussianFunction(float x, float dev)
{
  return ((1.0/sqrt(2.0*3.142857*dev))*exp(-(x*x)/(2.0*dev)));
} 
 
void main() 
{
    float dev = blurAmount*0.5*0.5;
    dev *= dev;
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    vec4 temp =  vec4(0.0,0.0,0.0,0.0);
    float strength = 1.0 - blurStrength;
    float half1 = float(blurAmount)*0.5;
    float texel = 1.0/128.0;
    int count = int(blurAmount);
    if (direction == 0)
    {
        for (int i=0;i<count;i++) {
            float offset = float(i) - half1;
            temp = texture2D(TEX, tc+vec2(offset*texel*blurScale,0.0))*GaussianFunction(offset*strength, dev);
            color += temp;
        }
    }
  else
  {
        for (int i=0;i<count;i++) {
            float offset = float(i) - half1;
            temp = texture2D(TEX, tc+vec2(0.0,offset*texel*blurScale))*GaussianFunction(offset*strength, dev);
            color += temp;
        }
  }
  
    gl_FragColor = clamp(color, 0.0, 1.0);
    gl_FragColor.w = 1.0;
//    gl_FragColor = texture2D(TEX, tc);
}
