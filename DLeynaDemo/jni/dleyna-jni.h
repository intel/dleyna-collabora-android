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

#ifndef DLEYNA_JNI_H__
#define DLEYNA_JNI_H__

#include <android/log.h>

#define TAG "DLeynaDemo"

#define LOGD(format, ...) \
    __android_log_print(ANDROID_LOG_DEBUG, TAG, format, ##__VA_ARGS__);

#define LOGI(format, ...) \
    __android_log_print(ANDROID_LOG_INFO, TAG, format, ##__VA_ARGS__);

#define LOGW(format, ...) \
    __android_log_print(ANDROID_LOG_WARN, TAG, format, ##__VA_ARGS__);

#define LOGE(format, ...) \
    __android_log_print(ANDROID_LOG_ERROR, TAG, format, ##__VA_ARGS__);

#endif /* DLEYNA_JNI_H__ */