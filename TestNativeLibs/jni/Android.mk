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

# dleyna-jni
include $(CLEAR_VARS)
MY_GLIB := glib-2.34.3
MY_LIBSOUP := libsoup-2.40.3
MY_GSSDP := gssdp-0.14.2
MY_GUPNP := gupnp-0.20.3
MY_GUPNPAV := gupnp-av-0.12.1

LOCAL_MODULE := dleyna-jni

LOCAL_C_INCLUDES := \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0/gobject \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0/gio \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0/glib \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/glib-2.0/glib/deprecated \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gio-unix-2.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gio-unix-2.0/gio \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GLIB) \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GLIB)/glib \
\
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/libsoup-2.4 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/libsoup-2.4/libsoup \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_LIBSOUP) \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_LIBSOUP)/tests \
\
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gssdp-1.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gssdp-1.0/libgssdp \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GSSDP) \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GSSDP)/tests \
\
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GUPNP) \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GUPNP)/tests \
\
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gupnp-av-1.0 \
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/gupnp-av-1.0/libgupnp-av \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GUPNPAV) \
    ../../NativeLibs/sources-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/$(MY_GUPNPAV)/tests \
\
    ../../NativeLibs/install-$(TARGET_ARCH_ABI)-$(TARGET_PLATFORM)/include/libxml2

LOCAL_SRC_FILES := \
    dleyna-jni.c \
    tests.c \
    $(MY_GLIB)/glib/tests/array-test.c \
    $(MY_GLIB)/glib/tests/asyncqueue.c \
    $(MY_GLIB)/tests/asyncqueue-test.c \
    $(MY_GLIB)/glib/tests/atomic.c \
    $(MY_GLIB)/tests/atomic-test.c \
    $(MY_GLIB)/glib/tests/base64.c \
    $(MY_GLIB)/tests/bit-test.c \
    $(MY_GLIB)/glib/tests/bytes.c \
    $(MY_GLIB)/glib/tests/checksum.c \
    $(MY_GLIB)/tests/child-test.c \
    $(MY_GLIB)/glib/tests/cond.c \
    $(MY_GLIB)/glib/tests/convert.c \
    $(MY_GLIB)/glib/tests/dataset.c \
    $(MY_GLIB)/glib/tests/date.c \
    $(MY_GLIB)/glib/tests/dir.c \
    $(MY_GLIB)/tests/dirname-test.c \
    $(MY_GLIB)/glib/tests/environment.c \
    $(MY_GLIB)/tests/env-test.c \
    $(MY_GLIB)/glib/tests/error.c \
    $(MY_GLIB)/glib/tests/fileutils.c \
    $(MY_GLIB)/tests/file-test.c \
    $(MY_GLIB)/glib/tests/gdatetime.c \
    $(MY_GLIB)/glib/tests/gvariant.c \
    $(MY_GLIB)/glib/tests/gwakeuptest.c \
    $(MY_GLIB)/glib/tests/hash.c \
    $(MY_GLIB)/glib/tests/hmac.c \
    $(MY_GLIB)/glib/tests/hostutils.c \
    $(MY_GLIB)/glib/tests/keyfile.c \
    $(MY_GLIB)/glib/tests/list.c \
    $(MY_GLIB)/glib/tests/mainloop.c \
    $(MY_GLIB)/tests/mainloop-test.c \
    $(MY_GLIB)/glib/tests/mappedfile.c \
    $(MY_GLIB)/tests/mapping-test.c \
    $(MY_GLIB)/glib/tests/mutex.c \
    $(MY_GLIB)/glib/tests/node.c \
    $(MY_GLIB)/glib/tests/once.c \
    $(MY_GLIB)/tests/qsort-test.c \
    $(MY_GLIB)/glib/tests/rand.c \
    $(MY_GLIB)/glib/tests/regex.c \
    $(MY_GLIB)/glib/tests/rec-mutex.c \
    $(MY_GLIB)/tests/relation-test.c \
    $(MY_GLIB)/glib/tests/rwlock.c \
    $(MY_GLIB)/glib/tests/scannerapi.c \
    $(MY_GLIB)/glib/tests/sequence.c \
    $(MY_GLIB)/tests/slice-test.c $(MY_GLIB)/tests/memchunks.c \
    $(MY_GLIB)/glib/tests/slist.c \
    $(MY_GLIB)/glib/tests/sort.c \
    $(MY_GLIB)/tests/spawn-test.c \
    $(MY_GLIB)/glib/tests/strfuncs.c \
    $(MY_GLIB)/glib/tests/string.c \
    $(MY_GLIB)/tests/testglib.c \
    $(MY_GLIB)/glib/tests/testing.c \
    $(MY_GLIB)/glib/tests/test-printf.c \
    $(MY_GLIB)/tests/threadpool-test.c \
    $(MY_GLIB)/tests/thread-test.c \
    $(MY_GLIB)/tests/type-test.c \
\
    $(MY_GLIB)/gobject/tests/dynamictests.c \
    $(MY_GLIB)/tests/gobject/gvalue-test.c \
    $(MY_GLIB)/tests/gobject/paramspec-test.c \
    $(MY_GLIB)/gobject/testgobject.c \
    $(MY_GLIB)/gobject/tests/threadtests.c \
\
    $(MY_GLIB)/gio/tests/async-close-output-stream.c \
    $(MY_GLIB)/gio/tests/buffered-input-stream.c \
    $(MY_GLIB)/gio/tests/buffered-output-stream.c \
    $(MY_GLIB)/gio/tests/cancellable.c \
    $(MY_GLIB)/gio/tests/contenttype.c \
    $(MY_GLIB)/gio/tests/contexts.c \
    $(MY_GLIB)/gio/tests/converter-stream.c \
    $(MY_GLIB)/gio/tests/data-input-stream.c \
    $(MY_GLIB)/gio/tests/data-output-stream.c \
    $(MY_GLIB)/gio/tests/echo-server.c \
    $(MY_GLIB)/gio/tests/fileattributematcher.c \
    $(MY_GLIB)/gio/tests/file.c \
    $(MY_GLIB)/gio/tests/filter-cat.c \
    $(MY_GLIB)/gio/tests/filter-streams.c \
    $(MY_GLIB)/gio/tests/g-file.c \
    $(MY_GLIB)/gio/tests/g-file-info.c \
    $(MY_GLIB)/gio/tests/g-icon.c \
    $(MY_GLIB)/tests/gio-test.c \
    $(MY_GLIB)/gio/tests/gschema-compile.c \
    $(MY_GLIB)/gio/tests/gsettings.c \
    $(MY_GLIB)/gio/tests/httpd.c \
    $(MY_GLIB)/gio/tests/inet-address.c \
    $(MY_GLIB)/gio/tests/io-stream.c \
    $(MY_GLIB)/gio/tests/live-g-file.c \
    $(MY_GLIB)/gio/tests/memory-input-stream.c \
    $(MY_GLIB)/gio/tests/memory-output-stream.c \
    $(MY_GLIB)/gio/tests/network-address.c \
    $(MY_GLIB)/gio/tests/network-monitor.c \
    $(MY_GLIB)/gio/tests/permission.c \
    $(MY_GLIB)/gio/tests/pollable.c \
    $(MY_GLIB)/gio/tests/proxy.c \
    $(MY_GLIB)/gio/tests/proxy-test.c \
    $(MY_GLIB)/gio/tests/readwrite.c \
    $(MY_GLIB)/gio/tests/resolver.c \
    $(MY_GLIB)/gio/tests/resources.c \
    $(MY_GLIB)/gio/tests/send-data.c \
    $(MY_GLIB)/gio/tests/simple-async-result.c \
    $(MY_GLIB)/gio/tests/sleepy-stream.c \
    $(MY_GLIB)/gio/tests/socket.c \
    $(MY_GLIB)/gio/tests/socket-client.c \
    $(MY_GLIB)/gio/tests/socket-server.c \
    $(MY_GLIB)/gio/tests/srvtarget.c \
    $(MY_GLIB)/gio/tests/tls-certificate.c \
    $(MY_GLIB)/gio/tests/tls-interaction.c \
    $(MY_GLIB)/gio/tests/unix-fd.c \
    $(MY_GLIB)/gio/tests/unix-streams.c \
    $(MY_GLIB)/gio/tests/vfs.c \
    $(MY_GLIB)/gio/tests/volumemonitor.c \
\
    $(MY_GLIB)/gio/tests/gtesttlsbackend.c \
    $(MY_GLIB)/gio/tests/gtlsconsoleinteraction.c \
    $(MY_GLIB)/gio/tests/test_resources2.c \
\
    $(MY_LIBSOUP)/tests/chunk-test.c \
    $(MY_LIBSOUP)/tests/coding-test.c \
    $(MY_LIBSOUP)/tests/connection-test.c \
    $(MY_LIBSOUP)/tests/context-test.c \
    $(MY_LIBSOUP)/tests/continue-test.c \
    $(MY_LIBSOUP)/tests/cookies-test.c \
    $(MY_LIBSOUP)/tests/date.c \
    $(MY_LIBSOUP)/tests/dns.c \
    $(MY_LIBSOUP)/tests/forms-test.c \
    $(MY_LIBSOUP)/tests/get.c \
    $(MY_LIBSOUP)/tests/header-parsing.c \
    $(MY_LIBSOUP)/tests/misc-test.c \
    $(MY_LIBSOUP)/tests/multipart-test.c \
    $(MY_LIBSOUP)/tests/ntlm-test.c \
    $(MY_LIBSOUP)/tests/redirect-test.c \
    $(MY_LIBSOUP)/tests/requester-test.c \
    $(MY_LIBSOUP)/tests/server-auth-test.c \
    $(MY_LIBSOUP)/tests/simple-httpd.c \
    $(MY_LIBSOUP)/tests/simple-proxy.c \
    $(MY_LIBSOUP)/tests/sniffing-test.c \
    $(MY_LIBSOUP)/tests/socket-test.c \
    $(MY_LIBSOUP)/tests/ssl-test.c \
    $(MY_LIBSOUP)/tests/streaming-test.c \
    $(MY_LIBSOUP)/tests/timeout-test.c \
    $(MY_LIBSOUP)/tests/tld-test.c \
    $(MY_LIBSOUP)/tests/uri-parsing.c \
    $(MY_LIBSOUP)/tests/test-utils.c \
\
    $(MY_GSSDP)/tests/test-browser.c \
    $(MY_GSSDP)/tests/test-publish.c \
\
    $(MY_GUPNP)/tests/test-browsing.c \
    $(MY_GUPNP)/tests/test-introspection.c \
    $(MY_GUPNP)/tests/test-proxy.c \
    $(MY_GUPNP)/tests/test-server.c \
\
    $(MY_GUPNPAV)/tests/check-feature-list-parser.c \
    $(MY_GUPNPAV)/tests/check-search.c \
    $(MY_GUPNPAV)/tests/fragments.c \
    $(MY_GUPNPAV)/tests/test-search-criteria-parser.c

# DOES NOT LINK
#   $(MY_GLIB)/tests/module-test.c $(MY_GLIB)/tests/libmoduletestplugin_a.c $(MY_GLIB)/tests/libmoduletestplugin_b.c \
#   $(MY_LIBSOUP)/tests/auth-test.c \
#   $(MY_LIBSOUP)/tests/proxy-test.c \
#   $(MY_LIBSOUP)/tests/pull-api.c \
#   $(MY_LIBSOUP)/tests/range-test.c \
#   $(MY_LIBSOUP)/tests/xmlrpc-server-test.c \
#   $(MY_LIBSOUP)/tests/xmlrpc-test.c \

# DOES NOT COMPILE
#   $(MY_GLIB)/gio/tests/gdbus-test-codegen.c \

LOCAL_CFLAGS += -DSRCDIR=\"/data/data/com.intel.dleyna.testnativelibs/files/SRCDIR\"
LOCAL_LDLIBS := -llog -landroid -lz
LOCAL_STATIC_LIBRARIES := gupnp-av gupnp gssdp libsoup libxml2 gthread gio gobject gmodule glib iconv libintl ffi
include $(BUILD_SHARED_LIBRARY)
