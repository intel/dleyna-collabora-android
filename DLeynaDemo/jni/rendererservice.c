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

#include <libdleyna/core/main-loop.h>
#include <libdleyna/renderer/control-point-renderer.h>

#include "com_intel_dleyna_RendererService.h"
#include "util.h"

#define DLR_RENDERER_SERVICE_NAME "dleyna-renderer-service"

JNIEXPORT jint JNICALL Java_com_intel_dleyna_RendererService_dleynaMainLoopStart(
    JNIEnv *env, jobject peer, jobject connector)
{
    LOGI("dleynaMainLoopStart: peer=%p connector=%p", peer, connector);
    // TODO: Some day there will be a dleyna_control_point_get_version() and it
    // should be used to get the version of dleyna-renderer to pass here.
	return dleyna_main_loop_start(DLR_RENDERER_SERVICE_NAME, "version X.Y",
            dleyna_control_point_get_renderer(), (gpointer)connector);
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_RendererService_dleynaMainLoopQuit(
    JNIEnv *env, jobject peer, jobject connector)
{
    LOGI("dleynaMainLoopQuit");
    dleyna_main_loop_quit();
}

