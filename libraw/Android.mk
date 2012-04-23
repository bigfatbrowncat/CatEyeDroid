#
# libraw makefile for Android NDK
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libraw
LOCAL_CFLAGS    := -DLIBRAW_LIBRARY_BUILD -DNEEDS_SWAB -fexceptions -ffast-math -O3 -funroll-loops
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/internal $(LOCAL_PATH)/libraw 
LOCAL_SRC_FILES := src/dcraw_common.cpp \
                   src/dcraw_fileio.cpp \
                   src/demosaic_packs.cpp \
                   src/libraw_cxx.cpp \
                   src/libraw_datastream.cpp \
                   src/libraw_c_api.cpp

include $(BUILD_STATIC_LIBRARY)

LOCAL_PATH := $(LOCAL_PATH_BACKUP)