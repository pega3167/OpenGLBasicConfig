package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-09-05.
 */
public class Stage {
    public Planet[] planetList;
    public int listSize;
    public int userNum;
    public int turnStartFrame;
    public int currentFrame;
    public int turn;
    public boolean isAimMode;

    public Stage(int programImage, int listSize, int userNum) {
        this.listSize = listSize;
        this.userNum = userNum;
        isAimMode = true;
        turnStartFrame = 0;
        currentFrame = 0;
        turn = 0;
        planetList = new Planet[listSize];
    }
}
