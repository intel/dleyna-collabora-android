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

public class ServerListAdapter extends BaseAdapter {

    private List<ServerLite> serverList = new ArrayList<ServerLite>();

    public ServerListAdapter(Collection<ServerLite> servers) {
        super();
        serverList.addAll(servers);
        Collections.sort(serverList, new Comparator<ServerLite>() {
            public int compare(ServerLite lhs, ServerLite rhs) {
                return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());
            }
        });
        if (App.LOG) Log.d(App.TAG, "ServerListAdapter: ctor");
    }

    public int getCount() {
        if (App.LOG) Log.d(App.TAG, "ServerListAdapter: getCount() = " + serverList.size());
        return serverList.size();
    }

    public Object getItem(int position) {
        if (App.LOG) Log.d(App.TAG, "ServerListAdapter: getItem() = " + position);
        return serverList.get(position);
    }

    public long getItemId(int position) {
        if (App.LOG) Log.d(App.TAG, "ServerListAdapter: getItemId() = " + position);
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (App.LOG)
            Log.d(App.TAG, "ServerListAdapter: getView() (input is null ? " + (convertView == null ? Boolean.TRUE : Boolean.FALSE) + ")");
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.object_list_item, parent, false);
        }

        ServerLite server = serverList.get(position);

        ImageView iconView = (ImageView) convertView.findViewById(R.id.object_list_item_icon);
        //iconView.setImageURI(Uri.parse(server.getIconUrl()));
        Bitmap bmp = (server.getBitmap() != null) ? server.getBitmap() : BitmapFactory.decodeResource(convertView.getContext().getApplicationContext().getResources(), R.drawable.icon);
        iconView.setImageBitmap(bmp);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.object_list_item_name);
        nameTextView.setText(server.getDisplayName());

        return convertView;
    }
}
