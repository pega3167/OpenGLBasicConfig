package openglbasicconfig.leesg.com.openglbasicconfig;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Created by LeeSG on 2015-08-28.
 */
public class HangulBitmap {
    //한글을 비트맵변환하기 위한 변수
    public Bitmap bitmap;
    private Bitmap.Config config;
    // 폰트 결정
    public final static int NANUM = 0;
    public final static int SHARPCORE = 1;
    public final static int MARLBORO = 2;
    public final static int RALEWAY = 3;
    public final static int TRANSFORMERS = 4;
    public final static int BMJUA = 5;

    //캔버스를 이용하여 한글 출력
    Canvas canvas;
    Paint mPaint;
    private Typeface nanum;
    private Typeface sharpCore;
    private Typeface marlboro;
    private Typeface raleWay;
    private Typeface transformers;
    private Typeface BMJua;
    private String htext;
    //생성자 폰트를 설정
    public HangulBitmap(Activity mActivity) {
        nanum = Typeface.createFromAsset(mActivity.getAssets(), "font/NanumGothic.ttf");
        sharpCore = Typeface.createFromAsset(mActivity.getAssets(), "font/SharpCore.ttf");
        marlboro = Typeface.createFromAsset(mActivity.getAssets(), "font/Marlboro.ttf");
        raleWay = Typeface.createFromAsset(mActivity.getAssets(), "font/Raleway-Bold.ttf");
        transformers = Typeface.createFromAsset(mActivity.getAssets(), "font/TransformersMovie.ttf");
        BMJua = Typeface.createFromAsset(mActivity.getAssets(), "font/BMJUA_ttf.ttf");
    }
    // 텍스트를 출력하여 비트맵으로 변환함
    public float GetBitmap (Bitmap bitmap, String text, int textSize, int fontColor, int canvasColor, float scale, int fontType, int alignMode) {
        canvas=new Canvas(bitmap);
        //캔버스 색상이 -1이 아닐 경우 배경색을 그린다.
        if(canvasColor != -1) {
            canvas.drawColor(canvasColor);
        }
        //페인트를 이용하여 출력
        mPaint = new Paint();
        mPaint.setColor(fontColor);
        mPaint.setTextSize(textSize);
        //투명도를 설정하기 위해 AntiAlias를 지정한다.
        if(canvasColor == -1)
            mPaint.setAntiAlias(false);
        else
            mPaint.setAntiAlias(true);
        switch (fontType) {
            case NANUM: {
                mPaint.setTypeface(nanum);
                break;
            } case SHARPCORE: {
                mPaint.setTypeface(sharpCore);
                break;
            } case MARLBORO : {
                mPaint.setTypeface(marlboro);
                break;
            } case RALEWAY : {
                mPaint.setTypeface(raleWay);
                break;
            } case TRANSFORMERS : {
                mPaint.setTypeface(transformers);
                break;
            } case BMJUA : {
                mPaint.setTypeface(BMJua);
                break;
            }


            default: {
                mPaint.setTypeface(nanum); break;
            }
        }
        //텍스트를 출력하고 출력범위 산정을 위해 텍스트 폭을 반환한다.
        float textWidth = mPaint.measureText(text);
        //텍스트의 스케일을 조정한다.
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        //mPaint.setTextScaleX(0.8f);
        float x;
        switch(alignMode) {
            // -1 : left / 0 : center / 1 right / default : center
            case -1 : x = 0;
                break;
            case 0 : x = (bitmap.getWidth() - bounds.width())/2;
                break;
            case 1 : x = (bitmap.getWidth() - bounds.width());
                break;
            default: x = (bitmap.getWidth() - bounds.width())/2;
        }
        float y = (bitmap.getHeight() + bounds.height())/2 - textSize*0.1f;
        //캔버스에 텍스트를 그린다.
        canvas.drawText(text, x, y, mPaint);
        return canvas.getWidth();
    }

    public float GetBitmap (Bitmap bitmap, String text, int textSize, int fontColor, int canvasColor, float scale) {
        canvas=new Canvas(bitmap);
        //캔버스 색상이 -1이 아닐 경우 배경색을 그린다.
        if(canvasColor != -1) {
            canvas.drawColor(canvasColor);
        }
        //페인트를 이용하여 출력
        mPaint = new Paint();
        mPaint.setColor(fontColor);
        mPaint.setTextSize(textSize);
        //투명도를 설정하기 위해 AntiAlias를 지정한다.
        if(canvasColor == -1)
            mPaint.setAntiAlias(false);
        else
            mPaint.setAntiAlias(true);

        mPaint.setTypeface(BMJua);

        //텍스트를 출력하고 출력범위 산정을 위해 텍스트 폭을 반환한다.
        float textWidth = mPaint.measureText(text);
        //텍스트의 스케일을 조정한다.
        //mPaint.setTextScaleX(0.8f);
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        //mPaint.setTextScaleX(0.8f);
        float x = (bitmap.getWidth() - bounds.width())/2;
        float y = (bitmap.getHeight() + bounds.height())/2 - textSize*0.1f;
        //캔버스에 텍스트를 그린다.
        canvas.drawText(text, x, y, mPaint);
        return canvas.getWidth();
    }

}
