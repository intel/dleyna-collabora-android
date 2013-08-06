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

#include "com_intel_dleyna_GError.h"
#include "util.h"


JNIEXPORT jint JNICALL Java_com_intel_dleyna_GError_getCodeNative(
    JNIEnv* env, jclass clazz, jlong _gerr)
{
    GError* gerr = JLONG_TO_PTR(_gerr);
    return gerr->code;
}

JNIEXPORT jstring JNICALL Java_com_intel_dleyna_GError_getMessageNative(
    JNIEnv* env, jclass clazz, jlong _gerr)
{
    GError* gerr = JLONG_TO_PTR(_gerr);
    return gerr->message ? (*env)->NewStringUTF(env, gerr->message) : NULL;
}
