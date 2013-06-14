/*
 * Copyright (C) 2008 OpenedHand Ltd.
 *
 * Authors: Jorn Baayen <jorn@openedhand.com>
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

#include <libgupnp-av/gupnp-search-criteria-parser.h>
#include <stdlib.h>

#ifdef __BIONIC__
#include <android/log.h>
#define LOG_TAG "gupnpav-tests-search"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif

static const char * const searches[] = {
        "dc:title contains \"foo\"",
        "dc:title contains 'foo'",
        "upnp:class = \"object.container.person.musicArtist\"",
        "upnp:class = \"object.container.person.musicArtist\" and @refID exists false",
};

int
gupnpav_check_search_test_main (int argc, char **argv)
{
        GUPnPSearchCriteriaParser *parser;
        GError *error;
        int i;

#if !GLIB_CHECK_VERSION (2, 35, 0)
        g_type_init ();
#endif

        parser = gupnp_search_criteria_parser_new ();

        for (i = 0; i < G_N_ELEMENTS (searches); i++) {
                error = NULL;
                gupnp_search_criteria_parser_parse_text (parser, searches[i], &error);
                if (error) {
                        g_printerr ("\n\nCannot parse '%s': %s\n",
                                    searches[i], error->message);
#ifdef __BIONIC__
                        LOGW ("Cannot parse '%s': %s", searches[i],
                                error->message);
#endif
                        g_error_free (error);

                        return EXIT_FAILURE;
                }
                /* TODO: obviously an important next step is to verify that the
                   data was actually parsed correctly */
                g_print (".");
#ifdef __BIONIC__
                LOGI (".");
#endif
        }

        g_print ("\n");
#ifdef __BIONIC__
        LOGI ("Test completed");
#endif
        return EXIT_SUCCESS;
}
