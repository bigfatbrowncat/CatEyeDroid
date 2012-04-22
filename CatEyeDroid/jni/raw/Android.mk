# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.crg/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# the purpose of this sample is to demonstrate how one can
# generate two distinct shared libraries and have them both
# uploaded in
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH := $(call my-dir)

# libraw
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libraw
LOCAL_CFLAGS    := -DLIBRAW_LIBRARY_BUILD -DNEEDS_SWAB -fexceptions -ffast-math -O3 -funroll-loops
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/../
LOCAL_SRC_FILES := dcraw_common.cpp \
                   dcraw_fileio.cpp \
                   demosaic_packs.cpp \
                   libraw_cxx.cpp \
                   libraw_datastream.cpp \
                   libraw_c_api.cpp

include $(BUILD_STATIC_LIBRARY)

LOCAL_PATH := $(LOCAL_PATH_BACKUP)