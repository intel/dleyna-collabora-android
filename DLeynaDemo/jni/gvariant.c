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

#include "com_intel_dleyna_GVariant.h"
#include "util.h"


JNIEXPORT void JNICALL Java_com_intel_dleyna_GVariant_refSink(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    g_variant_ref_sink(gv);
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_GVariant_unref(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    g_variant_unref(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newBooleanNative(
    JNIEnv* env, jclass clazz, jboolean value)
{
    return PTR_TO_JLONG(g_variant_new_boolean(value));
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newUInt32Native(
    JNIEnv* env, jclass clazz, jint value)
{
    return PTR_TO_JLONG(g_variant_new_uint32(value));
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newDoubleNative(
    JNIEnv* env, jclass clazz, jdouble value)
{
    return PTR_TO_JLONG(g_variant_new_double(value));
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newStringNative(
    JNIEnv* env, jclass clazz, jstring value)
{
    const jbyte* str = (*env)->GetStringUTFChars(env, value, NULL);
    if (str == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new_string(str);
    (*env)->ReleaseStringUTFChars(env, value, str);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newObjectPathNative(
    JNIEnv* env, jclass clazz, jstring value)
{
    const jbyte* str = (*env)->GetStringUTFChars(env, value, NULL);
    if (str == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new_object_path(str);
    (*env)->ReleaseStringUTFChars(env, value, str);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newArrayNative(
    JNIEnv* env, jclass clazz, jobjectArray gva, jlong elemType)
{
    // TODO
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_intel_dleyna_GVariant_getBooleanNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    return g_variant_get_boolean(gv);
}

JNIEXPORT jint JNICALL Java_com_intel_dleyna_GVariant_getUInt32Native(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    return g_variant_get_uint32(gv);
}

JNIEXPORT jdouble JNICALL Java_com_intel_dleyna_GVariant_getDoubleNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    return g_variant_get_double(gv);
}

JNIEXPORT jstring JNICALL Java_com_intel_dleyna_GVariant_getStringNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    const char* str = g_variant_get_string(gv, NULL);
    return (*env)->NewStringUTF(env, str);
}

JNIEXPORT jdoubleArray JNICALL Java_com_intel_dleyna_GVariant_getArrayOfDoubleNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    gsize n = g_variant_n_children(gv);
    // TODO
    return 0;
}

JNIEXPORT jobjectArray JNICALL Java_com_intel_dleyna_GVariant_getArrayOfStringNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gvA = JLONG_TO_PTR(_gv);
    gsize n = g_variant_n_children(gvA);
    jclass stringClass = (*env)->FindClass(env, "java/lang/String");
    jobjectArray jA = (*env)->NewObjectArray(env, n, stringClass, NULL);
    int i;
    for (i=0; i < n; i++) {
        GVariant* gvStr = g_variant_get_child_value(gvA, i);
        gsize len;
        const gchar* str = g_variant_get_string(gvStr, &len);
        jstring jStr = (*env)->NewStringUTF(env, str);
        (*env)->SetObjectArrayElement(env, jA, i, jStr);
        g_variant_unref(gvStr);
    }
    return jA;
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_getChildValueNative(
    JNIEnv* env, jclass clazz, jlong _container, jint index)
{
    GVariant* container = JLONG_TO_PTR(_container);
    GVariant* child = g_variant_get_child_value(container, index);
    return PTR_TO_JLONG(child);
}

JNIEXPORT jstring JNICALL Java_com_intel_dleyna_GVariant_getTypeStringNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    const gchar* str = g_variant_get_type_string(gv);
    return (*env)->NewStringUTF(env, str);
}
