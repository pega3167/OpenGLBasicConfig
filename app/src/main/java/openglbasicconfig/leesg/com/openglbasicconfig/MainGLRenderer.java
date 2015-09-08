package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
    Square mIntroScreen;
    Button mIntroButtons[];
    Square mStageScreen;
    Button mStageButtons[];
    Button mShootButton;
    Button mMissileButton[];
    Button mModeButton[];
    Button mBackButton;
    Aim mAim;
    //3D 오브젝트
    Mesh mMissile;
    Planet mUser;
    // planet texture
    int planetTexureHandle[] = new int[1];
    // Stage
    Stage mStage[] = new Stage[1];
    boolean isAnimation = false;
    // frame
    int frame = 0;
    long startTime = 0;
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
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_R"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_G"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_B"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_A"), 0.0f);
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
        mMissile.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample", true));


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
        //missile select button
        mMissileButton = new Button[5];
        for ( int i = 0 ; i < 5 ; i++) {
            mMissileButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mMissileButton[i].setBitmap(mBitmapLoader.getImageHandle("drawable/missilebutton", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
            mMissileButton[i].setPos(mScreenConfig.getmVirtualHeight() / 12, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 4 - i * mScreenConfig.getmVirtualHeight() / 6);
            mMissileButton[i].setIsActive(false);
        }
        mModeButton = new Button[2];
        for(int i = 0 ; i < 2 ; i++) {
            mModeButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mModeButton[i].setPos(mScreenConfig.getmVirtualWidth() - mScreenConfig.getmVirtualHeight() / 12 - i * mScreenConfig.getmVirtualHeight() / 6 , 11 * mScreenConfig.getmVirtualHeight() / 12);
            mModeButton[i].setIsActive(true);
        }
        mModeButton[0].setBitmap(mBitmapLoader.getImageHandle("drawable/aim", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        mModeButton[1].setBitmap(mBitmapLoader.getImageHandle("drawable/eye", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        for(int i = 0 ; i < ConstMgr.STAGEBUTTON_NUM ; i++) {
            mStageButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/stage", false),mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
            mStageButtons[i].setPos(mScreenConfig.getmVirtualHeight() / 6 + i * mScreenConfig.getmVirtualHeight() / 6, 4 * mScreenConfig.getmVirtualHeight() / 6);
            mStageButtons[i].setIsActive(true);
        }
        mShootButton = new Button(mProgramImage, mProgramSolidColor, this);
        mShootButton.setBitmap(mBitmapLoader.getImageHandle("drawable/launch", false), mScreenConfig.getmVirtualWidth() / 6, mScreenConfig.getmVirtualWidth() / 6);
        mShootButton.setPos(mScreenConfig.getmVirtualWidth() * 11 / 12, mScreenConfig.getmVirtualWidth() / 12);
        mShootButton.setIsActive(true);
        //폰트

        //행성 텍스쳐 로드
        planetTexureHandle[0] = mBitmapLoader.getImageHandle("drawable/earthmap", true);
        //유저 정보 로드
        Vector3f temp = new Vector3f();
        mUser = new Planet(mProgramImage, 0.0f, 0.1f, 0.2f, 1.0f, 1, 1, 0.0001f, 1.0f, temp);
        mUser.setBitmap(planetTexureHandle[0], 1024, 512);
        temp.setXYZ(1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD);
        temp.setXYZ(-1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD);
        //스테이지 정보 로드
        initStage(0);
    }

    public void initStage(int stageNum) {
        Vector3f temp = new Vector3f();
        temp.setXYZ(0, 0, 0);
        switch(stageNum) {
            case 0: {
                mStage[stageNum] = new Stage(mProgramImage, 4, 3);
                mStage[stageNum].planetList[0] = new Planet(mProgramImage, 0.0f, 0.5f, 0.0f, 0.0f, 1, 1, 0.0001f, 1.0f, temp);
                mStage[stageNum].planetList[1] = new Planet(mProgramImage, 1.0f, 0.1f, 0.2f, 0.1f, 1, 1, 0.0001f, 1.0f, temp);
                mStage[stageNum].planetList[2] = new Planet(mProgramImage, 2.0f, 0.1f, 0.1f, 1.3f, 1, 1, 0.0001f, 1.0f, temp);
                mStage[stageNum].planetList[3] = new Planet(mProgramImage, 3.0f, 0.1f, 0.03f, 0.6f, 1, 1, 0.0001f, 1.0f, temp);

                for(int i = 0 ; i < mStage[stageNum].listSize ; i ++) {
                    mStage[stageNum].planetList[i].setBitmap(planetTexureHandle[0], 1024, 512);
                }
                mStage[stageNum].planetList[3].cannons[0].aim = new Aim(mProgramImage);
                break;
            }
        }
    }

    public void update() {
        if(ConstMgr.RENDER_MODE == ConstMgr.RENDER_ANIMATION) {
            //update missile info
        }
        // send light & material properties
        mLight.sendLight();
        mLight.sendMaterial();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "viewMatrix"), 1, false, mCamera.viewMatrix, 0);
    }

    public void beforeSimul() {
        for(int j = 0 ; j < mStage[ConstMgr.STAGE].listSize ; j++) {
            for(int k = 0 ; k < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); k++) {
                if(mStage[ConstMgr.STAGE].planetList[j].cannons[k].aim.getIsAimed()) {
                    mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.setIsActive(true);
                    Log.e("","" + mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.getVelocity().x);
                    for (int i = 0; i < ConstMgr.FRAME_PER_TURN; i++) {
                        mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.updateBuffer(i, mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize);
                        mStage[ConstMgr.STAGE].updatePosInList(ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn + i);
                        Vector3f pos =new Vector3f();
                        pos.copy(mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.positionBuffer[i]);
                    }
                    mStage[ConstMgr.STAGE].updatePosInList(ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn);
                }
            }
        }
    }
    public void afterSimul() {
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
            if (turnframe < ConstMgr.FRAME_PER_TURN / 10 * 8) {
                for (int j = 0; j < mStage[ConstMgr.STAGE].listSize; j++) {
                    for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); i++) {
                        if(mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.getIsActive()) {
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
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_R"), 0.5f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_G"), 0.5f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_B"), 0.5f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_A"), 0.0f);
        for(int i = 0 ; i < 2; i++) {
            mModeButton[i].draw(orth);
        }
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_R"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_G"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_B"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_A"), 0.0f);
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
                    if(mIntroButtons[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                    }else if(mIntroButtons[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        //정말 종료하시겠습니까? 팝업창
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
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_INTRO;
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
}