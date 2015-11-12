package openglbasicconfig.leesg.com.openglbasicconfig;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by LeeSG on 2015-10-08.
 */
public class UserData {
    //내부 저장소에 저장된 데이터 불러오기/저장하기.
    private int gold; //첫줄
    private int exp;  //둘째줄
    private int level;//셋째줄
    private int clearedStage;//넷째줄
    private int inventorySize; //5
    private int itemSize;       //6
    private int[] inventory;    //7~
    public equipItem[] equipList = new equipItem[5];
    private ItemList itemDB;
    MainActivity mMainActivity;
    public Planet userPlanet;   //inventory 뒤~
    //private weapon[] weaponList;
    //private int weaponListSize;

    public UserData(MainActivity mainActivity, ItemList itemList) {
        mMainActivity = mainActivity;
        loadSaveData();
        itemDB = itemList;
    }

    private void firstTimeData() {
        gold = 100;
        exp = 0;
        level = 1;
        clearedStage = 0;
        inventorySize = 10;
        itemSize = 2;
        inventory = new int[inventorySize];
        inventory[0] = 0;
        inventory[1] = 0;
        equipList = new equipItem[5];
        for(int i = 0 ; i < 5 ; i++) {
            equipList[i] = new equipItem();
        }
    }

    public void loadSaveData() {
        String str_Path = mMainActivity.getFilesDir().getAbsolutePath();
        try {
            FileInputStream fis = new FileInputStream(str_Path + "/savedata.dat");
            InputStreamReader ins = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(ins);
            String[] resultData = new String[100];
            String temp = "";
            int cnt = 0;
            while ((temp = bufferedReader.readLine()) != null) {
                if (cnt < 100) {
                    resultData[cnt] = temp;
                    cnt++;
                } else Log.e("Error", "Too long file.");
            }
            if (!(cnt < 6)) {
                gold = Integer.parseInt(resultData[0]);
                exp = Integer.parseInt(resultData[1]);
                level = Integer.parseInt(resultData[2]);
                clearedStage = Integer.parseInt(resultData[3]);
                inventorySize = Integer.parseInt(resultData[4]);
                itemSize = Integer.parseInt(resultData[5]);
                inventory = new int[inventorySize];
                for (int i = 0; i < itemSize; i++) {
                    inventory[i] = Integer.parseInt(resultData[6 + i]);
                }
                for(int i = 0 ; i < 5 ; i++) {
                    equipList[i] = new equipItem();
                }
                for(int i = 0 ; i < 5 ; i++) {
                    equipList[i].DBindex = Integer.parseInt(resultData[6 + itemSize+ 5*i]);
                    equipList[i].invenIndex = Integer.parseInt(resultData[6 + itemSize + 5*i + 1]);
                    equipList[i].pos.x = Float.parseFloat(resultData[6 + itemSize + 5 * i + 2]);
                    equipList[i].pos.y = Float.parseFloat(resultData[6 + itemSize + 5 * i + 3]);
                    equipList[i].pos.z = Float.parseFloat(resultData[6 + itemSize + 5 * i + 4]);
                }
            } else Log.e("Error", "Too short file");
        } catch (Exception e) {
            Log.e("", "" + e);
            firstTimeData();
        }
    }

    public void saveSaveData() {
        String str_Path = mMainActivity.getFilesDir().getAbsolutePath();
        try {
            FileOutputStream fos = mMainActivity.openFileOutput("savedata.dat", Context.MODE_PRIVATE);
            String fileCont = Integer.toString(gold) + "\n" + Integer.toString(exp) + "\n" + Integer.toString(level) + "\n"
                    + Integer.toString(clearedStage) + "\n" + Integer.toString(inventorySize) + "\n" + Integer.toString(itemSize);
            for (int i = 0; i < itemSize; i++) {
                fileCont = fileCont + "\n" + Integer.toString(inventory[i]);
            }
            for(int i = 0 ; i < 5 ; i++) {
                fileCont = fileCont + "\n" + equipList[i].DBindex;
                fileCont = fileCont + "\n" + equipList[i].invenIndex;
                fileCont = fileCont + "\n" + equipList[i].pos.x;
                fileCont = fileCont + "\n" + equipList[i].pos.y;
                fileCont = fileCont + "\n" + equipList[i].pos.z;
            }
            fos.write(fileCont.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "Failed saving");
        }
    }
    public void equipCannon(ParticleSystem particleSystem, float radius) {
        particleSystem.clearBuffer();
        particleSystem.clearEmitterList();
        userPlanet.clearCannonList();
        for(int i = 0 ; i < 5 ; i++) {
            if(equipList[i].DBindex != -1) {
                int index = equipList[i].DBindex;
                int j = particleSystem.addEmitter();
                Vector3f temp = new Vector3f(equipList[i].pos.x, equipList[i].pos.y, equipList[i].pos.z);
                temp.y = 0;
                temp.normalize();
                temp.copy(temp.multScalar(radius));
                userPlanet.addCannon(temp, itemDB.getItem(index).getAttackPoint(), 0.05f, itemDB.getItem(index).getType(), j);
            } else
                break;
        }
    }
    public class equipItem {
        public int DBindex;
        public int invenIndex;
        public Vector3f pos;
        public equipItem() {
            DBindex = -1;
            invenIndex = -1;
            pos = new Vector3f();
        }
    }
    public void unEquipItem (int equipIndex) {
        for(int i = equipIndex; i < 4 ; i++) {
            this.equipList[i].DBindex = this.equipList[i+1].DBindex;
            this.equipList[i].invenIndex = this.equipList[i+1].invenIndex;
            this.equipList[i].pos.copy(this.equipList[i+1].pos);
        }
        equipList[4].DBindex = -1;
        equipList[4].DBindex = -1;
        equipList[4].pos.setXYZ(0,0,0);
    }

    //gets
    public int getGold() {
        return gold;
    }
    public int getExp() {
        return exp;
    }
    public int getLevel() {
        return level;
    }
    public int getClearedStage() {
        return clearedStage;
    }
    public int getItemSize() {
        return itemSize;
    }
    public int getInventorySize() {
        return inventorySize;
    }
    public int getInventory(int i) {return inventory[i];}
    //sets
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setExp(int exp) {
        this.exp = exp;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setClearedStage(int clearedStage) {
        this.clearedStage = clearedStage;
    }
    public void setitemSize(int itemSize) {
        this.itemSize = itemSize;
    }
    public void setInventorySize(int inventorySize) {
        this.inventorySize = inventorySize;
    }
    public void addItem(int i) {
        inventory[itemSize] = i;
        itemSize++;
        Log.e("itemSize",""+itemSize);
    }
}

