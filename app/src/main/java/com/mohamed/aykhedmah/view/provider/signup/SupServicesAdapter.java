package com.mohamed.aykhedmah.view.provider.signup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mohamed.aykhedmah.R;
import com.mohamed.aykhedmah.data.network.response.subservices.SubData;

import java.util.List;


public class SupServicesAdapter extends BaseAdapter {

    private List<SubData> items ;
    private int bgType ;
    public SupServicesAdapter(List<SubData> items){
        this.items=items;
        this.bgType=bgType;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {

                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.spinner_item_layout_light, viewGroup, false);

            textView = (TextView) view;
        } else {
            textView = (TextView) view;
        }
        if (i != 0){
            textView.setText(items.get(i).getTitle());
        }else {
            textView.setText(items.get(i).getTitle());
        }
        return textView;
    }
}
