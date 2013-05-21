/*
 * dLeyna
 *
 * Copyright (C) 2013 Intel Corporation. All rights reserved.
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
 * Tom Keel <thomas.keel@intel.com>
 */

package com.intel.dleyna;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

import com.intel.dleyna.dleynademo.App;

public class RendererService extends Service {

    public RendererService() {
        JNI.initialize();
        JNI.cleanTempDir();
    }

    private final IBinder binder = new RendererInterface.Stub() {

        private RemoteCallbackList<RendererCallbackInterface> callbacks =
                new RemoteCallbackList<RendererCallbackInterface>();

        public void registerCallback(RendererCallbackInterface cb) {
            if (App.LOG) Log.i(App.TAG, "RendererService: registerCallback");
            callbacks.register(cb);
        }

//      How to broadcast callbacks:
//      {
//          int n = callbacks.beginBroadcast();
//          for (int i = 0; i < n; i++) {
//              try {
//                  callbacks.getBroadcastItem(i).aMethod();
//              } catch (RemoteException e) {
//                  // The RemoteCallbackList removes dead objects.
//              }
//          }
//          callbacks.finishBroadcast();
//      }
    };

    public IBinder onBind(Intent intent) {
        return binder;
    }
}
