package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by LeeSG on 2015-08-13.
 */
public class MainGLRenderer implements GLSurfaceView.Renderer {
    //매트릭스
    private final float[] mMtrxProjectionAndView = new float[16]; // proj x view
    private final float[] mMtrxOrthoAndView = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16]; //proj x view x model
    //프로그램
    private static int mProgramImage;
    private static int mProgramSolidColor;
    //프로그램 시간
    long mLastTime;
    //디바이스의 넓이, 높이
    public static int mDeviceWidth;
    public static int mDeviceHeight;
    //주 액티비티
    MainActivity mActivity;
    Context mContext;
    ScreenConfig mScreenConfig;
    BitmapLoader mBitmapLoader;
    Camera mCamera;
    //객체
    Square mIntroScreen;
    Button mIntroButtons[];
    Square mStageScreen;
    Button mStageButtons[];
    Button mBackButton;
    //3D 오브젝트
    Sphere mSphere;
    Mesh sample;
    Mesh sample3;
    //생성자
    public MainGLRenderer(MainActivity activity, int width, int height) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mLastTime = System.currentTimeMillis() + 100;
        mDeviceWidth = width;
        mDeviceHeight = height;
        mScreenConfig = new ScreenConfig(width, height);
    }
    //멈춤
    public void onPause() {
    }
    // 재시작
    public void onResume() {
        mLastTime = System.currentTimeMillis();
    }
    // 서피스뷰 변경
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height){
        GLES20.glViewport(0, 0, (int) mDeviceWidth, (int) mDeviceHeight);
        Matrix.setIdentityM(mMtrxProjectionAndView, 0);
        Matrix.setIdentityM(mMtrxOrthoAndView, 0);
        // look at
        mCamera.setViewMatrix();
        // perspective
        mCamera.setProjectionMatrix();
        // perspective x lookat
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
    }
    // 서피스뷰 생성
    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        mScreenConfig = new ScreenConfig(mDeviceWidth, mDeviceHeight);
        mScreenConfig.setSize(1280, 720);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgramImage = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramImage, vertexShader);
        GLES20.glAttachShader(mProgramImage, fragmentShader);
        GLES20.glLinkProgram(mProgramImage);
        GLES20.glUseProgram(mProgramImage);
        //initialize screen
        mIntroScreen = new Square(mProgramImage);
        mStageScreen = new Square(mProgramImage);
        //initialize buttons
        mBackButton = new Button(mProgramImage, mProgramSolidColor, this);
        mIntroButtons = new Button[ConstMgr.INTROBUTTON_NUM];
        for(int i = 0 ; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        mStageButtons = new Button[ConstMgr.STAGEBUTTON_NUM];
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        //initialize mesh
        mSphere = new Sphere(mProgramImage);
        sample = new Mesh(mProgramImage, mActivity);
        sample3 = new Mesh(mProgramImage, mActivity);
        //initialize camera
        mCamera = new Camera();
        mCamera.setEye(0f, 250f, 0f);
        mCamera.setAt(0f, 0f, 0f);
        mCamera.setUp(0f, 0f, -1f);
        mCamera.setViewBox((float) Math.PI / 6.0f, (float) mDeviceWidth / (float) mDeviceHeight, 1.0f, 100000.0f);
        mCamera.setViewMatrix();
        mCamera.setTwoDViewMatrix();
        mCamera.setProjectionMatrix();
        mCamera.setOrthoProjectionMatrix(mScreenConfig.mVirtualWidth, mScreenConfig.mVirtualHeight);
        // orthoperspective x lookat
        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
        //bit map loader initialize
        mBitmapLoader = new BitmapLoader(mContext);
        //지구
        mSphere.setBitmap(mBitmapLoader.getImageHandle("drawable/earthmap", true), 1024, 512);
        //sample
        sample.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample", true));
        sample.loadOBJ("missile");
        sample3.setBitmap(mBitmapLoader.getImageHandle("drawable/sample3", true));
        sample3.loadOBJ("sample3");
        //intro 화면
        mIntroScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/intro", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        mIntroScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mIntroScreen.setIsActive(true);
        //buttons
        for(int i = 0; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/button" + i, false), mScreenConfig.getmVirtualWidth() / 5, mScreenConfig.getmVirtualHeight() / 5);
            mIntroButtons[i].setIsActive(true);
            mIntroButtons[i].setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2 - i * mScreenConfig.getmVirtualHeight()*3/10);
        }
        //stage 화면
        mStageScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/stagescreen", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        mStageScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mStageScreen.setIsActive(true);
        //buttons
        mBackButton.setBitmap(mBitmapLoader.getImageHandle("drawable/back2", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        mBackButton.setPos(mScreenConfig.getmVirtualWidth() - mScreenConfig.getmVirtualHeight() / 12, 11 * mScreenConfig.getmVirtualHeight() / 12);
        mBackButton.setIsActive(true);
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/stage", false),mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
            mStageButtons[i].setPos(mScreenConfig.getmVirtualHeight() / 6 + i * mScreenConfig.getmVirtualHeight() / 6, 4 * mScreenConfig.getmVirtualHeight() / 6);
            mStageButtons[i].setIsActive(true);
        }
    }
    // 쉐이더
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vPosition;" +
                    " v_texCoord = a_texCoord;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    " gl_FragColor = texture2D( s_texture, v_texCoord);" +
                    "}";

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        long now = System.currentTimeMillis();
        if (mLastTime > now)
            return;
        long elapsed = now - mLastTime;
        if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_INTRO)
            RenderIntro(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE)
            RenderStage(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME)
            RenderGame(mMtrxProjectionAndView, mMtrxOrthoAndView);
        mLastTime = now;
    }
    //초기화면
    private void RenderIntro(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mIntroScreen.draw(orth);
        for(int i = 0 ; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i].draw(orth);
        }
    }
    //스테이지 선택 화면
    private void RenderStage(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mStageScreen.draw(orth);
        mBackButton.draw(orth);
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i].draw(orth);
        }
    }
    //게임 화면
    private void RenderGame(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mBackButton.draw(orth);
        float[] tempMatrix = new float[16];
        //model matrix 생성
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, (float) (mLastTime/10 % 360), 0f, 0.0001f, 0.0005f);
        Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
        //최종 P * V * M 매트릭스
        Matrix.multiplyMM(mMVPMatrix, 0, pv, 0, mModelMatrix, 0);
        //mSphere.draw(mMVPMatrix);
        sample3.draw(mMVPMatrix);
    }

    //터치 이벤트
    private int mPointerId;
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        mPointerId = event.getPointerId(0);
        final int action = event.getAction();
        if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_INTRO) {
            //인트로 화면 이벤트
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if(mIntroButtons[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                    }else if(mIntroButtons[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        //정말 종료하시겠습니까? 팝업창
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE) {
            //스테이지 선택창 이벤트
            switch(action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
                        if(mStageButtons[i].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_GAME;
                        }
                    }
                    if(mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_INTRO;
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if(mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                    }
                }
            }
        }
        return true;
    }
    private void selectTouch(int x, int y) {
        Log.e("", "선택한 좌표는 "+x+","+y);
    }
}
