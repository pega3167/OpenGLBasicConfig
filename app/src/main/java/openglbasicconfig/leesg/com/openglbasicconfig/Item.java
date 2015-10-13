package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by LeeSG on 2015-10-11.
 */
public class Item {
    private String  name; //템 이름
    private String info; //템 설명
    private int cost; //설치시 들어가는 에너지
    private int price; //템 가격
    private int type; //실제 템 정보용  템을 완전 하드코딩할 시에는 필요없음 판단좀 쓸모없으면 뺴구영
    private int attackPoint; // 공격력
    private int weaponPoint; // 무기포인트
    private int count; //템 숫자
    private Square icon; //추가적으로 아이템 그림을 추가해야하는데 어캐할지 모름 ㅠㅠ

    public Item(Square icon){
        name = "";
        info = "";
        cost = 0;
        price = 0;
        type = 0;
        count = 0;
        attackPoint = 0;
        weaponPoint = 0;
        this.icon = icon;
    }
    public Item(String name, String info, Square icon){
        this.name = name;
        this.info = info;
        this.icon = icon;
        cost = 0;
        price = 0;
        type = 0;
        count = 0;
        attackPoint = 0;
        weaponPoint = 0;
    }


    public void setName(String name){
        this.name = name;
    }
    public void setInfo(String info){
        this.info = info;
    }
    public void setCost(int cost){
        this.cost = cost;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public void setType(int type){
        this.type = type;
    }
    public void setCount(int count){
        this.count = count;
    }
    public void setAttackPoint(int attackPoint) {
        this.attackPoint = attackPoint;
    }
    public void setWeaponPoint(int weaponPoint) {
        this.weaponPoint = weaponPoint;
    }
    public void setIcon(Square icon) {
        this.icon = icon;
    }

    public String getName(){
        return name;
    }
    public String getInfo(){
        return info;
    }
    public int getCost(){
        return cost;
    }
    public int getPrice(){
        return price;
    }
    public int getType(){
        return type;
    }
    public int getCount(){
        return count;
    }
    public int getAttackPoint() {
        return attackPoint;
    }
    public int getWeaponPoint() {
        return weaponPoint;
    }
    public Square getIcon() {
        return icon;
    }
}
