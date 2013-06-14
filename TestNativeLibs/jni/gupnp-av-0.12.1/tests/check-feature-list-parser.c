/*
 * Copyright (C) 2012 Intel Corporation
 *
 * Authors: Regis Merlino <regis.merlino@intel.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */

#include <libgupnp-av/gupnp-feature-list-parser.h>
#include <stdlib.h>
#include <string.h>

#ifdef __BIONIC__
#include <android/log.h>
#define LOG_TAG "gupnpav-tests-listparser"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif

static const char * const names[] = {
        "BOOKMARK",
        "EPG",
};

static const char * const versions[] = {
        "1",
        "2",
};

static const char * const ids[] = {
        "bookmark1,bookmark2,bookmark3",
        "epg1,epg2",
};

static const char *text =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        "<Features "
                "xmlns=\"urn:schemas-upnp-org:av:avs\" "
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                "xsi:schemaLocation=\""
                "urn:schemas-upnp-org:av:avs "
                "http://www.upnp.org/schemas/av/avs-v1-20060531.xsd\">"
                "<Feature name=\"BOOKMARK\" version=\"1\">"
                        "<objectIDs>bookmark1</objectIDs>"
                        "<objectIDs>bookmark2,bookmark3</objectIDs>"
                "</Feature>"
                "<Feature name=\"EPG\" version=\"2\">"
                        "<objectIDs>epg1,epg2</objectIDs>"
                "</Feature>"
        "</Features>";

static gboolean
check_feature (GUPnPFeature *feature)
{
        static int index = 0;

        if (strcmp (names[index], gupnp_feature_get_name (feature))) {
#ifdef __BIONIC__
                LOGI ("No match: '%s' != '%s'", names[index],
                        gupnp_feature_get_name (feature));
#endif
                        return FALSE;
        }

        if (strcmp (versions[index], gupnp_feature_get_version (feature))) {
#ifdef __BIONIC__
                LOGI ("No match: '%s' != '%s'", versions[index],
                        gupnp_feature_get_version (feature));
#endif
                        return FALSE;
        }

        if (strcmp (ids[index], gupnp_feature_get_object_ids (feature))) {
#ifdef __BIONIC__
                LOGI ("No match: '%s' != '%s'", ids[index],
                        gupnp_feature_get_object_ids (feature));
#endif
                        return FALSE;
        }

        index++;

        return TRUE;
}

int
gupnpav_list_parser_test_main (int argc, char **argv)
{
        GUPnPFeatureListParser *parser;
        GError                 *error;
        GList                  *features;
        GList                  *item;
        gboolean               success = TRUE;

#if !GLIB_CHECK_VERSION (2, 35, 0)
        g_type_init ();
#endif

        parser = gupnp_feature_list_parser_new ();

        error = NULL;
        features = gupnp_feature_list_parser_parse_text (parser, text, &error);
        if (features == NULL) {
                g_printerr ("Parse error: %s\n", error->message);
#ifdef __BIONIC__
                LOGW ("Parse error: %s", error->message);
#endif
                g_error_free (error);
                return EXIT_FAILURE;
        }

        for (item = features; item != NULL; item = g_list_next (item)) {
                success = check_feature ((GUPnPFeature *) item->data);
                if (!success)
                        break;
        }

        g_print ("\n");

        g_list_free_full (features, g_object_unref);
        g_object_unref (parser);

#ifdef __BIONIC__
        LOGI ("Test completed");
#endif
        return (success) ? EXIT_SUCCESS : EXIT_FAILURE;
}
