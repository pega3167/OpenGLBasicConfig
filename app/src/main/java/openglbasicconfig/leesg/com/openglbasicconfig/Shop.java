package openglbasicconfig.leesg.com.openglbasicconfig;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by LeeSG on 2015-11-11.
 */
public class Shop {
    //인터페이스
    Square backGround;
    Square shopFrame;
    Square itemBlock;
    Button buyButton;
    float posX, posY;
    int selectIndex = -1;
    //유저데이터
    UserData mUserData;
    //글로벌데이터
    MainGLRenderer mGLRenderer;
    ItemList itemDB;
    //아이템 객체
    Square[] itemName;
    Square[] itemAttackPoint;
    Square[] itemWeaponPoint;
    Square[] itemInfo;
    Square[] itemPrice;

    // 스크롤
    private float scroll;
    private float initScroll;
    private boolean isScroll;
    private float scrollStartY;
    private float upperBound;
    private float startX = -10;
    private float startY = -10;

    // isSelected return mode
    public final static int ITEM = 1;
    public final static int BUTTON = 2;

    public Shop(Square backGround, Square shopFrame, Square itemBlock, Button buyButton, UserData mUserData, MainGLRenderer mGLRenderer, ItemList itemDB,
                Square[] itemName, Square[] itemAttackPoint, Square[] weaponPoint, Square[] itemInfo, Square[] itemPrice) {
        this.backGround = backGround;
        this.shopFrame = shopFrame;
        this.itemBlock = itemBlock;
        this.buyButton = buyButton;
        this.mUserData = mUserData;
        this.mGLRenderer = mGLRenderer;
        this.itemDB = itemDB;
        this.itemName = itemName;
        this.itemAttackPoint =  itemAttackPoint;
        this.itemWeaponPoint = weaponPoint;
        this.itemInfo =  itemInfo;
        this.itemPrice =  itemPrice;

        this.scroll = 0.0f;
    }

    public void setPos(float x, float y) {
        posX = x;
        posY = y;
        this.upperBound = posY/0.8f;
        this.backGround.setPos(x,y);
        this.shopFrame.setPos(x,y);
        this.buyButton.setPos(x*1.6f,y/2f);
    }
    public void setIsActive(boolean isActive) {
        this.backGround.setIsActive(isActive);
        this.shopFrame.setIsActive(isActive);
        this.itemBlock.setIsActive(isActive);
        this.buyButton.setIsActive(isActive);
        for(int i = 0 ; i < itemDB.item_count; i++) {
            itemName[i].setIsActive(isActive);
            itemAttackPoint[i].setIsActive(isActive);
            itemWeaponPoint[i].setIsActive(isActive);
            itemInfo[i].setIsActive(isActive);
            itemPrice[i].setIsActive(isActive);
        }
    }
    public int isSelected(float x, float y) {
        if ((posX/2.5f - posX*2/7.0f <= x && posX/2.5f + posX*2/7.0f >= x) && (posY*1.6f + posY/3.6f >= y && posY*1.5f - posY/1.8f * 2.5f <= y)) {
            // item 부분 누름 스크롤or아이템 선택
            startX = x;
            startY = y;
            return ITEM;
        }
        if(buyButton.isSelectedNoSound((int)x,(int)y)) {
            return BUTTON;
        }
        return 0;
    }

    public boolean buy() {
        if(selectIndex < 0) {
            Toast.makeText(mGLRenderer.mActivity, "구입 하려는 아이템을 선택하세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUserData.getGold() < itemDB.getItem(selectIndex).getPrice()) {
            Toast.makeText(mGLRenderer.mActivity, "돈이 부족합니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUserData.getInventorySize() == mUserData.getItemSize()) {
            Toast.makeText(mGLRenderer.mActivity, "더 이상 아이템을 소지할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mUserData.addItem(selectIndex);
            mUserData.setGold(mUserData.getGold() - itemDB.getItem(selectIndex).getPrice());
            Toast.makeText(mGLRenderer.mActivity, "구입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    //스크롤 함수
    public void checkAndStartScroll(float x, float y) {
        if((x - startX) * (x-startX) + (y-startY)*(y-startY) > 100.0f) {
            initScroll = scroll;
            scrollStartY = y;
            isScroll = true;
        }
    }
    public void setScroll(float y) {
        if(isScroll) {
            scroll = initScroll + y - scrollStartY;
            if(scroll > upperBound) {
                scroll = upperBound;
            } else if (scroll < 0) {
                scroll = 0;
            }
        }
    }
    public void endScroll() {
        startX = -10;
        startY = -10;
        isScroll = false;
    }
    public boolean getIsScroll() {
        return isScroll;
    }
    public void checkItem(float x, float y) {
        float temp = posY*1.5f + scroll;
        float blockHalf = posY/3.6f;
        for(int i = 0 ; i < itemDB.item_count ; i++) {
            if(y <= temp + (1-2*i)*blockHalf && y >= temp - (2*i + 1)*blockHalf - blockHalf ) {
                selectIndex = i;
            }
        }
    }

    public void draw(float[] m) {
        backGround.draw(m);
        for(int i = 0 ; i < itemDB.item_count; i++) {
            itemBlock.setPos(posX /2.5f, posY*1.5f - i*posY/1.8f + scroll);
            itemBlock.draw(m);
            itemDB.getItem(i).getIcon().setPos(posX / 5.0f, posY*1.5f - i*posY/1.8f + scroll);
            itemDB.getItem(i).getIcon().draw(m);
            itemName[i].setPosRight(posX/1.6f, posY*1.6f - i*posY/1.8f + scroll);
            itemName[i].draw(m);
            itemPrice[i].setPosRight(posX/1.6f, posY*1.4f - i*posY/1.8f + scroll);
            itemPrice[i].draw(m);
        }
        if(selectIndex != -1) {
            itemDB.getItem(selectIndex).getIcon().setPos(posX, posY * 1.4f);
            itemDB.getItem(selectIndex).getIcon().scaledDraw(m, 1.5f);
            itemInfo[selectIndex].setPosLeft(posX * 0.85f, posY);
            itemInfo[selectIndex].draw(m);
            itemAttackPoint[selectIndex].setPosLeft(posX * 1.15f, posY * 1.3f);
            itemAttackPoint[selectIndex].draw(m);
            itemWeaponPoint[selectIndex].setPosLeft(posX * 1.15f, posY * 1.5f);
            itemWeaponPoint[selectIndex].draw(m);
            itemPrice[selectIndex].setPosRight(posX * 1.2f, posY / 2);
            itemPrice[selectIndex].scaledDraw(m, 1.5f);
            buyButton.draw(m);
        }
        shopFrame.draw(m);
    }

}
