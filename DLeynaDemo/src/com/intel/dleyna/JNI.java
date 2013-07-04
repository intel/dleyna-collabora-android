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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

import com.intel.dleyna.dleynademo.App;

/**
 * Native library initialization stuff.
 */
public class JNI {

    private static final String TEMP_DIR_BASENAME = "glib-tmp";
    private static final String CONF_DIR_BASENAME = ".config";
    private static final String RENDERER_CONF_FILENAME = "dleyna-renderer-service.conf";

    /** Then name of the home directory. */
    private static String homeDirName;

    /** The name of the temp directory. */
    private static String tempDirName;

    /** The name of the directory containing configuration files. */
    private static String confDirName;

    private static boolean initialized = false;

    /**
     * Load the native libraries, and initialize them.
     * Every application process must call this before attempting to use any native methods.
     */
    public static void initialize() {
        if (!initialized) {
            if (App.LOG) Log.i(App.TAG, "JNI: initialize");
            System.loadLibrary("dleyna-jni");
            System.loadLibrary("dleyna-connector-android");
            homeDirName = App.getInstance().getFilesDir().getAbsolutePath();
            tempDirName = homeDirName + '/' + TEMP_DIR_BASENAME;
            setDirNames(homeDirName, tempDirName);
            confDirName = homeDirName + '/' + CONF_DIR_BASENAME;
            writeConfigFiles();
            initialized = true;
        }
    }

    /** Inform the native layer of the names of home and temp dirs */
    public static native void setDirNames(String home, String temp);

    /**
     * Remove the temp directory tree, and create a new empty one.
     * This should be called only once during application initialization,
     * even if the app has multiple processes.
     */
    public static void cleanTempDir() {
        if (App.LOG) Log.i(App.TAG, "JNI: cleanTempDir");
        initialize();
        File tempDir = new File(tempDirName);
        deleteTree(tempDir);
        tempDir.mkdirs();
    }

    private static void deleteTree(File root) {
        if (root.exists()) {
            if (root.isDirectory()) {
                for (File f : root.listFiles()) {
                    deleteTree(f);
                }
            }
            root.delete();
        }
    }

    private static void writeConfigFiles() {
        if (App.LOG) Log.i(App.TAG, "JNI: writeConfigFiles");
        File confDir = new File(confDirName);
        File rendererConfFile = new File(confDir, RENDERER_CONF_FILENAME);
        if (!rendererConfFile.exists()) {
            confDir.mkdirs();
            AssetManager am = App.getInstance().getResources().getAssets();
            try {
                InputStream is = am.open(RENDERER_CONF_FILENAME);
                OutputStream os = new FileOutputStream(rendererConfFile);
                copy(is, os);
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copy(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] buf = new byte[4096];
        for (;;) {
            len = is.read(buf);
            if (len < 0) break;
            os.write(buf, 0, len);
        }
    }
}
