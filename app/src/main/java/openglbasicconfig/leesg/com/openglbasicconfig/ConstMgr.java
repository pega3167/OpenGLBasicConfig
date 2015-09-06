package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-22.
 */
public class ConstMgr {
    //화면 모드
    public final static int SCREEN_INTRO = 1;
    public final static int SCREEN_STAGE = 2;
    public final static int SCREEN_GAME = 3;
    //미사일 모드
    public final static int MISSILE_NOTHING = 0;
    public final static int MISSILE_STANDARD = 1;
    //쉴드등등 추가
    //버튼수
    public final static int INTROBUTTON_NUM = 2;
    public final static int STAGEBUTTON_NUM = 5;
    //현재 화면의 모드
    public static int SCREEN_MODE = SCREEN_INTRO;
    // Frame Per Second
    public final static int FPS = 64;
    // 한 턴의 프레임 수
    public final static int FRAME_PER_TURN = 640;
    //현재 stage
    public static int STAGE = 0;
    // Aim vertex 최대값
    public final static int MAX_AIM_VERTEXCOUNT = 1000;

}
