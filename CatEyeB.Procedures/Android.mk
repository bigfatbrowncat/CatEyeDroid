#
# CatEyeB.Procedures
#

LOCAL_PATH_BACKUP := $(LOCAL_PATH)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := libProcedures
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS    := -fexceptions -ffast-math -O3 -mfpmath=neon #-mfpmath=sse or -mfpmath=neon	TODO Test it!
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/../bitmaps/include \
                   $(LOCAL_PATH)/../colorlib/include
                   
LOCAL_SRC_FILES := src/com/cateye/procedures/hsb/HSBStageOperationProcessor.cpp				\
                   src/com/cateye/procedures/rgb/RGBStageOperationProcessor.cpp				\
                   src/com/cateye/procedures/limiter/LimiterStageOperationProcessor.cpp		\
                   src/com/cateye/procedures/compressor/CompressorStageOperationProcessor.cpp	\
                   src/com/cateye/procedures/downsample/DownsampleStageOperationProcessor.cpp

LOCAL_STATIC_LIBRARIES := bitmaps colorlib

include $(BUILD_SHARED_LIBRARY)
LOCAL_PATH := $(LOCAL_PATH_BACKUP)