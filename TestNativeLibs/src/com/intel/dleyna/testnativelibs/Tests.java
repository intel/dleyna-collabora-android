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

package com.intel.dleyna.testnativelibs;

import android.annotation.SuppressLint;

/**
 * This class contains just about everything concerning each and every
 * individual native test: the declaration of the native entry-point,
 * and an enum subclass for (you guessed it) enumerating the tests and
 * associating a couple of other things with each of them.
 */
public class Tests {

    // glib
    public static native void glibArrayTest();
    public static native void glibAsyncQueue();
    public static native void glibAsyncQueueTest();
    public static native void glibAtomic();
    public static native void glibAtomicTest();
    public static native void glibBase64();
    public static native void glibBitTest();
    public static native void glibBytes();
    public static native void glibChecksum();
    public static native void glibChildTest();
    public static native void glibCond();
    public static native void glibConvert();
    public static native void glibDataSet();
    public static native void glibDate();
    public static native void glibDir();
    public static native void glibDirNameTest();
    public static native void glibEnvironment();
    public static native void glibEnvTest();
    public static native void glibError();
    public static native void glibFileUtils();
    public static native void glibFileTest();
    public static native void glibGDateTime();
    public static native void glibGVariant();
    public static native void glibGWakeupTest();
    public static native void glibHash();
    public static native void glibHMAC();
    public static native void glibHostUtils();
    public static native void glibKeyFile();
    public static native void glibList();
    public static native void glibMainLoop();
    public static native void glibMainLoopTest();
    public static native void glibMappedFile();
    public static native void glibMappingTest();
    public static native void glibMutex();
    public static native void glibNode();
    public static native void glibOnce();
    public static native void glibQSortTest();
    public static native void glibRand();
    public static native void glibRecMutex();
    public static native void glibRegEx();
    public static native void glibRelationTest();
    public static native void glibRWLock();
    public static native void glibScannerAPI();
    public static native void glibSequence();
    public static native void glibSliceTest();
    public static native void glibSList();
    public static native void glibSort();
    public static native void glibSpawnTest();
    public static native void glibStrFuncs();
    public static native void glibString();
    public static native void glibTestGLib();
    public static native void glibTesting();
    public static native void glibTestPrintF();
    public static native void glibThreadPoolTest();
    public static native void glibThreadTest();
    public static native void glibTypeTest();

    // gobject
    public static native void gobjDynamicTests();
    public static native void gobjGValueTest();
    public static native void gobjParamSpecTest();
    public static native void gobjTestGObject();
    public static native void gobjThreadTests();

    // gio
    public static native void gioAsyncCloseOutputStream();
    public static native void gioBufferedInputStream();
    public static native void gioBufferedOutputStream();
    public static native void gioCancellable();
    public static native void gioContentType();
    public static native void gioContexts();
    public static native void gioConverterStream();
    public static native void gioDataInputStream();
    public static native void gioDataOutputStream();
    public static native void gioEchoServer();
    public static native void gioFileAttributeMatcher();
    public static native void gioFile();
    public static native void gioFilterCat();
    public static native void gioFilterStreams();
    public static native void gioGFile();
    public static native void gioGFileInfo();
    public static native void gioGIcon();
    public static native void gioGioTest();
    public static native void gioGSchemaCompile();
    public static native void gioGSettings();
    public static native void gioHttpD();
    public static native void gioInetAddress();
    public static native void gioIoStream();
    public static native void gioLiveGFile();
    public static native void gioMemoryInputStream();
    public static native void gioMemoryOutputStream();
    public static native void gioNetworkAddress();
    public static native void gioNetworkMonitor();
    public static native void gioPermission();
    public static native void gioPollable();
    public static native void gioProxy();
    public static native void gioProxyTest();
    public static native void gioReadWrite();
    public static native void gioResolver();
    public static native void gioResources();
    public static native void gioSendData();
    public static native void gioSimpleAsyncResult();
    public static native void gioSleepyStream();
    public static native void gioSocket();
    public static native void gioSocketClient();
    public static native void gioSocketServer();
    public static native void gioSrvTarget();
    public static native void gioTlsCertificate();
    public static native void gioTlsInteraction();
    public static native void gioUnixFd();
    public static native void gioUnixStreams();
    public static native void gioVfs();
    public static native void gioVolumeMonitor();

    // soup
    public static native void soupChunkTest();
    public static native void soupCodingTest();
    public static native void soupConnectionTest();
    public static native void soupContextTest();
    public static native void soupContinueTest();
    public static native void soupCookiesTest();
    public static native void soupDate();
    public static native void soupDns();
    public static native void soupFormsTest();
    public static native void soupGet();
    public static native void soupHeaderParsing();
    public static native void soupMiscTest();
    public static native void soupMultipartTest();
    public static native void soupNtlmTest();
    public static native void soupRedirectTest();
    public static native void soupRequesterTest();
    public static native void soupServerAuthTest();
    public static native void soupSimpleHttpd();
    public static native void soupSimpleProxy();
    public static native void soupSniffingTest();
    public static native void soupSocketTest();
    public static native void soupSslTest();
    public static native void soupStreamingTest();
    public static native void soupTimeoutTest();
    public static native void soupTldTest();
    public static native void soupUriParsing();

    // gssdp
    public static native void gssdpBrowserTest();
    public static native void gssdpPublishTest();

    /**
     * Enumeration of the various test programs.
     */
    public static enum Enum {

        // glib

        GLIB_ARRAY_TEST("array types and operations", new Impl() {
            public void exec() { glibArrayTest(); }
        }),
        GLIB_ASYNCQUEUE("asynchronous queues", new Impl() {
            public void exec() { glibAsyncQueue(); }
        }),
        GLIB_ASYNCQUEUE_TEST("asynchronous queues", new Impl() {
            public void exec() { glibAsyncQueueTest(); }
        }),
        GLIB_ATOMIC("atomic operations", new Impl() {
            public void exec() { glibAtomic(); }
        }),
        GLIB_ATOMIC_TEST("atomic operations", new Impl() {
            public void exec() { glibAtomicTest(); }
        }),
        GLIB_BASE64("base64 encoding/decoding", new Impl() {
            public void exec() { glibBase64(); }
        }),
        GLIB_BIT_TEST("bit operations", new Impl() {
            public void exec() { glibBitTest(); }
        }),
        GLIB_BYTES("arrays of bytes", new Impl() {
            public void exec() { glibBytes(); }
        }),
        GLIB_CHECKSUM("checksums", new Impl() {
            public void exec() { glibChecksum(); }
        }),
        GLIB_CHILD_TEST("fork two children", new Impl() {
            public void exec() { glibChildTest(); }
        }),
        GLIB_COND("condition variables", new Impl() {
            public void exec() { glibCond(); }
        }),
        GLIB_CONVERT("character set conversions", new Impl() {
            public void exec() { glibConvert(); }
        }),
        GLIB_DATASET("data - mem address association", new Impl() {
            public void exec() { glibDataSet(); }
        }),
        GLIB_DATE("calender stuff", new Impl() {
            public void exec() { glibDate(); }
        }),
        GLIB_DIR("directories", new Impl() {
            public void exec() { glibDir(); }
        }),
        GLIB_DIRNAME_TEST("extract directory name", new Impl() {
            public void exec() { glibDirNameTest(); }
        }),
        GLIB_ENVIRONMENT("environment variables", new Impl() {
            public void exec() { glibEnvironment(); }
        }),
        GLIB_ENV_TEST("environment variables", new Impl() {
            public void exec() { glibEnvTest(); }
        }),
        GLIB_ERROR("error reporting", new Impl() {
            public void exec() { glibError(); }
        }),
        GLIB_FILEUTILS("file utilities", new Impl() {
            public void exec() { glibFileUtils(); }
        }),
        GLIB_FILE_TEST("basic file operations", new Impl() {
            public void exec() { glibFileTest(); }
        }),
        GLIB_GDATETIME("date and time", new Impl() {
            public void exec() { glibGDateTime(); }
        }),
        GLIB_GVARIANT("", new Impl() {
            public void exec() { glibGVariant(); }
        }),
        GLIB_GWAKEUPTEST("g_wakeup_xxx functions", new Impl() {
            public void exec() { glibGWakeupTest(); }
        }),
        GLIB_HASH("hash tables", new Impl() {
            public void exec() { glibHash(); }
        }),
        GLIB_HMAC("secure HMAC digests", new Impl() {
            public void exec() { glibHMAC(); }
        }),
        GLIB_HOSTUTILS("internet host names", new Impl() {
            public void exec() { glibHostUtils(); }
        }),
        GLIB_KEYFILE("", new Impl() {
            public void exec() { glibKeyFile(); }
        }),
        GLIB_LIST("doublely linked lists", new Impl() {
            public void exec() { glibList(); }
        }),
        GLIB_MAINLOOP("main loop", new Impl() {
            public void exec() { glibMainLoop(); }
        }),
        GLIB_MAINLOOP_TEST("main loop, threads, mutexes, io channels, ...", new Impl() {
            public void exec() { glibMainLoopTest(); }
        }),
        GLIB_MAPPEDFILE("memory-mapped files", new Impl() {
            public void exec() { glibMappedFile(); }
        }),
        GLIB_MAPPING_TEST("memory-mapped files", new Impl() {
            public void exec() { glibMappingTest(); }
        }),
        GLIB_MUTEX("mutexes", new Impl() {
            public void exec() { glibMutex(); }
        }),
        GLIB_NODE("n-ary trees", new Impl() {
            public void exec() { glibNode(); }
        }),
        GLIB_ONCE("singletons", new Impl() {
            public void exec() { glibOnce(); }
        }),
        GLIB_QSORT_TEST("qsort", new Impl() {
            public void exec() { glibQSortTest(); }
        }),
        GLIB_RAND("psuedo-random numbers", new Impl() {
            public void exec() { glibRand(); }
        }),
        GLIB_REC_MUTEX("recursive mutexes", new Impl() {
            public void exec() { glibRecMutex(); }
        }),
        GLIB_REGEX("regular expressions", new Impl() {
            public void exec() { glibRegEx(); }
        }),
        GLIB_RELATION_TEST("relations", new Impl() {
            public void exec() { glibRelationTest(); }
        }),
        GLIB_RWLOCK("reader/writer locks", new Impl() {
            public void exec() { glibRWLock(); }
        }),
        GLIB_SCANNERAPI("lexical scanner", new Impl() {
            public void exec() { glibScannerAPI(); }
        }),
        GLIB_SEQUENCE("balanced binary trees", new Impl() {
            public void exec() { glibSequence(); }
        }),
        GLIB_SLICE_TEST("memory slices", new Impl() {
            public void exec() { glibSliceTest(); }
        }),
        GLIB_SLIST("singly linked lists", new Impl() {
            public void exec() { glibSList(); }
        }),
        GLIB_SORT("qsort", new Impl() {
            public void exec() { glibSort(); }
        }),
        GLIB_SPAWN_TEST("spawn processes", new Impl() {
            public void exec() { glibSpawnTest(); }
        }),
        GLIB_STRFUNCS("strings", new Impl() {
            public void exec() { glibStrFuncs(); }
        }),
        GLIB_STRING("strings", new Impl() {
            public void exec() { glibString(); }
        }),
        GLIB_TESTGLIB("some basic tests", new Impl() {
            public void exec() { glibTestGLib(); }
        }),
        GLIB_TESTING("test framework itself", new Impl() {
            public void exec() { glibTesting(); }
        }),
        GLIB_TEST_PRINTF("printf, sprintf, ...", new Impl() {
            public void exec() { glibTestPrintF(); }
        }),
        GLIB_THREADPOOL_TEST("thread pools", new Impl() {
            public void exec() { glibThreadPoolTest(); }
        }),
        GLIB_THREAD_TEST("threads, mutexes, ...", new Impl() {
            public void exec() { glibThreadTest(); }
        }),
        GLIB_TYPE_TEST("basic types", new Impl() {
            public void exec() { glibTypeTest(); }
        }),

        // gobject

        GOBJ_DYNAMIC_TESTS("", new Impl() {
            public void exec() { gobjDynamicTests(); }
        }),
        GOBJ_G_VALUE_TEST("", new Impl() {
            public void exec() { gobjGValueTest(); }
        }),
        GOBJ_PARAM_SPEC_TEST("", new Impl() {
            public void exec() { gobjParamSpecTest(); }
        }),
        GOBJ_TESTGOBJECT("", new Impl() {
            public void exec() { gobjTestGObject(); }
        }),
        GOBJ_THREADTESTS("", new Impl() {
            public void exec() { gobjThreadTests(); }
        }),

        // gio

        GIO_ASYNC_CLOSE_OUTPUT_STREAM("", new Impl() {
            public void exec() { gioAsyncCloseOutputStream(); }
        }),
        GIO_BUFFERED_INPUT_STREAM("", new Impl() {
            public void exec() { gioBufferedInputStream(); }
        }),
        GIO_BUFFERED_OUTPUT_STREAM("", new Impl() {
            public void exec() { gioBufferedOutputStream(); }
        }),
        GIO_CANCELLABLE("", new Impl() {
            public void exec() { gioCancellable(); }
        }),
        GIO_CONTENTTYPE("", new Impl() {
            public void exec() { gioContentType(); }
        }),
        GIO_CONTEXTS("", new Impl() {
            public void exec() { gioContexts(); }
        }),
        GIO_CONVERTER_STREAM("", new Impl() {
            public void exec() { gioConverterStream(); }
        }),
        GIO_DATA_INPUT_STREAM("", new Impl() {
            public void exec() { gioDataInputStream(); }
        }),
        GIO_DATA_OUTPUT_STREAM("", new Impl() {
            public void exec() { gioDataOutputStream(); }
        }),
        GIO_ECHO_SERVER("", new Impl() {
            public void exec() { gioEchoServer(); }
        }),
        GIO_FILEATTRIBUTEMATCHER("", new Impl() {
            public void exec() { gioFileAttributeMatcher(); }
        }),
        GIO_FILE("", new Impl() {
            public void exec() { gioFile(); }
        }),
        GIO_FILTER_CAT("", new Impl() {
            public void exec() { gioFilterCat(); }
        }),
        GIO_FILTER_STREAMS("", new Impl() {
            public void exec() { gioFilterStreams(); }
        }),
        GIO_G_FILE("", new Impl() {
            public void exec() { gioGFile(); }
        }),
        GIO_G_FILE_info("", new Impl() {
            public void exec() { gioGFileInfo(); }
        }),
        GIO_G_ICON("", new Impl() {
            public void exec() { gioGIcon(); }
        }),
        GIO_GIO_TEST("", new Impl() {
            public void exec() { gioGioTest(); }
        }),
        GIO_GSCHEMA_COMPILE("", new Impl() {
            public void exec() { gioGSchemaCompile(); }
        }),
        GIO_GSETTINGS("", new Impl() {
            public void exec() { gioGSettings(); }
        }),
        GIO_HTTPD("", new Impl() {
            public void exec() { gioHttpD(); }
        }),
        GIO_INET_ADDRESS("", new Impl() {
            public void exec() { gioInetAddress(); }
        }),
        GIO_IO_STREAM("", new Impl() {
            public void exec() { gioIoStream(); }
        }),
        GIO_LIVE_G_FILE("", new Impl() {
            public void exec() { gioLiveGFile(); }
        }),
        GIO_MEMORY_INPUT_STREAM("", new Impl() {
            public void exec() { gioMemoryInputStream(); }
        }),
        GIO_MEMORY_OUTPUT_STREAM("", new Impl() {
            public void exec() { gioMemoryOutputStream(); }
        }),
        GIO_NETWORK_ADDRESS("", new Impl() {
            public void exec() { gioNetworkAddress(); }
        }),
        GIO_NETWORK_MONITOR("", new Impl() {
            public void exec() { gioNetworkMonitor(); }
        }),
        GIO_PERMISSION("", new Impl() {
            public void exec() { gioPermission(); }
        }),
        GIO_POLLABLE("", new Impl() {
            public void exec() { gioPollable(); }
        }),
        GIO_PROXY("", new Impl() {
            public void exec() { gioProxy(); }
        }),
        GIO_PROXY_TEST("", new Impl() {
            public void exec() { gioProxyTest(); }
        }),
        GIO_READWRITE("", new Impl() {
            public void exec() { gioReadWrite(); }
        }),
        GIO_RESOLVER("", new Impl() {
            public void exec() { gioResolver(); }
        }),
        GIO_RESOURCES("", new Impl() {
            public void exec() { gioResources(); }
        }),
        GIO_SEND_DATA("", new Impl() {
            public void exec() { gioSendData(); }
        }),
        GIO_SIMPLE_ASYNC_RESULT("", new Impl() {
            public void exec() { gioSimpleAsyncResult(); }
        }),
        GIO_SLEEPY_STREAM("", new Impl() {
            public void exec() { gioSleepyStream(); }
        }),
        GIO_SOCKET("", new Impl() {
            public void exec() { gioSocket(); }
        }),
        GIO_SOCKET_CLIENT("", new Impl() {
            public void exec() { gioSocketClient(); }
        }),
        GIO_SOCKET_SERVER("", new Impl() {
            public void exec() { gioSocketServer(); }
        }),
        GIO_SRVTARGET("", new Impl() {
            public void exec() { gioSrvTarget(); }
        }),
        GIO_TLS_CERTIFICATE("", new Impl() {
            public void exec() { gioTlsCertificate(); }
        }),
        GIO_TLS_INTERACTION("", new Impl() {
            public void exec() { gioTlsInteraction(); }
        }),
        GIO_UNIX_FD("", new Impl() {
            public void exec() { gioUnixFd(); }
        }),
        GIO_UNIX_STREAMS("", new Impl() {
            public void exec() { gioUnixStreams(); }
        }),
        GIO_VFS("", new Impl() {
            public void exec() { gioVfs(); }
        }),
        GIO_VOLUMEMONITOR("", new Impl() {
            public void exec() { gioVolumeMonitor(); }
        }),

        // soup

        SOUP_CHUNK_TEST("", new Impl() {
            public void exec() { soupChunkTest(); }
        }),
        SOUP_CODING_TEST("", new Impl() {
            public void exec() { soupCodingTest(); }
        }),
        SOUP_CONNECTION_TEST("", new Impl() {
            public void exec() { soupConnectionTest(); }
        }),
        SOUP_CONTEXT_TEST("", new Impl() {
            public void exec() { soupContextTest(); }
        }),
        SOUP_CONTINUE_TEST("", new Impl() {
            public void exec() { soupContinueTest(); }
        }),
        SOUP_COOKIES_TEST("", new Impl() {
            public void exec() { soupCookiesTest(); }
        }),
        SOUP_DATE("", new Impl() {
            public void exec() { soupDate(); }
        }),
        SOUP_DNS("", new Impl() {
            public void exec() { soupDns(); }
        }),
        SOUP_FORMS_TEST("", new Impl() {
            public void exec() { soupFormsTest(); }
        }),
        SOUP_GET("", new Impl() {
            public void exec() { soupGet(); }
        }),
        SOUP_HEADER_PARSING("", new Impl() {
            public void exec() { soupHeaderParsing(); }
        }),
        SOUP_MISC_TEST("", new Impl() {
            public void exec() { soupMiscTest(); }
        }),
        SOUP_MULTIPART_TEST("", new Impl() {
            public void exec() { soupMultipartTest(); }
        }),
        SOUP_NTLM_TEST("", new Impl() {
            public void exec() { soupNtlmTest(); }
        }),
        SOUP_REDIRECT_TEST("", new Impl() {
            public void exec() { soupRedirectTest(); }
        }),
        SOUP_REQUESTER_TEST("", new Impl() {
            public void exec() { soupRequesterTest(); }
        }),
        SOUP_SERVER_AUTH_TEST("", new Impl() {
            public void exec() { soupServerAuthTest(); }
        }),
        SOUP_SIMPLE_HTTPD("", new Impl() {
            public void exec() { soupSimpleHttpd(); }
        }),
        SOUP_SIMPLE_PROXY("", new Impl() {
            public void exec() { soupSimpleProxy(); }
        }),
        SOUP_SNIFFING_TEST("", new Impl() {
            public void exec() { soupSniffingTest(); }
        }),
        SOUP_SOCKET_TEST("", new Impl() {
            public void exec() { soupSocketTest(); }
        }),
        SOUP_SSL_TEST("", new Impl() {
            public void exec() { soupSslTest(); }
        }),
        SOUP_STREAMING_TEST("", new Impl() {
            public void exec() { soupStreamingTest(); }
        }),
        SOUP_TIMEOUT_TEST("", new Impl() {
            public void exec() { soupTimeoutTest(); }
        }),
        SOUP_TLD_TEST("", new Impl() {
            public void exec() { soupTldTest(); }
        }),
        SOUP_URI_PARSING("", new Impl() {
            public void exec() { soupUriParsing(); }
        }),

        // gssdp

        GSSDP_BROWSER_TEST("", new Impl() {
            public void exec() { gssdpBrowserTest(); }
        }),
        GSSDP_PUBLISH_TEST("", new Impl() {
            public void exec() { gssdpPublishTest(); }
        });

        /** Short description of the test. */
        public String desc;

        /** The implementation of the test. */
        public Impl impl;

        private Enum(String desc, Impl test) {
            this.desc = desc;
            this.impl = test;
        }

        @SuppressLint("DefaultLocale")
        public String nameInLowerCase() {
            return name().toLowerCase();
        }
    }

    public interface Impl {
        public void exec();
    }

}
