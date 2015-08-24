package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-08-21.
 */
public class ScreenConfig {
    public int mDeviceWidth;
    public int mDeviceHeight;
    public int mVirtualWidth;
    public int mVirtualHeight;

    //기본설정
    public ScreenConfig(int deviceWidth, int deviceHeight) {
        this.mDeviceWidth = deviceWidth;
        this.mDeviceHeight = deviceHeight;
    }
    //가상설정
    public void setSize(int width, int height) {
        mVirtualWidth = width;
        mVirtualHeight = height;
    }
    // 설정한 가상 좌표 반환
    public int getmVirtualWidth() {
        return mVirtualWidth;
    }
    public int getmVirtualHeight() {
        return mVirtualHeight;
    }
    //가상 좌표를 기기 좌표로 변환
    public int virtualToDeviceX(int virtualX) {
        return (int)(virtualX * (float)this.mDeviceWidth/(float)this.mVirtualWidth);
    }
    public int virtualToDeviceY(int virtualY) {
        return (int)(this.mDeviceHeight - virtualY * (float)this.mDeviceHeight/(float)this.mVirtualHeight);
    }
    //기기 좌표를 가상 좌표로 전환
    public int deviceToVirtualX(int deviceX) {
        return (int)(deviceX * (float)this.mVirtualWidth/(float)this.mDeviceWidth);
    }
    public int deviceToVirtualY(int deviceY) {
        return (int)(this.mVirtualHeight - deviceY * (float)this.mVirtualHeight/(float)this.mDeviceHeight);
    }

}
