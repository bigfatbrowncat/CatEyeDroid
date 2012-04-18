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

LOCAL_PATH:= $(call my-dir)

# bitmaps
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libbitmaps
LOCAL_CFLAGS    := -fexceptions
LOCAL_SRC_FILES := PreciseBitmap.cpp \
                   PreviewBitmap.cpp

include $(BUILD_STATIC_LIBRARY)

# jpeg 8c
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libjpeg8c
LOCAL_CFLAGS    := -fexceptions
LOCAL_SRC_FILES := jaricom.c jcapimin.c jcapistd.c jcarith.c jccoefct.c jccolor.c \
               jcdctmgr.c jchuff.c jcinit.c jcmainct.c jcmarker.c jcmaster.c \
               jcomapi.c jcparam.c jcprepct.c jcsample.c jctrans.c jdapimin.c \
               jdapistd.c jdarith.c jdatadst.c jdatasrc.c jdcoefct.c jdcolor.c \
               jddctmgr.c jdhuff.c jdinput.c jdmainct.c jdmarker.c jdmaster.c \
               jdmerge.c jdpostct.c jdsample.c jdtrans.c jerror.c jfdctflt.c \
               jfdctfst.c jfdctint.c jidctflt.c jidctfst.c jidctint.c jquant1.c \
               jquant2.c jutils.c jmemmgr.c jmemnobs.c

include $(BUILD_STATIC_LIBRARY)


# libraw
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libraw
LOCAL_CFLAGS    := -DLIBRAW_LIBRARY_BUILD -DNEEDS_SWAB -fexceptions
LOCAL_SRC_FILES := dcraw_common.cpp \
                   dcraw_fileio.cpp \
                   demosaic_packs.cpp \
                   libraw_cxx.cpp \
                   libraw_datastream.cpp \
                   libraw_c_api.cpp

include $(BUILD_STATIC_LIBRARY)

# Core
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libCore
LOCAL_CFLAGS    := -fexceptions
LOCAL_SRC_FILES := CorePreciseBitmap.cpp \
                   CorePreviewBitmap.cpp

LOCAL_STATIC_LIBRARIES := libbitmaps

include $(BUILD_SHARED_LIBRARY)

# RawImageLoader
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libRawImageLoader
LOCAL_SRC_FILES := RawImageLoader.cpp

LOCAL_STATIC_LIBRARIES := libraw libbitmaps libjpeg8c

include $(BUILD_SHARED_LIBRARY)
