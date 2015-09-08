package openglbasicconfig.leesg.com.openglbasicconfig;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by LeeSG on 2015-08-28.
 */
public class HangulBitmap {
    //한글을 비트맵변환하기 위한 변수
    public Bitmap bitmap;
    private Bitmap.Config config;
    //캔버스를 이용하여 한글 출력
    Canvas canvas;
    Paint mPaint;
    private Typeface font;
    private String htext;
    //생성자 폰트를 설정
    public HangulBitmap(Activity mActivity) {
        font = Typeface.createFromAsset(mActivity.getAssets(), "NanumGothic.ttf");
    }
    // 텍스트를 출력하여 비트맵으로 변환함
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
        mPaint.setTypeface(font);
        //텍스트를 출력하고 출력범위 산정을 위해 텍스트 폭을 반환한다.
        float textWidth = mPaint.measureText(text);
        //텍스트의 스케일을 조정한다.
        mPaint.setTextScaleX(0.8f);
        //캔버스에 텍스트를 그린다.
        canvas.drawText(text, textWidth * 0.1f, textSize * 0.9f, mPaint);
        return textWidth;
    }
}
