package com.teespire.leftoverswap.Adapters;

import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teespire.leftoverswap.R;

/**
 * Created by Matih on 18/2/2015.
 */
public class LS_ShareIntentListAdapter extends ArrayAdapter {

    Activity context;
    Object[] items;
    boolean[] arrows;
    int layoutId;
    boolean fbExists;

    public LS_ShareIntentListAdapter(Activity context, int layoutId, Object[] items)
    {
        super(context, layoutId, items);

        this.context = context;
        this.items = items;
        this.layoutId = layoutId;
    }

    public View getView(int pos, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            convertView=inflater.inflate(layoutId, parent,false);
        }

        TextView label = (TextView) convertView.findViewById(R.id.textView_share_name);
        label.setText(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        ImageView image = (ImageView) convertView.findViewById(R.id.logo);

        image.setImageDrawable(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadIcon(context.getPackageManager()));

        return(convertView);
    }

    public void setFbExists(boolean fbExists) {
        this.fbExists = fbExists;
    }
}
