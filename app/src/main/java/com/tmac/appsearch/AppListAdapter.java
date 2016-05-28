package com.tmac.appsearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by T_MAC on 2016/5/21.
 */
public class AppListAdapter extends BaseAdapter {
    private List<AppInfo> mAppInfoList;

    public void updateData(List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAppInfoList == null ? 0 : mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_app, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo model = (AppInfo) getItem(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(model.getIconByte(), 0, model.getIconByte().length);
        if (bitmap != null) {
            holder.appIcon.setImageBitmap(bitmap);
//            bitmap.recycle();
        }
        holder.appName.setText(model.getAppName());
        return convertView;
    }

    private static class ViewHolder {
        TextView appName;
        ImageView appIcon;

        ViewHolder(View view) {
            appName = (TextView) view.findViewById(R.id.app_name);
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
        }
    }
}