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

#include <glib.h>
#include <jni.h>

#include "com_intel_dleyna_GMainLoop.h"
#include "util.h"


typedef struct {
    JNIEnv*   env;
    jmethodID runMID;
} This;

typedef struct {
    This*   this;
    jobject runnable;
} Invocation;

static gboolean run(gpointer _invo) {
    Invocation* invo = (Invocation*)_invo;
    This* this = invo->this;
    JNIEnv* env = this->env;
    jobject runnable = invo->runnable;
    if (this->runMID == NULL) {
        jclass runnableClass = (*env)->GetObjectClass(env, runnable);
        this->runMID = (*env)->GetMethodID(env, runnableClass, "run", "()V");
    }
    LOGI("gmainloop: run: runnable=%p", runnable);
    (*env)->CallVoidMethod(env, runnable, this->runMID);
    (*env)->DeleteGlobalRef(env, runnable);
    g_free(invo);
    return FALSE;
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GMainLoop_allocNative
    (JNIEnv* env, jobject peer)
{
    This* this = g_malloc0(sizeof(This));
    this->env = env;
    return PTR_TO_JLONG(this);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GMainLoop_freeNative
    (JNIEnv* env, jobject peer, jlong _this)
{
    g_free(JLONG_TO_PTR(_this));
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_GMainLoop_idleAddNative
    (JNIEnv* env, jobject peer, jlong _this, jobject runnable)
{
    This* this = JLONG_TO_PTR(_this);
    Invocation* invo = g_malloc0(sizeof(Invocation));
    invo->this = this;
    invo->runnable = (*env)->NewGlobalRef(env, runnable);
    LOGI("gmainloop: idleAddNative: runnable=%p", invo->runnable);
    g_idle_add(run, invo);
}
