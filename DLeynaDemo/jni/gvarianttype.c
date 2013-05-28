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

#include "dleyna-jni.h"
#include "com_intel_dleyna_GVariantType.h"


JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariantType_newBasicNative(
    JNIEnv* env, jclass clazz, jchar basicType)
{
    char type[2];

    type[0] = (char)basicType;
    type[1] = '\0';
    return (jlong)g_variant_type_new(type);
}

JNIEXPORT jlong JNICALL Java_com_intel_dleyna_GVariantType_newArrayNative(
    JNIEnv* env, jclass clazz, jlong elementType)
{
    return (jlong)g_variant_type_new_array((const GVariantType*)elementType);
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_GVariantType_free(
    JNIEnv* env, jclass clazz, jlong type)
{
    g_variant_type_free((GVariantType*)type);
}
