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
    MainActivity mMainActivity;
    public Planet userPlanet;//5째줄부터~
    //private weapon[] weaponList;
    //private int weaponListSize;

    public UserData(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        loadSaveData();
    }
    private void firstTimeData() {
        gold = 100;
        exp = 0;
        level = 1;
        clearedStage = 0;
    }
    public void loadSaveData() {
        String str_Path = mMainActivity.getFilesDir().getAbsolutePath();
        try {
            FileInputStream fis = new FileInputStream(str_Path + "/savedata.dat");
            InputStreamReader ins = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(ins);
            String[] resultData = new String[19];
            String temp="";
            int cnt = 0;
            while((temp = bufferedReader.readLine()) != null) {
                if(cnt < 19) {
                    resultData[cnt] = temp;
                    cnt++;
                } else Log.e("Error","Too long file.");
            }
            if(!(cnt<4)) {
                gold = Integer.parseInt(resultData[0]);
                exp = Integer.parseInt(resultData[1]);
                level = Integer.parseInt(resultData[2]);
                clearedStage = Integer.parseInt(resultData[3]);
            } else Log.e("Error","Too short file");
        } catch (Exception e) { Log.e("",""+e); firstTimeData();}
    }
    public void saveSaveData() {
        String str_Path = mMainActivity.getFilesDir().getAbsolutePath();
        try {
            FileOutputStream fos = mMainActivity.openFileOutput("savedata.dat", Context.MODE_PRIVATE);
            String fileCont = Integer.toString(gold)+"\n"+Integer.toString(exp) + "\n" + Integer.toString(level) + "\n" + Integer.toString(clearedStage);
            fos.write(fileCont.getBytes());
            fos.close();
        } catch(Exception e) {Log.e("Error","Failed saving");}
    }


    //gets
    public int getGold() { return gold; }
    public int getExp() { return exp; }
    public int getLevel() { return level; }
    public int getClearedStage() { return clearedStage; }

    //sets
    public void setGold(int gold) { this.gold = gold; }
    public void setExp(int exp) { this.exp = exp; }
    public void setLevel(int level) { this.level = level; }
    public void setClearedStage(int clearedStage) { this.clearedStage = clearedStage; }
}
