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

# gssdp
include $(CLEAR_VARS)
LOCAL_MODULE := gssdp
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgssdp-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gupnp
include $(CLEAR_VARS)
LOCAL_MODULE := gupnp
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgupnp-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gupnp-av
include $(CLEAR_VARS)
LOCAL_MODULE := gupnp-av
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgupnp-av-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# gupnp-dlna
include $(CLEAR_VARS)
LOCAL_MODULE := gupnp-dlna
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libgupnp-dlna-2.0.a
include $(PREBUILT_STATIC_LIBRARY)

# dleyna-core
include $(CLEAR_VARS)
LOCAL_MODULE := dleyna-core
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/libdleyna-core-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# dleyna-server
include $(CLEAR_VARS)
LOCAL_MODULE := dleyna-server
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/dleyna-server/libdleyna-server-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# dleyna-renderer
include $(CLEAR_VARS)
LOCAL_MODULE := dleyna-renderer
LOCAL_SRC_FILES := ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/dleyna-renderer/libdleyna-renderer-1.0.a
include $(PREBUILT_STATIC_LIBRARY)

# dleyna-connector-android

include $(CLEAR_VARS)
LOCAL_MODULE := dleyna-connector-android

LOCAL_C_INCLUDES := \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/glib-2.0/include \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/dleyna-1.0 \

LOCAL_SRC_FILES := connector.c

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := dleyna-jni
include $(BUILD_SHARED_LIBRARY)

# dleyna-jni

include $(CLEAR_VARS)
LOCAL_MODULE := dleyna-jni

LOCAL_C_INCLUDES := \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/lib/glib-2.0/include \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/dleyna-1.0 \

LOCAL_SRC_FILES := \
    gerror.c \
    gmainloop.c \
    gvariant.c \
    gvarianttype.c \
    jni.c \
    ifaddrs.c \
    serverservice.c \
    rendererservice.c \

LOCAL_LDLIBS := -llog -lz
LOCAL_STATIC_LIBRARIES := dleyna-server dleyna-renderer dleyna-core gupnp-dlna gupnp-av gupnp gssdp libsoup libxml2 gthread gio gobject gmodule glib iconv libintl ffi
include $(BUILD_SHARED_LIBRARY)
