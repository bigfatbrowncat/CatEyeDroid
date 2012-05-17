#
# CatEyeB.Core
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := libCore
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS    := -fexceptions -ffast-math -O3 -funroll-loops
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/../bitmaps/include \
                   $(LOCAL_PATH)/../libraw/libraw \
                   $(LOCAL_PATH)/../jpeg-8c/include
                   
LOCAL_SRC_FILES := src/com/cateye/core/jni/PreciseBitmap.cpp \
                   src/com/cateye/core/jni/PreviewBitmap.cpp \
                   src/com/cateye/core/jni/RawImageLoader.cpp

LOCAL_STATIC_LIBRARIES := libbitmaps libraw libjpeg8c

include $(BUILD_SHARED_LIBRARY)
LOCAL_PATH := $(LOCAL_PATH_BACKUP)