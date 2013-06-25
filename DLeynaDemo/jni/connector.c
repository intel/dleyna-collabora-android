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
#include <libdleyna/core/connector.h>

#include "util.h"


static gboolean initialize(
    const gchar *server_info,
    const gchar *root_info,
    GQuark error_quark,
    gpointer user_data)
{
    LOGI("connecto.initialize: ENTER: server=%p root=%p quark=0x%08x data=%p", server_info, root_info, error_quark, user_data);
    LOGI("connecto.initialize: EXIT");
    return TRUE;
}

static void shutdown(void)
{
}

static void connect(
    const gchar *server_name,
    dleyna_connector_connected_cb_t connected_cb,
    dleyna_connector_disconnected_cb_t disconnected_cb)
{
}

static void disconnect(void)
{
}

static gboolean watch_client(const gchar *client_name)
{
}

static void unwatch_client(const gchar *client_name)
{
}

static void set_client_lost_cb(dleyna_connector_client_lost_cb_t lost_cb)
{
}

static guint publish_object(
    dleyna_connector_id_t connection,
    const gchar *object_path,
    gboolean root,
    guint interface_index,
    const dleyna_connector_dispatch_cb_t *cb_table_1)
{
    return 0;
}

static guint publish_subtree(
    dleyna_connector_id_t connection,
    const gchar *object_path,
    const dleyna_connector_dispatch_cb_t *cb_table,
    guint cb_table_size,
    dleyna_connector_interface_filter_cb_t cb)
{
    return 0;
}

static void unpublish_object(
    dleyna_connector_id_t connection,
    guint object_id)
{
}

static void unpublish_subtree(
    dleyna_connector_id_t connection,
    guint object_id)
{
}

static void return_response(
    dleyna_connector_msg_id_t message_id,
    GVariant *parameters)
{
}

static void return_error(
    dleyna_connector_msg_id_t message_id,
    const GError *error)
{
}

static gboolean notify(
    dleyna_connector_id_t connection,
    const gchar *object_path,
    const gchar *interface_name,
    const gchar *notification_name,
    GVariant *parameters,
    GError **error)
{
    return FALSE;
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
