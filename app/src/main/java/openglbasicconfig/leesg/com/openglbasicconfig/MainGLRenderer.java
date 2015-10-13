package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by LeeSG on 2015-08-13.
 */
public class MainGLRenderer implements GLSurfaceView.Renderer {
    //매트릭스
    private float[] mMtrxProjectionAndView = new float[16]; // proj x view
    private float[] mMtrxOrthoAndView = new float[16];
    private float[] mModelViewMatrix = new float[16];
    //프로그램
    private static int mProgramImage;
    private static int mProgramBlur;
    private static int mProgramSolidColor;
    //프로그램 시간
    long mLastTime;
    long mPausedTime;
    //디바이스의 넓이, 높이
    public static int mDeviceWidth;
    public static int mDeviceHeight;
    //FBO
    // Render to Texture Variables
    private RTTQuad mRTT;
    private RTTQuad mBlur;


    //주 액티비티
    MainActivity mActivity;
    MainGLSurfaceView mGLSurfaceView;
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
    Square mMainScreen;
    // UI 아이콘
    Square mGoldIcon;
    Square mGoldAmount;
    Square mLevelIcon;
    Square mLevelFont;
    Square mWeaponPointFont;
    Square3D mBeacon;
    Vector3f mSelectPos;
    // Inventory
    Inventory mInven;
    Square invenBG;
    Square invenWF;
    Square blankItem;
    Square selectIcon;
    Square equipIcon;
    Button equipBtn;
    Button unEquipBtn;
    Square equipFrame;
    //item DB
    ItemList mItemList;
    Square[] itemIcon;

    // 화면 버튼
    Button mIntroButtons[];
    Button mMainButtons[];
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
    // 일시정지 창
    private static Pause mPause;
    private static Square pauseWindow;
    private static Button[] pauseBtns = new Button[3];
    //3D 오브젝트
    Mesh mMissile;
    Sphere mSpaceMap;
    orbital mOrb;
    orbital_arrow mAOrb;
    // 유저 데이터
    UserData mUserData;
    Planet mUser;
    boolean mUserGoldChanged = false;
    boolean mStageSelected = false;
    boolean mUserLevelChanged = false;
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
    public MainGLRenderer(MainActivity activity, int width, int height, MainGLSurfaceView mainGLSurfaceView) {
        mActivity = activity;
        mGLSurfaceView = mainGLSurfaceView;
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
        try {
            if (!mPause.getIsActive() && !mPause.getPause()) {
                mPause.setIsActive(true);
                mPausedTime = mLastTime;
                Log.e("now","now");
            }
        }catch (Exception e) {}
    }

    // 재시작
    public void onResume() {
    }
    // Rendering 화면을 텍스쳐로 변환

    // 서피스뷰 변경
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);
        Matrix.setIdentityM(mMtrxProjectionAndView, 0);
        Matrix.setIdentityM(mMtrxOrthoAndView, 0);
        // look at
        mCamera.setViewMatrix();
        // perspective
        mCamera.setProjectionMatrix();
        // perspective x lookat
//        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
//        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);

        //update user info
//        recoverResource();
//        Log.e("recovered!!","hmm..");
    }

    // 서피스뷰 생성
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mScreenConfig = new ScreenConfig(mDeviceWidth, mDeviceHeight);
        mScreenConfig.setSize(1280, 720);
        mProgramImage = ESShader.loadProgramFromAsset(mContext, "shaders/shader.vert", "shaders/shader.frag");
        mProgramBlur = ESShader.loadProgramFromAsset(mContext, "shaders/blur.vert", "shaders/blur.frag");
        GLES20.glUseProgram(mProgramImage);
        // 처음에만 초기화하고 인터럽트 발생 후에는 텍스쳐만 바인드 한다.
        if (mIsFirstCalled) {
            init();
            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_INTRO;
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
        initMainScreen();
        initStageScreen();
        initGameScreen();
        initEquipScreen();
        initPopup();
        initFBO();
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
        mMissile.loadOBJ("3Dobjects/missile");
        // 궤도와 궤도 화살표
        mOrb = new orbital(mProgramImage);
        mAOrb = new orbital_arrow(mProgramImage, 1);

        itemIcon = new Square[10];
        for (int i = 0; i < 10; i++) {
            itemIcon[i] = new Square(mProgramImage);
            itemIcon[i].setIsActive(true);
        }
        mItemList = new ItemList(itemIcon);

        setResourceObject();
    }

    private void initUser() {
        //유저 정보 로드
        mUserData = new UserData(mActivity, mItemList);
        Vector3f temp = new Vector3f();
        Vector3f pos = new Vector3f();
        mUser = new Planet(mProgramImage, 0.0f, 0.1f, 0.2f, 1.0f, 1, 1, 0.0001f, 1.0f, temp);
        mUserData.userPlanet = mUser;
        temp.setXYZ(1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD, 0);
        mParticleSystem.addEmitter(pos, pos);
        temp.setXYZ(-1.0f, 0.0f, 0.0f);
        mUser.addCannon(temp, 10, 0.015f, ConstMgr.MISSILE_STANDARD, 1);
        mParticleSystem.addEmitter(pos, pos);

        mGoldAmount = new Square(mProgramImage);
        mGoldAmount.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mGoldAmount.setIsActive(true);
        setResourceUser();
    }

    private void initStage() {
        Vector3f temp = new Vector3f();
        temp.setXYZ(0, 0, 0);
        mStage[0] = new Stage(mProgramImage, 4, 3);
        mStage[0].planetList[0] = new Planet(mProgramImage, 0.0f, 0.5f, 0.0f, 0.0f, 1, 1, 0.0005f, 1.0f, temp);
        mStage[0].planetList[1] = new Planet(mProgramImage, 1.0f, 0.1f, 0.2f, 0.1f, 1, 1, 0.0001f, 1.0f, temp);
        mStage[0].planetList[2] = new Planet(mProgramImage, 2.0f, 0.1f, 0.1f, 1.3f, 1, 1, 0.0001f, 1.0f, temp);
        mStage[0].planetList[3] = new Planet(mProgramImage, 3.0f, 0.1f, 0.03f, 0.6f, 1, 1, 0.0f, 1.0f, temp);

        mSpaceMap = new Sphere(mProgramImage);
        mSpaceMap.flip();
        setResourceStage();
    }

    private void initIntroScreen() {
        //initialize screen
        mIntroScreen = new Square(mProgramImage);
        mIntroScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mIntroScreen.setIsActive(true);
        //initioalize buttons
        mIntroButtons = new Button[ConstMgr.INTROBUTTON_NUM];
        for (int i = 0; i < ConstMgr.INTROBUTTON_NUM; i++) {
            mIntroButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        for (int i = 0; i < ConstMgr.INTROBUTTON_NUM; i++) {
            mIntroButtons[i].setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2 - i * mScreenConfig.getmVirtualHeight() * 3 / 10);
            mIntroButtons[i].setIsActive(true);
        }
        setResourceIntroScreen();
    }

    private void initMainScreen() {
        // 게임 틀
        mMainScreen = new Square(mProgramImage);
        mMainScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mMainScreen.setIsActive(true);
        //버튼
        mMainButtons = new Button[ConstMgr.MAINBUTTON_NUM];
        for (int i = 0; i < ConstMgr.MAINBUTTON_NUM; i++) {
            mMainButtons[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        mMainButtons[0].setIsActive(true);
        mMainButtons[0].setPos((float) mScreenConfig.getmVirtualWidth() * 29.0f / 32.0f, mScreenConfig.getmVirtualHeight() / 4);
        mMainButtons[1].setIsActive(true);
        mMainButtons[1].setPos(mScreenConfig.getmVirtualWidth() / 4, mScreenConfig.getmVirtualHeight() / 5);
        mMainButtons[2].setIsActive(true);
        mMainButtons[2].setPos((float) mScreenConfig.getmVirtualWidth() / 8, mScreenConfig.getmVirtualHeight() / 5);
        //UI 아이콘
        mGoldIcon = new Square(mProgramImage);
        mGoldIcon.setIsActive(true);
        mGoldIcon.setPos(mScreenConfig.getmVirtualWidth() / 2.0f, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
        mLevelIcon = new Square(mProgramImage);
        mLevelIcon.setIsActive(true);
        mLevelIcon.setPos(mScreenConfig.getmVirtualWidth() / 8.0f, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
        mLevelFont = new Square(mProgramImage);
        mLevelFont.setIsActive(true);
        setResourceMainScreen();
    }

    private void initEquipScreen() {
        mSelectPos = new Vector3f();
        mBeacon = new Square3D(mProgramImage, 0.5f, 0.5f);
        invenBG = new Square(mProgramImage);
        invenWF = new Square(mProgramImage);
        blankItem = new Square(mProgramImage);
        selectIcon = new Square(mProgramImage);
        equipIcon = new Square(mProgramImage);
        equipBtn = new Button(mProgramImage, mProgramSolidColor, this);
        unEquipBtn = new Button(mProgramImage, mProgramSolidColor, this);
        equipFrame = new Square(mProgramImage);
        mInven = new Inventory(this, invenBG, invenWF, blankItem, selectIcon, equipIcon, equipBtn, unEquipBtn, equipFrame,
                mUserData, mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight(), mSelectPos, mItemList);
        mInven.setPos(mScreenConfig.getmVirtualWidth() / 6.0f * 5, mScreenConfig.getmVirtualHeight() / 2);
        mInven.setIsActive(true);

        setResourceEquipScreen();
    }

    private void initStageScreen() {
        //initialize screen
        mStageScreen = new Square(mProgramImage);
        mStageScreen.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mStageScreen.setIsActive(true);
        // initialize buttons
        mStageButtons = new Button[ConstMgr.STAGEBUTTON_NUM];
        for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
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
        for (int i = 0; i < 5; i++) {
            mMissileButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mMissileButton[i].setPos(mScreenConfig.getmVirtualHeight() / 12, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 4 - i * mScreenConfig.getmVirtualHeight() / 6);
            mMissileButton[i].setIsActive(false);
        }
        mModeButton = new Button[2];
        for (int i = 0; i < 2; i++) {
            mModeButton[i] = new Button(mProgramImage, mProgramSolidColor, this);
            mModeButton[i].setPos(mScreenConfig.getmVirtualWidth() - mScreenConfig.getmVirtualHeight() / 12 - i * mScreenConfig.getmVirtualHeight() / 6, 11 * mScreenConfig.getmVirtualHeight() / 12);
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
        for (int i = 0; i < ConstMgr.POPUP_MODE_SIZE; i++) {
            popupStr[i] = new Square(mProgramImage);
        }
        for (int i = 0; i < ConstMgr.POPUP_BUTTON_SIZE; i++) {
            popupBtns[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        mPopup = new Popup(this, popupWindow, popupStr, popupBtns, mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        mPopup.setIsActive(false);
        // 일시정지
        pauseWindow = new Square(mProgramImage);
        for (int i = 0; i < 3; i++) {
            pauseBtns[i] = new Button(mProgramImage, mProgramSolidColor, this);
        }
        mPause = new Pause(this, pauseWindow, pauseBtns, mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        setResourcePopup();
    }

    private void recoverResource() {
        initResourceLoader();
        initPlanetResource();
        initFBO();
        setResourceObject();
        setResourceUser();
        setResourceStage();
        setResourceIntroScreen();
        setResourceMainScreen();
        setResourceStageScreen();
        setResourceGameScreen();
        setResourceEquipScreen();
        setResourcePopup();
    }

    private void setResourceObject() {
        mMissile.setBitmap(mBitmapLoader.getImageHandle("drawable/missilesample", true));
        //particleTextureHandle[0] = mBitmapLoader.getImageHandle("drawable/particle", true);
        mParticleSystem.setBitmap(mBitmapLoader.getImageHandle("drawable/particle", true));

        for (int i = 0; i < 10; i++) {
            try {
                itemIcon[i].setBitmap(mBitmapLoader.getImageHandle("drawable/item" + i, true), mScreenConfig.getmVirtualWidth() / 16, mScreenConfig.getmVirtualHeight() / 9);
            } catch (Exception e) {
                itemIcon[i].setBitmap(mBitmapLoader.getImageHandle("drawable/blank", true), mScreenConfig.getmVirtualWidth() / 16, mScreenConfig.getmVirtualHeight() / 9);
            }
        }
    }

    private void setResourceUser() {
        mUser.setBitmap(planetTexureHandle[0], 1024, 512, 36);
        String tempStr = NumberFormat.getNumberInstance(Locale.US).format(mUserData.getGold());
        mGoldAmount.setBitmap(mBitmapLoader.getHangulHandle(tempStr, mScreenConfig.getmVirtualHeight() / 15, Color.WHITE, -1, 1.0f, mHangulBitmap.TRANSFORMERS, 1), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 15);
        mGoldAmount.setPosRight(mScreenConfig.getmVirtualWidth() / 16 * 11, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
    }

    private void setResourceStage() {
        mStage[0].planetList[0].setBitmap(planetTexureHandle[1], 1024, 512, 36);
        mStage[0].planetList[1].setBitmap(planetTexureHandle[0], 1024, 512, 36);
        mStage[0].planetList[2].setBitmap(planetTexureHandle[0], 1024, 512, 36);
        mStage[0].planetList[3].setBitmap(planetTexureHandle[0], 1024, 512, 36);

        mSpaceMap.setBitmap(mBitmapLoader.getImageHandle("drawable/milkyafter", true), 1600, 859, 36);
    }

    private void setResourceIntroScreen() {
        mIntroScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/intro", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        for (int i = 0; i < ConstMgr.INTROBUTTON_NUM; i++) {
            mIntroButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/button" + i, false), mScreenConfig.getmVirtualWidth() / 5, mScreenConfig.getmVirtualHeight() / 5);
        }
    }

    private void setResourceMainScreen() {
        mMainScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/mainscreen", true), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        mMainButtons[0].setBitmap(mBitmapLoader.getImageHandle("drawable/launch", false), mScreenConfig.getmVirtualWidth() / 6, mScreenConfig.getmVirtualHeight() * 8 / 27);
        mMainButtons[1].setBitmap(mBitmapLoader.getImageHandle("drawable/shop", false), mScreenConfig.getmVirtualWidth() / 8, mScreenConfig.getmVirtualHeight() * 2 / 9);
        mMainButtons[2].setBitmap(mBitmapLoader.getImageHandle("drawable/equip", false), mScreenConfig.getmVirtualWidth() / 8, mScreenConfig.getmVirtualHeight() * 2 / 9);
        //UI ICON
        mGoldIcon.setBitmap(mBitmapLoader.getImageHandle("drawable/goldcoin", false), mScreenConfig.getmVirtualWidth() / 32, mScreenConfig.getmVirtualHeight() / 18);
        mLevelIcon.setBitmap(mBitmapLoader.getHangulHandle("Lv.", mScreenConfig.getmVirtualHeight() / 12, Color.WHITE, -1, 1.0f, mHangulBitmap.MARLBORO, 0), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 12);
        mLevelFont.setBitmap(mBitmapLoader.getHangulHandle(Integer.toString(mUserData.getLevel()), mScreenConfig.getmVirtualHeight() / 15, Color.WHITE, -1, 1.0f, mHangulBitmap.TRANSFORMERS, 1), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 15);
        mLevelFont.setPosRight(mScreenConfig.getmVirtualWidth() / 16.0f * 3, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
    }

    private void setResourceStageScreen() {
        mStageScreen.setBitmap(mBitmapLoader.getImageHandle("drawable/stagescreen", false), mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
            mStageButtons[i].setBitmap(mBitmapLoader.getImageHandle("drawable/stage" + i, false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        }
        mBackButton.setBitmap(mBitmapLoader.getImageHandle("drawable/button3", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);

    }

    private void setResourceGameScreen() {
        for (int i = 0; i < 5; i++) {
            mMissileButton[i].setBitmap(mBitmapLoader.getImageHandle("drawable/missilebutton", false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
        }
        mModeButton[0].setBitmap(mBitmapLoader.getImageHandle("drawable/button5", false), mScreenConfig.getmVirtualHeight() / 6.0f, mScreenConfig.getmVirtualHeight() / 6.0f);
        mModeButton[1].setBitmap(mBitmapLoader.getImageHandle("drawable/button6", false), mScreenConfig.getmVirtualHeight() / 6.0f, mScreenConfig.getmVirtualHeight() / 6.0f);
        mShootButton.setBitmap(mBitmapLoader.getImageHandle("drawable/launch", false), mScreenConfig.getmVirtualWidth() / 6.0f, mScreenConfig.getmVirtualWidth() / 6.0f);
    }

    private void setResourceEquipScreen() {
        mBeacon.setBitmap(mBitmapLoader.getImageHandle("drawable/beacon", false));
        invenBG.setBitmap(mBitmapLoader.getImageHandle("drawable/inventory_back", false), mScreenConfig.getmVirtualWidth() / 2.0f, mScreenConfig.getmVirtualHeight() / 9.0f * 8);
        invenWF.setBitmap(mBitmapLoader.getImageHandle("drawable/inventory_frame", false), mScreenConfig.getmVirtualWidth() / 2.0f, mScreenConfig.getmVirtualHeight() / 9.0f * 8);
        blankItem.setBitmap(mBitmapLoader.getImageHandle("drawable/blank", false), mScreenConfig.getmVirtualWidth() / 16.0f, mScreenConfig.getmVirtualHeight() / 9.0f);
        selectIcon.setBitmap(mBitmapLoader.getImageHandle("drawable/select", false), mScreenConfig.getmVirtualWidth() / 16.0f, mScreenConfig.getmVirtualHeight() / 9.0f);
        equipIcon.setBitmap(mBitmapLoader.getImageHandle("drawable/used", false), mScreenConfig.getmVirtualWidth() / 16.0f, mScreenConfig.getmVirtualHeight() / 9.0f);
        equipBtn.setBitmap(mBitmapLoader.getImageHandle("drawable/buttonequip", false), mScreenConfig.getmVirtualWidth() / 32.0f * 5, mScreenConfig.getmVirtualHeight() / 9.0f);
        equipFrame.setBitmap(mBitmapLoader.getImageHandle("drawable/equip_frame", false), mScreenConfig.getmVirtualWidth() / 5.0f * 3, mScreenConfig.getmVirtualHeight() / 9.0f * 2);
        unEquipBtn.setBitmap(mBitmapLoader.getImageHandle("drawable/buttonunequip", false), mScreenConfig.getmVirtualWidth() / 32.0f * 5, mScreenConfig.getmVirtualHeight() / 9.0f);

    }

    private void setResourcePopup() {
        popupWindow.setBitmap(mBitmapLoader.getImageHandle("drawable/popup", false), mScreenConfig.getmVirtualWidth() * 3 / 4, mScreenConfig.getmVirtualHeight() * 3 / 4);
        String[] popupStrs = {" ", "정말로 게임을 종료하시겠습니까?", "진행중인 게임을 포기하고 나가시겠습니까?", "조준 되지 않은 미사일이 있습니다. 턴을 진행하시겠습니까?",
                "게임에서 승리하셨습니다!", "패배하였습니다..."};
        for (int i = 0; i < ConstMgr.POPUP_MODE_SIZE; i++) {
            popupStr[i].setBitmap(mBitmapLoader.getHangulHandle(popupStrs[i], mScreenConfig.getmVirtualHeight() / 15, Color.WHITE, -1, 1.0f), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 15);
        }
        for (int i = 0; i < ConstMgr.POPUP_BUTTON_SIZE; i++) {
            popupBtns[i].setBitmap(mBitmapLoader.getImageHandle("drawable/popup" + i, false), mScreenConfig.getmVirtualWidth() / 5, (int) (mScreenConfig.getmVirtualWidth() / 11.5));
        }
        pauseWindow.setBitmap(mBitmapLoader.getImageHandle("drawable/pause", false), mScreenConfig.getmVirtualWidth() * 9 / 8, mScreenConfig.getmVirtualHeight() * 3 / 2);
        String[] pauseStrs = {"계속하기", "옵션", "나가기"};
        for (int i = 0; i < 3; i++) {
            pauseBtns[i].setBitmap(mBitmapLoader.getHangulHandle(pauseStrs[i], mScreenConfig.getmVirtualHeight() / 18, Color.WHITE, -1, 1.0f, HangulBitmap.BMJUA), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 18);
        }
    }

    // 모든 프레임에 항상 하는일(턴중 + 시뮬레이션중)
    private void update() {
        // send light & material properties
        mLight.sendLight();
        mLight.sendMaterial();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "viewMatrix"), 1, false, mCamera.viewMatrix, 0);
        Vector3f color = new Vector3f(1, 1, 1);
        mParticleSystem.addParticle(frame, color);
    }

    // 시뮬레이션 전에 하는일
    private void beforeSimul() {
        for (int i = 0; i < 2; i++) {
            mModeButton[i].setIsActive(false);
        }
        for (int j = 0; j < mStage[ConstMgr.STAGE].listSize; j++) {
            for (int k = 0; k < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); k++) {
                if (mStage[ConstMgr.STAGE].planetList[j].cannons[k].aim.getIsAimed()) {
                    mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.setIsActive(true);
                    for (int i = 0; i < ConstMgr.FRAME_PER_TURN; i++) {
                        if (mStage[ConstMgr.STAGE].planetList[j].cannons[k].missile.updateBuffer(i, mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize))
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
        for (int i = 0; i < 2; i++) {
            mModeButton[i].setIsActive(true);
        }
        mStage[ConstMgr.STAGE].turn += 1;
        mStage[ConstMgr.STAGE].currentFrame = (ConstMgr.FRAME_PER_TURN * mStage[ConstMgr.STAGE].turn);
        ConstMgr.RENDER_MODE = ConstMgr.RENDER_SETTING;
        mShootButton.setIsActive(true);
        mStage[ConstMgr.STAGE].updatePosInList(mStage[ConstMgr.STAGE].currentFrame);
        for (int i = 0; i < mStage[ConstMgr.STAGE].listSize; i++) {
            mStage[ConstMgr.STAGE].planetList[i].updateCannon(mStage[ConstMgr.STAGE].currentFrame);
        }
        for (int j = 0; j < mStage[ConstMgr.STAGE].listSize; j++) {
            for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); i++) {
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.setIsAimed(false);
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].missile.setIsActive(false);
                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].missile.setLife(ConstMgr.MAX_AIM_VERTEXCOUNT);
            }
        }
        mParticleSystem.deactivate();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        long now = System.currentTimeMillis();
        if (mLastTime > now)
            return;
        long elapsed = now - mLastTime;
        int tempFrame = frame;
        frame = (int) ((mLastTime - startTime) * ConstMgr.FPS / 1000);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "frame"), frame);
        if (ConstMgr.RENDER_MODE == ConstMgr.RENDER_ANIMATION) {
            mStage[ConstMgr.STAGE].currentFrame = frame - mStage[ConstMgr.STAGE].turnStartFrame;
            if (mStage[ConstMgr.STAGE].currentFrame > ConstMgr.FRAME_PER_TURN) {
                afterSimul();
            } else {
                mStage[ConstMgr.STAGE].currentFrame += mStage[ConstMgr.STAGE].turn * ConstMgr.FRAME_PER_TURN;
            }
        }
        // perspective x lookat
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
        switch (ConstMgr.SCREEN_MODE) {
            case ConstMgr.SCREEN_INTRO: {
                RenderIntro(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
            case ConstMgr.SCREEN_MAIN: {
                RenderMain(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
            case ConstMgr.SCREEN_STAGE: {
                RenderStage(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
            case ConstMgr.SCREEN_GAME: {
                if (frame != tempFrame) {
                    update();
                }
                RenderGame(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
            case ConstMgr.SCREEN_EQUIP: {
                RenderEquip(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
            case ConstMgr.SCREEN_TEST: {
                if (frame != tempFrame) {
                    update();
                }
                RenderTest(mMtrxProjectionAndView, mMtrxOrthoAndView);
                break;
            }
        }
        if (!mPause.getPause()) {
            mLastTime = now;
        }
    }

    //초기화면
    private void RenderIntro(float[] pv, float[] orth) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        mIntroScreen.draw(orth);
        for (int i = 0; i < ConstMgr.INTROBUTTON_NUM; i++) {
            mIntroButtons[i].draw(orth);
        }
        mPopup.draw(orth);
        //setRenderTexture(filterBuf1, renderTex1);
        //DrawQuad();
    }

    //메인화면
    private void RenderMain(float[] pv, float[] orth) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        float[] tempMatrix = new float[16];
        float[] modelMatrix = new float[16];
        // sky sphere
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        float scaleVal = mCamera.lenghToEye() / 250.0f;
        scaleVal *= 6.0f;
        Matrix.scaleM(tempMatrix, 0, scaleVal, scaleVal, scaleVal);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, mCamera.at[0], mCamera.at[1], mCamera.at[2]);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        mSpaceMap.draw(modelMatrix);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        //GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        // user행성
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 45 - mUser.getRotateY(), 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
        mUser.draw(modelMatrix);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mMainScreen.draw(orth);
        mLevelIcon.draw(orth);
        mLevelFont.draw(orth);
        mGoldIcon.draw(orth);
        mGoldAmount.draw(orth);

        for (int i = 0; i < ConstMgr.MAINBUTTON_NUM; i++) {
            mMainButtons[i].draw(orth);
        }
    }

    //스테이지 선택 화면
    private void RenderStage(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        mStageScreen.draw(orth);
        mBackButton.draw(orth);
        for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
            mStageButtons[i].draw(orth);
        }
        mPopup.draw(orth);
    }

    //게임 화면
    private void RenderGame(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float[] tempMatrix = new float[16];
        float[] modelMatrix = new float[16];
        Vector3f temp = new Vector3f();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //**************************************************************************************************************************************************************
        // 태양 glow effect texture -> fboIdTexStep2 에 glow texture 저장됨
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glViewport(0, 0, fboWidth, fboHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //mMVPMatrix 계산
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[0].getRadius(), mStage[ConstMgr.STAGE].planetList[0].getRadius(), mStage[ConstMgr.STAGE].planetList[0].getRadius());
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[0].getRotationSpeed(), 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[0].getOrbitRadius(), 0.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[0].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        temp.setXYZ(0, 0, 0);
        temp.multM(modelMatrix, 1.0f);
        mStage[ConstMgr.STAGE].planetList[0].setCurrentPos(temp);
        //최종 P * V * M 매트릭스
        Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
        mStage[ConstMgr.STAGE].planetList[0].draw(modelMatrix);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        // Blur
        Blur(orth);

        //**************************************************************************************************************************************************************
        // Render 3D
        // fboScene에 전체Scene을 저장
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboScene);
//        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);
        GLES20.glViewport(0, 0, fboSceneWidth, fboSceneHeight);
        GLES20.glUseProgram(mProgramImage);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        GLES20.glFrontFace(GLES20.GL_CCW);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // sky sphere
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        float scaleVal = mCamera.lenghToEye() / 250.0f;
        scaleVal *= 6.0f;
        Matrix.scaleM(tempMatrix, 0, scaleVal, scaleVal, scaleVal);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, mCamera.at[0], mCamera.at[1], mCamera.at[2]);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
        mSpaceMap.draw(modelMatrix);
        GLES20.glDisable(GLES20.GL_CULL_FACE);


        //행성
        //model matrix 계산
        for (int i = 0; i < 4; i++) {
            if (i == 0)
                GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
            else
                GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_PHONG);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[i].getRadius(), mStage[ConstMgr.STAGE].planetList[i].getRadius(), mStage[ConstMgr.STAGE].planetList[i].getRadius());
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRotationSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, mStage[ConstMgr.STAGE].planetList[i].getOrbitRadius(), 0.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            //현재 행성 위치 업데이트
            temp.setXYZ(0, 0, 0);
            temp.multM(modelMatrix, 1.0f);
            mStage[ConstMgr.STAGE].planetList[i].setCurrentPos(temp);
            //최종 P * V * M 매트릭스
            Matrix.multiplyMM(mModelViewMatrix, 0, mCamera.viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgramImage, "modelViewMatrix"), 1, false, mModelViewMatrix, 0);
            //GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            mStage[ConstMgr.STAGE].planetList[i].draw(modelMatrix);
            //GLES20.glDisable(GLES20.GL_TEXTURE_2D);
            // 행성 궤도!
            GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_AIM);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(modelMatrix, 0);
            float radius = mStage[ConstMgr.STAGE].planetList[i].getOrbitRadius();
            Matrix.scaleM(tempMatrix, 0, radius, radius, radius);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, 90 + mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
            mOrb.draw(modelMatrix);
            // 궤도 방향 표시 화살표
            GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_AIM);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.scaleM(tempMatrix, 0, radius, radius, radius);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
//            radius -= 1.0f;
//            Matrix.translateM(tempMatrix, 0, radius, 0, 0);
//            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
//            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, 88.5f + mStage[ConstMgr.STAGE].currentFrame * mStage[ConstMgr.STAGE].planetList[i].getRevolutionSpeed(), 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
            mAOrb.draw(modelMatrix);
            //mAOrb.setupVertexBuffer(mStage[ConstMgr.STAGE].planetList, i);
            //mAOrb.draw(pv);
        }

        // 미사일 조준 궤도
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_AIM);
        if ((ConstMgr.RENDER_MODE == ConstMgr.RENDER_SETTING) && (ConstMgr.TURN_MODE == ConstMgr.TURN_AIM)) {
            for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); i++) {
                if (mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.getIsAimed()) {
                    mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[i].aim.draw(pv);
                }
            }
        }

        // 파티클 시스템
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_PARTICLE_SYSTEM);
        if (ConstMgr.RENDER_MODE == ConstMgr.RENDER_ANIMATION) {
            int turnframe = mStage[ConstMgr.STAGE].currentFrame - mStage[ConstMgr.STAGE].turn * ConstMgr.FRAME_PER_TURN;
            if (turnframe < ConstMgr.FRAME_PER_TURN) {
                for (int j = 0; j < mStage[ConstMgr.STAGE].listSize; j++) {
                    for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[j].getCannonListSize(); i++) {
                        if (mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.getIsActive() && (turnframe < mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.getLife())) {
                            mParticleSystem.emitterList.elementAt(mStage[ConstMgr.STAGE].planetList[j].cannons[i].emitterIndex).pos.copy(mStage[ConstMgr.STAGE].planetList[j].cannons[i].missile.positionBuffer[turnframe]);
                            mParticleSystem.emitterList.elementAt(mStage[ConstMgr.STAGE].planetList[j].cannons[i].emitterIndex).isActive = true;
                        } else {
                            mParticleSystem.emitterList.elementAt(mStage[ConstMgr.STAGE].planetList[j].cannons[i].emitterIndex).isActive = false;
                        }
                    }
                }
                GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "life"), ConstMgr.PARTICLE_LIFE);
                GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "pointSize"), mDeviceWidth / mCamera.lenghToEye() * 2);
                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
                mParticleSystem.draw(pv);
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //**************************************************************************************************************************************************************
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        // render 3D Scene with Bloom effect of sun
        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_BLOOM);
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mRTT.setSize(mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());
        mRTT.drawWithoutTex(orth);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexScene);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep2);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX1"), 1);
        mRTT.drawElements();
        //**************************************************************************************************************************************************************
        // Render UI
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        //buttons
        //mBackButton.draw(orth);
        mShootButton.draw(orth);
        if (ConstMgr.TURN_MODE == ConstMgr.TURN_AIM) {
            for (int i = 0; i < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); i++) {
                mMissileButton[i].draw(orth);
            }
        }
        for (int i = 0; i < 2; i++) {
            mModeButton[i].draw(orth);
        }
        // popup
        mPopup.draw(orth);
        // Pause
        mPause.draw(orth);
    }

    private void RenderEquip(float[] pv, float[] orth) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        float[] tempMatrix = new float[16];
        float[] modelMatrix = new float[16];

        // user행성
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 45 - mUser.getRotateY(), 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
        mUser.draw(modelMatrix);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), 5.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_R"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_G"), 1.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_B"), 0.5f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_A"), 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        Vector3f norm = new Vector3f(0f, 0f, 1f);
        Vector3f axis = new Vector3f();
        Vector3f target = new Vector3f();
        target.copy(mSelectPos);
        axis.copy(norm.Cross(target));
        axis.normalize();
        float angle = (float) Math.acos(norm.dot(target));
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Vector3f.setRotate(tempMatrix, axis, angle);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, target.x, target.y, target.z);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, 45 - mUser.getRotateY(), 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
        mBeacon.draw(modelMatrix);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_R"), 0.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_G"), 0.85f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_B"), 1.0f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "color_A"), 1.0f);
        for (int i = 0; i < 5; i++) {
            if (mUserData.equipList[i].DBindex == -1)
                break;
            else {
                target.copy(mUserData.equipList[i].pos);
                axis.copy(norm.Cross(target));
                axis.normalize();
                angle = (float) Math.acos(norm.dot(target));
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.setIdentityM(modelMatrix, 0);
                Vector3f.setRotate(tempMatrix, axis, angle);
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.translateM(tempMatrix, 0, target.x, target.y, target.z);
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.rotateM(tempMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.rotateM(tempMatrix, 0, 45 - mUser.getRotateY(), 0.0f, 0.0f, 1.0f);
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
                Matrix.multiplyMM(modelMatrix, 0, pv, 0, modelMatrix, 0);
                mBeacon.draw(modelMatrix);
            }
        }
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        mMainScreen.draw(orth);
        mLevelIcon.draw(orth);
        mLevelFont.draw(orth);
        mGoldIcon.draw(orth);
        mGoldAmount.draw(orth);
        mInven.draw(orth);
    }

    private void RenderTest(float[] pv, float[] orth) {
        GLES20.glUseProgram(mProgramImage);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glViewport(0, 0, fboWidth, fboHeight);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Vector3f color = new Vector3f(1, 1, 1);
        mParticleSystem.addParticle(frame, color);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "life"), ConstMgr.PARTICLE_LIFE);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_PARTICLE_SYSTEM);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "pointSize"), mDeviceWidth / mCamera.lenghToEye() * 2);
        mParticleSystem.draw(pv);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        Blur(orth);

        GLES20.glUseProgram(mProgramImage);
        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_NORMAL);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 4, mScreenConfig.getmVirtualHeight() / 4 * 3);
        mRTT.drawWithoutTex(orth);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);
        mRTT.drawElements();
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 4, mScreenConfig.getmVirtualHeight() / 4);
        mRTT.drawWithoutTex(orth);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);
        mRTT.drawElements();
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 4 * 3, mScreenConfig.getmVirtualHeight() / 4);
        mRTT.drawWithoutTex(orth);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep2);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);
        mRTT.drawElements();
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramImage, "renderMode"), ConstMgr.RENDER_BLOOM);
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 4 * 3, mScreenConfig.getmVirtualHeight() / 4 * 3);
        mRTT.drawWithoutTex(orth);
        GLES20.glUseProgram(mProgramImage);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX"), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep2);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramImage, "TEX1"), 1);
        mRTT.drawElements();
    }

    //터치 이벤트
    float x1 = 1.f;
    float x2 = 1.f;
    float y1 = 1.f;
    float y2 = 1.f;
    float px = 1.f;
    float py = 1.f;
    float distance = 1.f;
    boolean aiming = false;
    boolean rotating = false;
    boolean touchingEarth = false;
    boolean touchingEquipWindow =false;
    float startingAngle = 0.0f;

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
                    if (mPopup.getIsActive()) {
                        if (mPopup.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            mPopup.setIsActive(false);
                        }
                    } else if (mIntroButtons[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        mCamera.setDefault();
                        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_MAIN;
                    } else if (mIntroButtons[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        mPopup.setMode(ConstMgr.POPUP_MODE_EXIT_APP);
                        mPopup.setIsActive(true);
                    }
                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_MAIN) {
            //메인 화면 이벤트
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    if (mMainButtons[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                    } else if (mMainButtons[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        //ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_SHOP;
                        mUserData.setGold(mUserData.getGold() + 100);
                        mUserGoldChanged = true;
                        mGLSurfaceView.onUpdateCall();

                        mUserData.saveSaveData();
                    } else if (mMainButtons[2].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        mInven.setScroll(0);
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_EQUIP;
                    } else {
                        rotating = true;
                        px = mScreenConfig.deviceToVirtualX(x);
                        startingAngle = mUser.getRotateY();
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (rotating) {
                        x1 = mScreenConfig.deviceToVirtualX(x);
                        mUser.setRotateY(startingAngle + (px - x1) / (float) mScreenConfig.getmVirtualWidth() * 360.0f);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    rotating = false;
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {

                }
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_STAGE) {
            //스테이지 선택창 이벤트
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    for (int i = 0; i < ConstMgr.STAGEBUTTON_NUM; i++) {
                        if (mStageButtons[i].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            mStageSelected = true;
                            mGLSurfaceView.onUpdateCall();
                            ConstMgr.STAGE = i;
                            mStage[ConstMgr.STAGE].updatePosInList(0);
                            mStage[ConstMgr.STAGE].currentFrame = 0;
                            mStage[ConstMgr.STAGE].turn = 0;
                            mUserData.equipCannon(mParticleSystem);
                            mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].copyUserData(mUser);
                            ConstMgr.RENDER_MODE = ConstMgr.RENDER_SETTING;
                            ConstMgr.TURN_MODE = ConstMgr.TURN_CAMERA;
                            for (int j = 0; j < ConstMgr.MODEBUTTON_NUM; j++) {
                                mModeButton[j].setIsActive(true);
                            }
                            for (int j = 0; j < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); j++) {
                                mMissileButton[j].setIsActive(false);
                            }
                            for (int j = 0; j < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); j++) {
                                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[j].aim.setIsAimed(false);
                            }
                            mShootButton.setIsActive(true);
                            ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_GAME;
                            startTime = mLastTime;
                            mCamera.setDefault();
                            return true;
                        }
                    }
                    if (mBackButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
//                        Vector3f pos = new Vector3f(0,0,0);
//                        mParticleSystem.addEmitter(pos, pos);
//                        mParticleSystem.activate();
//                        startTime = mLastTime;
                        mCamera.setDefault();
                        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
                        ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_MAIN;
                    }
                }
            }
            // 게임중 이벤트
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_GAME) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mPause.getIsActive()) {
                        mPause.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y));
                    } else if (mPopup.getIsActive()) {
                        if (mPopup.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                            mPopup.setIsActive(false);
                        }
                    } else if (mShootButton.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        beforeSimul();
                        ConstMgr.RENDER_MODE = ConstMgr.RENDER_ANIMATION;
                        ConstMgr.TURN_MODE = ConstMgr.TURN_CAMERA;
                        ConstMgr.CANNON = ConstMgr.CANNON_NOTHING;
                        mStage[ConstMgr.STAGE].turnStartFrame = frame;
                        mShootButton.setIsActive(false);
                    } else if (mModeButton[0].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.TURN_MODE = ConstMgr.TURN_AIM;
                        for (int j = 0; j < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); j++) {
                            mMissileButton[j].setIsActive(true);
                        }
                    } else if (mModeButton[1].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        ConstMgr.TURN_MODE = ConstMgr.TURN_CAMERA;
                        for (int j = 0; j < mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCannonListSize(); j++) {
                            mMissileButton[j].setIsActive(false);
                        }
                    } else if ((ConstMgr.RENDER_MODE == ConstMgr.RENDER_SETTING) && (ConstMgr.TURN_MODE == ConstMgr.TURN_AIM)) {
                        for (int i = 0; i < 5; i++) {
                            if (mMissileButton[i].isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                                ConstMgr.CANNON = i;
                                return true;
                            }
                        }
                        if (ConstMgr.CANNON != ConstMgr.CANNON_NOTHING) {
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
                    touchObjectCheck(x, y);
                    x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                    y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mPause.getPause() || mPopup.getIsActive()) {
                        break;
                    } else if (event.getPointerCount() > 1) {//멀티터치상태로 움직일때
                        px = (x1 + x2) / 2;
                        py = (y1 + y2) / 2;
                        x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                        y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
                        x2 = mScreenConfig.deviceToVirtualX((int) event.getX(1));  //////////////////////
                        y2 = mScreenConfig.deviceToVirtualY((int) event.getY(1));  //////////////////////
                        float tempdistance = (float) Math.sqrt((double) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
                        if (Math.abs(tempdistance - distance) > 50) {
                            //if(!bzoom&&!bmove) bzoom = true;
                            mCamera.setZoom((distance - tempdistance) / 5000.0f);
                            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                        } else {
                            //if(!bzoom&&!bmove) bmove = true;
                        }
                        if (Math.sqrt((px - ((x1 + x2) / 2)) * (px - ((x1 + x2) / 2)) + (py - ((y1 + y2) / 2)) * (py - ((y1 + y2) / 2))) > 5) {
                            mCamera.setMove((-py + (y1 + y2) / 2) / 200.0f, (-px + (x1 + x2) / 2) / 200.0f);
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
                    } else { // 싱글터치
                        if (ConstMgr.TURN_MODE == ConstMgr.TURN_CAMERA) {
                            px = x1;
                            py = y1;
                            x1 = mScreenConfig.deviceToVirtualX((int) event.getX(0));  //////////////////////
                            y1 = mScreenConfig.deviceToVirtualY((int) event.getY(0));  //////////////////////
//                            if (Math.abs(px - x1) > 2) {
//                                mCamera.setRotateY((px - x1) / 2.0f);
//                                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
//                            }
//                            if (Math.abs(py - y1) > 2) {
//                                mCamera.setRotateX((y1 - py) / 2.0f);
//                                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
//                            }
                            if (Math.abs(px - x1) > 2) {
                                mCamera.setRotateY((px - x1) / 2.0f);
                                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                            }
                            if (Math.abs(py - y1) > 2) {
                                if (mCamera.checkX((py - y1) / 2.0f)) {
                                    mCamera.setRotateX((py - y1) / 2.0f);
                                    Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                                }
                            }
                        } else if (aiming) {
                            if (ConstMgr.CANNON != ConstMgr.CANNON_NOTHING) {
                                //Vector3f target = new Vector3f();
                                Vector3f target = selectplane(x, y);
                                target = target.minus(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos());
                                target.normalize();
                                target = target.multScalar(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].getSpeed());
                                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setShootVelocity(target);
                                mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.setupVertexBuffer(mStage[ConstMgr.STAGE].planetList, mStage[ConstMgr.STAGE].listSize);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mPause.getPause() || mPopup.getIsActive()) {
                        break;
                    } else if (aiming) {
                        //Vector3f direction = new Vector3f();
                        //Vector3f cannonDirection = new Vector3f();
                        Vector3f direction = new Vector3f();
                        Vector3f cannonDirection = new Vector3f();
                        direction.copy(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootVelocity());
                        direction.normalize();
                        cannonDirection.copy(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].cannons[ConstMgr.CANNON].aim.getShootPos().minus(mStage[ConstMgr.STAGE].planetList[mStage[ConstMgr.STAGE].userNum].getCurrentPos()));
                        cannonDirection.normalize();
                        if (direction.dot(cannonDirection) > 0) {
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
                    distance = (float) Math.sqrt((double) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
            }
        }
        //장비화면 이벤트
        else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_EQUIP) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    switch (mInven.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y))) {
                        case Inventory.ITEM:
                            break;
                        case Inventory.BUTTON:
                            touchingEquipWindow = true;
                            break;
                        case Inventory.EQUIP :
                            touchingEquipWindow = true;
                            break;
                        case Inventory.BUTTON2 :
                            touchingEquipWindow = true;
                            break;
                        case 0:
                            if (touchEarth(x, y, mSelectPos)) {
                                rotating = false;
                                touchingEarth = true;
                            } else {
                                rotating = true;
                                touchingEarth = false;
                                px = mScreenConfig.deviceToVirtualX(x);
                                startingAngle = mUser.getRotateY();
                            }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (rotating) {
                        x1 = mScreenConfig.deviceToVirtualX(x);
                        mUser.setRotateY(startingAngle + (px - x1) / (float) mScreenConfig.getmVirtualWidth() * 360.0f);
                    } else if (touchingEarth) {
                        touchEarth(x, y, mSelectPos);
                    } else if (touchingEquipWindow) {
                        break;
                    } else if (mInven.getIsScroll()) {
                        mInven.setScroll(mScreenConfig.deviceToVirtualY(y));
                    } else {
                        mInven.checkAndStartScroll(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    rotating = false;
                    touchingEarth = false;
                    touchingEquipWindow = false;
                    if (mInven.getIsScroll()) {
                        mInven.endScroll();
                    } else {
                        int temp = mInven.isSelected(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y));
                        if (temp == Inventory.ITEM) {
                            mInven.checkItem(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y));
                        } else if (temp == Inventory.BUTTON) {
                            mInven.equip();
                        } else if (temp == Inventory.EQUIP) {
                            mInven.checkEquipItem(mScreenConfig.deviceToVirtualX(x), mScreenConfig.deviceToVirtualY(y));
                        } else if (temp == Inventory.BUTTON2) {
                            mInven.unEquip();
                        }
                    }
                    break;
            }
        } else if (ConstMgr.SCREEN_MODE == ConstMgr.SCREEN_TEST) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
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
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    private boolean touchEarth(int x, int y, Vector3f result) {
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
        Vector3f ray_wor = new Vector3f(ray_eye[0], ray_eye[1], ray_eye[2]);
        //check
        Vector3f temp1 = new Vector3f(mCamera.eye[0], mCamera.eye[1], mCamera.eye[2]);
        temp1.copy(temp1.plus(ray_wor.multScalar(mCamera.eye[1])));

        if (temp1.length() < 1.0f) {
            Vector3f returnVal = new Vector3f(mCamera.eye[0], mCamera.eye[1], mCamera.eye[2]);
            ray_wor.normalize();
            float EdotV = ray_wor.dot(returnVal);
            float EdotE = returnVal.dot(returnVal);
            float length = -EdotV - (float) Math.sqrt(1 - EdotE + EdotV * EdotV);
            returnVal.copy(returnVal.plus(ray_wor.multScalar(length)));
//
            float[] tempMatrix = new float[16];
            float[] modelMatrix = new float[16];
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, 90, 1.0f, 0.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.rotateM(tempMatrix, 0, 45 - mUser.getRotateY(), 0.0f, 0.0f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0);
            Matrix.invertM(modelMatrix, 0, modelMatrix, 0);
            returnVal.normalize();
            returnVal.multM(modelMatrix, 1.0f);
            returnVal.normalize();

            if (returnVal.length() != 1.0f) {
                return true;
            } else {
                result.copy(returnVal);
            }
            return true;
        } else {
            result.normalize();
            return false;
        }
    }

    private int touchObjectCheck(int x, int y) {
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
        Vector3f ray_wor = new Vector3f(ray_eye[0], ray_eye[1], ray_eye[2]);
        ray_wor.normalize();

        int result = -1;
        float tempdistance = 0.0f;
        for (int i = 0; i < 4; i++) {//모든 행성에대해서 잘모름 ㅠㅠ
            Vector3f temp1 = new Vector3f(mCamera.eye[0] - mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().x, mCamera.eye[1] - mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().y, mCamera.eye[2] - mStage[ConstMgr.STAGE].planetList[i].getCurrentPos().z);//planet현위치 넣기
            float b = ray_wor.dot(temp1);
            float c = temp1.dot(temp1) - mStage[ConstMgr.STAGE].planetList[i].getRadius() * mStage[ConstMgr.STAGE].planetList[i].getRadius(); //아마도 r^2으로 예상됨

            if ((b * b - c >= 0) && ((-b + Math.sqrt(b * b - c) > 0) || ((-b - Math.sqrt(b * b - c) > 0)))) {
                //mStage[ConstMgr.STAGE].planetList[i].setRadius(mStage[ConstMgr.STAGE].planetList[i].getRadius()+1.0f);
                //이때 i 번째 행성 선택
                if (result == -1) {
                    result = i;
                    tempdistance = temp1.length();
                } else {
                    if (tempdistance > temp1.length()) {
                        result = i;
                        tempdistance = temp1.length();
                    }
                }
            }
        }
        if (result != -1) {
            mCamera.setAtMove(mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().x, mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().y, mStage[ConstMgr.STAGE].planetList[result].getCurrentPos().z);
            Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
        }
        return result;
    }

    private Vector3f selectplane(int x, int y) {
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
        Vector3f ray_wor = new Vector3f(ray_eye[0], ray_eye[1], ray_eye[2]);
        ray_wor.normalize();

        Vector3f result = new Vector3f();
        Vector3f temp1 = new Vector3f(mCamera.eye[0], mCamera.eye[1], mCamera.eye[2]);//planet현위치 넣기
        float b = temp1.dot(new Vector3f(0.f, 1.f, 0.f));
        float c = ray_wor.dot(new Vector3f(0.f, 1.f, 0.f)); //아마도 r^2으로 예상됨

        if (c != 0 && (b / c) < 0) {
            //mStage[ConstMgr.STAGE].planetList[i].setRadius(mStage[ConstMgr.STAGE].planetList[i].getRadius()+1.0f);
            //이때 i 번째 행성 선택
            result.setXYZ(mCamera.eye[0] + ray_wor.x * (-b / c), mCamera.eye[1] + ray_wor.y * (-b / c), mCamera.eye[2] + ray_wor.z * (-b / c));
        }
        return result;
    }

    public void onBackPressed() {
        switch (ConstMgr.SCREEN_MODE) {
            case ConstMgr.SCREEN_INTRO: {
                if (mPopup.getIsActive()) {
                    onPopupResponse(ConstMgr.POPUP_MODE_EXIT_GAME, ConstMgr.POPUP_RES_NO);
                    ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_NONE;
                    mPopup.setMode(ConstMgr.POPUP_MODE_NONE);
                    mPopup.setIsActive(false);
                } else {
                    ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_EXIT_APP;
                    mPopup.setMode(ConstMgr.POPUP_MODE);
                    mPopup.setIsActive(true);
                }
                break;
            }
            case ConstMgr.SCREEN_MAIN: {
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_INTRO;
                break;
            }
            case ConstMgr.SCREEN_STAGE: {
                mCamera.setDefault();
                Matrix.multiplyMM(mMtrxProjectionAndView, 0, mCamera.projectionMatrix, 0, mCamera.viewMatrix, 0);
                Matrix.multiplyMM(mMtrxOrthoAndView, 0, mCamera.orthoProjectionMatrix, 0, mCamera.twoDViewMatrix, 0);
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_MAIN;
                break;
            }
            case ConstMgr.SCREEN_GAME: {
                if (mPause.getIsActive()) {
                    mPause.setIsActive(false);
                    mPause.setPause(false);
                    Long now = System.currentTimeMillis();
                    startTime += (now - mPausedTime);
                } else {
                    mPause.setIsActive(true);
                    mPausedTime = mLastTime;
                }
                break;
            }
            case ConstMgr.SCREEN_EQUIP: {
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_MAIN;
            }
        }
    }

    public void onPopupResponse(int mode, int response) {
        switch (mode) {
            case ConstMgr.POPUP_MODE_EXIT_APP: {
                if (response == ConstMgr.POPUP_RES_YES) {
                    mUserData.saveSaveData();
                    mPopup.setIsActive(false);
                    mActivity.moveTaskToBack(true);
                    mActivity.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else if (response == ConstMgr.POPUP_RES_NO) {
                    mPopup.setIsActive(false);
                }
                break;
            }
            case ConstMgr.POPUP_MODE_EXIT_GAME: {
                if (response == ConstMgr.POPUP_RES_YES) {
                    mPause.setPause(false);
                    Long now = System.currentTimeMillis();
                    startTime += (now - mPausedTime);
                    mPopup.setIsActive(false);
                    ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                } else if (response == ConstMgr.POPUP_RES_NO) {
                    mPopup.setIsActive(false);
                    mPause.setIsActive(true);
                }
                break;
            }
            case ConstMgr.POPUP_MODE_AIM: {
                if (response == ConstMgr.POPUP_RES_YES) {
                    mPopup.setIsActive(false);
                } else if (response == ConstMgr.POPUP_RES_NO) {
                    mPopup.setIsActive(false);
                }
                break;
            }
            case ConstMgr.POPUP_MODE_WIN: {
                mPopup.setIsActive(false);
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                break;
            }
            case ConstMgr.POPUP_MODE_LOSE: {
                mPopup.setIsActive(false);
                ConstMgr.SCREEN_MODE = ConstMgr.SCREEN_STAGE;
                break;
            }
        }
    }

    public void onPauseResponse(int buttonNum) {
        switch (buttonNum) {
            // 계속
            case 0: {
                mPause.setIsActive(false);
                mPause.setPause(false);
                Long now = System.currentTimeMillis();
                startTime += (now - mPausedTime);
                break;
            }
            // 옵션
            case 1:
                break;
            // 나가기
            case 2: {
                mParticleSystem.clearBuffer();
                mParticleSystem.deactivate();
                ConstMgr.RENDER_MODE = ConstMgr.RENDER_SETTING;
                mPopup.setMode(ConstMgr.POPUP_MODE_EXIT_GAME);
                mPause.setIsActive(false);
                mPopup.setIsActive(true);
            }
        }
    }

    //********************************************************************************************************
    //FBO
    private int fboId, fboIdStep1, fboIdStep2, fboScene;
    private int fboTex, fboTexStep1, fboTexStep2, fboTexScene;
    private int renderBufferId, renderBufferIdStep1, renderBufferIdStep2, renderBufferScene;
    private int fboWidth = 256;
    private int fboHeight = 256;
    private int fboSceneWidth;
    private int fboSceneHeight;

    public int InitiateFrameBuffer(int fbo, int tex, int rid) {
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        //Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rid);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rid);
        //we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return tex;
    }

    public int InitiateFrameBuffer(int fbo, int tex, int rid, int texWidth, int texHeight) {
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        //Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rid);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texWidth, texHeight);
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rid);
        //we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return tex;
    }

    private int[] initFBO() {
        int[] temp = new int[1];
        int[] ret = new int[2];

        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0);
        fboId = temp[0];
        //generate texture
        GLES20.glGenTextures(1, temp, 0);
        fboTex = temp[0];
        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferId = temp[0];
        int rtt = InitiateFrameBuffer(fboId, fboTex, renderBufferId);

        //step1 (blur horizontal)
        GLES20.glGenFramebuffers(1, temp, 0);
        fboIdStep1 = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTexStep1 = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferIdStep1 = temp[0];
        InitiateFrameBuffer(fboIdStep1, fboTexStep1, renderBufferIdStep1);

        //step2 (blur vertical)
        GLES20.glGenFramebuffers(1, temp, 0);
        fboIdStep2 = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTexStep2 = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferIdStep2 = temp[0];
        InitiateFrameBuffer(fboIdStep2, fboTexStep2, renderBufferIdStep2);

        // whole scene
        fboSceneWidth = mDeviceWidth;
        fboSceneHeight = mDeviceHeight;
        GLES20.glGenFramebuffers(1, temp, 0);
        fboScene = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTexScene = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferScene = temp[0];
        InitiateFrameBuffer(fboScene, fboTexScene, renderBufferScene, fboSceneWidth, fboSceneHeight);


        //init texture square
        mRTT = new RTTQuad(mProgramImage);
        mRTT.setIsActive(true);
        mRTT.setPos(mScreenConfig.getmVirtualWidth() / 4, mScreenConfig.getmVirtualHeight() / 4);
        mRTT.setSize(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);

        mBlur = new RTTQuad(mProgramBlur);
        mBlur.setIsActive(true);
        mBlur.setPos(mScreenConfig.getmVirtualWidth() / 2, mScreenConfig.getmVirtualHeight() / 2);
        mBlur.setSize(mScreenConfig.getmVirtualWidth(), mScreenConfig.getmVirtualHeight());

        return ret;
    }

    public void Blur(float[] orth) {
        GLES20.glUseProgram(mProgramBlur);
        GLES20.glViewport(0, 0, fboWidth, fboHeight);
        //apply horizontal blur on fboTex store result in fboTexStep1
        BlurStep(1, orth);
        //apply horizontal blur on fboTex store result in fboTexStep2
        BlurStep(2, orth);
    }

    public void BlurStep(int step, float[] orth) {
        //apply horizontal blur
        if (step == 1)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdStep1);
        else if (step == 2)//apply vertical blur
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdStep2);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mBlur.drawWithoutTex(orth);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (step == 1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
        else if (step == 2)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep1);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramBlur, "TEX"), 0);

        if (step == 1)
            GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramBlur, "direction"), 0);
        else if (step == 2)
            GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramBlur, "direction"), 1);
        if (step == 1)
            GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramBlur, "blurScale"), 0.56f);
        else if (step == 2)
            GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramBlur, "blurScale"), 1.0f);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramBlur, "blurAmount"), 20f);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramBlur, "blurStrength"), 0.5f);

        mBlur.drawElements();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void updateTexture() {
        if (mUserGoldChanged) {
            int temp = mUserData.getGold();
            String tempStr = "";
            tempStr = NumberFormat.getNumberInstance(Locale.US).format(temp);
            Log.e("", tempStr);
            mGoldAmount.setBitmap(mBitmapLoader.getHangulHandle(tempStr, mScreenConfig.getmVirtualHeight() / 15, Color.WHITE, -1, 1.0f, mHangulBitmap.TRANSFORMERS, 1), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 15);
            mGoldAmount.setPosRight(mScreenConfig.getmVirtualWidth() / 16 * 11, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
            mUserGoldChanged = false;
        }
        if (mStageSelected) {
            for (int i = 0; i < mUserData.userPlanet.getCannonListSize(); i++) {
                try {
                    mMissileButton[i].setBitmap(mBitmapLoader.getImageHandle("drawable/item" + mUserData.equipList[i].DBindex, false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
                    mStageSelected = false;
                } catch (Exception e) {
                    mMissileButton[i].setBitmap(mBitmapLoader.getImageHandle("drawable/blank" + mUserData.equipList[i].DBindex, false), mScreenConfig.getmVirtualHeight() / 6, mScreenConfig.getmVirtualHeight() / 6);
                }
            }

        }
        if (mUserLevelChanged) {
            mLevelFont.setBitmap(mBitmapLoader.getHangulHandle(Integer.toString(mUserData.getLevel()), mScreenConfig.getmVirtualHeight() / 15, Color.WHITE, -1, 1.0f, mHangulBitmap.TRANSFORMERS, 1), mBitmapLoader.getWordLength(), mScreenConfig.getmVirtualHeight() / 15);
            mLevelFont.setPosRight(mScreenConfig.getmVirtualWidth() / 16 * 5, mScreenConfig.getmVirtualHeight() - mScreenConfig.getmVirtualHeight() / 12);
            mUserLevelChanged = false;
        }
    }






}