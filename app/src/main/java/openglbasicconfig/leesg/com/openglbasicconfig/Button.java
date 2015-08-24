package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-23.
 */
public class Button extends Square{
    MainGLRenderer mMainGLRenderer;
    //생성자
    public Button(int programImage, int programSolidColor, MainGLRenderer mainGLRenderer) {
        super(programImage);
        mMainGLRenderer = mainGLRenderer;
    }
    public boolean isSelected(int x, int y) {
        boolean returnValue = super.isSelected(x,y);
        if(returnValue == true) {
            mMainGLRenderer.mActivity.soundButton();
        }
        return returnValue;
    }
}
