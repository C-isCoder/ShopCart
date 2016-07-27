package love.qiqi.shoppingcar;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iscod.
 * Time:2016/7/15-20:24.
 */
public class ShoppingCarData implements Serializable {
    public String name;
    public String kid;

    public boolean isChecked;
    public List<MealMenuData> goodList = new ArrayList<MealMenuData>();

    @Override
    public String toString() {
        return "\n" + "商家数据:" + "【name】:" + name + "【isChecked】:" + isChecked + "\n" + "商品数据:" + goodList.toString();
    }

}
