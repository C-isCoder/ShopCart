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
        final ShoppingCarData shoppingCarData = (ShoppingCarData) getGroup(groupPosition);
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
        final ShoppingCarData.Product goodData = (Product) getChild(groupPosition, childPosition);
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
