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

# RawImageLoader
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libRawImageLoader
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS    := -fexceptions -ffast-math -O3 -funroll-loops
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/../ \
                   $(LOCAL_PATH)/../../../bitmaps/include \
                   $(LOCAL_PATH)/../../../libraw/libraw \
                   $(LOCAL_PATH)/../../../jpeg-8c/include
LOCAL_SRC_FILES := RawImageLoader.cpp

LOCAL_STATIC_LIBRARIES := libraw libbitmaps libjpeg8c

include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(LOCAL_PATH_BACKUP)