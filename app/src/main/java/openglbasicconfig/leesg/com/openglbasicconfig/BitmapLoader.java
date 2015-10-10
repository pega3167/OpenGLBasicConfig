package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


/**
 * Created by LeeSG on 2015-08-24.
 */
public class BitmapLoader {
    private Bitmap bitmap;
    private int imageHandle;
    private float wordLength;
    private final android.graphics.Matrix flip = new android.graphics.Matrix();
    // 메인 객체
    Context mContext;
    HangulBitmap mHangulBitmap;

    public BitmapLoader (Context mainContext, HangulBitmap mainHangulBitmap) {
        mContext = mainContext;
        mHangulBitmap = mainHangulBitmap;
        flip.postScale(1.0f, -1.0f);
    }
    public int getImageHandle(String target, boolean needFlip) {
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), mContext.getResources().getIdentifier(target, null, mContext.getPackageName()));
        if(needFlip) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), flip, true);
        }
        imageHandle = getImageHandle(bitmap);
        bitmap.recycle();
        return imageHandle;
    }

    public int getHangulHandle(String text, int textSize, int fontColor, int canvasColor, float scale) {
        bitmap = Bitmap.createBitmap(textSize * text.length(), textSize, Bitmap.Config.ARGB_8888);
        wordLength = mHangulBitmap.GetBitmap(bitmap, text, textSize, fontColor, canvasColor, scale);
        imageHandle = getImageHandle(bitmap);
        bitmap.recycle();
        return imageHandle;
    }

    public int getHangulHandle(String text, int textSize, int fontColor, int canvasColor, float scale, int fontType) {
        bitmap = Bitmap.createBitmap(textSize * text.length(), textSize, Bitmap.Config.ARGB_8888);
        wordLength = mHangulBitmap.GetBitmap(bitmap, text, textSize, fontColor, canvasColor, scale, fontType, 0);
        imageHandle = getImageHandle(bitmap);
        bitmap.recycle();
        return imageHandle;
    }

    public int getHangulHandle(String text, int textSize, int fontColor, int canvasColor, float scale, int fontType, int alignMode) {
        bitmap = Bitmap.createBitmap(textSize * text.length(), textSize, Bitmap.Config.ARGB_8888);
        wordLength = mHangulBitmap.GetBitmap(bitmap, text, textSize, fontColor, canvasColor, scale, fontType, alignMode);
        imageHandle = getImageHandle(bitmap);
        bitmap.recycle();
        return imageHandle;
    }



    public float getWordLength() {
        return wordLength;
    }

    private int getImageHandle(Bitmap bitmap){
        int[] texturenames = new int[1];
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glGenTextures(1,texturenames,0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        return texturenames[0];
    }
}
