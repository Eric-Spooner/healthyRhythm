package com.ceg.med.healthyrhythm.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceg.med.healthyrhythm.R;

import java.util.ArrayList;

import static android.support.v7.content.res.AppCompatResources.getDrawable;

public class MyoListAdapter extends BaseAdapter {

    private ArrayList<MyoListItem> listData;
    private LayoutInflater layoutInflater;

    public MyoListAdapter(ArrayList<MyoListItem> listData, Context context) {
        this.listData = listData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.titleView = convertView.findViewById(R.id.title);
            holder.descriptionView = convertView.findViewById(R.id.description);
            holder.imaveView = convertView.findViewById(R.id.row_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleView.setText(listData.get(position).getName());
        holder.descriptionView.setText(listData.get(position).getMacAdress());
        if (listData.get(position).getScanRecord() != null) {
            holder.imaveView.setImageDrawable(getDrawable(convertView.getContext(), R.mipmap.hc));
        } else {
            holder.imaveView.setImageDrawable(getDrawable(convertView.getContext(), R.mipmap.hc));
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imaveView;
        TextView titleView;
        TextView descriptionView;
        boolean connected;
    }

}
