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

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newLongNative(
    JNIEnv* env, jclass clazz, jlong value)
{
    return PTR_TO_JLONG(g_variant_new_int64(value));
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
    JNIEnv* env, jclass clazz, jlongArray _gvElems, jlong elemType)
{
    jint n = (*env)->GetArrayLength(env, _gvElems);
    GVariant** gvElems = g_malloc_n(n, sizeof(GVariant*));
    int i;
    for (i=0; i < n; i++) {
        gvElems[i] = JLONG_TO_PTR(_gvElems);
    }
    GVariant* gva = g_variant_new_array(JLONG_TO_PTR(elemType), gvElems, (gsize)n);
    g_free(gvElems);
    return PTR_TO_JLONG(gva);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleInt64Native(
    JNIEnv* env, jclass clazz, jlong value)
{
    GVariant* gv = g_variant_new("(x)", value);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleDoubleNative(
    JNIEnv* env, jclass clazz, jdouble value)
{
    GVariant* gv = g_variant_new("(d)", value);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleStringNative(
    JNIEnv* env, jclass clazz, jstring str)
{
    const jbyte* strC = (*env)->GetStringUTFChars(env, str, NULL);
    if (strC == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new("(s)", strC);
    (*env)->ReleaseStringUTFChars(env, str, strC);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleStringStringNative(
    JNIEnv* env, jclass clazz, jstring str1, jstring str2)
{
    const jbyte* str1C = (*env)->GetStringUTFChars(env, str1, NULL);
    const jbyte* str2C = (*env)->GetStringUTFChars(env, str2, NULL);
    if (str1C == NULL || str2C == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new("(ss)", str1C, str2C);
    (*env)->ReleaseStringUTFChars(env, str1, str1C);
    (*env)->ReleaseStringUTFChars(env, str2, str2C);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleStringStringVariantNative(
    JNIEnv* env, jclass clazz, jstring str1, jstring str2, jlong vNative)
{
    const jbyte* str1C = (*env)->GetStringUTFChars(env, str1, NULL);
    const jbyte* str2C = (*env)->GetStringUTFChars(env, str2, NULL);
    if (str1C == NULL || str2C == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new("(ssv)", str1C, str2C, JLONG_TO_PTR(vNative));
    (*env)->ReleaseStringUTFChars(env, str1, str1C);
    (*env)->ReleaseStringUTFChars(env, str2, str2C);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleObjPathInt64Native(
    JNIEnv* env, jclass clazz, jstring str, jlong l)
{
    const jbyte* strC = (*env)->GetStringUTFChars(env, str, NULL);
    if (strC == NULL) {
        return 0;
    }
    GVariant* gv = g_variant_new("(ox)", strC, l);
    (*env)->ReleaseStringUTFChars(env, str, strC);
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleStringArrayStringArrayNative(
    JNIEnv* env, jclass clazz, jobjectArray sa1, jobjectArray sa2)
{
    GVariantBuilder tuple;
    int i;

    g_variant_builder_init(&tuple, G_VARIANT_TYPE("(aoas)"));
    // build first array
    g_variant_builder_open(&tuple, G_VARIANT_TYPE("ao"));
    for (i = 0; i < (*env)->GetArrayLength(env, sa1); ++i) {
        jstring str1 = (jstring)((*env)->GetObjectArrayElement(env, sa1, i));
        const jbyte* utfChars1 = (*env)->GetStringUTFChars(env, str1, JNI_FALSE);
        g_variant_builder_add(&tuple, "o", utfChars1);
        (*env)->ReleaseStringUTFChars(env, str1, utfChars1);
    }
    g_variant_builder_close(&tuple);

    // build second array
    g_variant_builder_open(&tuple, G_VARIANT_TYPE("as"));
    for (i = 0; i < (*env)->GetArrayLength(env, sa2); ++i) {
        jstring str2 = (jstring)((*env)->GetObjectArrayElement(env, sa2, i));
        const jbyte* utfChars2 = (*env)->GetStringUTFChars(env, str2, JNI_FALSE);
        g_variant_builder_add(&tuple, "s", utfChars2);
        (*env)->ReleaseStringUTFChars(env, str2, utfChars2);
    }
    g_variant_builder_close(&tuple);

    GVariant* gv = g_variant_builder_end(&tuple);

    //LOGD("newTupleStringArrayStringArray: gv=%p", gv);
    //LOGD("newTupleStringArrayStringArray: gvType=%s", g_variant_get_type_string(gv));
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleIntIntStringArrayStringNative(
    JNIEnv* env, jclass clazz, jint i1, jint i2, jobjectArray sa, jstring s)
{
    GVariantBuilder tuple;
    int i;

    g_variant_builder_init(&tuple, G_VARIANT_TYPE("(uuass)"));
    g_variant_builder_add(&tuple, "u", i1);
    g_variant_builder_add(&tuple, "u", i2);

    // build string array
    g_variant_builder_open(&tuple, G_VARIANT_TYPE("as"));
    for (i = 0; i < (*env)->GetArrayLength(env, sa); ++i) {
        jstring str = (jstring)((*env)->GetObjectArrayElement(env, sa, i));
        const jbyte* utfChars = (*env)->GetStringUTFChars(env, str, JNI_FALSE);
        g_variant_builder_add(&tuple, "s", utfChars);
        (*env)->ReleaseStringUTFChars(env, str, utfChars);
    }
    g_variant_builder_close(&tuple);

    const jbyte* utfChars = (*env)->GetStringUTFChars(env, s, JNI_FALSE);
    g_variant_builder_add(&tuple, "s", utfChars);
    (*env)->ReleaseStringUTFChars(env, s, utfChars);

    GVariant* gv = g_variant_builder_end(&tuple);

    //LOGD("newTupleIntIntStringArrayString: gv=%p", gv);
    //LOGD("newTupleIntIntStringArrayString: gvType=%s", g_variant_get_type_string(gv));
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariant_newTupleStringIntIntStringArrayStringNative(
    JNIEnv* env, jclass clazz, jstring s1, jint i1, jint i2, jobjectArray sa, jstring s2)
{
    GVariantBuilder tuple;
    int i;

    g_variant_builder_init(&tuple, G_VARIANT_TYPE("(suuass)"));

    const jbyte* utfChars1 = (*env)->GetStringUTFChars(env, s1, JNI_FALSE);
    g_variant_builder_add(&tuple, "s", utfChars1);
    (*env)->ReleaseStringUTFChars(env, s1, utfChars1);

    g_variant_builder_add(&tuple, "u", i1);
    g_variant_builder_add(&tuple, "u", i2);

    // build string array
    g_variant_builder_open(&tuple, G_VARIANT_TYPE("as"));
    for (i = 0; i < (*env)->GetArrayLength(env, sa); ++i) {
        jstring str = (jstring)((*env)->GetObjectArrayElement(env, sa, i));
        const jbyte* utfChars = (*env)->GetStringUTFChars(env, str, JNI_FALSE);
        g_variant_builder_add(&tuple, "s", utfChars);
        (*env)->ReleaseStringUTFChars(env, str, utfChars);
    }
    g_variant_builder_close(&tuple);

    const jbyte* utfChars2 = (*env)->GetStringUTFChars(env, s2, JNI_FALSE);
    g_variant_builder_add(&tuple, "s", utfChars2);
    (*env)->ReleaseStringUTFChars(env, s2, utfChars2);

    GVariant* gv = g_variant_builder_end(&tuple);

    //LOGD("newTupleStringIntIntStringArrayString: gv=%p", gv);
    //LOGD("newTupleStringIntIntStringArrayString: gvType=%s", g_variant_get_type_string(gv));
    return PTR_TO_JLONG(gv);
}

JNIEXPORT jboolean JNICALL Java_com_intel_dleyna_GVariant_getBooleanNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    return g_variant_get_boolean(gv);
}

JNIEXPORT jint JNICALL Java_com_intel_dleyna_GVariant_getInt64Native(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gv = JLONG_TO_PTR(_gv);
    return g_variant_get_int64(gv);
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
    GVariant* gvA = JLONG_TO_PTR(_gv);
    gsize n = g_variant_n_children(gvA);
    jdouble* buf = g_malloc_n(n, sizeof(jdouble));
    int i;
    for (i=0; i < n; i++) {
        GVariant* gvElem = g_variant_get_child_value(gvA, i);
        buf[i] = g_variant_get_double(gvElem);
        g_variant_unref(gvElem);
    }
    jdoubleArray jA = (*env)->NewDoubleArray(env, n);
    (*env)->SetDoubleArrayRegion(env, jA, 0, n, buf);
    g_free(buf);
    return jA;
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

JNIEXPORT jlongArray JNICALL Java_com_intel_dleyna_GVariant_getArrayOfNativeGVariantNative(
    JNIEnv* env, jclass clazz, jlong _gv)
{
    GVariant* gvA = JLONG_TO_PTR(_gv);
    gsize n = g_variant_n_children(gvA);
    jlong* buf = g_malloc_n(n, sizeof(jlong));
    int i;
    for (i=0; i < n; i++) {
        GVariant* gvElem = g_variant_get_child_value(gvA, i);
        buf[i] = PTR_TO_JLONG(gvElem);
        g_variant_unref(gvElem);
    }
    jlongArray jA = (*env)->NewLongArray(env, n);
    (*env)->SetLongArrayRegion(env, jA, 0, n, buf);
    g_free(buf);
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
