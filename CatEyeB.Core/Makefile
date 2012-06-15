PROJ = CatEyeB.Core

JNI_CPPFLAGS = -static-libgcc -static-libstdc++ -O3

LIBS = ../bitmaps/lib/bitmaps.dll.a \
       ../libraw/lib/raw.dll.a \
       ../jpeg-8c/lib/jpeg8c.dll.a

INCLUDES = ../bitmaps/include \
           ../libraw/libraw \
           ../jpeg-8c/include

JNI_NATIVE_LIB = Core.dll

JARNAME = Core.jar

all: classes jar jni_lib jni_headers
clean: classes_clean jar_clean jni_clean

../bitmaps/lib/bitmaps.dll.a:
	$(MAKE) -C ../bitmaps

../libraw/lib/raw.dll.a:
	$(MAKE) -C ../libraw
	
../jpeg-8c/lib/jpeg8c.dll.a:
	$(MAKE) -C ../jpeg-8c

include ../target_avian_win32_i386/makefiles/java_classes.mk
include ../target_avian_win32_i386/makefiles/java_jni.mk

.SILENT: