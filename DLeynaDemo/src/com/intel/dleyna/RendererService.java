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
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

import com.intel.dleyna.lib.IRendererCallback;
import com.intel.dleyna.lib.IRendererService;
import com.intel.dleyna.lib.Icon;

public class RendererService extends Service {

    public RendererService() {
        JNI.initialize();
        JNI.cleanTempDir();
    }

    private final IBinder binder = new IRendererService.Stub() {

        private RemoteCallbackList<IRendererCallback> callbacks =
                new RemoteCallbackList<IRendererCallback>();

        public void registerCallback(IRendererCallback cb) {
            Log.i("RendererService", "registerCallback");
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

        /*-----------------+
         | IRendererDevice |
         +-----------------*/

        public String getDeviceType(String objectPath) {
            return null;
        }

        public String getUniqueDeviceName(String objectPath) {
            return null;
        }

        public String getFriendlyName(String objectPath) {
            return null;
        }

        public String getIconURL(String objectPath) {
            return null;
        }

        public String getManufacturer(String objectPath) {
            return null;
        }

        public String getManufacturerURL(String objectPath) {
            return null;
        }

        public String getModelDescription(String objectPath) {
            return null;
        }

        public String getModelName(String objectPath) {
            return null;
        }

        public String getModelNumber(String objectPath) {
            return null;
        }

        public String getSerialNumber(String objectPath) {
            return null;
        }

        public String getPresentationURL(String objectPath) {
            return null;
        }

        public String getProtocolInfo(String objectPath) {
            return null;
        }

        public Icon getIcon(String objectPath) {
            return null;
        }

        public void cancel(String objectPath) {
        }

        /*--------------------+
        | IRendererController |
        +---------------------*/

        public void next(String objectPath) {
        }

        public void previous(String objectPath) {
        }

        public void pause(String objectPath) {
        }

        public void playPause(String objectPath) {
        }

        public void stop(String objectPath) {
        }

        public void play(String objectPath) {
        }

        public void seek(String objectPath, long offset) {
        }

        public void setPosition(String objectPath, long position) {
        }

        public void openUri(String objectPath, String uri) {
        }

        public String getPlaybackStatus(String objectPath) {
            return null;
        }

        public double getRate(String objectPath) {
            return 0;
        }

        public void setRate(String objectPath, double rate) {
        }

        public Bundle getMetadata(String objectPath) {
            return null;
        }

        public double getVolume(String objectPath) {
            return 0;
        }

        public void setVolume(String objectPath, double volume) {
        }

        public long getPosition(String objectPath) {
            return 0;
        }

        public long getMinimumRate(String objectPath) {
            return 0;
        }

        public long getMaximumRate(String objectPath) {
            return 0;
        }

        public boolean getCanGoNext(String objectPath) {
            return false;
        }

        public boolean getCanGoPrevious(String objectPath) {
            return false;
        }

        public boolean getCanPlay(String objectPath) {
            return false;
        }

        public boolean getCanPause(String objectPath) {
            return false;
        }

        public boolean getCanSeek(String objectPath) {
            return false;
        }

        public boolean getCanControl(String objectPath) {
            return false;
        }

        public int getNumberOfTracks(String objectPath) {
            return 0;
        }

        public void goToTrack(String objectPath, int track) {
        }

        public int getCurrentTrack(String objectPath) {
            return 0;
        }

        public void openUriEx(String objectPath, String uri, String metadata) {
        }

        public double[] getTransportPlaySpeeds(String objectPath) {
            return null;
        }

        public boolean getMute(String objectPath) {
            return false;
        }

        public void setMute(String objectPath, boolean value) {
        }

        /*-------------------+
         | IRendererPushHost |
         +-------------------*/

        public String hostFile(String objectPath, String path) {
            return null;
        }

        public void removeFile(String objectPath, String path) {
        }
    };

    public IBinder onBind(Intent intent) {
        return binder;
    }
}
