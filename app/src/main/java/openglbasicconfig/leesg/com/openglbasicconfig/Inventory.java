package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by LeeSG on 2015-10-11.
 */
public class Inventory {
    private Square background;
    private Square wFrame;
    private Square blankItem;
    private Square selected;
    private Square equiped;
    private Button equipButton;
    private Button unEquipButton;
    private Square equipFrame;

    //유저 데이터
    UserData mUserData;
    Vector3f mSelectPos;
    Popup mPopup;
    // global data
    MainGLRenderer mGLRenderer;
    ItemList itemDB;
    //private Square scrollBar;
    private int itemSize;
    private int invenSize;
    private boolean isActive;
    private float posX;
    private float posY;
    private float virtWidth;
    private float virtHeight;

    //scroll
    private float scroll;
    private float initScroll;
    private boolean isScroll;
    private float scrollStartY;
    private float itemWidth;
    private float itemHeight;
    private float offSetX;
    private float offSetY;
    private float offSetX2;
    private float offSetY2;
    private float startX = -10;
    private float startY = -10;
    private float upperBound;

    private int selectIndex = -1;
    private int equipListIndex = -1;

    // isSelected return mode
    public final static int ITEM = 1;
    public final static int BUTTON = 2;
    public final static int EQUIP = 3;
    public final static int BUTTON2 = 4;

    public Inventory(MainGLRenderer mainGLRenderer, Square background, Square windowFrame, Square blank, Square selection,
                     Square equiped, Button equip, Button unEquipBtn, Square equipFrame, UserData mUserData, float vertWidth, float vertHeight,
                     Vector3f selectPos, ItemList itemList)
    {
        this.mGLRenderer = mainGLRenderer;
        this.background = background;
        this.wFrame = windowFrame;
        this.blankItem = blank;
        this.selected = selection;
        this.equiped = equiped;
        this.equipButton = equip;
        this.unEquipButton = unEquipBtn;
        this.equipFrame = equipFrame;
        this.mUserData = mUserData;
        this.itemSize = mUserData.getItemSize();
        this.invenSize = mUserData.getInventorySize();
        this.virtWidth = vertWidth;
        this.virtHeight = vertHeight;
        this.mSelectPos = selectPos;
        this.itemDB = itemList;
        itemWidth = virtWidth/15.0f;
        itemHeight = virtHeight/8.0f;
        this.scroll = 0f;
        upperBound = itemHeight * ((invenSize - 1)/2 - 2);
    }

    public void setPos(float x, float y) {
        posX = x;
        posY = y;
        background.setPos(x, y);
        wFrame.setPos(x, y);
        equipButton.setPos(x, y - virtHeight / 24.0f * 7);
        equipFrame.setPos(x - virtWidth / 16.0f * 7, y - virtHeight / 3.0f);
        offSetX2 = x - virtWidth/16.0f * 7 - itemWidth * 3.5f;
        offSetY2 = y - virtHeight/3.0f;

        offSetX = posX - virtWidth/128.0f;
        offSetY = posY + virtHeight/10.0f;
        unEquipButton.setPos(offSetX2 + itemWidth * 6.5f, offSetY2);
    }

    public void setIsActive(boolean isActive) {
        background.setIsActive(isActive);
        wFrame.setIsActive(isActive);
        blankItem.setIsActive(isActive);
        selected.setIsActive(isActive);
        equiped.setIsActive(isActive);
        equipButton.setIsActive(isActive);
        unEquipButton.setIsActive(isActive);
        equipFrame.setIsActive(isActive);
        this.isActive = isActive;
    }

    public void draw(float[] m) {
        if(!isActive) return;
        background.draw(m);
        equipFrame.draw(m);
        // 인벤토리
        for( int i = 0 ; i < invenSize ; i++) {
            if(offSetY - i/2 * itemHeight + scroll > offSetY + itemHeight) {
                continue;
            } else if (offSetY - i/2 * itemHeight + scroll < offSetY - 3.0 * itemHeight) {
                break;
            } else {
                if (i < itemSize) {
                    itemDB.getItem(mUserData.getInventory(i)).getIcon().setPos(offSetX + i % 2 * itemWidth, offSetY - i / 2 * itemHeight + scroll);
                    itemDB.getItem(mUserData.getInventory(i)).getIcon().draw(m);
                } else {
                    blankItem.setPos(offSetX + i % 2 * itemWidth, offSetY - i / 2 * itemHeight + scroll);
                    blankItem.draw(m);
                }
            }
        }
        for(int j = 0 ; j < 5 ; j++) {
            int i = mUserData.equipList[j].invenIndex;
            if(i == -1 || offSetY - i/2 * itemHeight + scroll > offSetY + itemHeight || offSetY - i/2 * itemHeight + scroll < offSetY - 3.0 * itemHeight) {
                continue;
            }
            equiped.setPos(offSetX + i % 2 * itemWidth, offSetY - i / 2 * itemHeight + scroll);
            equiped.draw(m);
        }
        if(selectIndex < itemSize && !(offSetY - selectIndex/2 * itemHeight + scroll > offSetY + itemHeight) && !(offSetY - selectIndex/2 * itemHeight + scroll < offSetY - 3.0 * itemHeight)) {
            if(selectIndex != -1) {
                selected.setPos(offSetX + selectIndex % 2 * itemWidth, offSetY - selectIndex / 2 * itemHeight + scroll);
                selected.draw(m);
            }
        }

        //장착창
        for (int i = 0 ; i < 5; i++) {
            int j = mUserData.equipList[i].DBindex;
            if(j == -1) {
                blankItem.setPos(offSetX2 + i * itemWidth * 1.2f, offSetY2);
                blankItem.draw(m);
            }
            else {
                itemDB.getItem(j).getIcon().setPos(offSetX2 + i * itemWidth * 1.2f, offSetY2);
                itemDB.getItem(j).getIcon().draw(m);
            }
        }
        if(equipListIndex != -1) {
            selected.setPos(offSetX2 + equipListIndex * itemWidth * 1.2f, offSetY2);
            selected.draw(m);
        }
        wFrame.draw(m);
        equipButton.draw(m);
        unEquipButton.draw(m);
    }

    public void setIsScroll(boolean isScroll) { this.isScroll = isScroll; }
    public boolean getIsScroll() { return isScroll; }

    public int isSelected(float x, float y) {
        if ((offSetX - itemWidth / 2.0f <= x && offSetX + itemWidth * 1.5f >= x) && (offSetY + itemHeight / 2.0f >= y && offSetY - itemHeight * 2.5 <= y)) {
            // item 부분 누름 스크롤or아이템 선택
            startX = x;
            startY = y;
            return ITEM;
        } else if (equipButton.isSelectedNoSound((int) x, (int) y)) {
            // 장착 버튼 눌름.
            return BUTTON;
        } else if ((x > offSetX2 - itemWidth / 2.0f && x < offSetX2 + itemWidth * 5.3f) && (y > offSetY2 - itemHeight / 2.0f && y < offSetY2 + itemHeight / 2.0f)) {
            return EQUIP;
        } else if (unEquipButton.isSelectedNoSound((int)x, (int)y)) {
            return BUTTON2;
        }

        return 0;
    }
    public void equip() {
        if(selectIndex == -1) {
            //선택한 아이템이 없을경우.
            Toast.makeText(mGLRenderer.mActivity, "장착할 아이템을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(selectIndex < itemSize) {
            //선택한 아이템을 장착
            if(mSelectPos.x == 0 && mSelectPos.y == 0 && mSelectPos.z == 0) {
                Toast.makeText(mGLRenderer.mActivity, "설치할 위치를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            for(int i = 0 ; i < 5 ; i++) {
                if(mUserData.equipList[i].invenIndex == selectIndex) {
                    //이미 장착중
                    mUserData.equipList[i].pos.copy(mSelectPos);
                    Toast.makeText(mGLRenderer.mActivity, "설치한 위치가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    mGLRenderer.mActivity.equipSound();
                    mSelectPos.setXYZ(0,0,0);
                    return;
                }
            }
            int equipIndex = 0;
            for(equipIndex = 0 ; equipIndex < 5 ; equipIndex++) {
                if(mUserData.equipList[equipIndex].invenIndex == -1) {
                    mUserData.equipList[equipIndex].invenIndex = selectIndex;
                    mUserData.equipList[equipIndex].DBindex = mUserData.getInventory(selectIndex);
                    mUserData.equipList[equipIndex].pos.copy(mSelectPos);
                    mGLRenderer.mActivity.equipSound();
                    mSelectPos.setXYZ(0,0,0);
                    return;
                }
            }
            Toast.makeText(mGLRenderer.mActivity, "더 이상 설치할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void unEquip() {
        if(equipListIndex == -1) {
            Toast.makeText(mGLRenderer.mActivity, "해제할 무기 슬롯을 선택하세요.", Toast.LENGTH_SHORT).show();
        }
        else try {
            mUserData.unEquipItem(equipListIndex);
            equipListIndex = -1;
            mGLRenderer.mActivity.equipSound();
            mSelectPos.setXYZ(0, 0, 0);
        } catch(Exception e) {Log.e("",""+e);}
    }
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
    public void checkItem(float x, float y) {
        int row = -1;
        int col;
        if(x < offSetX + itemWidth / 2.0f) {
            col = 0;
        } else col = 1;
        for(int i = 0 ; i < (invenSize + 1) /2 ; i++) {
            float temp = offSetY + scroll + itemHeight/2;
            if(temp - i*itemHeight >= y && temp - (i+1)*itemHeight < y) {
                row = i;
            }
        }
        selectIndex =  row*2 + col;
        equipListIndex = -1;
    }
    public void checkEquipItem(float x, float y) {
        for (int i = 0 ; i<5; i++) {
            if(x > offSetX2 - itemWidth/2.0f + i*itemWidth*1.2f && x < offSetX2+itemWidth/2.0f + i*itemWidth*1.2f) {
                if(mUserData.equipList[i].invenIndex != -1) {
                    equipListIndex = i;
                    selectIndex = -1;
                    return;
                }
            }
        }
    }
}
