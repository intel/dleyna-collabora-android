/*
 * Copyright (C) 2012, 2013 Intel Corporation.
 *
 * Authors: Krzesimir Nowak <krnowak@openismus.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */

#include <glib.h>

#include "gupnp-dlna-value-type.h"
#include "gupnp-dlna-value-list-private.h"
#include "gupnp-dlna-restriction-private.h"
#include "gupnp-dlna-info-set.h"

static void
value_type_not_null (void)
{
        g_assert (gupnp_dlna_value_type_bool () != NULL);
        g_assert (gupnp_dlna_value_type_fraction () != NULL);
        g_assert (gupnp_dlna_value_type_int () != NULL);
        g_assert (gupnp_dlna_value_type_string () != NULL);
}

static void
value_list_single (void)
{
        GUPnPDLNAValueList *list;
        gboolean result;

        /* bool */
        list = gupnp_dlna_value_list_new
                                     (gupnp_dlna_value_type_bool ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_single (list, "true");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "false");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "wrong");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* fraction */
        list = gupnp_dlna_value_list_new
                                 (gupnp_dlna_value_type_fraction ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_single (list, "1/2");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "15/3");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "wrong");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_single (list, "1/0");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_single (list, "1/2/3");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_single (list, "wrong/bad");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* int */
        list = gupnp_dlna_value_list_new
                                      (gupnp_dlna_value_type_int ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_single (list, "1");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "-15");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "wrong");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_single (list, "1.4");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* string */
        list = gupnp_dlna_value_list_new
                                   (gupnp_dlna_value_type_string ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_single (list, "1");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "-15");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "wrong");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_single (list, "1.4");
        g_assert (result == TRUE);
        gupnp_dlna_value_list_free (list);
}

static void
value_list_range (void)
{
        GUPnPDLNAValueList *list;
        gboolean result;

        /* bool ranges have no sense. */
        list = gupnp_dlna_value_list_new
                                     (gupnp_dlna_value_type_bool ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_range (list, "true", "true");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "false", "true");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "false", "false");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "false", "true");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "false", "wrong");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "wrong", "true");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "wrong", "bad");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* fraction */
        list = gupnp_dlna_value_list_new
                                 (gupnp_dlna_value_type_fraction ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_range (list, "1/2", "3/4");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_range (list, "-8/3", "15/3");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_range (list, "wrong", "bad");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1/0", "-4/3");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1/2/3", "3/2");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "a/b", "c/d");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1/2", "1/4");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1/2", "-1/2");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* int */
        list = gupnp_dlna_value_list_new
                                      (gupnp_dlna_value_type_int ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_range (list, "1", "3");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_range (list, "-15", "15");
        g_assert (result == TRUE);
        result = gupnp_dlna_value_list_add_range (list, "wrong", "bad");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1.4", "3");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1", "-3");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);

        /* string ranges have no sense */
        list = gupnp_dlna_value_list_new
                                   (gupnp_dlna_value_type_string ());
        g_assert (list != NULL);
        result = gupnp_dlna_value_list_add_range (list, "1", "3");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "-15", "x");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "wrong", "bad");
        g_assert (result == FALSE);
        result = gupnp_dlna_value_list_add_range (list, "1.4", "-1");
        g_assert (result == FALSE);
        gupnp_dlna_value_list_free (list);
}

static void
restriction_construction (void)
{
        GUPnPDLNARestriction *r = gupnp_dlna_restriction_new
                                        (NULL);
        GHashTable *e = gupnp_dlna_restriction_get_entries (r);

        g_assert_cmpstr (gupnp_dlna_restriction_get_mime (r), ==, NULL);
        g_assert_cmpuint (g_hash_table_size (e), ==, 0);
        gupnp_dlna_restriction_free (r);

        r = gupnp_dlna_restriction_new ("mime");
        e = gupnp_dlna_restriction_get_entries (r);
        g_assert_cmpstr (gupnp_dlna_restriction_get_mime (r), ==, "mime");
        g_assert_cmpuint (g_hash_table_size (e), ==, 0);
        gupnp_dlna_restriction_free (r);
}

static void
restriction_empty (void)
{
        GUPnPDLNARestriction *r =
                                       gupnp_dlna_restriction_new (NULL);
        GUPnPDLNAValueList *list = gupnp_dlna_value_list_new
                                         (gupnp_dlna_value_type_bool ());

        g_assert (gupnp_dlna_restriction_is_empty (r));
        gupnp_dlna_value_list_add_single (list, "true");
        g_assert (gupnp_dlna_restriction_add_value_list (r,
                                                                "bool",
                                                                list));
        g_assert (!gupnp_dlna_restriction_is_empty (r));
        gupnp_dlna_restriction_free (r);
        r = gupnp_dlna_restriction_new ("mime");
        g_assert (!gupnp_dlna_restriction_is_empty (r));
        gupnp_dlna_restriction_free (r);
}

static void
restriction_adding_value_lists (void)
{
        GUPnPDLNARestriction *r =
                                       gupnp_dlna_restriction_new (NULL);
        GUPnPDLNAValueList *list = gupnp_dlna_value_list_new
                                         (gupnp_dlna_value_type_bool ());

        g_assert (gupnp_dlna_restriction_is_empty (r));
        /* adding empty value list should fail */
        g_assert (!gupnp_dlna_restriction_add_value_list (r,
                                                                 "bool",
                                                                 list));
        g_assert (gupnp_dlna_restriction_is_empty (r));
        gupnp_dlna_value_list_add_single (list, "true");
        g_assert (gupnp_dlna_restriction_add_value_list (r,
                                                                "bool",
                                                                list));
        g_assert (!gupnp_dlna_restriction_is_empty (r));
        list = gupnp_dlna_value_list_new
                                         (gupnp_dlna_value_type_bool ());
        gupnp_dlna_value_list_add_single (list, "false");
        /* adding value list with already existing name should fail */
        g_assert (!gupnp_dlna_restriction_add_value_list (r,
                                                                 "bool",
                                                                 list));
        gupnp_dlna_value_list_free (list);
        gupnp_dlna_restriction_free (r);
}

static void
restriction_merge (void)
{
        /* TODO: Write a test. */
}

static void
info_set_adding_values (void)
{
        GUPnPDLNAInfoSet *info_set = gupnp_dlna_info_set_new ("mime");

        g_assert (info_set != NULL);
        g_assert (gupnp_dlna_info_set_add_bool (info_set, "b", TRUE));
        /* invalid fraction */
        g_assert (!gupnp_dlna_info_set_add_fraction (info_set, "f", 1, 0));
        g_assert (gupnp_dlna_info_set_add_fraction (info_set, "f", 1, 2));
        g_assert (gupnp_dlna_info_set_add_int (info_set, "i", 42));
        g_assert (gupnp_dlna_info_set_add_string (info_set, "s", "str"));
        /* that name already exists */
        g_assert (!gupnp_dlna_info_set_add_bool (info_set, "b", FALSE));

        gupnp_dlna_info_set_free (info_set);
}

static void
info_set_fit (void)
{
        GUPnPDLNARestriction *r = gupnp_dlna_restriction_new ("mime");
        GUPnPDLNAValueList *v = gupnp_dlna_value_list_new
                                        (gupnp_dlna_value_type_bool());
        GUPnPDLNAInfoSet *s;

        /* restriction */
        g_assert (gupnp_dlna_value_list_add_single (v, "true"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "b1", v));
        v = gupnp_dlna_value_list_new
                                      (gupnp_dlna_value_type_fraction());
        g_assert (gupnp_dlna_value_list_add_single (v, "1/2"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "f1", v));
        v = gupnp_dlna_value_list_new
                                      (gupnp_dlna_value_type_fraction());
        g_assert (gupnp_dlna_value_list_add_range (v, "1/4", "2/3"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "f2", v));
        v = gupnp_dlna_value_list_new
                                           (gupnp_dlna_value_type_int());
        g_assert (gupnp_dlna_value_list_add_single (v, "13"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "i1", v));
        v = gupnp_dlna_value_list_new
                                           (gupnp_dlna_value_type_int());
        g_assert (gupnp_dlna_value_list_add_range (v, "42", "55"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "i2", v));
        v = gupnp_dlna_value_list_new
                                        (gupnp_dlna_value_type_string());
        g_assert (gupnp_dlna_value_list_add_single (v, "aaa"));
        g_assert (gupnp_dlna_restriction_add_value_list (r, "s1", v));

        /* info set with exactly fitting values and same mime*/
        s = gupnp_dlna_info_set_new ("mime");
        g_assert (gupnp_dlna_info_set_add_bool (s, "b1", TRUE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f1", 1, 2));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f2", 1, 3));
        g_assert (gupnp_dlna_info_set_add_int (s, "i1", 13));
        g_assert (gupnp_dlna_info_set_add_int (s, "i2", 50));
        g_assert (gupnp_dlna_info_set_add_string (s, "s1", "aaa"));

        g_assert (gupnp_dlna_info_set_fits_restriction (s, r));

        /* add some more values not considered by restriction */
        g_assert (gupnp_dlna_info_set_add_bool (s, "b2", FALSE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f3", 4, 5));
        g_assert (gupnp_dlna_info_set_add_int (s, "i3", 7));
        g_assert (gupnp_dlna_info_set_add_string (s, "s2", "bbb"));

        g_assert (gupnp_dlna_info_set_fits_restriction (s, r));

        gupnp_dlna_info_set_free (s);

        /* info set with exactly fitting values but different mime */
        s = gupnp_dlna_info_set_new ("asdf");
        g_assert (gupnp_dlna_info_set_add_bool (s, "b1", TRUE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f1", 1, 2));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f2", 1, 3));
        g_assert (gupnp_dlna_info_set_add_int (s, "i1", 13));
        g_assert (gupnp_dlna_info_set_add_int (s, "i2", 50));
        g_assert (gupnp_dlna_info_set_add_string (s, "s1", "aaa"));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        /* add some more values not considered by restriction */
        g_assert (gupnp_dlna_info_set_add_bool (s, "b2", FALSE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f3", 4, 5));
        g_assert (gupnp_dlna_info_set_add_int (s, "i3", 7));
        g_assert (gupnp_dlna_info_set_add_string (s, "s2", "bbb"));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        gupnp_dlna_info_set_free (s);

        /* info set with same mime, exact fitting set but not fitting values */
        s = gupnp_dlna_info_set_new ("mime");
        g_assert (gupnp_dlna_info_set_add_bool (s, "b1", FALSE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f1", 3, 2));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f2", 3, 3));
        g_assert (gupnp_dlna_info_set_add_int (s, "i1", 17));
        g_assert (gupnp_dlna_info_set_add_int (s, "i2", 57));
        g_assert (gupnp_dlna_info_set_add_string (s, "s1", "aaaa"));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        /* add some more values not considered by restriction */
        g_assert (gupnp_dlna_info_set_add_bool (s, "b2", FALSE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f3", 4, 5));
        g_assert (gupnp_dlna_info_set_add_int (s, "i3", 7));
        g_assert (gupnp_dlna_info_set_add_string (s, "s2", "bbb"));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        gupnp_dlna_info_set_free (s);

        /* info set with same mime but with too few values */
        s = gupnp_dlna_info_set_new ("mime");
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f2", 1, 3));
        g_assert (gupnp_dlna_info_set_add_int (s, "i1", 13));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        /* add some more values not considered by restriction */
        g_assert (gupnp_dlna_info_set_add_bool (s, "b2", FALSE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f3", 4, 5));
        g_assert (gupnp_dlna_info_set_add_int (s, "i3", 7));
        g_assert (gupnp_dlna_info_set_add_string (s, "s2", "bbb"));

        g_assert (!gupnp_dlna_info_set_fits_restriction (s, r));

        /* add missing values */
        g_assert (gupnp_dlna_info_set_add_bool (s, "b1", TRUE));
        g_assert (gupnp_dlna_info_set_add_fraction (s, "f1", 1, 2));
        g_assert (gupnp_dlna_info_set_add_int (s, "i2", 50));
        g_assert (gupnp_dlna_info_set_add_string (s, "s1", "aaa"));

        g_assert (gupnp_dlna_info_set_fits_restriction (s, r));

        gupnp_dlna_info_set_free (s);
        gupnp_dlna_restriction_free (r);
}

int
gupnpdlna_sets_test_main (int argc, char **argv)
{
        g_test_init (&argc, &argv, NULL);

        g_test_add_func ("/value-type/not-null", value_type_not_null);
        g_test_add_func ("/value-list/single", value_list_single);
        g_test_add_func ("/value-list/range", value_list_range);
        g_test_add_func ("/restriction/construction", restriction_construction);
        g_test_add_func ("/restriction/empty", restriction_empty);
        g_test_add_func ("/restriction/adding-value-lists",
                         restriction_adding_value_lists);
        g_test_add_func ("/restriction/merge", restriction_merge);
        g_test_add_func ("/info-set/adding-values", info_set_adding_values);
        g_test_add_func ("/info-set/fit", info_set_fit);

        g_test_run ();

        return 0;
}
