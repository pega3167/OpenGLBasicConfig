package openglbasicconfig.leesg.com.openglbasicconfig;

/**
 * Created by Administrator on 2015-10-13.
 */
public class ItemList {
    int item_count = 10;
    private Item[] List = new Item[item_count];
    public ItemList(Square[] icons){
        List[0] = new Item(icons[0]);
        List[0].setName("기본 포탑1");
        List[0].setInfo("처음쓰는 무기 잼");
        List[0].setPrice(500);
        List[0].setCost(50);
        List[0].setType(1);//아직 잘모르겟음
        List[0].setAttackPoint(50);
        List[0].setWeaponPoint(50);

        List[1] = new Item(icons[1]);
        List[1].setName("기본 포탑2");
        List[1].setInfo("두번째쓰는 무기 잼");
        List[1].setPrice(1000);
        List[1].setCost(100);
        List[1].setType(1);
        List[1].setAttackPoint(60);
        List[1].setWeaponPoint(55);

        List[2] = new Item(icons[2]);
        List[2].setName("기본 포탑3");
        List[2].setInfo("세번째쓰는 무기 잼");
        List[2].setPrice(1000);
        List[2].setCost(100);
        List[2].setType(1);
        List[2].setAttackPoint(70);
        List[2].setWeaponPoint(60);
    }

    public Item getItem(int index){
        return List[index];
    }
}