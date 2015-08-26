package openglbasicconfig.leesg.com.openglbasicconfig;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private MainGLSurfaceView mGLSurfaceView;
    //효과음 설정
    private SoundPool mSoundPool;
    private int mSoundExplosion;
    private int mSoundButton;
    //액티비티 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //전체화면
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        mGLSurfaceView = new MainGLSurfaceView(this, width, height);
        setContentView(mGLSurfaceView);
        //사운드 풀을 이용하여 효과음 메소드 생성
        mSoundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptorBtn = assetManager.openFd("button01.ogg");
            mSoundButton = mSoundPool.load(descriptorBtn, 1);
        }
        catch(Exception exAsset){}
    }

    public BufferedReader getAssetFile(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
        } catch (IOException e) {
            System.err.println(filename);
        }
        return reader;
    }
    // 버튼 효과음
    public void soundButton() {
        try {
            mSoundPool.play(mSoundButton, 1.0f, 1.0f, 0, 0, 1.0f);
        }
        catch(Exception e) {}
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
