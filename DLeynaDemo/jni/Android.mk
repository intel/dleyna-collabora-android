#
# dLeyna
#
# Copyright (C) 2013 Intel Corporation. All rights reserved.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU Lesser General Public License,
# version 2.1, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
# for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin St - Fifth Floor, Boston, MA 02110-1301 USA.
#
# Tom Keel <thomas.keel@intel.com>
#

LOCAL_PATH := $(call my-dir)

# glib
include $(CLEAR_VARS)
LOCAL_MODULE := glib
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libglib-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gio
include $(CLEAR_VARS)
LOCAL_MODULE := gio
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgio-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gobject
include $(CLEAR_VARS)
LOCAL_MODULE := gobject
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgobject-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gmodule
include $(CLEAR_VARS)
LOCAL_MODULE := gmodule
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgmodule-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gthread
include $(CLEAR_VARS)
LOCAL_MODULE := gthread
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgthread-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# iconv
include $(CLEAR_VARS)
LOCAL_MODULE := iconv
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)

# intl
include $(CLEAR_VARS)
LOCAL_MODULE := intl
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libintl.a
include $(PREBUILT_STATIC_LIBRARY)

# ffi
include $(CLEAR_VARS)
LOCAL_MODULE := ffi
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libffi.a
include $(PREBUILT_STATIC_LIBRARY)

# libsoup
include $(CLEAR_VARS)
LOCAL_MODULE := libsoup
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libsoup-2.4.a
include $(PREBUILT_STATIC_LIBRARY)

# libxml2
include $(CLEAR_VARS)
LOCAL_MODULE := libxml2
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libxml2.a
include $(PREBUILT_STATIC_LIBRARY)

# dleyna-jni
include $(CLEAR_VARS)
MY_GLIB := glib-2.34.3
MY_LIBSOUP := libsoup-2.40.3
LOCAL_MODULE := dleyna-jni

LOCAL_C_INCLUDES := \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0/glib \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/glib-2.0/include \
    ../../NativeLibs/sources/dleyna-core/libdleyna/core \

LOCAL_SRC_FILES := \
    dleyna-jni.c \
    connector.c \
    gvariant.c \
    gvarianttype.c \

LOCAL_LDLIBS := -llog -landroid -lz
LOCAL_STATIC_LIBRARIES := libsoup libxml2 gthread gio gobject gmodule glib iconv libintl ffi
include $(BUILD_SHARED_LIBRARY)
