package love.qiqi.shoppingcar;


import java.io.Serializable;

/**
 * Created by iscod.
 * Time:2016/7/11-17:32.
 */
public class MealMenuData implements Serializable {
    public String price;
    public String name;
    public String weekname;

    public boolean isCheck;
    public int number = 1;

    @Override
    public String toString() {
        return "\n" + "[price]:" + price + "[name]:" + name + "[isCheck]:" + isCheck + "[number]:" + number;
    }
}
