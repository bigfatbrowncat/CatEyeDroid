PROJ = CatEyeB.Procedures

JNI_CPPFLAGS = -static-libgcc -static-libstdc++ -O3 -ffast-math

SYSLIBPATH = /mingw/lib
SYSBINPATH = /mingw/bin
PTHREAD_BIN = $(shell echo `dlltool -I $(SYSLIBPATH)/libpthread.a`)

LIBS := ../bitmaps/lib/bitmaps.dll.a \
        ../colorlib/lib/colorlib.a \
        $(SYSBINPATH)/$(PTHREAD_BIN)

DEP_JARS = ../CatEyeB.Core/out/jar/Core.jar

EXTLIBS := #pthread

INCLUDES = ../bitmaps/include \
           ../colorlib/include

JNI_NATIVE_LIB = Procedures.dll

JARNAME = Procedures.jar

all: classes jar jni_lib jni_headers
clean: classes_clean jar_clean jni_clean

../CatEyeB.Core/out/jar/Core.jar:
	$(MAKE) -C ../CatEyeB.Core

../bitmaps/lib/bitmaps.dll.a:
	$(MAKE) -C ../bitmaps

../colorlib/lib/colorlib.a:
	$(MAKE) -C ../colorlib

include ../target_avian_win32_i386/makefiles/java_classes.mk
include ../target_avian_win32_i386/makefiles/java_jni.mk

.SILENT: