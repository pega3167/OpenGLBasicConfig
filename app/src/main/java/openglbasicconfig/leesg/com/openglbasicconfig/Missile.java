package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-28.
 */
public class Missile extends Mesh{
    private Vector3f currentPos;
    private Vector3f velocity;

    //생성자
    public Missile(int mProgramImage, MainActivity mainActivity) {
        super(mProgramImage, mainActivity);
        currentPos = new Vector3f();
    }
}
