PROJ = CatEye (avian, windows, i386)

EXTERNAL = ../external
PLATFORM = windows
ARCH = i386
PWD = $(shell pwd)

BIN = out/avian_win32_i386/bin
JARPATH = out/avian_win32_i386/jar
EMBEDDEDPATH = out/avian_win32_i386/embedded
CLASSPATH = out/avian_win32_i386/classes

MAKE_AVIAN = make -f avian_win32_i386.mk

SYSLIBPATH = /mingw/lib
SYSBINPATH = /mingw/bin
PTHREAD_BIN = $(shell echo `dlltool -I $(SYSLIBPATH)/libpthread.a`)

DEP_JARS = ../external/jar/classpath.jar \
           ../external/jar/swt.jar \
           ../CatEyeB.Core/$(JARPATH)/Core.jar \
           ../CatEyeB.Procedures/$(JARPATH)/Procedures.jar \
           ../CatEyeB.Ui/$(JARPATH)/Ui.jar \
           ../CatEyeB.Ui.Swt/$(JARPATH)/Ui.Swt.jar

DEP_BINS = $(SYSBINPATH)/$(PTHREAD_BIN) \
           ../CatEyeB.Core/$(BIN)/Core.dll \
           ../CatEyeB.Procedures/$(BIN)/Procedures.dll \
           ../bitmaps/$(BIN)/bitmaps.dll \
           ../jpeg-8c/$(BIN)/jpeg8c.dll \
           ../libraw/$(BIN)/raw.dll

EMBEDDED_TARGET_NAME = CatEye

all: avian_embed

clean:
	rm -rf out

../libraw/$(BIN)/raw.dll:
	$(MAKE_AVIAN) -C ../libraw

../jpeg-8c/$(BIN)/jpeg8c.dll:
	$(MAKE_AVIAN) -C ../jpeg-8c

../bitmaps/$(BIN)/bitmaps.dll:
	$(MAKE_AVIAN) -C ../bitmaps

../CatEyeB.Core/$(BIN)/Core.dll:
	$(MAKE_AVIAN) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(BIN)/Procedures.dll:
	$(MAKE_AVIAN) -C ../CatEyeB.Procedures

$(BIN)/$(PTHREAD_BIN): $(SYSBINPATH)/$(PTHREAD_BIN)
	echo "[$(PROJ)] Copying $@"; \
	cp $(SYSBINPATH)/$(PTHREAD_BIN) $(BIN)/$(PTHREAD_BIN)

$(BIN)/raw.dll: ../libraw/$(BIN)/raw.dll
	echo "[$(PROJ)] Copying $@"; \
	cp ../libraw/$(BIN)/raw.dll $(BIN)/raw.dll

$(BIN)/jpeg8c.dll: ../jpeg-8c/$(BIN)/jpeg8c.dll
	echo "[$(PROJ)] Copying $@"; \
	cp ../jpeg-8c/$(BIN)/jpeg8c.dll $(BIN)/jpeg8c.dll

$(BIN)/bitmaps.dll: ../bitmaps/$(BIN)/bitmaps.dll
	echo "[$(PROJ)] Copying $@"; \
	cp ../bitmaps/$(BIN)/bitmaps.dll $(BIN)/bitmaps.dll

$(BIN)/Core.dll: ../CatEyeB.Core/$(BIN)/Core.dll
	echo "[$(PROJ)] Copying $@"; \
	cp ../CatEyeB.Core/$(BIN)/Core.dll $(BIN)/Core.dll

$(BIN)/Procedures.dll: ../CatEyeB.Procedures/$(BIN)/Procedures.dll
	echo "[$(PROJ)] Copying $@"; \
	cp ../CatEyeB.Procedures/$(BIN)/Procedures.dll $(BIN)/Procedures.dll

../CatEyeB.Core/$(JARPATH)/Core.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Core

../CatEyeB.Procedures/$(JARPATH)/Procedures.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Procedures

../CatEyeB.Ui/$(JARPATH)/Ui.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Ui

../CatEyeB.Ui.Swt/$(JARPATH)/Ui.Swt.jar:
	$(MAKE_AVIAN) -C ../CatEyeB.Ui.Swt

avian_embed: $(DEP_JARS) $(DEP_BINS) extract_dep_jars $(BIN)/Core.dll $(BIN)/Procedures.dll $(BIN)/bitmaps.dll $(BIN)/jpeg8c.dll $(BIN)/raw.dll $(BIN)/$(PTHREAD_BIN)
	# Here we implement the default Avian embedding procedure
	mkdir -p $(EMBEDDEDPATH); \
	cd $(EMBEDDEDPATH); \
	echo "Unpacking libavian.a"; \
	ar x $(PWD)/$(EXTERNAL)/lib/libavian.a; \
	cp $(PWD)/$(EXTERNAL)/jar/classpath.jar boot.jar; \
	echo "Packing boot.jar"; \
	"$(JAVA_HOME)/bin/jar" uf boot.jar -C $(PWD)/$(CLASSPATH) .; \
	echo "Converting boot.jar to object file"; \
	$(PWD)/$(EXTERNAL)/bin/binaryToObject boot.jar boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM) $(ARCH); \
	g++ -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" \
        -D_JNI_IMPLEMENTATION_ -c $(PWD)/src/embedded-main.cpp -o embedded-main.o; \
    dlltool -z $(EMBEDDED_TARGET_NAME).def *.o; \
    dlltool -d $(EMBEDDED_TARGET_NAME).def -e $(EMBEDDED_TARGET_NAME).exp; \
    g++ $(EMBEDDED_TARGET_NAME).exp *.o -L$(PWD)/$(EXTERNAL)/lib -lmingwthrd -lm -lz -lws2_32 \
        -mwindows -mconsole -static-libgcc -static-libstdc++ -o $(EMBEDDED_TARGET_NAME).exe; \
    mv -f $(EMBEDDED_TARGET_NAME).exe $(PWD)/$(BIN)
    #strip --strip-all $(EMBEDDED_TARGET_NAME).exe

include ../target_avian_win32_i386/makefiles/dep_copy.mk

.PHONY: all bitmaps raw jpeg8c core clean colorlib procedures ui ui_swt avian_embed
.SILENT: