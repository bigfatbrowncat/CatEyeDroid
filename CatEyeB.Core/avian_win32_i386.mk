PROJ = CatEyeB.Core

MAKE_AVIAN = make -f avian_win32_i386.mk
JNI_CPPFLAGS = -static-libgcc -static-libstdc++ -O3

LIBPATH = out/avian_win32_i386/lib

LIBS = ../bitmaps/$(LIBPATH)/bitmaps.dll.a \
       ../libraw/$(LIBPATH)/raw.dll.a \
       ../jpeg-8c/$(LIBPATH)/jpeg8c.dll.a

INCLUDES = ../bitmaps/include \
           ../libraw/libraw \
           ../jpeg-8c/include

JNI_NATIVE_LIB = Core.dll

JARNAME = Core.jar

all: classes jar jni_lib jni_headers
clean: classes_clean jar_clean jni_clean

../bitmaps/$(LIBPATH)/bitmaps.dll.a:
	$(MAKE_AVIAN) -C ../bitmaps

../libraw/$(LIBPATH)/raw.dll.a:
	$(MAKE_AVIAN) -C ../libraw
	
../jpeg-8c/$(LIBPATH)/jpeg8c.dll.a:
	$(MAKE_AVIAN) -C ../jpeg-8c

include ../target_avian_win32_i386/makefiles/java_classes.mk
include ../target_avian_win32_i386/makefiles/java_jni.mk

.SILENT: