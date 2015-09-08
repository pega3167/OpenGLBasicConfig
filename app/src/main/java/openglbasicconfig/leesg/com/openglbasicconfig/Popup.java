package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-09-08.
 */
public class Popup {
    private Square popup;
    private Square[] hangulStr = new Square[ConstMgr.POPUP_MODE_SIZE];
    private Button[] button = new Button[4];
    private int mode = ConstMgr.POPUP_MODE_NONE;
    private MainGLRenderer mGLRenderer;

    private boolean isActive = false;
    public Popup(MainGLRenderer mainGLRenderer, Square popup, Square[] strings, Button[] buttons, int virtWidth, int virtHeight) {
        mGLRenderer= mainGLRenderer;
        this.popup = popup;
        this.popup.setPos(virtWidth/2, virtHeight/2);
        hangulStr = strings;
        for(int i = 0 ; i < ConstMgr.POPUP_MODE_SIZE; i++) {
            hangulStr[i].setPos(virtWidth/2, virtHeight*2/3);
        }
        button = buttons;
        button[0].setPos(virtWidth/3, virtHeight/3);
        button[1].setPos(virtWidth*2/3, virtHeight/3);
        button[2].setPos(virtWidth/2, virtHeight/3);
        button[3].setPos(virtWidth*2/3, virtHeight/3);
    }

    public void setMode(int mode) { this.mode = mode; ConstMgr.POPUP_MODE = mode; }
    public int getMode () { return this.mode; }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        popup.setIsActive(isActive);
        for(int i = 0 ; i< ConstMgr.POPUP_MODE_SIZE ; i++) {
            hangulStr[i].setIsActive(false);
        }
        for(int i = 0 ; i< 4 ; i++) {
            button[i].setIsActive(false);
        }
        if(isActive) {
            hangulStr[mode].setIsActive(true);
            if(mode == ConstMgr.POPUP_MODE_EXIT_APP || mode == ConstMgr.POPUP_MODE_EXIT_GAME || mode == ConstMgr.POPUP_MODE_AIM) {
                button[0].setIsActive(true);
                button[1].setIsActive(true);
            } else if(mode == ConstMgr.POPUP_MODE_WIN || mode == ConstMgr.POPUP_MODE_LOSE) {
                button[2].setIsActive(true);
            }
        }
    }
    public boolean getIsActive() { return this.isActive; }
    public boolean isSelected(int x, int y) {
        if(button[0].isSelected(x,y)) {
            mGLRenderer.onPopupResponse(mode, ConstMgr.POPUP_RES_YES);
            ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_NONE;
            mode = ConstMgr.POPUP_MODE_NONE;
            return true;
        } else if(button[1].isSelected(x, y)) {
            mGLRenderer.onPopupResponse(mode, ConstMgr.POPUP_RES_NO);
            ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_NONE;
            mode = ConstMgr.POPUP_MODE_NONE;
            return true;
        } else if(button[2].isSelected(x, y)) {
            mGLRenderer.onPopupResponse(mode, ConstMgr.POPUP_RES_CONFIRM);
            ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_NONE;
            mode = ConstMgr.POPUP_MODE_NONE;
            return true;
        } else if(button[3].isSelected(x, y)) {
            mGLRenderer.onPopupResponse(mode, ConstMgr.POPUP_RES_CANCLE);
            ConstMgr.POPUP_MODE = ConstMgr.POPUP_MODE_NONE;
            mode = ConstMgr.POPUP_MODE_NONE;
            return true;
        }
        return false;
    }

    void draw(float[] m) {
        popup.draw(m);
        hangulStr[mode].draw(m);
        for(int i = 0 ; i< 3; i++) {
            button[i].draw(m);
        }
    }
}


