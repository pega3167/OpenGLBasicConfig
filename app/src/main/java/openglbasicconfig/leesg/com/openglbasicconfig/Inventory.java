package openglbasicconfig.leesg.com.openglbasicconfig;

import android.util.Log;

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
    //private Item[] itemList;
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
    private float startX = -10;
    private float startY = -10;
    private float upperBound;

    private int selectIndex = -1;
    private int equipListIndex = -1;
    private int[] equipedList = new int[5];

    // isSelected return mode
    public final static int ITEM = 1;
    public final static int BUTTON = 2;

    public Inventory(Square background, Square windowFrame, Square blank, Square selection, Square equiped, Button equip, /*Item[] itemList,*/ int itemSize, int inventorySize, float vertWidth, float vertHeight) {
        this.background = background;
        this.wFrame = windowFrame;
        this.blankItem = blank;
        this.selected = selection;
        this.equiped = equiped;
        this.equipButton = equip;
        //this.itemList = itemList;
        this.itemSize = itemSize;
        this.invenSize = inventorySize;
        this.virtWidth = vertWidth;
        this.virtHeight = vertHeight;
        itemWidth = virtWidth/15.0f;
        itemHeight = virtHeight/8.0f;
        this.scroll = 0f;
        upperBound = itemHeight * ((invenSize-1)/2 - 2);
    }

    public void setPos(float x, float y) {
        posX = x;
        posY = y;
        background.setPos(x, y);
        wFrame.setPos(x, y);
        equipButton.setPos(x, y - virtHeight / 24.0f * 7);
        offSetX = posX - virtWidth/128.0f;
        offSetY = posY + virtHeight/10.0f;
    }

    public void setIsActive(boolean isActive) {
        background.setIsActive(isActive);
        wFrame.setIsActive(isActive);
        blankItem.setIsActive(isActive);
        selected.setIsActive(isActive);
        equiped.setIsActive(isActive);
        equipButton.setIsActive(isActive);
        this.isActive = isActive;
    }

    public void draw(float[] m) {
        if(!isActive) return;
        background.draw(m);
        for( int i = 0 ; i < itemSize ; i++) {
            //draw item
        }
        for (int i = itemSize; i < invenSize ; i++) {
            if(offSetY - i/2 * itemHeight + scroll > offSetY + itemHeight) {
                continue;
            } else if (offSetY - i/2 * itemHeight + scroll < offSetY - 3.0 * itemHeight) {
                break;
            } else {
                blankItem.setPos(offSetX + i % 2 * itemWidth, offSetY - i / 2 * itemHeight + scroll);
                blankItem.draw(m);
                if(i == selectIndex) {
                    selected.setPos(offSetX + i % 2 * itemWidth, offSetY - i / 2 * itemHeight + scroll);
                    selected.draw(m);
                }
            }
        }
        wFrame.draw(m);
        equipButton.draw(m);
    }

    public void setIsScroll(boolean isScroll) { this.isScroll = isScroll; }
    public boolean getIsScroll() { return isScroll; }

    public int isSelected(float x, float y) {
        if((offSetX - itemWidth/2.0f <= x && offSetX + itemWidth * 1.5f >= x) && (offSetY + itemHeight/2.0f >= y && offSetY - itemHeight*2.5 <=y )) {
            startX = x;
            startY = y;
            return ITEM;
        } else if (equipButton.isSelected((int)x, (int)y)) {
            return BUTTON;
        }
        return 0;
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
    }
}
