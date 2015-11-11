package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by Administrator on 2015-10-13.
 */
public class ItemList {
    int item_count = 5;
    private Item[] List = new Item[item_count];
    public ItemList(Square[] icons){
        List[0] = new Item(icons[0]);
        List[0].setName("녹슨 포탑");
        List[0].setInfo("발사가 되는지조차 의심이 드는 포탑");
        List[0].setPrice(100);
        List[0].setCost(50);
        List[0].setType(1);//아직 잘모르겟음
        List[0].setAttackPoint(10);
        List[0].setWeaponPoint(50);

        List[1] = new Item(icons[1]);
        List[1].setName("일반 포탑");
        List[1].setInfo("포탑의 수리하여 안정적인 출력을 가지게 된 포탑");
        List[1].setPrice(500);
        List[1].setCost(100);
        List[1].setType(1);
        List[1].setAttackPoint(50);
        List[1].setWeaponPoint(100);

        List[2] = new Item(icons[2]);
        List[2].setName("강화형 포탑");
        List[2].setInfo("기존의 포탑에 포신을 개량하여 포탄의 공격력을 향상 시킨 포탑");
        List[2].setPrice(1200);
        List[2].setCost(100);
        List[2].setType(1);
        List[2].setAttackPoint(100);
        List[2].setWeaponPoint(150);

        List[3] = new Item(icons[3]);
        List[3].setName("개량형 포탑");
        List[3].setInfo("포탑에 쓰인 재료를 가공하여 효율적인 생산을 하게 된 포탑");
        List[3].setPrice(700);
        List[3].setCost(100);
        List[3].setType(1);
        List[3].setAttackPoint(70);
        List[3].setWeaponPoint(120);

        List[4] = new Item(icons[4]);
        List[4].setName("강화형 포탑 MK-II");
        List[4].setInfo("강화형 포탑을 더욱 개량하여 강력한 공격력을 가지는 포탑");
        List[4].setPrice(2000);
        List[4].setCost(100);
        List[4].setType(1);
        List[4].setAttackPoint(200);
        List[4].setWeaponPoint(250);
    }

    public Item getItem(int index){
        return List[index];
    }
}