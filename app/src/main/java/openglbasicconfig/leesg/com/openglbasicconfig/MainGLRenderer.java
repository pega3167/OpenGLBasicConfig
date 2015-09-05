package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Vector;

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
    private final float[] mModelViewMatrix = new float[16];
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
    HangulBitmap mHangulBitmap;
    Camera mCamera;
    Light mLight;
    //객체
    Square mFontSample;
    Square mIntroScreen;
    Button mIntroButtons[];
    Square mStageScreen;
    Button mStageButtons[];
    Button mBackButton;
    Aim mAim;
    //3D 오브젝트
    Planet testPlanet[];
    Mesh sample;
    Mesh mMissile;
    // frame
    int frame = 0;
    long startTime = 0;
    //missile List
    Vector<Missile> testMissile = new Vector<Missile> ();
    //생성자
    public MainGLRenderer(MainActivity activity, int width, int height) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mLastTime = System.currentTimeMillis() + 100;
        mDeviceWidth = width;
        mDeviceHeight = height;
        mScreenConfig = new ScreenConfig(width, height);
        mHangulBitmap = new HangulBitmap(mActivity);
        mBitmapLoader = new BitmapLoader(mContext, mHangulBitmap);
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
        mProgramImage = ESShader.loadProgramFromAsset(mContext,"shaders/shader.vert","shaders/shader.frag");
        if(mProgramImage == 0) {
            Log.e("","Failed loading shaders");
        }
        GLES20.glUseProgram(mProgramImage);
        //initialize font
        mFontSample = new Square(mProgramImage);
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
        mMissile = new Mesh(mProgramImage, mActivity);
        mMissile.loadOBJ("missile");
        mMissile.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample",true));
        testPlanet = new Planet[4];
        Vector3f temp = new Vector3f();
        temp.setXYZ(0f,0f,0f);
        testPlanet[0] = new Planet(mProgramImage, 0.0f, 0.5f, 0.0f, 0.0f, 1, 1, 0.0001f, 1.0f, temp);
        testPlanet[1] = new Planet(mProgramImage, 1.0f, 0.1f, 0.2f, 0.1f, 1, 1, 0.0001f, 1.0f, temp);
        testPlanet[2] = new Planet(mProgramImage, 2.0f, 0.1f, 0.1f, 1.3f, 1, 1, 0.0001f, 1.0f, temp);
        testPlanet[3] = new Planet(mProgramImage, 3.0f, 0.1f, 0.03f, 0.6f, 1, 1, 0.0001f, 1.0f, temp);
        sample = new Mesh(mProgramImage, mActivity);
        //initialize camera
        mCamera = new Camera();
        mCamera.setEye(0f, 250f, 0f);
        mCamera.setAt(0f, 0f, 0f);
        mCamera.setUp(0f, 0f, 1f);
        mCamera.setViewBox((float) Math.PI / 3.0f, (float) mDeviceWidth / (float) mDeviceHeight, 1.0f, 100000.0f);
        mCamera.setViewMatrix();
        mCamera.setTwoDViewMatrix();
        mCamera.setProjectionMatrix();
        mCamera.setOrthoProjectionMatrix(mScreenConfig.mVirtualWidth, mScreenConfig.mVirtualHeight);
        // orthoperspective x lookat
        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
        //initialize light
        mLight = new Light(mProgramImage);
        mLight.setPosition(0.0f, 0.0f, 0.0f, 1.0f);
        mLight.setAmbient(0.5f, 0.5f, 0.5f, 1.0f);
        mLight.setDiffuse(0.7f, 0.7f, 0.7f, 1.0f);
        mLight.setSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        //initialize material
        mLight.setMAmbient(0.6f, 0.6f, 0.6f, 1.0f);
        mLight.setMDiffuse(0.7f, 0.7f, 0.7f, 1.0f);
        mLight.setMSpecular(0.8f, 0.8f, 0.8f, 1.0f);
        mLight.setShininess(10);
        mLight.sendLight();
        mLight.sendMaterial();
        //bit map loader initialize
        //지구
        for(int i = 0 ; i < 4 ; i ++) {
            testPlanet[i].setBitmap(mBitmapLoader.getImageHandle("drawable/earthmap", true), 1024, 512);
        }
        //sample
        sample.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample", true));
        sample.loadOBJ("missile");
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
        //폰트
        mFontSample.setBitmap(mBitmapLoader.getHangulHandle("하하하", 480, Color.BLUE, -1, 1.0f), (int) mBitmapLoader.getWordLength(), 480);
        mFontSample.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mFontSample.setIsActive(true);
        //궤도
        mAim = new Aim(mProgramImage);
        mAim.setShootPos(-3.0f, 0.0f, 0.0f); mAim.setShootVelocity(0.01f, 0.0f, 0.0f);
        mAim.setBitmap(mBitmapLoader.getImageHandle("drawable/white", true));
        mAim.setupVertexBuffer(testPlanet, 4);
    }

    public void update() {
        for ( int i = 0; i < testMissile.size(); i++) {
            testMissile.elementAt(i).updateCurrentPos();
            testMissile.elementAt(i).updateVelocity(testPlanet, 4);
            testMissile.elementAt(i).updateAngle();
        }
        mAim.setShootPos(-3.0f, 0.0f, 0.0f); mAim.setShootVelocity(0.01f, 0.0f, 0.0f);
        mAim.setupVertexBuffer(testPlanet, 4);
        // send light & material properties
        mLight.sendLight();
        mLight.sendMaterial();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "viewMatrix"), 1, false, mCamera.viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        long now = System.currentTimeMillis();
        if (mLastTime > now)
            return;
        long elapsed = now - mLastTime;
        int tempFrame = frame;
        frame = (int)((mLastTime - startTime) * ConstMgr.FPS / 1000);
        if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_INTRO)
            RenderIntro(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE)
            RenderStage(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            if (frame != tempFrame) {
                update();
            }
            RenderGame(mMtrxProjectionAndView, mMtrxOrthoAndView);
        }
        mLastTime = now;
    }
    //초기화면
    private void RenderIntro(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
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
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
        mStageScreen.draw(orth);
        mBackButton.draw(orth);
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i].draw(orth);
        }
        mFontSample.draw(orth);
    }
    //게임 화면
    private void RenderGame(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Render UI
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
        mBackButton.draw(orth);

        // Render 3D
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        float[] tempMatrix = new float[16];
        Vector3f temp = new Vector3f();
        //행성
        //model matrix 계산
        for( int i = 0 ; i < 4 ; i++ ) {
            if(i==0) GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
            else GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, testPlanet[i].getRadius(), testPlanet[i].getRadius(), testPlanet[i].getRadius());
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, frame * testPlanet[i].getRotationSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, testPlanet[i].getOrbitRadius(), 0.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, frame * testPlanet[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            //현재 행성 위치 업데이트
            temp.multM(mModelMatrix, 1.0f);
            testPlanet[i].setCurrentPos(temp);
            //최종 P * V * M 매트릭스
            Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, pv, 0, mModelMatrix, 0);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
            testPlanet[i].draw(mMVPMatrix);
        }
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
        mAim.draw(pv);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        //미사일
        for( int i = 0 ; i < testMissile.size() ; i++ ) {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, 0.0001f, 0.0001f, 0.0001f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, testMissile.elementAt(i).getAngle() - 90, 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, testMissile.elementAt(i).getCurrentPos().x, testMissile.elementAt(i).getCurrentPos().y, testMissile.elementAt(i).getCurrentPos().z);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            //최종 P * V * M 매트릭스
            Matrix.multiplyMM(mMVPMatrix , 0 , pv, 0 , mModelMatrix, 0);
            Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, mModelMatrix, 0);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
            mMissile.draw(mMVPMatrix);
        }
        //mFontSample.draw(orth);
    }

    //터치 이벤트
    float x1= 1.f;
    float x2= 1.f;
    float y1= 1.f;
    float y2= 1.f;
    float px=1.f;
    float py=1.f;
    float distance = 1.f;

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
                            startTime = mLastTime;
                        }
                    }
                    if(mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_INTRO;
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if(mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        //ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                        Missile temp = new Missile();
                        testMissile.add(temp);
                        testMissile.elementAt(testMissile.size() - 1).setCurrentPos(-3.0f, 0.0f, 0.0f);
                        testMissile.elementAt(testMissile.size() - 1).setVelocity(0.01f, 0.0f, 0.0f);
                    }
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() > 1){//멀티터치상태로 움직일때
                        px = (x1+x2)/2;
                        py = (y1+y2)/2;
                        x1 = event.getX(0);
                        y1 = event.getY(0);
                        x2 = event.getX(1);
                        y2 = event.getY(1);
                        float tempdistance = (float)Math.sqrt((double)((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
                        if(Math.abs(tempdistance - distance)>50) {
                            //if(!bzoom&&!bmove) bzoom = true;
                            mCamera.setZoom((distance - tempdistance) / 5000.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                        else{
                            //if(!bzoom&&!bmove) bmove = true;
                        }
                        if(Math.sqrt((px - ((x1 + x2) / 2)) * (px - ((x1 + x2) / 2)) + (py - ((y1+y2)/2))*(py-((y1+y2)/2)))>5){
                            mCamera.setMove((-py + (y1 + y2) / 2) / 200.0f,(-px + (x1 + x2) / 2) / 200.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                        /*
                        if(bzoom) {
                            mCamera.setZoom((distance - tempdistance) / 5000.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                        if(bmove){
                            mCamera.setMove((-py + (y1 + y2) / 2) / 200.0f,(-px + (x1 + x2) / 2) / 200.0f);
                            //mCamera.setRotateX((tempdistance - distance)/100);
                            //mCamera.setRotateY((tempdistance - distance)/100);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                        */
                    }else{ // 싱글터치

                        px = x1;
                        py = y1;
                        x1 = event.getX(0);
                        y1 = event.getY(0);
                        if(Math.abs(px - x1)>2) {
                            mCamera.setRotateY((px - x1) / 2.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                        if(Math.abs(py - y1)>2) {
                            mCamera.setRotateX((y1 - py) / 2.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    x2 = event.getX(1);
                    y2 = event.getY(1);
                    distance = (float)Math.sqrt((double)((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
            }
        }
        return true;
    }
    private void selectTouch(int x, int y) {
        Log.e("", "선택한 좌표는 "+x+","+y);
    }
}