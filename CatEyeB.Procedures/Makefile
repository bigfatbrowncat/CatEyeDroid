#################### Macros #####################

PROJ = CatEyeB.BasicStageOperations

# CC macros
COMMON_CFLAGS = -static-libgcc
CFLAGS = -O3
CDEFINES =

# CPP macros (additional to C macros)
COMMON_CPPFLAGS = -static-libstdc++
CPPFLAGS = 
CPPDEFINES =

# Universal macros
LIBS = $(TARGET_LIB)/bitmaps.dll.a $(TARGET_LIB)/colorlib.a -lpthread
CUSTOM_INCLUDES = -I"../bitmaps/include" -I"../colorlib/include"
SOURCE = src
TARGET = ../CatEyePC
JAVA_JNI_CLASSES = com.cateye.procedures.hsb.HSBStageOperationProcessor				\
                   com.cateye.procedures.rgb.RGBStageOperationProcessor				\
                   com.cateye.procedures.limiter.LimiterStageOperationProcessor		\
                   com.cateye.procedures.compressor.CompressorStageOperationProcessor	\
                   com.cateye.procedures.downsample.DownsampleStageOperationProcessor
NATIVE_LIB = Procedures.dll

all: shared_lib classes headers

include $(TARGET)/makefiles/cpp_java_cross.mk