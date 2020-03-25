package com.qimalocl.manage.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.DeviceDetailActivity;
import com.qimalocl.manage.activity.LockStorageActivity;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.utils.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hawk on 2016/9/1.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> implements Comparator<SearchResult> {
    private Activity mActivity;
    private Context mContext;
    private List<SearchResult> mDataList;

    public DeviceListAdapter(Activity activity, Context context, List<SearchResult> datas) {
        mActivity = activity;
        mContext = context;
        mDataList = datas;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device_list, parent, false));
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        final SearchResult result = (SearchResult) mDataList.get(position);

        holder.mac.setText(result.getAddress());
        holder.rssi.setText(String.format("Rssi: %d", result.rssi));
        holder.name.setText(result.getName());
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public void notifyDataChanged() {
        Collections.sort(this.mDataList, this);
        notifyDataSetChanged();
    }

    @Override
    public int compare(SearchResult o1, SearchResult o2) {
        if (StringUtils.checkBikeTag(o1.getName()) || StringUtils.checkBikeTag(o2.getName())) {
            if (StringUtils.checkBikeTag(o1.getName()) && StringUtils.checkBikeTag(o2.getName())) {
                return o2.rssi - o1.rssi;
            }
            else if(StringUtils.checkBikeTag(o1.getName())) {
                return -1;
            }
            else if (StringUtils.checkBikeTag(o2.getName())) {
                return 1;
            }
        }

        return 0;
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.mac) TextView mac;
        @BindView(R.id.rssi) TextView rssi;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layItem)
        public void onItemClick() {
            final SearchResult result = (SearchResult) mDataList.get(getAdapterPosition());
            Intent intent = new Intent();
//            intent.setClass(mContext, DeviceDetailActivity.class);
            intent.setClass(mContext, LockStorageActivity.class);
            intent.putExtra("mac", result.getAddress());
            intent.putExtra("name", result.getName());
            intent.putExtra("type", "5");
            mContext.startActivity(intent);
            mActivity.finish();
        }
    }
}
