#
# colorlib
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := libcolorlib
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS    := -fexceptions -ffast-math -O3 -funroll-loops
LOCAL_C_INCLUDES:= 
                   
LOCAL_SRC_FILES := src/HSV.cpp \
                   src/RGB.cpp

LOCAL_STATIC_LIBRARIES := 

include $(BUILD_STATIC_LIBRARY)
LOCAL_PATH := $(LOCAL_PATH_BACKUP)