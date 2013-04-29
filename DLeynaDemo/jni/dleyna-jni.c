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


#include <jni.h>
#include <stddef.h>

#include "dleyna-jni.h"

// Added to glib to allow us to inform it about dir names.
extern void set_dir_names(const char*, const char*);

// The JNI version we are expecting.
#define MY_JNI_VERSION JNI_VERSION_1_6

// This gets called when this native library is loaded.
//
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;

    LOGI("JNI_OnLoad: version = 0x%08x\n", MY_JNI_VERSION);

    if ((*vm)->GetEnv(vm, (void**)&env, MY_JNI_VERSION) != JNI_OK) {
        LOGE("JNI_OnLoad: FAIL\n");
        return -1;
    }

    return MY_JNI_VERSION;
}

// The application informs us of the name of the home and temp directories.
// We pass them on to glib, and chdir to the home directory.
//
void Java_com_intel_dleyna_JNI_setDirNames(JNIEnv* env, jclass clazz, jstring homeJ, jstring tempJ)
{
    const char *homeC = (*env)->GetStringUTFChars(env, homeJ, NULL);
    const char *tempC = (*env)->GetStringUTFChars(env, tempJ, NULL);

    LOGI("setDirNames: home: %s\n", homeC);
    LOGI("setDirNames: temp: %s\n", tempC);
    set_android_dir_names(homeC, tempC);

    if (chdir(homeC) < 0) {
        LOGE("setDirNames: ERROR can't chdir to %s\n", homeC);
    }

    (*env)->ReleaseStringUTFChars(env, homeJ, homeC);
    (*env)->ReleaseStringUTFChars(env, tempJ, tempC);
}
