package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-22.
 */
public class ConstMgr {
    //화면 모드
    public final static int SCREEN_INTRO = 1;
    public final static int SCREEN_STAGE = 2;
    public final static int SCREEN_GAME = 3;
    public final static int SCREEN_TEST = 4;
    //미사일 모드
    public final static int MISSILE_NOTHING = 0;
    public final static int MISSILE_STANDARD = 1;
    //쉴드등등 추가
    //버튼수
    public final static int INTROBUTTON_NUM = 2;
    public final static int STAGEBUTTON_NUM = 1;
    //현재 화면의 모드
    public static int SCREEN_MODE = SCREEN_INTRO;
    // Frame Per Second
    public final static int FPS = 64;
    // 한 턴의 프레임 수
    public final static int FRAME_PER_TURN = 640;
    //현재 stage
    public static int STAGE = 0;
    // Aim vertex 최대값
    public final static int MAX_AIM_VERTEXCOUNT = 512;
    // 게임 스크린에서 세팅 화면과 애니메이션
    public final static int RENDER_SETTING = 0;
    public final static int RENDER_ANIMATION = 1;
    public static int RENDER_MODE = RENDER_SETTING;
    // 게임 스크린에서의 카메라 모드와 조준 모드 선택사항
    public final static int TURN_CAMERA = 0;
    public final static int TURN_AIM = 1;
    public static int TURN_MODE = TURN_CAMERA;
    // 조준 선택된 미사일
    public final static int CANNON_ONE = 0;
    public final static int CANNON_TWO = 1;
    public final static int CANNON_THREE = 2;
    public final static int CANNON_FOUR = 3;
    public final static int CANNON_FIVE = 4;
    public final static int CANNON_NOTHING = 5;
    public static int CANNON = CANNON_NOTHING;
    // 파티클 시스템 파티클 수명
    public final static int PARTICLE_LIFE = FPS*2;

    // 팝업
    public final static int POPUP_MODE_NONE = 0;
    public final static int POPUP_MODE_EXIT_APP = 1;
    public final static int POPUP_MODE_EXIT_GAME = 2;
    public final static int POPUP_MODE_AIM = 3;
    public final static int POPUP_MODE_WIN = 4;
    public final static int POPUP_MODE_LOSE = 5;
    public final static int POPUP_MODE_SIZE = 6;
    public static int POPUP_MODE = POPUP_MODE_NONE;

    public final static int POPUP_RES_YES = 0;
    public final static int POPUP_RES_NO = 1;
    public final static int POPUP_RES_CONFIRM = 2;
    public final static int POPUP_RES_CANCLE = 3;
    public final static int POPUP_BUTTON_SIZE = 4;
}
