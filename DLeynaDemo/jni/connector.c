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

#include <libdleyna/core/connector.h>

#include "util.h"

#define DLR_RENDERER_SERVICE_NAME "dleyna-renderer-service"

static JNIEnv*      sEnv;
static jobject      sPeer;
static jmethodID    sMIDShutdown;
static jmethodID    sMIDConnect;
static jmethodID    sMIDDisconnect;
static jmethodID    sMIDSetClientLostCB;
static jmethodID    sMIDPublishObject;
static jmethodID    sMIDReturnResponse;
static jmethodID    sMIDReturnError;
static jmethodID    sMIDNotify;

static dleyna_connector_connected_cb_t      sSayConnected;
static dleyna_connector_disconnected_cb_t   sSayDisconnected;
static dleyna_connector_client_lost_cb_t    sSayClientLost;

JNIEXPORT void JNICALL Java_com_intel_dleyna_Connector_setJNIEnvNative(
    JNIEnv* env, jobject connector)
{
    LOGI("connector.setJNIEnv: env=%p", env);
    sEnv = env;
}

static gboolean initialize(
    const gchar *server_info,
    const gchar *root_info,
    GQuark error_quark,
    jobject peer)
{
    LOGI("connector.initialize: server=%p root=%p quark=0x%08x peer=%p",
            server_info, root_info, error_quark, peer);

    // Cache a reference to the peer object.
    sPeer = (*sEnv)->NewGlobalRef(sEnv, peer);
    LOGI("connector.initialize: sPeer=%p", sPeer);

    // Cache method ids.
    jclass clazz = (*sEnv)->GetObjectClass(sEnv, peer);
    sMIDShutdown        = (*sEnv)->GetMethodID(sEnv, clazz, "shutdown", "()V");
    sMIDConnect         = (*sEnv)->GetMethodID(sEnv, clazz, "connect", "(Ljava/lang/String;JJ)V");
    sMIDDisconnect      = (*sEnv)->GetMethodID(sEnv, clazz, "disconnect", "()V");
    sMIDSetClientLostCB = (*sEnv)->GetMethodID(sEnv, clazz, "setClientLostCallback", "(J)V");
    sMIDPublishObject   = (*sEnv)->GetMethodID(sEnv, clazz, "publishObject", "(Ljava/lang/String;ZLjava/lang/String;J)I");
    sMIDReturnResponse  = (*sEnv)->GetMethodID(sEnv, clazz, "returnResponse", "(JJ)V");
    sMIDReturnError     = (*sEnv)->GetMethodID(sEnv, clazz, "returnError", "(JJ)V");
    sMIDNotify          = (*sEnv)->GetMethodID(sEnv, clazz, "notify", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V");

    // Invoke peer.
    jmethodID mid = (*sEnv)->GetMethodID(sEnv, clazz, "initialize", "(Ljava/lang/String;Ljava/lang/String;I)Z");
    jstring siJ = (*sEnv)->NewStringUTF(sEnv, server_info);
    jstring riJ = (*sEnv)->NewStringUTF(sEnv, root_info);
    jint q = (jint)error_quark;
    jboolean b = (*sEnv)->CallBooleanMethod(sEnv, peer, mid, siJ, riJ, q);
    return (gboolean)b;
}

static void shutdown(void)
{
    LOGI("connector.shutdown");
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDShutdown);
    (*sEnv)->DeleteGlobalRef(sEnv, sPeer);
}

static gboolean doConnect(gpointer p) {
    LOGI("connector.doConnect");
    sSayConnected(sPeer);
    return FALSE;
}

static void connect(
    const gchar *server_name,
    dleyna_connector_connected_cb_t connected_cb,
    dleyna_connector_disconnected_cb_t disconnected_cb)
{
    LOGI("connector.connect: server_name=%s ccb=%p dcb=%p", server_name, connected_cb, disconnected_cb);

    sSayConnected = connected_cb;
    sSayDisconnected = connected_cb;

    g_idle_add(doConnect, NULL);

    jstring snJ = (*sEnv)->NewStringUTF(sEnv, server_name);
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDConnect, snJ, PTR_TO_JLONG(connected_cb), PTR_TO_JLONG(disconnected_cb));
}

static void disconnect(void)
{
    LOGE("connector.disconnect");
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDDisconnect);
}

static gboolean watch_client(const gchar *client_name)
{
    LOGI("connector.watch_client");
}

static void unwatch_client(const gchar *client_name)
{
    LOGI("connector.unwatch_client");
}

static void set_client_lost_cb(dleyna_connector_client_lost_cb_t lost_cb)
{
    LOGI("connector.set_client_lost_cb");
    sSayClientLost = lost_cb;
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDSetClientLostCB, PTR_TO_JLONG(lost_cb));
}

static guint publish_object(
    dleyna_connector_id_t peer,
    const gchar *object_path,
    gboolean root,
    const gchar *iface_name,
    const dleyna_connector_dispatch_cb_t *cb_table_1)
{
    LOGI("connector.publish_object");
    jstring objectPath = (*sEnv)->NewStringUTF(sEnv, object_path);
    jstring ifaceName = (*sEnv)->NewStringUTF(sEnv, iface_name);
    return (*sEnv)->CallIntMethod(sEnv, sPeer, sMIDPublishObject, objectPath,
            (jboolean)root, ifaceName, PTR_TO_JLONG(cb_table_1[0])); 
}

static guint publish_subtree(
    dleyna_connector_id_t peer,
    const gchar *object_path,
    const dleyna_connector_dispatch_cb_t *cb_table,
    guint cb_table_size,
    dleyna_connector_interface_filter_cb_t cb)
{
    LOGI("connector.publish_subtree");
    return 0;
}

static void unpublish_object(
    dleyna_connector_id_t peer,
    guint object_id)
{
    LOGI("connector.unpublish_object");
}

static void unpublish_subtree(
    dleyna_connector_id_t peer,
    guint object_id)
{
    LOGI("connector.unpublish_subtree");
}

static void return_response(
    dleyna_connector_msg_id_t message_id,
    GVariant *parameters)
{
    LOGI("connector.return_response");
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDReturnResponse, PTR_TO_JLONG(message_id), PTR_TO_JLONG(parameters));
}

static void return_error(
    dleyna_connector_msg_id_t message_id,
    const GError *error)
{
    LOGI("connector.return_error");
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDReturnError, PTR_TO_JLONG(message_id), PTR_TO_JLONG(error));
}

static gboolean notify(
    dleyna_connector_id_t peer,
    const gchar *object_path,
    const gchar *interface_name,
    const gchar *notification_name,
    GVariant *parameters,
    GError **error)
{
    LOGI("connector.notify");
    jstring objPath = (*sEnv)->NewStringUTF(sEnv, object_path);
    jstring ifaceName = (*sEnv)->NewStringUTF(sEnv, interface_name);
    jstring notifName = (*sEnv)->NewStringUTF(sEnv, notification_name);
    (*sEnv)->CallVoidMethod(sEnv, sPeer, sMIDNotify, objPath, ifaceName, notifName, PTR_TO_JLONG(parameters));
    return TRUE;
}

static dleyna_connector_t connector = {
	.initialize = initialize,
	.shutdown = shutdown,
	.connect = connect,
	.disconnect = disconnect,
	.watch_client = watch_client,
	.unwatch_client = unwatch_client,
	.set_client_lost_cb = set_client_lost_cb,
	.publish_object = publish_object,
	.publish_subtree = publish_subtree,
	.unpublish_object = unpublish_object,
	.unpublish_subtree = unpublish_subtree,
	.return_response = return_response,
	.return_error = return_error,
	.notify = notify,
};

extern const dleyna_connector_t *dleyna_connector_get_interface(void)
{
    return &connector;
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_Connector_dispatchNative(
    JNIEnv* env, jobject peer, jlong _dispatchFunc, jstring sender,
    jstring objId, jstring iface, jstring method, jlong _args, jlong _msgId)
{
    const char* senderC = (*env)->GetStringUTFChars(env, sender, NULL);
    const char* objIdC = (*env)->GetStringUTFChars(env, objId, NULL);
    const char* ifaceC = (*env)->GetStringUTFChars(env, iface, NULL);
    const char* methodC = (*env)->GetStringUTFChars(env, method, NULL);

    dleyna_connector_dispatch_cb_t dispatchFunc = JLONG_TO_PTR(_dispatchFunc);
    GVariant* args = JLONG_TO_PTR(_args);
    dleyna_connector_msg_id_t msgId = JLONG_TO_PTR(_msgId); 

    LOGI("connector.dispatch: peer=%p sender=%s obj=%s iface=%s meth=%s args=%p msgId=%p",
            peer, senderC, objIdC, ifaceC, methodC, args, msgId);

    dispatchFunc(peer, senderC, objIdC, ifaceC, methodC, args, msgId);

    (*env)->ReleaseStringUTFChars(env, sender, senderC);
    (*env)->ReleaseStringUTFChars(env, objId, objIdC);
    (*env)->ReleaseStringUTFChars(env, iface, ifaceC);
    (*env)->ReleaseStringUTFChars(env, method, methodC);
}
