# ShoppingCar
仿照淘宝的购物车。
# 核心类：
## Adapter
``` java
package love.qiqi.shoppingcar;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CisCoder.
 * Time:2016/7/16-13:55.
 */
public class ShoppingCarExAdapter extends BaseExpandableListAdapter {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 购物车数据
     */
    private List<ShoppingCarData> mList;
    /**
     * 加载Item布局
     */
    private LayoutInflater mInflater;
    /**
     * 通知Activity 刷新的接口
     */
    private ShoppingCarModifyInterface mNotification;

    public ShoppingCarExAdapter(Context context, List<ShoppingCarData> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = null;
        final GroupViewHolder holder;
        final ShoppingCarData shoppingCarData = mList.get(groupPosition);
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = mInflater.inflate(R.layout.item_shopping_car, null);
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.item_shopping_car_cb_check);
            holder.mName = (TextView) convertView.findViewById(R.id.item_shopping_car_tv_name);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.mCheckBox.setChecked(shoppingCarData.isChecked);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingCarData.isChecked = holder.mCheckBox.isChecked();
                //如果店铺被选中即全选店铺下所有商品
                if (holder.mCheckBox.isChecked()) {
                    List<MealMenuData> list = mList.get(groupPosition).goodList;
                    for (MealMenuData m : list) {
                        m.isCheck = true;
                    }
                } else {
                    List<MealMenuData> list = mList.get(groupPosition).goodList;
                    for (MealMenuData m : list) {
                        m.isCheck = false;
                    }
                }
                //TODO 通知接口
                mNotification.notification(groupPosition);
                notifyDataSetChanged();
            }
        });
        holder.mName.setText(shoppingCarData.name == null ? "" : shoppingCarData.name);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //TODO  不是一个好的方法 强制重绘  getView 复用的不是当前GroupItem下的childView 这样会导致存储的Checked状态乱序（Child的id不是唯一的了。）
        //TODO 而是 前面显示过的GroupItem的ChildView 这样的话 设置的Tag值就没办法保持唯一性就不能记录 CheckBox状态了。待解决。
        convertView = null;
        final ChildViewHolder holder;
        final MealMenuData goodData = mList.get(groupPosition).goodList.get(childPosition);
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.item_shopping_car_list, null);
            holder.mCheck = (CheckBox) convertView.findViewById(R.id.item_shopping_car_list_cb_check);
            holder.mCount = (TextView) convertView.findViewById(R.id.item_shopping_car_list_tv_number);
            holder.mDate = (TextView) convertView.findViewById(R.id.item_shopping_car_list_tv_date);
            holder.mPrice = (TextView) convertView.findViewById(R.id.item_shopping_car_list_tv_price);
            holder.mPlus = (ImageButton) convertView.findViewById(R.id.item_shopping_car_list_iv_plus);
            holder.mMinus = (ImageButton) convertView.findViewById(R.id.item_shopping_car_list_iv_minus);
            holder.mName = (TextView) convertView.findViewById(R.id.item_shopping_car_list_tv_name);
            holder.mImage = (ImageView) convertView.findViewById(R.id.item_shopping_car_list_iv_image);
            //TODO 用了别人的一个仿饿了么的动画，但是跟全选 有冲突，
            //TODO 原因是 全选后要通知Adapter更新因为每次点击都要去判断是否全选每次都通知刷新，这个控件就会每点一次就进初始化。伸展不开。
            //holder.mShopping = (ShoppingView) convertView.findViewById(R.id.shopping_view);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.mCheck.setChecked(goodData.isCheck);
        holder.mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodData.isCheck = holder.mCheck.isChecked();
                //TODO 通知接口更新
                mNotification.notification(groupPosition);
            }
        });
        holder.mDate.setText(goodData.weekname == null ? "" : goodData.weekname);
        holder.mName.setText(goodData.name == null ? "" : goodData.name);
        holder.mPrice.setText(goodData.price == null ? "0" : goodData.price);
        holder.mCount.setText(goodData.number + "");
        //加号
        holder.mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mCheck.setChecked(true);
                goodData.number++;
                goodData.isCheck = holder.mCheck.isChecked();
                holder.mCount.setText(goodData.number + "");
                //通知Activity计算价格
                mNotification.notification(groupPosition);
            }
        });
        //减号
        holder.mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodData.number > 1) {
                    goodData.isCheck = holder.mCheck.isChecked();
                    goodData.number--;
                    holder.mCount.setText(goodData.number + "");
                    //通知Activity计算价格
                    mNotification.notification(groupPosition);
                }
            }
        });
//        holder.mShopping.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
//            @Override
//            public void onAddClick(int num) {
//                holder.mCheck.setChecked(true);
//                goodData.number = num;
//                goodData.isCheck = holder.mCheck.isChecked();
//                //TODO 通知计算价格
//                mNotification.notification(groupPosition);
//            }
//
//            @Override
//            public void onMinusClick(int num) {
//                if (num < 1) return;
//                goodData.number = num;
//                goodData.isCheck = holder.mCheck.isChecked();
//                //TODO 通知计算价格
//                mNotification.notification(groupPosition);
//            }
//        });
        return convertView;
    }

    /**
     * 供外部获取数据
     *
     * @return List<ShoppingCarData>
     */
    public List<ShoppingCarData> getList() {
        return mList;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).goodList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).goodList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        String id = String.valueOf(groupPosition) + String.valueOf(childPosition);
        return id.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * GroupViewHolder
     */
    class GroupViewHolder {
        CheckBox mCheckBox;
        TextView mName;
    }

    /**
     * ChildViewHolder
     */
    class ChildViewHolder {
        TextView mName;
        TextView mPrice;
        TextView mDate;
        ImageView mImage;
        CheckBox mCheck;
        TextView mCount;
        ImageButton mPlus;
        ImageButton mMinus;
    }

    /**
     * 设置刷新监听
     *
     * @param listener
     */
    public void setModifyNotificationListener(ShoppingCarModifyInterface listener) {
        if (listener == null)
            throw new NullPointerException("ShoppingCarModifyInterface not null");
        mNotification = listener;
    }

    /**
     * Activity 通知 adapter 店铺下商品全选
     *
     * @param groupPosition
     */
    public void notificationChildAll(int groupPosition) {
        List<MealMenuData> list = mList.get(groupPosition).goodList;
        for (MealMenuData m : list) {
            m.isCheck = true;
        }
        notifyDataSetChanged();
    }
}
```
#Activity
```java
package love.qiqi.shoppingcar;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShoppingCarActivity extends Activity implements ShoppingCarModifyInterface {
    @BindView(R.id.shopping_car_lv_list)            //列表
            ExpandableListView lvList;
    @BindView(R.id.shopping_car_tv_total)           //合计
            TextView tvTotal;
    @BindView(R.id.shopping_car_tv_edit)            //编辑
            TextView tvEdit;
    @BindView(R.id.shopping_car_ll_price)           //合计
            LinearLayout llPrice;
    @BindView(R.id.shopping_car_btn_submit)         //提交
            Button btnSubmit;
    @BindView(R.id.shopping_car_cb_all)             //全选
            CheckBox cbAll;
    private boolean isDelete = false;
    private List<ShoppingCarData> mListData;        //接口返回数据
    private List<ShoppingCarData> mSelectCarData;   //选择数据
    private ShoppingCarExAdapter mCarExAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        if (mListData == null) {
            mListData = new ArrayList<>();
            ShoppingCarData carData = new ShoppingCarData();
            carData.name = "章鱼小丸子";
            MealMenuData data = new MealMenuData();
            data.name = "大丸子";
            data.price = "8";
            data.weekname = "2016-07-27";
            carData.goodList.add(data);
            MealMenuData data1 = new MealMenuData();
            data1.name = "小丸子";
            data1.price = "4";
            data1.weekname = "2016-07-27";
            carData.goodList.add(data1);
            MealMenuData data2 = new MealMenuData();
            data2.name = "鱼丸面";
            data2.price = "20";
            data2.weekname = "2016-07-27";
            carData.goodList.add(data2);
            mListData.add(carData);


            ShoppingCarData carData1 = new ShoppingCarData();
            carData1.name = "琪琪蛋卷";
            MealMenuData data3 = new MealMenuData();
            data3.name = "奶蛋卷";
            data3.price = "13";
            data3.weekname = "2016-07-27";
            carData1.goodList.add(data3);
            MealMenuData data4 = new MealMenuData();
            data4.name = "抹茶蛋卷";
            data4.price = "6";
            data4.weekname = "2016-07-27";
            carData1.goodList.add(data4);
            MealMenuData data5 = new MealMenuData();
            data5.name = "香草蛋卷";
            data5.price = "12";
            data5.weekname = "2016-07-27";
            carData1.goodList.add(data5);
            mListData.add(carData1);
        }
        if (mSelectCarData == null) {
            mSelectCarData = new ArrayList<>();
        }
    }

    private void initView() {
        mCarExAdapter = new ShoppingCarExAdapter(this, mListData);
        lvList.setAdapter(mCarExAdapter);
        int size = mListData.size();
        for (int i = 0; i < size; i++) {
            lvList.expandGroup(i);//展开所有
        }
        mCarExAdapter.setModifyNotificationListener(this);
    }

    //编辑
    @OnClick(R.id.shopping_car_tv_edit)
    void edit() {
        String edit = tvEdit.getText().toString();
        if (TextUtils.equals("编辑", edit)) {
            llPrice.setVisibility(View.INVISIBLE);
            btnSubmit.setText("删除");
            isDelete = true;
            tvEdit.setText("完成");
        } else {
            llPrice.setVisibility(View.VISIBLE);
            btnSubmit.setText("提交订单");
            tvEdit.setText("编辑");
            isDelete = false;
        }
    }

    //全选
    @OnClick(R.id.shopping_car_cb_all)
    void all() {
        if (cbAll.isChecked()) {
            //TODO 全选
            selectAll(true);
        } else {
            //TODO 取消全选
            selectAll(false);
        }
    }

    //提交
    @OnClick(R.id.shopping_car_btn_submit)
    void submit() {
        if (isDelete) {
            //TODO 删除
            delete();
        } else {
            //TODO 提交订单支付页面
        }
    }

    /**
     * 删除
     */
    private void delete() {
        mSelectCarData.clear();
        mSelectCarData.addAll(mCarExAdapter.getList());
        List<ShoppingCarData> groupList = new ArrayList<>();
        for (ShoppingCarData s : mSelectCarData) {
            if (s.isChecked) {
                groupList.add(s);
            } else {
                List<MealMenuData> childList = new ArrayList<>();
                for (MealMenuData m : s.goodList) {
                    if (m.isCheck) {
                        childList.add(m);
                    }
                }
                s.goodList.removeAll(childList);
            }
        }
        mListData.removeAll(groupList);
        mCarExAdapter.notifyDataSetChanged();
    }

    /**
     * 全选按钮
     *
     * @param b
     */
    private void selectAll(boolean b) {
        mSelectCarData.clear();
        mSelectCarData.addAll(mCarExAdapter.getList());
        for (ShoppingCarData s : mSelectCarData) {
            s.isChecked = b;
            for (MealMenuData m : s.goodList) {
                m.isCheck = b;
            }
        }
        calculatePrice(-1);//计算价格
        mCarExAdapter.notifyDataSetChanged();
    }

    /**
     * 计算数据
     *
     * @param groupPosition
     */
    @Override
    public void notification(int groupPosition) {
        if (!isDelete) {
            calculatePrice(groupPosition);
        }
    }

    /**
     * 计算价格
     *
     * @param groupPosition 传-1 表示只计算价格。
     */
    private void calculatePrice(int groupPosition) {
        double price = 0.00;
        mSelectCarData.clear();
        mSelectCarData.addAll(mCarExAdapter.getList());
        //先判断是否是全选
        if (groupPosition != -1) {
            if (isALLSelect(groupPosition)) {
                //是全选
                mSelectCarData.get(groupPosition).isChecked = true;
                for (MealMenuData m : mSelectCarData.get(groupPosition).goodList) {
                    m.isCheck = true;
                }
            } else {
                mSelectCarData.get(groupPosition).isChecked = false;
            }
            //判断所有店铺是否全选
            // ***注意判断顺序 先判断商品（Child），在判断店铺（Group）不然会出现错位的情况 即慢半拍。
            if (isALLSelect()) {
                cbAll.setChecked(true);
            } else {
                cbAll.setChecked(false);
            }
            mCarExAdapter.notifyDataSetChanged();

        }
        Log.d("ShoppingCar", "购物车数据-Size:" + mSelectCarData.size() + "\n" + mSelectCarData.toString());
        for (ShoppingCarData s : mSelectCarData) {
            for (MealMenuData m : s.goodList) {
                if (m.isCheck) {
                    price += m.number * Double.parseDouble(m.price);
                }
            }
        }
        tvTotal.setText("￥" + price);
    }

    /**
     * 判断商铺下的商品是全部选中
     *
     * @param groupPosition
     * @return
     */
    private boolean isALLSelect(int groupPosition) {
        for (MealMenuData m : mSelectCarData.get(groupPosition).goodList) {
            if (!m.isCheck) {
                return false;//有一个没有选中的 返回false表示不是全选
            }
        }
        return true;
    }

    /**
     * 判断全部是否全选
     *
     * @return
     */
    private boolean isALLSelect() {
        for (ShoppingCarData s : mSelectCarData) {
            if (!s.isChecked) {
                return false;//有一个店铺没有选中的 返回false表示不是全选
            }
        }
        return true;
    }

}


```
