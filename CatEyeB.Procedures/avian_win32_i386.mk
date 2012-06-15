PROJ = CatEyeB.Procedures

MAKE_AVIAN = make -f avian_win32_i386.mk
JNI_CPPFLAGS = -static-libgcc -static-libstdc++ -O3 -ffast-math

SYSLIBPATH = /mingw/lib
SYSBINPATH = /mingw/bin
PTHREAD_BIN = $(shell echo `dlltool -I $(SYSLIBPATH)/libpthread.a`)

JARPATH = out/avian_win32_i386/jar
LIBPATH = out/avian_win32_i386/lib

LIBS := ../bitmaps/$(LIBPATH)/bitmaps.dll.a \
        ../colorlib/$(LIBPATH)/colorlib.a \
        $(SYSBINPATH)/$(PTHREAD_BIN)

DEP_JARS = ../CatEyeB.Core/$(JARPATH)/Core.jar

EXTLIBS := #pthread

INCLUDES = ../bitmaps/include \
           ../colorlib/include

JNI_NATIVE_LIB = Procedures.dll

JARNAME = Procedures.jar

all: classes jar jni_lib jni_headers
clean: classes_clean jar_clean jni_clean

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Core

../bitmaps/$(LIBPATH)/bitmaps.dll.a:
	$(MAKE_AVIAN) -C ../bitmaps

../colorlib/$(LIBPATH)/colorlib.a:
	$(MAKE_AVIAN) -C ../colorlib

include ../target_avian_win32_i386/makefiles/java_classes.mk
include ../target_avian_win32_i386/makefiles/java_jni.mk

.SILENT: