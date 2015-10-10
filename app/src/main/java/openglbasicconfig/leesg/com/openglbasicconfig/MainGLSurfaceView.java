package openglbasicconfig.leesg.com.openglbasicconfig;


import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by LeeSG on 2015-08-13.
 */
public class MainGLSurfaceView extends GLSurfaceView {
    //렌더러
    private final MainGLRenderer mRenderer;
    //생성자
    public MainGLSurfaceView(MainActivity activity, int width, int height){
        super(activity.getApplicationContext());
        //OpenGL ES 2.0 context를 생성한다.
        setEGLContextClientVersion(2);
        //GLSurfaceView 를 사용하기 위해 Context 를 이용해 렌더러를 생성한다.
        mRenderer = new MainGLRenderer(activity, width, height, this);
        setRenderer(mRenderer);
        //렌더모드 - 연속해서 그린다.
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRenderer.onTouchEvent(event);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    public void onBackPressed() {
        mRenderer.onBackPressed();
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
    }
    public void onUpdateCall() {
        queueEvent(new Runnable() {
                       @Override
                       public void run() {
                           mRenderer.updateGold();
                       }
                   }
        );
    }
}
