package com.aidan.inventoryworkplatform.ItemListPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Aidan on 2016/11/20.
 */

public class ItemListAdapter extends BaseAdapter {
    private List<Item> itemList;
    private Context context;
    private TextView contentInformationTextView;
    public ItemListAdapter(List<Item> itemList){
        this.itemList = itemList;
    }
    @Override
    public int getCount() {
        return itemList.size()+1;
    }

    @Override
    public Item getItem(int position) {
        if (position < 0 || position > itemList.size()) {
            return null;
        }
        return itemList.get(position);
    }

    public  List<Item> getItems() {
        return itemList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(context == null)setContext(parent);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate( R.layout.item_item,null,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else viewHolder = (ViewHolder)convertView.getTag();

        if(position == 0 ){
            viewHolder.idTextView.setText(R.string.item_little_id);
            viewHolder.waterTextView.setText(R.string.item_water_id);

            viewHolder.nameTextView.setText(R.string.name);
            viewHolder.statusTextView.setText(R.string.status);
        }else{
            Item item = itemList.get(position-1);
            viewHolder.idTextView.setText(item.getNumber());
            viewHolder.waterTextView.setText(item.getSerialNumber());

            viewHolder.nameTextView.setText(item.getNickName());
            viewHolder.statusTextView.setText(item.PA308);
        }
        return convertView;
    }
    private void setContext(ViewGroup parent){
            synchronized (ItemListAdapter.class){
                if(context == null){
                    context = parent.getContext();
                }
            }
    }
    @Override
    public void notifyDataSetChanged(){
        if(contentInformationTextView != null){
            contentInformationTextView.setText(
                    String.format(
                            contentInformationTextView.getContext().getResources().
                                    getText(R.string.already_check_information).toString(),countChecked(),itemList.size() ));
        }
        super.notifyDataSetChanged();

    }

    private int countChecked(){
        int ans = 0;
        for(Item i : itemList){
            if(i.isConfirm()){
                ans++;
            }
        }
        return ans;
    }

    class ViewHolder{
        TextView idTextView;
        TextView waterTextView;
        TextView nameTextView;
        TextView statusTextView;
        public ViewHolder(View v){
            idTextView = (TextView)v.findViewById(R.id.idTextView);
            waterTextView = (TextView)v.findViewById(R.id.waterTextView);
            nameTextView = (TextView)v.findViewById(R.id.nameTextView);
            statusTextView = (TextView)v.findViewById(R.id.statusTextView);
        }
    }

    public void setContentInformationTextView(TextView contentInformationTextView) {
        this.contentInformationTextView = contentInformationTextView;
    }
}
