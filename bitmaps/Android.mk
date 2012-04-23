#
# libbitmaps makefile for Android NDK
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libbitmaps
LOCAL_CFLAGS    := -fexceptions -O3 -DBUILDING_LIBBITMAP
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/include
LOCAL_SRC_FILES := src/PreciseBitmap.cpp \
                   src/PreviewBitmap.cpp

include $(BUILD_STATIC_LIBRARY)

LOCAL_PATH := $(LOCAL_PATH_BACKUP)