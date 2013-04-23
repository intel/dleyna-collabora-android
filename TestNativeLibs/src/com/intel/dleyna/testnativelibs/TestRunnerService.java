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

package com.intel.dleyna.testnativelibs;

import com.intel.dleyna.JNI;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class TestRunnerService extends Service {

    private final IBinder binder = new TestRunnerInterface.Stub() {

        /**
         * Execute the named test, i.e. {@link Tests.Enum#valueOf(String)}.
         */
        public void exec(String name) throws RemoteException {
            if (App.LOG) Log.i(App.TAG, "TestRunnerService: exec: " + name);
            JNI.initialize();
            JNI.cleanTempDir();
            Tests.Enum.valueOf(name).impl.exec();
        }

        /**
         * Die -- exit this process.
         */
        public void die() throws RemoteException {
            if (App.LOG) Log.i(App.TAG, "TestRunnerService: die");
            Runtime.getRuntime().exit(0);
        }
    };

    public IBinder onBind(Intent intent) {
        return binder;
    }
}
