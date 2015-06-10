/*
 * dLeyna
 *
 * Copyright (C) 2015 Intel Corporation. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St - Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.intel.dleyna.dleynademo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.IServerController;
import com.intel.dleyna.lib.Server;
import com.intel.dleyna.lib.ServerControllerListener;
import com.intel.dleyna.lib.ServerManager;
import com.intel.dleyna.lib.ServerManagerListener;

/**
 * This is the server activity of the dLeyna demo app.
 */
public class DleynaServerActivity extends Activity {

    private static final String TAG = App.TAG;

    private Map<String, Server> serverMap = new ConcurrentHashMap<String, Server>();
    private Map<String, ServerLite> serverLiteMap = new ConcurrentHashMap<String, ServerLite>();

    private Stack<String> objectPathStack = new Stack<String>();

    private ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onCreate");
        setContentView(R.layout.dleyna_server);

        listView = (ListView) findViewById(R.id.server_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView) view.findViewById(R.id.object_list_item_name)).getText().toString();
                if (App.LOG) Log.d(TAG, "DleynaServerActivity: onItemClick() = " + name);

                String objectPath;

                BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
                if (adapter instanceof ServerListAdapter) {
                    ServerLite server = (ServerLite) adapter.getItem(position);
                    objectPath = server.getObjectPath();
                } else if (adapter instanceof ContainerListAdapter) {
                    ContainedObjectLite containedObject = (ContainedObjectLite) adapter.getItem(position);
                    objectPath = containedObject.getObjectPath();

                    if (!containedObject.isContainer()) {
                        String url = containedObject.getUrl();
                        if (url != null) {
                            Uri uri = Uri.parse(url);
                            Intent renderIntent = new Intent(Intent.ACTION_VIEW, uri);

                            // verify it resolves
                            PackageManager packageManager = getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(renderIntent, 0);
                            boolean isIntentSafe = !activities.isEmpty();
                            if (isIntentSafe) {
                                startActivity(renderIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "No renderers found", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "URL is null", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                } else {
                    if (App.LOG)
                        Log.w(TAG, "DleynaServerActivity: onItemClick() Unknown Adapter " + adapter.getClass().getName());
                    return;
                }

                // server or container
                objectPathStack.push(objectPath);
                populateChildren(objectPath);
            }
        });

        if (savedInstanceState != null) {
            ArrayList<String> objectPathList = savedInstanceState.getStringArrayList("com.intel.dleyna.dleynademo.OBJECT_PATH_STACK");
            if (objectPathList != null) {
                // restore stack after rotation
                objectPathStack.clear();
                objectPathStack.addAll(objectPathList);
            }
        }

        new ConnectTask().execute(!objectPathStack.empty());
    }

    protected void onRestart() {
        super.onRestart();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onRestart");
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onStart");
    }
    
    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onResume");
    }

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onPause");
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onDestroy");
        serverManagerDisconnect();
    }

    protected void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        if (App.LOG) Log.d(TAG, "DleynaServerActivity: onSaveInstanceState");

        String[] objectPathArray = objectPathStack.toArray(new String[objectPathStack.size()]);
        ArrayList<String> objectPathList = new ArrayList<String>(Arrays.asList(objectPathArray));
        instanceState.putStringArrayList("com.intel.dleyna.dleynademo.OBJECT_PATH_STACK", objectPathList);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.server_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                serverManagerRescan();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !objectPathStack.empty()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (objectPathStack.size() > 1) {
                        String objectPath = objectPathStack.pop();
                        if (App.LOG)
                            Log.d(TAG, "DleynaServerActivity: backbutton() popping = " + objectPath);
                        objectPath = objectPathStack.peek();
                        if (App.LOG)
                            Log.d(TAG, "DleynaServerActivity: backbutton() peeking = " + objectPath);
                        populateChildren(objectPath);
                    } else {
                        String objectPath = objectPathStack.pop();
                        if (App.LOG)
                            Log.d(TAG, "DleynaServerActivity: backbutton() popping = " + objectPath);
                        populateServers();
                    }
                }
            }, 10);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void populateServers() {
        new GetServersTask().execute();
    }

    private void populateChildren(String objectPath) {
        if (objectPath == null) {
            if (App.LOG)
                Log.d(TAG, "populateChildren object path is null.  was device just rotated?");
            return;
        }

        String serverObjectPath = objectPath;
        Server s = serverMap.get(serverObjectPath);
        if (s == null) {
            // try the server part of the object path
            String[] parts = serverObjectPath.split("/");
            if (parts.length > 5) {
                serverObjectPath = parts[0] + "/" + parts[1] + "/" + parts[2] + "/" + parts[3] + "/" + parts[4] + "/" + parts[5];
                if (App.LOG)
                    Log.d(TAG, "retry getServerObject:  serverObjectPath = " + serverObjectPath);
                s = serverMap.get(serverObjectPath);
            }
        }
        if (s == null) {
            Log.w(TAG, String.format("Server Object not found: objectPath=%s", objectPath));
            Toast.makeText(DleynaServerActivity.this, "Path not found: " + objectPath, Toast.LENGTH_SHORT).show();
            return;
        }

        new GetChildrenTask(s, objectPath).execute();
    }

    private void serverManagerConnect() {
        serverMgr.connect(DleynaServerActivity.this);
    }

    private void serverManagerDisconnect() {
        serverMgr.disconnect();
    }

    private void serverManagerRescan() {
        serverManagerDisconnect();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
        new ConnectTask().execute(!objectPathStack.empty());

//        try {
//            serverMgr.rescan();
//        } catch (RemoteException e) {
//            if (App.LOG) Log.w(TAG, "RemoteException in rescan() " + e.getMessage());
//        } catch (DLeynaException e) {
//            if (App.LOG) Log.w(TAG, "DLeynaException in rescan() " + e.getMessage());
//        }
    }

    private String serverManagerGetVersion() {
        String version = null;
        try {
            version = serverMgr.getVersion();
        } catch (RemoteException e) {
            if (App.LOG) Log.w(TAG, "RemoteException in getServers() " + e.getMessage());
        } catch (DLeynaException e) {
            if (App.LOG) Log.w(TAG, "DLeynaException in getServers() " + e.getMessage());
        }
        return version;
    }

    private Server[] serverManagerGetServers() {
        Server[] servers = null;
        try {
            servers = serverMgr.getServers();
        } catch (RemoteException e) {
            if (App.LOG) Log.w(TAG, "RemoteException in getVersion() " + e.getMessage());
        } catch (DLeynaException e) {
            if (App.LOG) Log.w(TAG, "DLeynaException in getVersion() " + e.getMessage());
        }
        return servers;
    }

    private void newServer(Server s) {
        String objectPath = s.getObjectPath();
        String name = null;
        String iconUrl = null;
        try {
            name = s.getFriendlyName();
        } catch (Exception e) {
            if (App.LOG) Log.w(TAG, "DleynaServerActivity: failed to get friendly name for " + objectPath);
        }
        try {
            iconUrl = s.getIconUrl();
        } catch (Exception e) {
            if (App.LOG) Log.w(TAG, "DleynaServerActivity: failed to get icon url for " + objectPath);
        }

        serverMap.put(objectPath, s);
        ServerLite server = new ServerLite(objectPath, name, iconUrl);
        new GetIconTask(server).execute();

        if (App.LOG) Log.d(TAG, "DleynaServerActivity: New server " + server);
    }

    private ServerManager serverMgr = new ServerManager(new ServerManagerListener() {

        public void onConnected() {
            if (App.LOG) Log.i(TAG, "DleynaServerActivity: onServerConnected");
        }

        public void onDisconnected() {
            if (App.LOG) Log.i(TAG, "DleynaServerActivity: onServerDisconnected");
            serverMap.clear();
            serverLiteMap.clear();

            ServerListAdapter serverListAdapter = new ServerListAdapter(serverLiteMap.values());
            listView.setAdapter(serverListAdapter);
        }

        public void onServerFound(final Server s) {
            s.addControllerListener(new ServerControllerListener() {
                String objectPath = s.getObjectPath();

                public void onSystemUpdateId(IServerController c, int systemId) {
                    if (App.LOG)
                        Log.i(TAG, "DleynaServerActivity: system update for " + objectPath + ", id = " + systemId);
                }
            });

            newServer(s);
        }

        public void onServerLost(final Server s) {
            serverMap.remove(s.getObjectPath());
            ServerLite server = serverLiteMap.remove(s.getObjectPath());

            if (server == null) {
                if (App.LOG)
                    Log.i(TAG, "DleynaServerActivity: Lost unknown server for " + s.getObjectPath());
            } else {
                if (App.LOG) Log.i(TAG, "DleynaServerActivity: Lost server " + server.getDisplayName());

                if ((!objectPathStack.empty()) && objectPathStack.get(0).equals(s.getObjectPath())) {
                    // current server was lost
                    ServerListAdapter serverListAdapter = new ServerListAdapter(serverLiteMap.values());
                    listView.setAdapter(serverListAdapter);
                }

                if (serverLiteMap.isEmpty()) {
                    Toast.makeText(DleynaServerActivity.this, "No Servers", Toast.LENGTH_LONG).show();
                }
            }
        }
    });

    class GetIconTask extends AsyncTask<Void, Void, Bitmap> {

        private ServerLite server;

        public GetIconTask(ServerLite server) {
            this.server = server;
        }

        protected Bitmap doInBackground(Void... params) {
            Bitmap bmp = null;
            String iconUrl = server.getIconUrl();
            if ((iconUrl != null) && (iconUrl.length() > 0)) {
                try {
                    URL url = new URL(iconUrl);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    if (App.LOG)
                        Log.w(TAG, "DleynaServerActivity: failed to get icon for url " + params[0], e);
                }
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                server.setBitmap(result);
            }

            serverLiteMap.put(server.getObjectPath(), server);

            if (objectPathStack.empty()) {
                ServerListAdapter serverListAdapter = new ServerListAdapter(serverLiteMap.values());
                listView.setAdapter(serverListAdapter);
            }
        }
    }

    class GetServersTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            Server[] servers = serverManagerGetServers();
            if (servers != null) {
                for (Server s : servers) {
                    newServer(s);
                }
            }
            return null;
        }
    }

    class GetChildrenTask extends AsyncTask<Void, Void, List<ContainedObjectLite>> {

        private Server server;
        private String objectPath;

        public GetChildrenTask(Server server, String objectPath) {
            this.server = server;
            this.objectPath = objectPath;
        }

        protected List<ContainedObjectLite> doInBackground(Void... params) {
            List<ContainedObjectLite> container = new ArrayList<ContainedObjectLite>();
            Bundle[] children = null;
            try {
                children = server.getChildren(objectPath);
            } catch (RemoteException e) {
                if (App.LOG) Log.w(TAG, "RemoteException in getChildren() " + e.getMessage());
            } catch (DLeynaException e) {
                if (App.LOG) Log.w(TAG, "DLeynaException in getChildren() " + e.getMessage());
            }

            if (children != null) {
                for (Bundle child : children) {
                    String path = null;
                    String displayName = null;
                    String url = null;
                    String albumArtUrl = null;
                    String mimeType = null;
                    boolean isContainer = false;

                    for (String key : child.keySet()) {
                        if (key.equalsIgnoreCase("objectpath")) {
                            path = child.getString(key);
                        } else if (key.equalsIgnoreCase("displayname")) {
                            displayName = child.getString(key);
                        } else if (key.equalsIgnoreCase("urls")) {
                            String[] urls = child.getStringArray(key);
                            if (urls != null && urls.length > 0) {
                                url = urls[0];
                            }
                        } else if (key.equalsIgnoreCase("albumarturl")) {
                            albumArtUrl = child.getString(key);
                        } else if (key.equalsIgnoreCase("mimetype")) {
                            mimeType = child.getString(key);
                        } else if (key.equalsIgnoreCase("container")) {
                            isContainer = child.getBoolean(key);
                        } else {
                            // for future
                        }
                    }
                    container.add(new ContainedObjectLite(path, displayName, url, albumArtUrl, mimeType, isContainer));
                    if (App.LOG)
                        Log.d(TAG, "Added child: " + displayName + ", isContainer: " + isContainer);
                }
            }

            // get bitmaps
            for (ContainedObjectLite object : container) {
                String iconUrl = object.getIconUrl();
                if ((iconUrl != null) && (iconUrl.length() > 0)) {
                    Bitmap bmp = null;
                    try {
                        URL url = new URL(iconUrl);
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (Exception e) {
                        if (App.LOG)
                            Log.w(TAG, "DleynaServerActivity: failed to get icon for url " + iconUrl, e);
                    }
                    object.setBitmap(bmp);
                }
            }

            return container;
        }

        protected void onPostExecute(List<ContainedObjectLite> result) {
            if (result != null) {
                ContainerListAdapter containerListAdapter = new ContainerListAdapter(result);
                listView.setAdapter(containerListAdapter);
                if (result.isEmpty()) {
                    Toast.makeText(DleynaServerActivity.this, "Container is empty", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class ConnectTask extends AsyncTask<Boolean, Void, Boolean> {

        protected void onPreExecute() {
            super.onPreExecute();
            serverManagerConnect();
        }

        protected Boolean doInBackground(Boolean... params) {

            boolean mustRestore = (params != null) ? params[0] : false;
            boolean stillWaiting = true;
            int retries = 10;
            while (stillWaiting) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }

                stillWaiting = (serverManagerGetVersion() == null);
                if ((!stillWaiting) && mustRestore && (!objectPathStack.empty())) {
                    stillWaiting = (--retries > 0);
                    if (App.LOG)
                        Log.d(TAG, "Waiting for connection with " + objectPathStack.elementAt(0));
                    // wait for connection with desired server (give up after 10 seconds)
                    Server[] servers = serverManagerGetServers();
                    for (Server server : servers) {
                        if (objectPathStack.elementAt(0).equals(server.getObjectPath())) {
                            stillWaiting = false;
                            break;
                        }
                    }
                }
            }

            if (retries == 0) {
                // retry failed -- display server list
                if (App.LOG)
                    Log.d(TAG, "Connection retry count = " + retries + ", refresh servers");
                objectPathStack.clear();
            }

            return mustRestore;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                // restore state after rotation
                if (objectPathStack.empty()) {
                    if (App.LOG) Log.d(TAG, "DleynaServerActivity:connectTask() nothing to peek");
                    populateServers();
                } else {
                    String objectPath = objectPathStack.peek();
                    if (App.LOG) Log.d(TAG, "DleynaServerActivity: connectTask() peeking = " + objectPath);
                    populateChildren(objectPath);
                }
            }
        }
    }
}
