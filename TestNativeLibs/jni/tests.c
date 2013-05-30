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

#include <string.h>
#include <jni.h>

// Convenience macro for defining native test cases. Example usage:
//
//    DEFINE_TEST(foo_test, fooTest, "arg1", "arg2")
//
// will generate
//
//    extern int foo_test_main(int argc, char* argv[]);
//    void Java_com_intel_dleyna_testnativelibs_Tests_fooTest(JNIEnv* env, jclass clazz) {
//        static char* argv[] = { "foo_test", "arg1", "arg2", NULL };
//        foo_test_main(3, argv);
//    }
//
#define DEFINE_TEST(CNAME, JNAME, ...) \
    extern int CNAME ## _main(int argc, char* argv[]); \
    void Java_com_intel_dleyna_testnativelibs_Tests_ ## JNAME(JNIEnv* env, jclass clazz) { \
        static char* argv[] = { #CNAME, ##__VA_ARGS__, NULL }; \
        CNAME ## _main( (sizeof(argv) / sizeof(char*)) - 1, argv); \
    }

// glib

DEFINE_TEST(glib_array_test, glibArrayTest)
DEFINE_TEST(glib_asyncqueue, glibAsyncQueue)
DEFINE_TEST(glib_asyncqueue_test, glibAsyncQueueTest)
DEFINE_TEST(glib_atomic, glibAtomic)
DEFINE_TEST(glib_atomic_test, glibAtomicTest)
DEFINE_TEST(glib_base64, glibBase64)
DEFINE_TEST(glib_bit_test, glibBitTest)
DEFINE_TEST(glib_bytes, glibBytes)
DEFINE_TEST(glib_checksum, glibChecksum)
DEFINE_TEST(glib_child_test, glibChildTest)
DEFINE_TEST(glib_cond, glibCond)
DEFINE_TEST(glib_convert, glibConvert)
DEFINE_TEST(glib_dataset, glibDataSet)
DEFINE_TEST(glib_date, glibDate)
DEFINE_TEST(glib_dir, glibDir)
DEFINE_TEST(glib_dirname_test, glibDirNameTest)
DEFINE_TEST(glib_environment, glibEnvironment)
DEFINE_TEST(glib_env_test, glibEnvTest)
DEFINE_TEST(glib_error, glibError)
DEFINE_TEST(glib_fileutils, glibFileUtils)
DEFINE_TEST(glib_file_test, glibFileTest)
DEFINE_TEST(glib_gdatetime, glibGDateTime, "-k", "--verbose")
DEFINE_TEST(glib_gvariant, glibGVariant)
DEFINE_TEST(glib_gwakeuptest, glibGWakeupTest, "-k", "--verbose")
DEFINE_TEST(glib_hash, glibHash)
DEFINE_TEST(glib_hmac, glibHMAC)
DEFINE_TEST(glib_hostutils, glibHostUtils)
DEFINE_TEST(glib_keyfile, glibKeyFile)
DEFINE_TEST(glib_list, glibList)
DEFINE_TEST(glib_mainloop, glibMainLoop)
DEFINE_TEST(glib_mainloop_test, glibMainLoopTest)
DEFINE_TEST(glib_mappedfile, glibMappedFile)
DEFINE_TEST(glib_mapping_test, glibMappingTest)
DEFINE_TEST(glib_mutex, glibMutex)
DEFINE_TEST(glib_node, glibNode)
DEFINE_TEST(glib_once, glibOnce)
DEFINE_TEST(glib_qsort_test, glibQSortTest)
DEFINE_TEST(glib_rand, glibRand)
DEFINE_TEST(glib_rec_mutex, glibRecMutex)
DEFINE_TEST(glib_regex, glibRegEx)
DEFINE_TEST(glib_relation_test, glibRelationTest)
DEFINE_TEST(glib_rwlock, glibRWLock, "--verbose")
DEFINE_TEST(glib_scannerapi, glibScannerAPI)
DEFINE_TEST(glib_sequence, glibSequence)
DEFINE_TEST(glib_slice_test, glibSliceTest, "3", "G", "5431")
DEFINE_TEST(glib_slist, glibSList)
DEFINE_TEST(glib_sort, glibSort)
DEFINE_TEST(glib_spawn_test, glibSpawnTest)
DEFINE_TEST(glib_strfuncs, glibStrFuncs)
DEFINE_TEST(glib_string, glibString)
DEFINE_TEST(glib_testglib, glibTestGLib, "-k", "--verbose")
DEFINE_TEST(glib_testing, glibTesting, "-k", "--verbose")
DEFINE_TEST(glib_test_printf, glibTestPrintF, "-k", "--verbose")
DEFINE_TEST(glib_threadpool_test, glibThreadPoolTest)
DEFINE_TEST(glib_thread_test, glibThreadTest)
DEFINE_TEST(glib_type_test, glibTypeTest)

// gobject

DEFINE_TEST(gobj_dynamictests, gobjDynamicTests, "-k")
DEFINE_TEST(gobj_gvalue_test, gobjGValueTest, "-k", "--verbose")
DEFINE_TEST(gobj_paramspec_test, gobjParamSpecTest, "-k", "--verbose")
DEFINE_TEST(gobj_testgobject, gobjTestGObject)
DEFINE_TEST(gobj_threadtests, gobjThreadTests, "-k", "--verbose")

// gio

DEFINE_TEST(gio_async_close_output_stream, gioAsyncCloseOutputStream, "-k", "--verbose")
DEFINE_TEST(gio_buffered_input_stream, gioBufferedInputStream, "-k", "--verbose")
DEFINE_TEST(gio_buffered_output_stream, gioBufferedOutputStream, "-k", "--verbose")
DEFINE_TEST(gio_cancellable, gioCancellable, "-k", "--verbose")
DEFINE_TEST(gio_contenttype, gioContentType, "-k", "--verbose")
DEFINE_TEST(gio_contexts, gioContexts, "-k", "--verbose")
DEFINE_TEST(gio_converter_stream, gioConverterStream, "-k", "--verbose")
DEFINE_TEST(gio_data_input_stream, gioDataInputStream, "-k", "--verbose")
DEFINE_TEST(gio_data_output_stream, gioDataOutputStream, "-k", "--verbose")
DEFINE_TEST(gio_echo_server, gioEchoServer, "--port", "7772")
DEFINE_TEST(gio_fileattributematcher, gioFileAttributeMatcher, "-k", "--verbose")
DEFINE_TEST(gio_file, gioFile, "-k", "--verbose")
DEFINE_TEST(gio_filter_cat, gioFilterCat)
DEFINE_TEST(gio_filter_streams, gioFilterStreams, "-k", "--verbose")
DEFINE_TEST(gio_g_file, gioGFile, "-k", "--verbose")
DEFINE_TEST(gio_g_file_info, gioGFileInfo, "-k", "--verbose")
DEFINE_TEST(gio_g_icon, gioGIcon, "-k", "--verbose")
DEFINE_TEST(gio_gio_test, gioGioTest, "2")
DEFINE_TEST(gio_gschema_compile, gioGSchemaCompile, "-k", "--verbose")
DEFINE_TEST(gio_gsettings, gioGSettings, "-k", "--verbose")
DEFINE_TEST(gio_httpd, gioHttpD, "--port", "7772", "/data/data/com.intel.dleyna.testnativelibs/files/SRCDIR")
DEFINE_TEST(gio_inet_address, gioInetAddress, "-k", "--verbose")
DEFINE_TEST(gio_io_stream, gioIoStream, "-k", "--verbose")
DEFINE_TEST(gio_live_g_file, gioLiveGFile, "-k", "--verbose")
DEFINE_TEST(gio_memory_input_stream, gioMemoryInputStream, "-k", "--verbose")
DEFINE_TEST(gio_memory_output_stream, gioMemoryOutputStream, "-k", "--verbose")
DEFINE_TEST(gio_network_address, gioNetworkAddress, "-k", "--verbose")
DEFINE_TEST(gio_network_monitor, gioNetworkMonitor, "-k", "--verbose")
DEFINE_TEST(gio_permission, gioPermission, "-k", "--verbose")
DEFINE_TEST(gio_pollable, gioPollable, "-k", "--verbose")
DEFINE_TEST(gio_proxy, gioProxy, "-k", "--verbose")
DEFINE_TEST(gio_proxy_test, gioProxyTest, "-k", "--verbose")
DEFINE_TEST(gio_readwrite, gioReadWrite, "-k", "--verbose")
DEFINE_TEST(gio_resolver, gioResolver, "mail.utexas.edu", "google.com")
DEFINE_TEST(gio_resources, gioResources, "-k", "--verbose")
DEFINE_TEST(gio_send_data, gioSendData, "--verbose")
DEFINE_TEST(gio_simple_async_result, gioSimpleAsyncResult, "-k", "--verbose")
DEFINE_TEST(gio_sleepy_stream, gioSleepyStream, "-k", "--verbose")
DEFINE_TEST(gio_socket, gioSocket, "-k", "--verbose")
DEFINE_TEST(gio_socket_client, gioSocketClient, "--verbose", "10.0.2.2:7771")
DEFINE_TEST(gio_socket_server, gioSocketServer, "--verbose", "--port", "7772")
DEFINE_TEST(gio_srvtarget, gioSrvTarget, "-k", "--verbose")
DEFINE_TEST(gio_tls_certificate, gioTlsCertificate, "-k", "--verbose")
DEFINE_TEST(gio_tls_interaction, gioTlsInteraction, "-k", "--verbose")
DEFINE_TEST(gio_unix_fd, gioUnixFd, "-k", "--verbose")
DEFINE_TEST(gio_unix_streams, gioUnixStreams, "-k", "--verbose")
DEFINE_TEST(gio_vfs, gioVfs, "-k", "--verbose")
DEFINE_TEST(gio_volumemonitor, gioVolumeMonitor, "-k", "--verbose")

// soup

DEFINE_TEST(soup_chunk_test, soupChunkTest, "--debug")
DEFINE_TEST(soup_coding_test, soupCodingTest, "--debug")
DEFINE_TEST(soup_connection_test, soupConnectionTest, "--debug")
DEFINE_TEST(soup_context_test, soupContextTest, "--debug")
DEFINE_TEST(soup_continue_test, soupContinueTest, "--debug")
DEFINE_TEST(soup_cookies_test, soupCookiesTest, "--debug")
DEFINE_TEST(soup_date, soupDate, "--debug")
DEFINE_TEST(soup_dns, soupDns, "intel.com", "google.com", "mail.utexas.edu", "developer.gnome.org")
DEFINE_TEST(soup_forms_test, soupFormsTest, "--debug")
DEFINE_TEST(soup_get, soupGet, "-d", "-p", "http://proxy-ir.intel.com:911", "http://developer.android.com/tools/sdk/ndk/index.html")
DEFINE_TEST(soup_header_parsing, soupHeaderParsing, "--debug")
DEFINE_TEST(soup_misc_test, soupMiscTest, "--debug")
DEFINE_TEST(soup_multipart_test, soupMultipartTest, "--debug")
DEFINE_TEST(soup_ntlm_test, soupNtlmTest, "--debug")
DEFINE_TEST(soup_redirect_test, soupRedirectTest, "--debug")
DEFINE_TEST(soup_requester_test, soupRequesterTest, "--debug")
DEFINE_TEST(soup_server_auth_test, soupServerAuthTest, "--debug")
DEFINE_TEST(soup_simple_httpd, soupSimpleHttpd, "-p", "7772")
DEFINE_TEST(soup_simple_proxy, soupSimpleProxy, "-p", "7772")
DEFINE_TEST(soup_sniffing_test, soupSniffingTest, "--debug")
DEFINE_TEST(soup_socket_test, soupSocketTest, "--debug")
DEFINE_TEST(soup_ssl_test, soupSslTest, "--debug")
DEFINE_TEST(soup_streaming_test, soupStreamingTest, "--debug")
DEFINE_TEST(soup_timeout_test, soupTimeoutTest, "--debug")
DEFINE_TEST(soup_tld_test, soupTldTest, "--debug")
DEFINE_TEST(soup_uri_parsing, soupUriParsing, "--debug")

// gssdp

DEFINE_TEST(gssdp_browser_test, gssdpBrowserTest)
DEFINE_TEST(gssdp_publish_test, gssdpPublishTest)

