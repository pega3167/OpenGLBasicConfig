package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
    // 카메라 & 빛
    Camera mCamera;
    Light mLight;
    // 화면 이미지
    Square mIntroScreen;
    Square mStageScreen;
    // 화면 버튼
    Button mIntroButtons[];
    Button mStageButtons[];
    Button mShootButton;
    Button mMissileButton[];
    Button mModeButton[];
    Button mBackButton;
    // 팝업창
    private static Square popupWindow;
    private static Square[] popupStr = new Square[ConstMgr.POPUP_MODE_SIZE];
    private static Button[] popupBtns = new Button[ConstMgr.POPUP_BUTTON_SIZE];
    private static Popup mPopup;
    //3D 오브젝트
    Mesh mMissile;
    Planet mUser;
    // 파티클 시스템
    ParticleSystem mParticleSystem;
    int particleTextureHandle[] = new int[1];
    // planet texture
    int planetTexureHandle[] = new int[2];
    // Stage
    Stage mStage[] = new Stage[1];
    // frame
    int frame = 0;
    long startTime = 0;
    // Flag
    private boolean mIsFirstCalled = true;
    private boolean mIsDraw = false;
    //missile List

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
        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);
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
        GLES20.glUseProgram(mProgramImage);
        // 처음에만 초기화하고 인터럽트 발생 후에는 텍스쳐만 바인드 한다.
        if (mIsFirstCalled) {
            init();
        } else {
            recoverResource();
        }
        mIsFirstCalled = false;
    }

    private void init() {
        initResourceLoader();
        initPlanetResource();
        initObject();
        initUser();
        initStage();
        initIntroScreen();
        initStageScreen();
        initGameScreen();
        initPopup();
    }
    private void initResourceLoader() {
        mHangulBitmap = new HangulBitmap(mActivity);
        mBitmapLoader = new BitmapLoader(mContext, mHangulBitmap);
    }
    private void initPlanetResource() {
        planetTexureHandle[0] = mBitmapLoader.getImageHandle("drawable/earthmap", true);
        planetTexureHandle[1] = mBitmapLoader.getImageHandle("drawable/th_sun", true);
    }
    private void initObject() {
        mIsDraw = false;
        //initialize camera
        mCamera = new Camera();
        mCamera.setEye(0f, 250f, 0f);
        mCamera.setAt(0f, 0f, 0f);
        mCamera.setUp(0f, 0f, 1f);
        mCamera.setViewBox((float) Math.PI / 3, (float) mDeviceWidth / (float) mDeviceHeight, 1.0f, 100000.0f);
        mCamera.setViewMatrix();
        mCamera.setTwoDViewMatrix();
        mCamera.setProjectionMatrix();
        mCamera.setOrthoProjectionMatrix(mScreenConfig.mVirtualWidth, mScreenConfig.mVirtualHeight);
        // perspective x lookat
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "viewMatrix"), 1, false, mCamera.viewMatrix, 0);
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
        // 파티클 시스템 초기화
        mParticleSystem = new ParticleSystem(mProgramImage);
        //initialize mesh
        mMissile = new Mesh(mProgramImage, mActivity);
        mMissile.loadOBJ("missile");
        setResourceObject();
    }
    private void initUser() {
        //유저 정보 로드
        Vector3f temp = new Vector3f();
        mUser = new Planet(mProgramImage, 0.0f, 0.1f, 0.2f, 1.0f, 1, 1, 0.0001f, 1.0f, temp);
        temp.setXYZ(1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD);
        temp.setXYZ(-1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD);
        setResourceUser();
    }
    private void initStage() {
        Vector3f temp = new Vector3f();
        temp.setXYZ(0, 0, 0);
        mStage[0] = new Stage(mProgramImage, 4, 3);
        mStage[0].planetList[0] = new Planet(mProgramImage, 0.0f, 0.5f, 0.0f, 0.0f, 1, 1, 0.0001f, 1.0f, temp);
        mStage[0].planetList[1] = new Planet(mProgramImage, 1.0f, 0.1f, 0.2f, 0.1f, 1, 1, 0.0001f, 1.0f, temp);
        mStage[0].planetList[2] = new Planet(mProgramImage, 2.0f, 0.1f, 0.1f, 1.3f, 1, 1, 0.0001f, 1.0f, temp);
        mStage[0].planetList[3] = new Planet(mProgramImage, 3.0f, 0.1f, 0.03f, 0.6f, 1, 1, 0.0001f, 1.0f, temp);
        setResourceStage();
    }
    private void initIntroScreen() {
        //initialize screen
        mIntroScreen = new Square(mProgramImage);
        mIntroScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mIntroScreen.setIsActive(true);
        //initioalize buttons
        mIntroButtons = new Button[ConstMgr.INTROBUTTON_NUM];
        for(int i = 0 ; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        for(int i = 0; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i].setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2 - i * mScreenConfig.getmVirtualHeight() * 3 / 10);
            mIntroButtons[i].setIsActive(true);
        }
        setResourceIntroScreen();
    }
    private void initStageScreen() {
        //initialize screen
        mStageScreen = new Square(mProgramImage);
        mStageScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mStageScreen.setIsActive(true);
        // initialize buttons
        mStageButtons = new Button[ConstMgr.STAGEBUTTON_NUM];
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mStageButtons[i].setPos(mScreenConfig.getmVirtualHeight() / 6 + i * mScreenConfig.getmVirtualHeight() / 5, 4 * mScreenConfig.getmVirtualHeight() / 6);
            mStageButtons[i].setIsActive(true);
        }
        //back button
        mBackButton = new Button(mProgramImage, mProgramSolidColor, this);
        mBackButton.setPos(mScreenConfig.getmVirtualWidth() - mScreenConfig.getmVirtualHeight() / 12, 11 * mScreenConfig.getmVirtualHeight() / 12);
        mBackButton.setIsActive(true);
        setResourceStageScreen();
    }
    private void initGameScreen() {
        //missile select button
        mMissileButton = new Button[5];
        for ( int i = 0 ; i < 5 ; i++) {
            mMissileButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mMissileButton[i].setPos(mScreenConfig.getmVirtualHeight() / 12, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 4 - i * mScreenConfig.getmVirtualHeight() / 6);
            mMissileButton[i].setIsActive(false);
        }
        mModeButton = new Button[2];
        for(int i = 0 ; i < 2 ; i++) {
            mModeButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mModeButton[i].setPos(mScreenConfig.getmVirtualWidth() - mScreenConfig.getmVirtualHeight() / 12 - i * mScreenConfig.getmVirtualHeight() / 6 , 11 * mScreenConfig.getmVirtualHeight() / 12);
            mModeButton[i].setIsActive(true);
        }
        mShootButton = new Button(mProgramImage, mProgramSolidColor, this);
        mShootButton.setPos(mScreenConfig.getmVirtualWidth() * 11 / 12, mScreenConfig.getmVirtualWidth() / 12);
        mShootButton.setIsActive(true);
        setResourceGameScreen();
    }
    private void initPopup() {
        // 팝업
        popupWindow = new Square(mProgramImage);
        for(int i = 0 ; i < ConstMgr.POPUP_MODE_SIZE ; i++) {
            popupStr[i] = new Square(mProgramImage);
        }
        for(int i = 0 ; i < ConstMgr.POPUP_BUTTON_SIZE ; i++) {
            popupBtns[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        mPopup = new Popup(this, popupWindow, popupStr, popupBtns, mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        setResourcePopup();
    }

    private void recoverResource() {
        initResourceLoader();
        initPlanetResource();
        setResourceObject();
        setResourceUser();
        setResourceStage();
        setResourceIntroScreen();
        setResourceStageScreen();
        setResourceGameScreen();
        setResourcePopup();
    }
    private void setResourceObject() {
        mMissile.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample", true));
        //particleTextureHandle[0] = mBitmapLoader.getImageHandle("drawable/particle", true);
        mParticleSystem.setBitmap(mBitmapLoader.getImageHandle("drawable/particle", true));
    }
    private void setResourceUser() {
        mUser.setBitmap(planetTexureHandle[0], 1024, 512, 36);
    }
    private void setResourceStage() {
        mStage[0].planetList[0].setBitmap(planetTexureHandle[1], 1024, 512, 36);
        mStage[0].planetList[1].setBitmap(planetTexureHandle[0], 1024, 512, 36);
        mStage[0].planetList[2].setBitmap(planetTexureHandle[0], 1024, 512, 36);
        mStage[0].planetList[3].setBitmap(planetTexureHandle[0], 1024, 512, 36);
    }
    private void setResourceIntroScreen() {
        mIntroScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/intro", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        for(int i = 0; i < ConstMgr.INTROBUTTON_NUM ; i++) {
            mIntroButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/button" + i, false), mScreenConfig.getmVirtualWidth() / 5, mScreenConfig.getmVirtualHeight() / 5);
        }
    }
    private void setResourceStageScreen() {
        mStageScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/stagescreen", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/stage" + i, false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        }
        mBackButton.setBitmap(mBitmapLoader.getImageHandle("drawable/button3", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);

    }
    private void setResourceGameScreen() {
        for ( int i = 0 ; i < 5 ; i++) {
            mMissileButton[i].setBitmap(mBitmapLoader.getImageHandle("drawable/missilebutton", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        }
        mModeButton[0].setBitmap(mBitmapLoader.getImageHandle("drawable/button5", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        mModeButton[1].setBitmap(mBitmapLoader.getImageHandle("drawable/button6", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        mShootButton.setBitmap(mBitmapLoader.getImageHandle("drawable/launch", false), mScreenConfig.getmVirtualWidth() / 6, mScreenConfig.getmVirtualWidth() / 6);
    }
    private void setResourcePopup() {
        popupWindow.setBitmap(mBitmapLoader.getImageHandle("drawable/popup", false), mScreenConfig.getmVirtualWidth() * 3 / 4, mScreenConfig.getmVirtualHeight() * 3 / 4);
        String[] popupStrs = {" ", "정말로 게임을 종료하시겠습니까?", "진행중인 게임을 포기하고 나가시겠습니까?","조준 되지 않은 미사일이 있습니다. 턴을 진행하시겠습니까?",
                "게임에서 승리하셨습니다!", "패배하였습니다..."};
        for(int i = 0 ; i < ConstMgr.POPUP_MODE_SIZE ; i++) {
            popupStr[i].setBitmap(mBitmapLoader.getHangulHandle(popupStrs[i], mScreenConfig.getmVirtualHeight()/15, Color.WHITE, -1, 1.0f), (int)mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight()/15);
        }
        for(int i = 0 ; i < ConstMgr.POPUP_BUTTON_SIZE ; i++) {
            popupBtns[i].setBitmap(mBitmapLoader.getImageHandle("drawable/popup" + i, false),mScreenConfig.getmVirtualWidth()/5, (int)(mScreenConfig.getmVirtualWidth()/11.5));
        }
    }

    // 모든 프레임에 항상 하는일(턴중 + 시뮬레이션중)
    private void update() {
        // send light & material properties
        mLight.sendLight();
        mLight.sendMaterial();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "viewMatrix"), 1, false, mCamera.viewMatrix, 0);
    }
    // 시뮬레이션 전에 하는일
    private void beforeSimul() {
        for(int j = 0 ; j < mStage[ConstMgr.STAGE].listSize ; j++) {
            for(int k = 0 ; k < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); k++) {
                if(mStage[ConstMgr.STAGE].planetList[j].cannons[k].aim.getIsAimed()) {
                    mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.setIsActive(true);
                    for (int i = 0; i < ConstMgr.FRAME_PER_TURN; i++) {
                        if(mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.updateBuffer(i, mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize))
                            break;
                        mStage[ConstMgr.STAGE].updatePosInList(ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn + i);
                    }
                    mStage[ConstMgr.STAGE].updatePosInList(ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn);
                }
            }
        }
    }
    // 시뮬레이션 후에 하는일
    private void afterSimul() {
        mStage[ConstMgr.STAGE].turn += 1;
        mStage[ConstMgr.STAGE].currentFrame = (ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn );
        ConstMgr.RENDER_MODE = ConstMgr.RENDER_SETTING;
        mShootButton.setIsActive(true);
        mStage[ConstMgr.STAGE].updatePosInList(mStage[ConstMgr.STAGE].currentFrame);
        for(int i = 0 ; i < mStage[ConstMgr.STAGE].listSize; i++) {
            mStage[ConstMgr.STAGE].planetList[i].updateCannon(mStage[ConstMgr.STAGE].currentFrame);
        }
        for(int j = 0 ; j < mStage[ConstMgr.STAGE].listSize ; j++) {
            for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); i++) {
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.setIsAimed(false);
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].missile.setIsActive(false);
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].missile.setLife(ConstMgr.MAX_AIM_VERTEXCOUNT);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        long now = System.currentTimeMillis();
        if (mLastTime > now)
            return;
        long elapsed = now - mLastTime;
        int tempFrame = frame;
        frame = (int)((mLastTime - startTime) * ConstMgr.FPS / 1000);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "frame"), frame);
        if(ConstMgr.RENDER_MODE == ConstMgr.RENDER_ANIMATION) {
            mStage[ConstMgr.STAGE].currentFrame = frame - mStage[ConstMgr.STAGE].turnStartFrame;
            if (mStage[ConstMgr.STAGE].currentFrame > ConstMgr.FRAME_PER_TURN) {
                afterSimul();
            } else {
                mStage[ConstMgr.STAGE].currentFrame += mStage[ConstMgr.STAGE].turn * ConstMgr.FRAME_PER_TURN;
            }
        }

        if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_INTRO)
            RenderIntro(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE)
            RenderStage(mMtrxProjectionAndView, mMtrxOrthoAndView);
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            if (frame != tempFrame) { update(); }
            RenderGame(mMtrxProjectionAndView, mMtrxOrthoAndView);
        } else if (ConstMgr.SCREEN_MODE == 4) {
            if (frame != tempFrame) {update();}
            RenderTest(mMtrxProjectionAndView, mMtrxOrthoAndView);
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
        mPopup.draw(orth);
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
        mPopup.draw(orth);
    }
    //게임 화면
    private void RenderGame(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Render 3D
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        float[] tempMatrix = new float[16];
        Vector3f temp = new Vector3f();
        //스카이 스피어
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, 100, 100, 100);
        Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
        //최종 P * V * M 매트릭스
        Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, pv, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        /*
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
        spaceSphere.draw(mMVPMatrix);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        */

        //행성
        //model matrix 계산
        for( int i = 0 ; i < 4 ; i++ ) {
            if(i==0) GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
            else GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[i].getRadius(), mStage[ConstMgr.STAGE].planetList[i].getRadius(), mStage[ConstMgr.STAGE].planetList[i].getRadius());
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRotationSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[i].getOrbitRadius(), 0.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
            //현재 행성 위치 업데이트
            temp.setXYZ(0, 0, 0);
            temp.multM(mModelMatrix, 1.0f);
            mStage[ConstMgr.STAGE].planetList[i].setCurrentPos(temp);
            //최종 P * V * M 매트릭스
            Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, pv, 0, mModelMatrix, 0);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
            mStage[ConstMgr.STAGE].planetList[i].draw(mMVPMatrix);
        }
        //미사일
        if(ConstMgr.RENDER_MODE == ConstMgr.RENDER_ANIMATION) {
            int turnframe = mStage[ConstMgr.STAGE].currentFrame - mStage[ConstMgr.STAGE].turn * ConstMgr.FRAME_PER_TURN;
            if (turnframe < ConstMgr.FRAME_PER_TURN) {
                for (int j = 0; j < mStage[ConstMgr.STAGE].listSize; j++) {
                    for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); i++) {
                        if(mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.getIsActive() && (turnframe < mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.getLife())) {
                            Matrix.setIdentityM(tempMatrix, 0);
                            Matrix.setIdentityM(mModelMatrix, 0);
                            Matrix.scaleM(tempMatrix, 0, 0.0001f, 0.0001f, 0.0001f);
                            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
                            Matrix.setIdentityM(tempMatrix, 0);
                            Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.angleBuffer[turnframe] - 90, 0.0f, 1.0f, 0.0f);
                            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
                            Matrix.setIdentityM(tempMatrix, 0);
                            Vector3f pos = new Vector3f();
                            pos.copy(mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.positionBuffer[turnframe]);
                            Matrix.translateM(tempMatrix, 0, pos.x, pos.y, pos.z);
                            Matrix.multiplyMM(mModelMatrix, 0, tempMatrix, 0, mModelMatrix, 0);
                            //최종 P * V * M 매트릭스
                            Matrix.multiplyMM(mMVPMatrix, 0, pv, 0, mModelMatrix, 0);
                            Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, mModelMatrix, 0);
                            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
                            mMissile.draw(mMVPMatrix);
                        }
                    }
                }
            }
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // Render UI
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
        //buttons
        //mBackButton.draw(orth);
        mShootButton.draw(orth);
        if(ConstMgr.TURN_MODE == ConstMgr.TURN_AIM) {
            for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); i++) {
                mMissileButton[i].draw(orth);
            }
        }
        for(int i = 0 ; i < 2; i++) {
            mModeButton[i].draw(orth);
        }
        // 궤도
        if((ConstMgr.RENDER_MODE == ConstMgr.RENDER_SETTING) && (ConstMgr.TURN_MODE == ConstMgr.TURN_AIM)) {
            GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 1);
            for(int i = 0 ; i < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize() ; i++) {
                if( mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.getIsAimed()) {
                    mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.draw(pv);
                }
            }
            GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        }
        mPopup.draw(orth);
    }

    private void RenderTest(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Vector3f color = new Vector3f(1, 1, 1);
        mParticleSystem.addParticle(frame, ConstMgr.PARTICLE_LIFE, color);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "life"), ConstMgr.PARTICLE_LIFE);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bUI"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bPS"), 1);
        mParticleSystem.draw(pv);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "bPS"), 0);
    }

    //터치 이벤트
    float x1= 1.f;
    float x2= 1.f;
    float y1= 1.f;
    float y2= 1.f;
    float px=1.f;
    float py=1.f;
    float distance = 1.f;
    boolean aiming = false;

    private int mPointerId;
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        mPointerId = event.getPointerId(0);
        final int action = event.getActionMasked();
        if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_INTRO) {
            //인트로 화면 이벤트
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    if(mPopup.getIsActive()){
                        if(mPopup.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            mPopup.setIsActive(false);
                        }
                    }else if(mIntroButtons[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                    }else if(mIntroButtons[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        mPopup.setMode(ConstMgr.POPUP_MODE_EXIT_APP);
                        mPopup.setIsActive(true);
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE) {
            //스테이지 선택창 이벤트
            switch(action) {
                case MotionEvent.ACTION_DOWN: {
                    for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
                        if(mStageButtons[i].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            ConstMgr.STAGE = 0;
                            mStage[ConstMgr.STAGE].updatePosInList(0);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].copyUserData(mUser);
                            for( int j = 0 ; j < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize() ; j++) {
                                mMissileButton[j].setIsActive(true);
                            }

                            //initStage(ConstMgr.STAGE);
                            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_GAME;
                            startTime = mLastTime;
                        }
                    }
                    if(mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        Vector3f pos = new Vector3f(0,0,0);
                        mParticleSystem.addEmitter(pos, pos);
                        startTime = mLastTime;
                        ConstMgr.SCREEN_MODE = 4;
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if(mShootButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        beforeSimul();
                        ConstMgr.RENDER_MODE = ConstMgr.RENDER_ANIMATION;
                        ConstMgr.TURN_MODE = ConstMgr.TURN_CAMERA;
                        ConstMgr.CANNON = ConstMgr.CANNON_NOTHING;
                        mStage[ConstMgr.STAGE].turnStartFrame = frame;
                        mShootButton.setIsActive(false);
                    } else if(mModeButton[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.TURN_MODE = ConstMgr.TURN_AIM;
                    } else if(mModeButton[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.TURN_MODE = ConstMgr.TURN_CAMERA;
                    } else if ((ConstMgr.RENDER_MODE == ConstMgr.RENDER_SETTING) && (ConstMgr.TURN_MODE == ConstMgr.TURN_AIM)) {
                        for(int i = 0 ; i < 5 ; i ++){
                            if(mMissileButton[i].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                                ConstMgr.CANNON = i;
                                return true;
                            }
                        } if(ConstMgr.CANNON != ConstMgr.CANNON_NOTHING) {
                            aiming = true;
                            //Vector3f target = new Vector3f();
                            Vector3f target = selectplane(x, y);
                            target = target.minus(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos());
                            target.normalize();
                            target = target.multScalar(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].getSpeed());
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setShootVelocity(target);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setupVertexBuffer(mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setIsAimed(true);///
                        }
                    }
                    touchObjectCheck(x,y);
                    x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                    y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() > 1){//멀티터치상태로 움직일때
                        px = (x1+x2)/2;
                        py = (y1+y2)/2;
                        x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                        y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
                        x2 = mScreenConfig.deviceToVirtualX((int) event.getX(1));  //////////////////////
                        y2 = mScreenConfig.deviceToVirtualY((int) event.getY(1));  //////////////////////
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
                        if(ConstMgr.TURN_MODE == ConstMgr.TURN_CAMERA) {
                            px = x1;
                            py = y1;
                            x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                            y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
                            if (Math.abs(px - x1) > 2) {
                                mCamera.setRotateY((px - x1) / 2.0f);
                                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                            }
                            if (Math.abs(py - y1) > 2) {
                                mCamera.setRotateX((y1 - py) / 2.0f);
                                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                            }
                        } else if (aiming) {
                            if(ConstMgr.CANNON != ConstMgr.CANNON_NOTHING) {
                                //Vector3f target = new Vector3f();
                                Vector3f target = selectplane(x,y);
                                target = target.minus(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos());
                                target.normalize();
                                target = target.multScalar(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].getSpeed());
                                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setShootVelocity(target);
                                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setupVertexBuffer(mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP :
                    if(aiming) {
                        //Vector3f direction = new Vector3f();
                        //Vector3f cannonDirection = new Vector3f();
                        Vector3f direction = new Vector3f();
                        Vector3f cannonDirection = new Vector3f();
                        direction.copy(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootVelocity());
                        direction.normalize();
                        cannonDirection.copy(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos().minus(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCurrentPos()));
                        cannonDirection.normalize();
                        if(direction.dot(cannonDirection) > 0) {
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setIsAimed(true);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].missile.setCurrentPos(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos());
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].missile.setVelocity(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootVelocity());
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].missile.setIsActive(true);
                        } else {
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setIsAimed(false);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].missile.setIsActive(false);
                        }
                        aiming = false;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));    ///////////////////////
                    y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  ////////////////////
                    x2 = mScreenConfig.deviceToVirtualX((int) event.getX(1));  //////////////////////
                    y2 = mScreenConfig.deviceToVirtualY((int) event.getY(1));  //////////////////////
                    distance = (float)Math.sqrt((double)((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
            }
        }
        return true;
    }

    private int touchObjectCheck(int x, int y){
        float temp_x = (2.0f * x) / mDeviceWidth - 1.0f;
        float temp_y = 1.0f - (2.0f * y) / mDeviceHeight;
        float temp_z = 1.0f;
        float[] ray_nds = new float[3];
        ray_nds[0] = temp_x;
        ray_nds[1] = temp_y;
        ray_nds[2] = temp_z;
        float[] ray_clip = new float[4];
        ray_clip[0] = ray_nds[0];
        ray_clip[1] = ray_nds[1];
        ray_clip[2] = -1.0f;
        ray_clip[3] = 1.0f;
        float[] ray_eye = new float[4];
        float[] temp_inv = new float[16];
        Matrix.invertM(temp_inv, 0, mCamera.projectionMatrix, 0);
        Matrix.multiplyMV(ray_eye, 0, temp_inv, 0, ray_clip, 0);
        ray_eye[2] = -1.0f;
        ray_eye[3] = 0.0f;
        Matrix.invertM(temp_inv, 0, mCamera.viewMatrix, 0);
        Matrix.multiplyMV(ray_eye, 0, temp_inv, 0, ray_eye, 0);
        Vector3f ray_wor = new Vector3f(ray_eye[0],ray_eye[1],ray_eye[2]);
        ray_wor.normalize();

        int result = -1;
        float tempdistance = 0.0f;
        for(int i=0;i<4;i++){//모든 행성에대해서 잘모름 ㅠㅠ
            Vector3f temp1 = new Vector3f(mCamera.eye[0]-mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().x,mCamera.eye[1]-mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().y,mCamera.eye[2]-mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().z);//planet현위치 넣기
            float b = ray_wor.dot(temp1);
            float c = temp1.dot(temp1) - mStage[ConstMgr.STAGE].planetList[i].getRadius()*mStage[ConstMgr.STAGE].planetList[i].getRadius(); //아마도 r^2으로 예상됨

            if ((b*b - c >= 0) && ((-b + Math.sqrt(b * b - c ) > 0) || ((-b - Math.sqrt(b * b - c) > 0))))
            {
                //mStage[ConstMgr.STAGE].planetList[i].setRadius(mStage[ConstMgr.STAGE].planetList[i].getRadius()+1.0f);
                //이때 i 번째 행성 선택
                if(result == -1){
                    result = i;
                    tempdistance = temp1.length();
                }
                else{
                    if(tempdistance>temp1.length()){
                        result = i;
                        tempdistance = temp1.length();
                    }
                }
            }
        }
        if(result != -1) {
            mCamera.setAtMove(mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().x, mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().y, mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().z);
            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
        }
        return result;
    }
    private Vector3f selectplane(int x, int y){
        float temp_x = (2.0f * x) / mDeviceWidth - 1.0f;
        float temp_y = 1.0f - (2.0f * y) / mDeviceHeight;
        float temp_z = 1.0f;
        float[] ray_nds = new float[3];
        ray_nds[0] = temp_x;
        ray_nds[1] = temp_y;
        ray_nds[2] = temp_z;
        float[] ray_clip = new float[4];
        ray_clip[0] = ray_nds[0];
        ray_clip[1] = ray_nds[1];
        ray_clip[2] = -1.0f;
        ray_clip[3] = 1.0f;
        float[] ray_eye = new float[4];
        float[] temp_inv = new float[16];
        Matrix.invertM(temp_inv, 0, mCamera.projectionMatrix, 0);
        Matrix.multiplyMV(ray_eye, 0, temp_inv, 0, ray_clip, 0);
        ray_eye[2] = -1.0f;
        ray_eye[3] = 0.0f;
        Matrix.invertM(temp_inv, 0, mCamera.viewMatrix, 0);
        Matrix.multiplyMV(ray_eye, 0, temp_inv, 0, ray_eye, 0);
        Vector3f ray_wor = new Vector3f(ray_eye[0],ray_eye[1],ray_eye[2]);
        ray_wor.normalize();

        Vector3f result = new Vector3f();
        Vector3f temp1 = new Vector3f(mCamera.eye[0],mCamera.eye[1],mCamera.eye[2]);//planet현위치 넣기
        float b = temp1.dot(new Vector3f(0.f,1.f,0.f));
        float c = ray_wor.dot(new Vector3f(0.f,1.f,0.f)); //아마도 r^2으로 예상됨

        if (c!=0 && (b/c)<0)
        {
            //mStage[ConstMgr.STAGE].planetList[i].setRadius(mStage[ConstMgr.STAGE].planetList[i].getRadius()+1.0f);
            //이때 i 번째 행성 선택
            result.setXYZ(mCamera.eye[0] + ray_wor.x * (-b / c), mCamera.eye[1] + ray_wor.y * (-b / c), mCamera.eye[2] + ray_wor.z * (-b / c));
        }
        return result;
    }

    public void onPopupResponse(int mode, int response) {
        if(mode == ConstMgr.POPUP_MODE_EXIT_APP) {
            if(response == ConstMgr.POPUP_RES_YES) {
                mPopup.setIsActive(false);
                mActivity.moveTaskToBack(true);
                mActivity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }else if(response == ConstMgr.POPUP_RES_NO) {
                mPopup.setIsActive(false);
            }
        } else if (mode == ConstMgr.POPUP_MODE_EXIT_GAME) {
            if(response == ConstMgr.POPUP_RES_YES) {
                mPopup.setIsActive(false);
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
            }else if(response == ConstMgr.POPUP_RES_NO) {
                mPopup.setIsActive(false);
            }
        } else if (mode == ConstMgr.POPUP_MODE_AIM) {
            if(response == ConstMgr.POPUP_RES_YES) {
                mPopup.setIsActive(false);
            }else if(response == ConstMgr.POPUP_RES_NO) {
                mPopup.setIsActive(false);
            }
        } else if (mode == ConstMgr.POPUP_MODE_WIN) {
            mPopup.setIsActive(false);
            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
        } else if (mode == ConstMgr.POPUP_MODE_LOSE) {
            mPopup.setIsActive(false);
            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
        }

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