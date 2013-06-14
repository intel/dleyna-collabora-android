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
#define LOG_TAG "gupnpav-tests-criteriaparser"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif

static void
begin_parens_cb (GUPnPSearchCriteriaParser *parser,
                 gpointer                   user_data)
{
        g_print ("(");
#ifdef __BIONIC__
        LOGI ("(");
#endif
}

static void
end_parens_cb (GUPnPSearchCriteriaParser *parser,
               gpointer                   user_data)
{
        g_print (")");
#ifdef __BIONIC__
        LOGI (")");
#endif
}

static void
conjunction_cb (GUPnPSearchCriteriaParser *parser,
                gpointer                   user_data)
{
        g_print (" and ");
#ifdef __BIONIC__
        LOGI (" and ");
#endif
}

static void
disjunction_cb (GUPnPSearchCriteriaParser *parser,
                gpointer                   user_data)
{
        g_print (" or ");
#ifdef __BIONIC__
        LOGI (" or ");
#endif
}

static gboolean
expression_cb (GUPnPSearchCriteriaParser *parser,
               const char                *property,
               GUPnPSearchCriteriaOp      op,
               const char                *value,
               GError                   **error,
               gpointer                   user_data)
{
        g_print ("%s %d %s", property, op, value);
#ifdef __BIONIC__
        LOGI ("%s %d %s", property, op, value);
#endif

        return TRUE;
}

int
gupnpav_criteria_parser_test_main (int argc, char **argv)
{
        GUPnPSearchCriteriaParser *parser;
        GError *error;

        g_assert (argc == 2);

#if !GLIB_CHECK_VERSION (2, 35, 0)
        g_type_init ();
#endif

        parser = gupnp_search_criteria_parser_new ();

        g_signal_connect (parser,
                          "begin_parens",
                          G_CALLBACK (begin_parens_cb),
                          NULL);
        g_signal_connect (parser,
                          "end_parens",
                          G_CALLBACK (end_parens_cb),
                          NULL);
        g_signal_connect (parser,
                          "conjunction",
                          G_CALLBACK (conjunction_cb),
                          NULL);
        g_signal_connect (parser,
                          "disjunction",
                          G_CALLBACK (disjunction_cb),
                          NULL);
        g_signal_connect (parser,
                          "expression",
                          G_CALLBACK (expression_cb),
                          NULL);

        error = NULL;

#ifdef __BIONIC__
        LOGI ("Search criteria string: %s", argv[1]);
#endif
        gupnp_search_criteria_parser_parse_text (parser, argv[1], &error);
        if (error != NULL) {
                g_printerr ("Parse error: %s\n", error->message);
#ifdef __BIONIC__
                LOGW ("Parse error: %s", error->message);
#endif
                g_error_free (error);
                return EXIT_FAILURE;
        }

        g_print ("\n");

        g_object_unref (parser);

#ifdef __BIONIC__
        LOGI ("Test completed");
#endif
        return EXIT_SUCCESS;
}
