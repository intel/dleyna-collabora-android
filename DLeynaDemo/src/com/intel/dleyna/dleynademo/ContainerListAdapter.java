package com.intel.dleyna.dleynademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContainerListAdapter extends BaseAdapter {

    private List<ContainedObjectLite> containedObjectList = new ArrayList<ContainedObjectLite>();

    public ContainerListAdapter(Collection<ContainedObjectLite> containers) {
        super();
        containedObjectList.addAll(containers);
        Collections.sort(containedObjectList, new Comparator<ContainedObjectLite>() {
            public int compare(ContainedObjectLite lhs, ContainedObjectLite rhs) {
                return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());
            }
        });
        if (App.LOG) Log.d(App.TAG, "ContainerListAdapter: ctor");
    }

    public int getCount() {
        if (App.LOG)
            Log.d(App.TAG, "ContainerListAdapter: getCount() = " + containedObjectList.size());
        return containedObjectList.size();
    }

    public Object getItem(int position) {
        if (App.LOG) Log.d(App.TAG, "ContainerListAdapter: getItem() = " + position);
        return containedObjectList.get(position);
    }

    public long getItemId(int position) {
        if (App.LOG) Log.d(App.TAG, "ContainerListAdapter: getItemId() = " + position);
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (App.LOG)
            Log.d(App.TAG, "ContainerListAdapter: getView() (input is null ? " + (convertView == null ? Boolean.TRUE : Boolean.FALSE) + ")");
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.object_list_item, parent, false);
        }

        ContainedObjectLite containedObject = containedObjectList.get(position);

        ImageView iconView = (ImageView) convertView.findViewById(R.id.object_list_item_icon);
        //iconView.setImageURI(Uri.parse(containedObject.getIconUrl()));
        Bitmap bmp = (containedObject.getBitmap() != null) ? containedObject.getBitmap() :
                containedObject.isContainer() ?
                        BitmapFactory.decodeResource(convertView.getContext().getApplicationContext().getResources(), R.drawable.ic_folder) :
                        BitmapFactory.decodeResource(convertView.getContext().getApplicationContext().getResources(), R.drawable.icon);
        iconView.setImageBitmap(bmp);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.object_list_item_name);
        nameTextView.setText(containedObject.getDisplayName());

        return convertView;
    }
}
