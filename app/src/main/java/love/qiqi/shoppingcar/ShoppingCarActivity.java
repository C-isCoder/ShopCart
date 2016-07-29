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
        if (mSelectCarData == null) {
            mSelectCarData = new ArrayList<>();
        }
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
    }

    private void initView() {
        mCarExAdapter = new ShoppingCarExAdapter(this, mListData);
        lvList.setAdapter(mCarExAdapter);
        lvList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //屏蔽点击事件
                return true;
            }
        });
//        int size = mListData.size();
//        for (int i = 0; i < size; i++) {
//            lvList.expandGroup(i);//展开所有
//        }
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

