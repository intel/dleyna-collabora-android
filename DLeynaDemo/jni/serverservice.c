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
 */

#include <glib.h>
#include <jni.h>

#include <libdleyna/core/main-loop.h>
#include <libdleyna/server/control-point-server.h>

#include "com_intel_dleyna_ServerService.h"
#include "util.h"

#define DLS_SERVER_SERVICE_NAME "dleyna-server-service"

JNIEXPORT jint JNICALL Java_com_intel_dleyna_ServerService_dleynaMainLoopStart(
    JNIEnv *env, jobject peer, jobject connector)
{
    LOGI("dleynaMainLoopStart: peer=%p connector=%p", peer, connector);
    return dleyna_main_loop_start(DLS_SERVER_SERVICE_NAME,
            dleyna_control_point_get_server(), (gpointer)connector);
}

JNIEXPORT void JNICALL Java_com_intel_dleyna_ServerService_dleynaMainLoopQuit(
    JNIEnv *env, jobject peer, jobject connector)
{
    LOGI("dleynaMainLoopQuit");
    dleyna_main_loop_quit();
}

