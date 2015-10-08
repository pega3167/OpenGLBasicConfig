package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-10-08.
 */
public class UserData {
    private int gold;
    private int exp;
    private int clearedStage;
    public Planet userPlanet;
    //private weapon[] weaponList;
    //private int weaponListSize;

    //gets
    public int getExp() { return exp; }
    public int getGold() { return gold; }
    public int getClearedStage() { return clearedStage; }

    //sets
    public void setExp(int exp) { this.exp = exp; }
    public void setGold(int gold) { this.gold = gold; }
    public void setClearedStage(int clearedStage) { this.clearedStage = clearedStage; }
}
