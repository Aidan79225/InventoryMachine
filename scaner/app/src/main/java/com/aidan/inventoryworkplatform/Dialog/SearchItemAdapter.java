package com.aidan.inventoryworkplatform.Dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.adapter.ISearchableAdapter;
import com.aidan.inventoryworkplatform.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Aidan on 2017/3/30.
 */

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> implements ISearchableAdapter {
    List<SearchableItem> dataList = new ArrayList<>();
    List<SearchableItem> showList = new ArrayList<>();
    OnClickListener onClickListener;
    Closeable closeable;

    public SearchItemAdapter(List<SearchableItem> dataList, Closeable closeable) {
        this.dataList = dataList;
        showList.addAll(dataList);
        this.closeable = closeable;
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        holder.reload(showList.get(position));
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    @Override
    public void search(String key) {
        showList.clear();
        for (SearchableItem item : dataList) {
            if (item.getName().contains(key)) {
                showList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void stopSearch() {
        showList.clear();
        showList.addAll(dataList);
        notifyDataSetChanged();
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        View rootView;

        public SearchItemViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        }

        public void reload(final SearchableItem item) {
            nameTextView.setText(item.getName());
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(item);
                    }
                    if(closeable != null){
                        closeable.close();
                    }
                }
            });
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(SearchableItem item);
    }

    public interface Closeable {
        void close();
    }

}
