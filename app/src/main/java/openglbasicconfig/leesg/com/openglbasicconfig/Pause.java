package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-10-09.
 */
public class Pause {
    private Square mPauseWindow;
    private Button[] button = new Button[3];
    private MainGLRenderer mGLRenderer;
    private boolean isActive = false;
    private boolean pause = false;
    private float virtWidth;
    private float virtHeight;
    public Pause(MainGLRenderer mainGLRenderer, Square pauseWindow, Button[] buttons, float width, float height) {
        mGLRenderer = mainGLRenderer;
        virtWidth = width;
        virtHeight = height;
        mPauseWindow = pauseWindow;
        mPauseWindow.setPos(virtWidth/2,virtHeight/2);
        button = buttons;
        button[0].setPos(virtWidth/2,virtHeight/4*3-virtHeight/18);
        button[1].setPos(virtWidth/2,virtHeight/2);
        button[2].setPos(virtWidth/2,virtHeight/4+virtHeight/20);
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        mPauseWindow.setIsActive(isActive);
        for(int i = 0 ; i < 3  ; i++) {
            button[i].setIsActive(isActive);
        }
        if(isActive) { pause=true; }
    }
    public boolean isSelected(int x, int y) {
        if(button[0].isSelected(x,y)) {
            mGLRenderer.onPauseResponse(0);
            return true;
        } else if (button[1].isSelected(x,y)) {
            mGLRenderer.onPauseResponse(1);
            return true;
        } else if (button[2].isSelected(x,y)) {
            mGLRenderer.onPauseResponse(2);
            return true;
        }
        return false;
    }

    public boolean getIsActive() { return isActive; }
    public boolean getPause() { return pause; }
    public void setPause(boolean bPause) { this.pause = bPause; }
    public void draw(float[] orth) {
        if(isActive) {
            mPauseWindow.draw(orth);
            for (int i = 0; i < 3; i++) {
                button[i].draw(orth);
            }
        }
    }
}
